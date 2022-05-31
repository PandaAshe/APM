package com.jiace.apm.core.dbf

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.jiace.apm.Application
import java.io.File

/**
 * @author: yw
 * @date: 2021-05-27
 * @description: 数据库操作
 */
object DBManager {
    /** 关联的上下文 */
    private var mDeviceDatabase: SQLiteDatabase? = null
    private var mDataDatabase: SQLiteDatabase? = null

    /**
     * 获取关联的上下文
     * @return Context?
     */
    fun getContext() = Application.get()

    /**
     * 设置关联的上下文
     * @param context Context?
     */
    fun setContext(context: Context?) {
        // mContext = context
    }

    /**
     * 初始化
     * @param context Context
     */
    fun init(context: Context) {

        setContext(context)

        // 建立主文件夹
        val folder = File(Application.sFolder)
        if (!folder.exists()) {
            if (!folder.mkdir()) throw Exception("未能创建数据文件夹 " + Application.sFolder)
        }


        val tmpFolder = File(Application.sTempFolder)
        if (!tmpFolder.exists()) {
            if (!tmpFolder.mkdir()) throw Exception("未能创建临时文件夹 " + Application.sTempFolder)
        }

        // 删除临时压缩文件
        try {
            val fileList = folder.list()
            for (i in fileList.indices) {
                val fileExt = fileList[i].split("\\u002E").toTypedArray()
                if (fileExt.size >= 2 && fileExt[fileExt.size - 1] == "zip") {
                    val zipFile: File = File(
                        Application.sFolder,
                        fileList[i]
                    )
                    zipFile.delete()
                }
            }
        } catch (ignore: Exception) {
        }
        val deviceDBHelper = DBDeviceHelper(context, Application.sFolder + "/Device.db3")
        mDeviceDatabase = deviceDBHelper.writableDatabase

        val dataDBHelper = DBDataHelper(context, Application.sFolder + "/Data.db3")
        mDataDatabase = dataDBHelper.writableDatabase

    }

    /**
     * Device.db3
     * @return SQLiteDatabase?
     */
    fun getDeviceDatabase() = mDeviceDatabase

    /**
     * Data.db3
     * @return SQLiteDatabase?
     */
    fun getDataDatabase() = mDataDatabase

    /**
     * 清除资源
     */
    fun close() {
        mDeviceDatabase?.close()
        mDeviceDatabase = null

        mDataDatabase?.close()
        mDataDatabase = null

        setContext(null)
    }

}