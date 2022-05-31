package com.jiace.apm.until

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.jiace.apm.Application
import com.jiace.apm.R
import com.jiace.apm.core.dataStruct.Doc
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

private val INVALID_SYMBOL = arrayOf("\\", "/", ":", "*", "?", "\"", "<", ">", "|")
const val TEXT_ERROR = "“ / , \\ , : , * , ? , \" , < , > , | ” 字符"

/** 测试用的深度 */
var testDepth: Int = 0
/** 圈数 */
var testCycles: Int = 0


/** 发布事件 */
fun Any.post() {
    EventBus.getDefault().post(this)
}

/** 订阅 */
fun register(any: Any) {
    EventBus.getDefault().register(any)
}

/** 结束订阅 */
fun unRegister(any: Any) {
    EventBus.getDefault().unregister(any)
}


// 检测有没有非法字符
fun checkTextIsValid(text: String): Boolean {
    INVALID_SYMBOL.map {
        if (text.contains(it))
            return false
    }
    return true
}

fun getString(@StringRes id: Int) = Application.get().getString(id)

fun showToast(content: String) {
    Toast.makeText(Application.get(),content,Toast.LENGTH_LONG).show()
}

fun showSnackbar(view: View,message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun showSnackMessage(view: View,message: String,isShow: Boolean): Boolean {
    if (isShow) {
        showSnackbar(view,message)
        return false
    }
    return true
}

fun getStringFromResources(context: Context,@StringRes id: Int) = context.resources.getString(id)

/*fun rsaEncrypt(context: Context, sourceText: String): String {
    val publicKey = getStringFromResources(context, R.string.public_key)
    val data = X509EncodedKeySpec(Base64.decode(publicKey.toByteArray(), Base64.DEFAULT))

    val factory = KeyFactory.getInstance("RSA")
    val key = factory.generatePublic(data)

    val cipher =  Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE,key)
    val encryptData = cipher.doFinal(sourceText.toByteArray())
    return Base64.encodeToString(encryptData, Base64.DEFAULT)
}*/

fun getStringArray(@ArrayRes id: Int): Array<String> {
    return Application.get().baseContext.resources.getStringArray(id)
}

fun getColor(@ColorRes id: Int) = ContextCompat.getColor(Application.get(),id)

fun showProcessDialog(context: Context,title: String,content: String): ProgressDialog = ProgressDialog.show(context, title, content,false,false)

fun Float.dip2px(context: Context) = run {
    DisplayUtil.getDisplayMetrics(context).run {
        DisplayUtil.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this@dip2px, this)
    }
}

/** 创建照片文件 */
@Throws(Exception::class)
fun createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir = Application.get().getExternalFilesDir(Environment.DIRECTORY_DCIM)
    return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir)
}

/** 压缩照片 */
@Throws(Exception::class)
fun compressImage(originImage: File): File {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
    }
    var compressBitMap = BitmapFactory.decodeFile(originImage.path,options)
    compressBitMap = Bitmap.createScaledBitmap(compressBitMap,compressBitMap.width / 2,compressBitMap.height / 2,true)
    val compressImageName = originImage.name.split(".")[0]

    val storageDir = Application.get().getExternalFilesDir(Environment.DIRECTORY_DCIM)
    val compressFileImage = File.createTempFile(compressImageName,".png",storageDir)

    FileOutputStream(compressFileImage).use {
        compressBitMap.compress(Bitmap.CompressFormat.JPEG,80,it)
    }
    return compressFileImage
}

fun compressImage(context: Context,uri: Uri): File {
    return compressImage(File(uri.toFilePath(context)))
}

