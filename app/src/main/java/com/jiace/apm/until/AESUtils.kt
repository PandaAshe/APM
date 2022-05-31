package com.jiace.apm.until

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author: yw
 * @date: 2021-06-04
 * @description: AES加解密
 */
object AESUtils {

    //AES/CBC/NoPadding (128)
    //AES 秘钥长度是16位，每位8个字节，所以是  16*8=128

    //   算法/工作模式/填充模式
    //   AES/CBC/NoPadding (128)
    //   AES/CBC/PKCS5Padding(128)
    //   AES/ECB/NoPadding(128)
    //   AES/ECB/PKCS5Padding(128)
    private val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private val CRYPT_ALGORITHM = "AES"

    private val cipher = Cipher.getInstance(TRANSFORMATION)

    /**
     * AES加密
     * @param aesKey String 密码(16个字节,不足自动在后补0,超出截取前16个字节)
     * @param originContent String 需要加密的字符串
     * @return String
     */
    fun encode(aesKey: String, originContent: String): String {
        initCipher(aesKey, Cipher.ENCRYPT_MODE)
        // 3、Base64编码 加密后的内容
        return Base64.encodeToString(cipher.doFinal(originContent.toByteArray()), Base64.NO_WRAP)
    }

    /**
     * AES解密
     * @param aesKey String 密码(16个字节,不足自动在后补0,超出截取前16个字节)
     * @param encryptContent String 需要解密的字符串
     * @return String
     */
    fun decode(aesKey: String, encryptContent: String): String {
        initCipher(aesKey, Cipher.DECRYPT_MODE)
        // 3、Base64解码 解密后的内容
        return String(cipher.doFinal(Base64.decode(encryptContent, Base64.NO_WRAP)))
    }

    private fun initCipher(aesKey: String, mode: Int) {
        // 1、初始化AES相关的操作
        val key: SecretKeySpec = initKey(aesKey)
        // 2、初始化cipher对象(参数一：解密模式)
        if (TRANSFORMATION.contains("CBC")) {
            // CBC工作模式需要额外添加参数,否则报错：java.security.InvalidKeyException: Parameters missing
            val iv = IvParameterSpec(key.encoded)
            cipher.init(mode, key, iv)
        } else {
            cipher.init(mode, key)
        }
    }

    private fun initKey(aesKey: String): SecretKeySpec {
        val bytes = aesKey.toByteArray().toMutableList()
        // 判断bytes的长度是否为16个字节,如果不是,尾部用0补齐
        while (bytes.size > 16) bytes.removeLast()
        repeat(16 - bytes.size) {
            bytes.add(0x00)
        }
        return SecretKeySpec(bytes.toByteArray(), CRYPT_ALGORITHM)
    }
}