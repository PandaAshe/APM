package com.jiace.apm.core.service

import com.jiace.apm.until.Utils

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/18.
3 * Description:
4 *
5 */
open class Frame constructor(){

    companion object {

        /** 帧头 */
        const val FrameHead = 0x4A.toByte()

        /** 配置扭矩传感器编号 */
        const val Command_Config_Sensor = 0x10.toByte()

        /** 发送主机运行状态 */
        const val Command_Machine_Statue = 0x11.toByte()

        /** 回复配置扭矩传感器编号 */
        const val Respond_Config_Sensor = 0x20.toByte()

        /** 回复主机运行状态 */
        const val Respond_Machine_Statue = 0x21.toByte()

        /** 帧尚未收完 */
        const val Status_NoFinish = 0x01

        /** 帧头错误 */
        const val Status_HeadError = 0x02

        /** 帧长度错误 */
        const val Status_LengthError = 0x03

        /** 帧类型错误  */
        const val Status_TypeError = 0x04

        /** 帧校验和错误  */
        const val Status_CheckError = 0x05

        /** 帧尾错误  */
        const val Status_TailError =0x06

        /** 正确  */
        const val Status_OK = 0x00
    }

    /**
     * 检查是否为完整数据
     * @param buff ByteArray?
     * @param offset Int
     * @return Int
     */
    fun checkFrameFromBuffer(buff: ByteArray?, offset: Int):Int {
        buff?.let {

            // 帧是否达到最小长度
            if(it.size < 8 || it.size <= offset) {
                return Status_NoFinish
            }

            // 帧头是否正确
            val head = it[offset]
            if(head != FrameHead) {
                return Status_HeadError
            }

            // 帧长度是否正确
            val dataLength = it[offset + 6].toInt().and(0xFF)
            if(dataLength + 8 > it.size) {
                return Status_NoFinish
            }

            // 校验和是否正确
            var sum = 0
            for(i in 0 until dataLength + 8) {
                sum += it[offset + i]
            }
            if(sum.and(0xFF) != 0) {
                return Status_CheckError
            }

            return Status_OK
        }
        return Status_NoFinish
    }

    fun getMinDataLength() = 0

    fun getMaxDataLength() = 127

    fun getMinFrameLength() = 8 + getMinDataLength()

    fun getMaxFrameLength() = 8 + getMaxDataLength()


    /** 帧头 */
    private val mFrameHead = FrameHead

    /** 发送方ID */
    private var mSendID = 0

    /** 命令字 */
    private var mCommand:Byte = 0

    /** 数据 */
    private var mData: ByteArray? = null

    /** 校验和 */
    private var mCheckSum:Byte = 0

    /** 帧是否有效 */
    private var mIsValid = false


    /**
     * 从缓冲区中取出数据帧
     * @param buff ByteArray
     * @param offset Int
     */
    fun getFrameFromBuffer(buff: ByteArray, offset: Int) {
        mIsValid = false
        if(checkFrameFromBuffer(buff, offset) == Status_OK) {

            // 发送方ID
            mSendID = Utils.bytesToInt( buff, offset + 1)

            // 命令字
            mCommand = buff[offset + 5]

            // 附加数据长度
            val dataLength = buff[offset + 6].toInt().and(0xFF)

            if(dataLength > 0) {
                mData = ByteArray(dataLength)
                System.arraycopy(buff, offset + 7, mData!!, 0, dataLength)
            } else {
                mData = null
            }

            // 校验
            mCheckSum = buff[offset + dataLength + 7]

            mIsValid = true
        }
    }

    /**
     * 构造数据包
     * @param sendID Int
     * @param command Byte
     * @param buffer ByteArray?
     */
    fun makeFrame(sendID: Int, command: Byte, buffer: ByteArray?) {
        mSendID = sendID
        mCommand = command
        mData = buffer
        mCheckSum = calcCheckSum()
        mIsValid = true
    }

    /**
     * 计算校验和
     * @return Byte
     */
    private fun calcCheckSum():Byte {
        var sum = 0
        sum += mFrameHead
        sum += mSendID.and(0xFF).toByte()
        sum += mSendID.shr(8).and(0xFF).toByte()
        sum += mSendID.shr(16).and(0xFF).toByte()
        sum += mSendID.shr(24).and(0xFF).toByte()
        sum += mCommand
        sum += getDataLenght().and(0xFF).toByte()
        mData?.forEach {
            sum += it
        }

        return (0 - sum).toByte()
    }

    fun isValid() = mIsValid

    fun getSenderId() = mSendID

    fun getCommand() = mCommand

    fun getDataLenght():Int  {
        var length = 0
        mData?.let {
            length = it.size
        }
        return length
    }

    fun getData() = mData

    /**
     * 将数据包转换为字节数组
     * @return ByteArray?
     */
    fun toBytes() = run{
        mCheckSum = calcCheckSum()
        if(mIsValid) {
            val bytes = arrayListOf<Byte>()
            bytes.add(mFrameHead)
            bytes.add(mSendID.and(0xFF).toByte())
            bytes.add(mSendID.shr(8).and(0xFF).toByte())
            bytes.add(mSendID.shr(16).and(0xFF).toByte())
            bytes.add(mSendID.shr(24).and(0xFF).toByte())
            bytes.add(mCommand)
            if(mData == null) {
                bytes.add(0)
            } else {
                bytes.add(mData!!.size.and(0xFF).toByte())
                mData?.forEach {
                    bytes.add(it)
                }
            }
            bytes.add(mCheckSum)
            bytes.toByteArray()
        } else null
    }

    /**
     * 生成一个同样的数据帧
     * @return JyeFrame
     */
    fun clone(): Frame {
        val frame = Frame()
        frame.mCommand = mCommand
        frame.mSendID = mSendID
        frame.mData = mData?.clone()
        frame.mCheckSum = mCheckSum
        frame.mIsValid = mIsValid
        return frame
    }
}

/** 构造发送主机状态数据帧 */
class MainMachineStatue(sampleMachineId: Int, status: Int) : Frame() {
    init {
        val data = byteArrayOf((status and 0xFF).toByte())
        makeFrame(sampleMachineId, Command_Machine_Statue,data)
    }
}

/**构造配置扭矩传感器 */
class ConfigSensor(sampleMachineId: Int, bluetoothId: String) : Frame() {
    init {
        val data = Utils.stringToByte(bluetoothId,9)
        makeFrame(sampleMachineId, Command_Config_Sensor,data)
    }
}






