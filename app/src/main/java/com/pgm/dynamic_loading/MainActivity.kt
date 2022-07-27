package com.pgm.dynamic_loading

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.pgm.dynamic_loading.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val contentLayoutId = R.layout.activity_main
    private val manager: SplitInstallManager by lazy { SplitInstallManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        updateDynamicFeatureButtonState()
        binding.buttonAppDownload.setOnClickListener {
            val request = SplitInstallRequest.newBuilder()
                .addModule(DYNAMIC_MODULE_NAME)
                .build()

            manager.startInstall(request)
                .addOnSuccessListener {showToast("（狀態監聽）已安裝模塊 ${DYNAMIC_MODULE_NAME}") }
                .addOnFailureListener {
                    showToast("（狀態監聽）加載錯誤 ${DYNAMIC_MODULE_NAME}")
                    /*when (errorCode) {
                        SplitInstallErrorCode.NETWORK_ERROR -> {
                            showToast("网络错误：无法获取拆分详情")
                            //showToast("Error Loading ${DYNAMIC_MODULE_NAME}")
                            // network connection.
                        }
                        SplitInstallErrorCode.ACCESS_DENIED -> {
                            showToast("当前设备情况下不允许下载")
                        }
                        SplitInstallErrorCode.NO_ERROR -> {
                            showToast("00000")
                        }
                        SplitInstallErrorCode.SESSION_NOT_FOUND -> {
                            showToast("未找到请求的会话")
                        }
                        SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED -> {
                            showToast("当前应用程序正在运行的会话过多，必须首先解决现有会话")
                        }
                        SplitInstallErrorCode.API_NOT_AVAILABLE -> {
                            showToast("拆分安装 API 不可用")
                        }
                        SplitInstallErrorCode.INCOMPATIBLE_WITH_EXISTING_SESSION -> {
                            showToast("请求的会话包含来自现有活动会话的模块以及新模块")
                        }
                        SplitInstallErrorCode.INSUFFICIENT_STORAGE -> {
                            showToast("存儲空間不足，安裝失敗")
                        }
                        SplitInstallErrorCode.INTERNAL_ERROR -> {
                            showToast("處理拆分安裝的未知錯誤")
                        }
                        SplitInstallErrorCode.INVALID_REQUEST -> {
                            showToast("請求無效")
                        }
                        SplitInstallErrorCode.MODULE_UNAVAILABLE -> {
                            showToast("请求的模块不可用（对于此用户/设备，对于已安装的 apk）")
                        }
                        SplitInstallErrorCode.SESSION_NOT_FOUND -> {
                            showToast("未找到请求的会话。")
                        }
                        SplitInstallErrorCode.SPLITCOMPAT_COPY_ERROR -> {
                            showToast("为 SplitCompat 复制文件时出错")
                        }
                        SplitInstallErrorCode.SPLITCOMPAT_EMULATION_ERROR -> {
                            showToast("splitCompat 仿真中的错误")
                        }
                        SplitInstallErrorCode.SPLITCOMPAT_VERIFICATION_ERROR -> {
                            showToast("调用 SplitCompat 时出现签名验证错误")
                        }
                    }*/
                }
                .addOnCompleteListener {showToast("（狀態監聽）已完成該項動作的監聽 ")}

            manager.registerListener {
                when (it.status()) {
                    //SplitInstallSessionStatus.DOWNLOADING -> showToast(getString(R.string.downloading_feature))
                    SplitInstallSessionStatus.INSTALLED -> {
                        showToast(getString(R.string.feature_ready_to_be_used))
                        updateDynamicFeatureButtonState()
                    }
                    else -> { /*Do nothing in this example */
                    }
                }
            }
        }

        binding.buttonAppOpen.setOnClickListener {
            val intent = Intent()
            intent.setClassName(BuildConfig.APPLICATION_ID, "com.pgm.ondemand.OnDemandActivity")
            startActivity(intent)
        }

        binding.buttonAppUninstall.setOnClickListener {
            requestUninstall()
        }
    }

    private fun updateDynamicFeatureButtonState() {
        binding.buttonAppOpen.isEnabled = manager.installedModules.contains(DYNAMIC_MODULE_NAME)
        binding.buttonAppUninstall.isEnabled = manager.installedModules.contains(DYNAMIC_MODULE_NAME)
    }

    /**
     * 請求卸載對應 module
     */
    private fun requestUninstall() {
        val installModule = manager.installedModules
        showToast("現已安裝的module有 $installModule")

        manager.deferredUninstall(listOf("ondemand"))
            .addOnSuccessListener { showToast("（狀態監聽）卸載模塊 ${DYNAMIC_MODULE_NAME} 成功") }
            .addOnFailureListener { exception ->
                showToast("（狀態監聽）卸載模塊 ${DYNAMIC_MODULE_NAME} 失敗")
            }
            .addOnCompleteListener { showToast("（狀態監聽）已完成該項動作的監聽") }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DYNAMIC_MODULE_NAME = "ondemand"
        private const val errorCode = 1
    }
}