fun Uri.toFilePath(context: Context): String {
    val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = context.contentResolver.query(this,filePathColumn,null,null,null)
    var picturePath = ""
    cursor?.use {
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        if (columnIndex != -1) {
            picturePath = cursor.getString(columnIndex)
        }
    }
    if (picturePath.isEmpty()) {
        val storageDir = Application.get().getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val copyFile = File.createTempFile("${SystemClock.currentThreadTimeMillis()}",".jpg",storageDir)
        context.contentResolver.openInputStream(this).use {
            copyFile.writeBytes(it!!.readBytes())
        }
        return copyFile.absolutePath
    }
    return picturePath
}


/**
 * 创建自定义Date格式的GSon
 * @return (com.google.gson.Gson..com.google.gson.Gson?)
 */
fun buildGson() = GsonBuilder().registerTypeAdapter(Date::class.java, UtilDateGSON()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()


fun getMD5(str: String): String {
    try {
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(str.toByteArray())
        val hash = md5.digest()
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b and 0xFF.toByte() < 0x10) hex.append("0")
            hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
        }
        return hex.toString()
    } catch (ignore: java.lang.Exception) {
    }
    return ""
}



/**
 * 弹出一个居中的对话框
 * */
fun showCenterToast(context: Context,info: String) {
    val linearLayout = (LayoutInflater.from(context).inflate(R.layout.layout_toast, LinearLayout(context), false));
    val textView = linearLayout.findViewById<TextView>(R.id.textToast)
    textView.text = info
    Toast(context).apply {
        duration = Toast.LENGTH_SHORT;
        setGravity(Gravity.CENTER, 0, 0);
        view = linearLayout;
        show();
    }
}

/** 拓展方法 Any.toInt() */
fun Any.toIntValue() = this.toString().toInt()


/** 格式化时间 */
fun formatSecondToTime(time: Int): String {
    if (time <= 0) {
        return "00:00"
    }
    val minute: Int
    val second: Int
    return if (time >= 60) {
        minute = time / 60
        second = time % 60
        "%s:%s".format(formatInt(minute),formatInt(second))
    } else {
        second = time
        "00:%s".format(formatInt(second))
    }
}

private fun formatInt(second: Int): String {
    if (second / 10 == 0) {
        return String.format("0%s",second)
    }
    return "%s".format(second)
}

/** 检查当前文件夹 */
fun checkFileDir(): File? {
    val currentDate = Utils.formatDate(Date())
    // 创建文件夹
    try {
        for (i in 0..Int.MAX_VALUE) {
            val dir = if (i == 0) {
                File("${Application.sDataFolder}${currentDate}/")
            } else {
                File("${Application.sDataFolder}${currentDate}(${i})/")
            }
            if (!dir.exists()) {
                return if (dir.mkdirs()) {
                    dir
                } else {
                    null
                }
            }
        }
    } catch (e: Exception) {
        return null
    }
    return null
}

@Throws(Exception::class)
fun exportFile(jyDocs: ArrayList<Doc>): File {
    val dir = checkFileDir() ?: throw Exception("创建文件夹异常")
    val files = Array<File?>(jyDocs.size) { null }
    jyDocs.mapIndexed { index, doc ->
        val data = doc.saveToExcel()
        val file = checkFileName(dir,"${doc.mBasicInfo.mProjectName}_${doc.mBasicInfo.mPileNo}")
        if (!file.exists()) {
            if (file.createNewFile()) {
                file.appendBytes(data)
                files[index] = file
            } else {
                throw Exception("创建文件异常")
            }
        } else {
            if (file.delete()) {
                file.appendBytes(data)
                files[index] = file
            } else {
                throw Exception("创建文件异常")
            }
        }
    }
    return Utils.makeZipFiles(dir,files)
}

/** check file name */
fun checkFileName(dir: File,fileName: String): File {
    for (i in 0..Int.MAX_VALUE) {
        val file = if (i == 0) {
            File(dir,"$fileName.txt")
        } else {
            File(dir,"$fileName($i).txt")
        }
        if (!file.exists()) {
            if (file.createNewFile()) {
                return file
            }
        }
    }
    throw Exception("创建文件失败")
}

