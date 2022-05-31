package com.jiace.apm.ui.file

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.View
import androidx.core.content.FileProvider
import com.jiace.apm.common.OnFreshFileList
import com.jiace.apm.common.dialog.LoadingDialog
import com.jiace.apm.common.dialog.SureAlertDialog
import com.jiace.apm.core.dataStruct.Doc
import com.jiace.apm.core.dbf.TBBasicInfoHelper
import com.jiace.apm.core.dbf.TBRecycleHelper
import com.jiace.apm.until.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileController(val context: Context, private val fileActivity: FileListActivity) :
    FileControllerInterface {

    /** 文件列表 */
    val mFileList = ArrayList<HashMap<String, Any>>()

    /** 当前数据页 */
    private var mCurrentPageIndex = 1

    /** 数据源 */
    private var mDataMode = Mode.Origin

    enum class Mode {
        Recycle, Origin
    }

    companion object {
        const val PageRange = 30
        private val OriginText = arrayOf("文件管理", "数据回收站")
    }

    init {
       register(this)
    }

    fun onDestroy() {
        EventBus.getDefault().unregister(this)
    }

    // <editor-fold desc="相应操作">
    override fun onRefresh() {
        mCurrentPageIndex = 1
        when (fileActivity.getQueryMode()) {
            FileListActivity.QUERY_PLIE_NO -> {
                queryFileByPileNo(false, fileActivity.mQueryTerm)
            }

            FileListActivity.QUERY_NAME -> {
                queryFileByProjectName(false, fileActivity.mQueryTerm)
            }

            FileListActivity.QUERY_NUM -> {
                queryFileByNo(false, fileActivity.mQueryTerm)
            }

            FileListActivity.QUERY_COUNT -> {
                queryRecentData(false, fileActivity.mQueryCount)
            }

            FileListActivity.QUERY_TIME -> {
                queryFileByStartTime(
                    false,
                    fileActivity.mQueryStartTime,
                    fileActivity.mQueryEndTime
                )
            }
        }
    }

    override fun onLoadMore() {
        mCurrentPageIndex += 1
        when (fileActivity.getQueryMode()) {
            FileListActivity.QUERY_PLIE_NO -> {
                queryFileByPileNo(true, fileActivity.mQueryTerm)
            }

            FileListActivity.QUERY_NAME -> {
                queryFileByProjectName(true, fileActivity.mQueryTerm)
            }

            FileListActivity.QUERY_NUM -> {
                queryFileByNo(true, fileActivity.mQueryTerm)
            }

            FileListActivity.QUERY_COUNT -> {
                queryRecentData(true, fileActivity.mQueryCount)
            }

            FileListActivity.QUERY_TIME -> {
                queryFileByStartTime(
                    true,
                    fileActivity.mQueryStartTime,
                    fileActivity.mQueryEndTime
                )
            }
        }
    }

    override fun onFileChoose(position: Int) {
        if (mDataMode == Mode.Origin) {
            val basicInfoId = mFileList[position]["BasicInfoId"] as Long
            FilePreviewDialog(fileActivity, basicInfoId).show()
        }
    }

    override fun onFileDelete(checkedList: ArrayList<Int>) {
        fileActivity.onCancelPickClick()
        val message = if (mDataMode == Mode.Origin) {
            "确定要将这些数据吗移动到回收站吗？"
        } else {
            "确定要彻底删除这些数据吗？"
        }
        val bottomText = if (mDataMode == Mode.Origin) {
            "确定"
        } else {
            "删除"
        }

        SureAlertDialog(context).apply {
            setTitle("提示")
            setMessage(message)
            setPositionButton(bottomText) { dialog ->
                dialog.dismiss()

                val ld = LoadingDialog(context).apply {
                    setCancelButtonGone()
                    setMessageText("正在移动至回收站...")
                    show()
                }

                if (mDataMode == Mode.Origin) {
                    flow {
                        checkedList.map {
                            val basicInfoId = mFileList[it]["BasicInfoId"] as Long
                            if (TBRecycleHelper.isBasicInfoExist(basicInfoId)) {
                                TBRecycleHelper.deleteBasicInfo(basicInfoId)
                                TBBasicInfoHelper.queryBasicInfo(basicInfoId)
                                    ?.let { basicInfo ->
                                        TBRecycleHelper.insertBasicInfo(basicInfo)
                                        TBBasicInfoHelper.deleteBasicInfo(basicInfoId)
                                    }
                            } else {
                                TBBasicInfoHelper.queryBasicInfo(basicInfoId)
                                    ?.let { basicInfo ->
                                        TBRecycleHelper.insertBasicInfo(basicInfo)
                                        TBBasicInfoHelper.deleteBasicInfo(basicInfoId)
                                    }
                            }
                        }
                        emit(1)
                    }.flowOn(Dispatchers.IO).onEach {
                        ld.dismiss()
                        checkedList.sort()
                        for (i in checkedList.size -1 downTo  0) {
                            mFileList.removeAt(checkedList[i])
                        }
                        showCenterToast(context,"移动至回收站成功")
                        fileActivity.notifyDataSetChanged()
                    }.launchIn(applicationScope)

                } else {
                    ld.setMessageText("正在删除数据...")
                    flow {
                        checkedList.map {
                            val basicInfoId = mFileList[it]["BasicInfoId"] as Long
                            TBRecycleHelper.deleteBasicInfoAndDetailsData(basicInfoId)
                        }
                        emit(0)
                    }.flowOn(Dispatchers.IO).onEach {
                        ld.dismiss()
                        showCenterToast(context,"删除成功")
                        checkedList.sort()
                        for (i in checkedList.size -1 downTo  0) {
                            mFileList.removeAt(checkedList[i])
                        }
                        fileActivity.notifyDataSetChanged()
                    }.launchIn(applicationScope)
                }
            }
            setNegativeButton("取消") {
                it.dismiss()
            }
            show()
        }
    }

    override fun onUndoFile(checkedList: ArrayList<Int>) {

        fileActivity.onCancelPickClick()

        SureAlertDialog(context).apply {
            setTitle("提示")
            setMessage("确定要恢复这些数据吗？")
            setPositionButton("恢复") { dialog ->
                dialog.dismiss()

                val ld = LoadingDialog(context).apply {
                    setCancelButtonGone()
                    setMessageText("正在恢复数据...")
                    show()
                }

                flow {
                    checkedList.map {
                        val basicInfoId = mFileList[it]["BasicInfoId"] as Long
                        if (TBBasicInfoHelper.isBasicInfoExist(basicInfoId)) {
                            TBRecycleHelper.deleteBasicInfo(basicInfoId)
                        } else {
                            TBRecycleHelper.queryBasicInfo(basicInfoId)?.let { basicInfo ->
                                TBBasicInfoHelper.insertBasicInfo(basicInfo)
                                TBRecycleHelper.deleteBasicInfo(basicInfoId)
                            }
                        }
                    }
                    emit(1)
                }.flowOn(Dispatchers.IO).onEach {
                    ld.dismiss()
                    showCenterToast(context,"操作成功")
                    checkedList.sort()
                    for (i in checkedList.size -1 downTo  0) {
                        mFileList.removeAt(checkedList[i])
                    }
                    fileActivity.notifyDataSetChanged()
                }.launchIn(applicationScope)
            }
            setNegativeButton("取消") {
                it.dismiss()
            }
            show()
        }
    }

    override fun onFileUpload() {

    }

    override fun onModifyProject(position: Int) {

    }

    override fun onShareFile(checkedList: ArrayList<Int>) {
        fileActivity.onCancelPickClick()
        val checkedIndex = IntArray(checkedList.size) { -1 }
        checkedList.mapIndexed { index, i ->
            checkedIndex[index] = i
        }
        shareFile(checkedList) { shareFile(it) }
    }

    /** 分享文件 */
    private fun shareFile(checkedList: ArrayList<Int>, callback: (file: File) -> Unit)  {
        val jyDocs = ArrayList<Doc>()
        applicationScope.launch(Dispatchers.Main) {
            val ld = LoadingDialog(context).apply {
                setMessageText("正在生成文件中...")
                setCancelButtonGone()
                show()
            }
            var errorMessage = "创建文件失败"
            val file = withContext(Dispatchers.IO) {
                checkedList.map {
                    val basicInfoId = mFileList[it]["BasicInfoId"] as Long
                    jyDocs.add(Doc(basicInfoId))
                }
                try {
                   exportFile(jyDocs)
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorMessage = e.message ?: ""
                    null
                }
            }
            ld.dismiss()
            if (file == null) {
                showCenterToast(context, "分享文件异常($errorMessage)")
                return@launch
            }
            callback.invoke(file)
        }
    }

    // 生成文件
    private fun parseJyDocs(checkedList: ArrayList<Int>, callback: (file: ArrayList<Doc>) -> Unit) {
        val jyDocs = ArrayList<Doc>()
        applicationScope.launch(Dispatchers.Main) {
            val ld = LoadingDialog(context).apply {
                setMessageText("正在生成文件中...")
                setCancelButtonGone()
                show()
            }
            val errorMessage = "创建文件失败"
            val files = withContext(Dispatchers.IO) {
                checkedList.map {
                    val basicInfoId = mFileList[it]["BasicInfoId"] as Long
                    jyDocs.add(Doc(basicInfoId))
                }
            }
            ld.dismiss()
            if (files.isNullOrEmpty()) {
                showCenterToast(context, "分享文件异常($errorMessage)")
                return@launch
            }
            callback.invoke(jyDocs)
        }
    }

    override fun onFileExport(checkedList: ArrayList<Int>) {

        fileActivity.onCancelPickClick()
    }

    override fun onChangeOrigin(view: View) {
        val index = if (mDataMode == Mode.Origin) {
            0
        } else {
            1
        }
        ChoosePopupWindow(context, index, OriginText) {
            val chooseMode = if (it == 0) {
                Mode.Origin
            } else {
                Mode.Recycle
            }
            if (chooseMode != mDataMode) {
                mDataMode = chooseMode
                // mFileList.clear()
                // fileActivity.notifyDataSetChanged()
            }
            val pageName = when (mDataMode) {
                Mode.Recycle -> {
                    OriginText[1]
                }
                Mode.Origin -> {
                    OriginText[0]
                }
            }
            fileActivity.updatePageName(pageName, mDataMode == Mode.Origin)
            fileActivity.autoChangeQueryMode()
            onRefresh()
        }.apply {
            showAsDropDown(view)
        }
    }

    override fun onRecoverTesting(checkedIndex: Int) {

    }

    override fun onChangeServer(position: Int, isUpload: Int) {

    }
    // </editor-fold>

    // <editor-fold desc="数据查询操作">
    override fun queryFileByProjectName(isLoadMoreData: Boolean, queryTerm: String) {
        queryData(isLoadMoreData) {
            if (mDataMode == Mode.Origin) {
                TBBasicInfoHelper.queryBasicInfoByProjectName(
                    queryTerm,
                    mCurrentPageIndex,
                    PageRange
                )
            } else {
                TBRecycleHelper.queryRecycleByProjectName(
                    queryTerm,
                    mCurrentPageIndex,
                    PageRange
                )
            }
        }
    }

    override fun queryFileByPileNo(isLoadMoreData: Boolean, queryTerm: String) {
        queryData(isLoadMoreData) {
            if (mDataMode == Mode.Origin) {
                TBBasicInfoHelper.queryBasicInfoByPileNo(queryTerm, mCurrentPageIndex, PageRange)
            } else {
                TBRecycleHelper.queryRecycleByPileNo(queryTerm, mCurrentPageIndex, PageRange)
            }
        }
    }

    override fun queryFileByNo(isLoadMoreData: Boolean, queryTerm: String) {
        queryData(isLoadMoreData) {
            TBBasicInfoHelper.queryBasicInfoBySerialNo(queryTerm, mCurrentPageIndex, PageRange)
        }
    }

    override fun queryFileByStartTime(isLoadMoreData: Boolean, startTime: Date, endTime: Date) {
        queryData(isLoadMoreData) {
            if (mDataMode == Mode.Origin) {
                TBBasicInfoHelper.queryBasicInfoByTestTime(
                    startTime,
                    endTime,
                    mCurrentPageIndex,
                    PageRange
                )
            } else {
                TBRecycleHelper.queryRecycleByTestTime(
                    startTime,
                    endTime,
                    mCurrentPageIndex,
                    PageRange
                )
            }

        }
    }

    override fun queryRecentData(isLoadMoreData: Boolean, count: Int) {
        queryData(isLoadMoreData) {
            if (mDataMode == Mode.Origin) {
                TBBasicInfoHelper.queryBasicInfoByRecentCount(mCurrentPageIndex, count)
            } else {
                TBRecycleHelper.queryRecycle(mCurrentPageIndex, count)
            }
        }
    }

    /** 查询数据库 */
    private fun queryData(isLoadMoreData: Boolean, action: () -> ArrayList<HashMap<String, Any>>) {
        applicationScope.launch(Dispatchers.Main) {
            val data = withContext(Dispatchers.Default) { action.invoke() }
            if (isLoadMoreData) {
                mFileList.addAll(data)
            } else {
                mFileList.clear()
                mFileList.addAll(data)
            }
            if (isLoadMoreData) {
                fileActivity.stopLoadingMore(data.size != 0)
            } else {
                fileActivity.stopFreshData()
            }
            fileActivity.notifyDataSetChanged()
        }
    }
    // </editor-fold>

    // <editor-fold desc="分享数据">
    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(context, "com.jiace.apm.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = getMimeType(file.absolutePath)
        fileActivity.startActivity(Intent.createChooser(intent, "分享"))
    }

    private fun getMimeType(filePath: String): String {
        val mmr = MediaMetadataRetriever()
        var mime: String? = "*/*"
        try {
            mmr.setDataSource(filePath)
            mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        } catch (e: Exception) {
            return mime ?: ""
        }
        return mime ?: ""
    }


    // </editor-fold>

    /** 响应查询事件*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFreshFileList(queryEvent: OnFreshFileList) {
        onRefresh()
    }
}