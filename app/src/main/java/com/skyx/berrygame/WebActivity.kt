package com.skyx.berrygame

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.skyx.berrygame.bean.JsMethod
import com.skyx.berrygame.bean.VersionInfo
import com.skyx.berrygame.databinding.ActivityWebBinding
import com.skyx.berrygame.utils.LanguageHelper
import org.json.JSONObject
import java.util.Locale


class WebActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding
    private var canGoDesktop = true// 是否允許返回到手機桌面
    private var exitTime: Long = 0
    private var mLanguage = ""

    private var webView: WebView? = null
    var WEB_URL = "https://www.berrygame.xyz"
//    var WEB_URL = "http://192.168.1.216:8080/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initWebView()
    }

    private fun initWebView() {
        val intent = intent
        val webUrl = intent.getStringExtra("webUrl").toString()
        val language = intent.getStringExtra("language").toString()
        if (!TextUtils.isEmpty(webUrl)) {
            WEB_URL = webUrl
            canGoDesktop = false
            binding.llTopBar.visibility = View.VISIBLE
            binding.llTip.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(language)) {
            LanguageHelper.getAttachBaseContext(this, language)
        }
        binding.ivBack.setOnClickListener {v ->
            if (webView!!.canGoBack()) {
                webView!!.goBack()//返回上一个页面
            } else {
                finish()
            }
        }
        binding.ivOpenBrowser.setOnClickListener{v ->
            val intent = Intent()
            intent.setAction("android.intent.action.VIEW")
            val content_url = Uri.parse(webView?.url)
            intent.setData(content_url)
            startActivity(intent)
        }
        //webView = findViewById(R.id.webView)
        webView = binding.webView
        webView?.loadUrl(WEB_URL)

        val webClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                binding.progressBar.visibility = View.GONE
            }
        }

        val webChromeClient = object: WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                val showTitle: String = title.toString()
                binding.tvTitle.text = showTitle
            }
        }

        //下面这些直接复制就好
        webView?.webViewClient = webClient
        webView?.webChromeClient = webChromeClient

        webView?.addJavascriptInterface(JsMethod {
            val params = it
            val jsonObject:JSONObject = JSONObject(params)
            val action:String = jsonObject.getString("action")
            val type:String = jsonObject.getString("type")
            when(action) {
                "jumpWebView" -> {
                    val jumpUrl:String = jsonObject.getString("url")
                    if ("outer" == type) {
                        //Log.d("Mortal[outer]", "外部浏览器")
                        val intent = Intent()
                        intent.setAction("android.intent.action.VIEW")
                        val content_url = Uri.parse(jumpUrl)
                        intent.setData(content_url)
                        startActivity(intent)
                    } else if ("self" == type) {
                        //Log.d("Mortal[self]", "新开页面")
                        val intent = Intent(this, WebActivity::class.java)
                        intent.putExtra("webUrl", jumpUrl)
                        if (!TextUtils.isEmpty(mLanguage)) {
                            intent.putExtra("language", mLanguage)
                        }
                        startActivity(intent)
                    }
                }
                "changeLanguage" -> {
                    if (!TextUtils.isEmpty(type)) {
                        mLanguage = type
                        Log.d("BerryGame[js切换语言]", mLanguage)
                        LanguageHelper.getAttachBaseContext(this, mLanguage)
                    }
                }
                "appDownload" -> {
                    val jumpUrl:String = jsonObject.getString("url")
                    Log.d("BerryGame[downloadUrl]", jumpUrl)
                    val intent = Intent()
                    intent.setAction("android.intent.action.VIEW")
                    val content_url = Uri.parse(jumpUrl)
                    intent.setData(content_url)
                    startActivity(intent)
                }
                "getAppVersion" -> {
                    val packageInfo = packageManager.getPackageInfo(packageName, 0)
                    val versionName = packageInfo.versionName
                    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        packageInfo.versionCode
                    }
                    val versionInfo: VersionInfo = VersionInfo(versionName, versionCode)
                    val gson = Gson()
                    val versionInfoStr = gson.toJson(versionInfo)
                    webView?.post { run {
                        // 注意回调参数已经是字符串了，不要再套一层字符串，不然js中JSON要parse两次
                        webView?.loadUrl("javascript:currentAppVersionCallBack($versionInfoStr)")
                    } }
                }
            }
        }, "JSBerryGame")

        var webSettings = webView!!.settings
        webSettings.userAgentString = webSettings.userAgentString + "BerryGame"
        webSettings.javaScriptEnabled = true  // 开启 JavaScript 交互
        webSettings.domStorageEnabled = true // 打开本地缓存提供JS调用,至关重要(不然网页的存储无效)
        //webSettings.setAppCacheEnabled = true // 启用或禁用缓存
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
        //webSettings.setAppCachePath = cacheDir.path // 设置应用缓存路径
        webSettings?.databaseEnabled = true// 启用数据库
        //webSettings.setSupportZoom(false) // 支持缩放 默认为true 是下面那个的前提
        webSettings.builtInZoomControls = true // 设置内置的缩放控件 若为false 则该WebView不可缩放
        webSettings.displayZoomControls = false // 隐藏原生的缩放控件
        webSettings.useWideViewPort = true
        webSettings.blockNetworkImage = false // 禁止或允许WebView从网络上加载图片
        webSettings.loadsImagesAutomatically = true // 支持自动加载图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.safeBrowsingEnabled = true // 是否开启安全模式
        }
        webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
        webSettings.domStorageEnabled = true // 启用或禁用DOM缓存
        webSettings.setSupportMultipleWindows(true) // 设置WebView是否支持多窗口
        // 设置自适应屏幕, 两者合用
//        webSettings.useWideViewPort = true  // 将图片调整到适合webview的大小
//        webSettings.loadWithOverviewMode = true  // 缩放至屏幕的大小
        webSettings.allowFileAccess = true // 设置可以访问文件
        webSettings.setGeolocationEnabled(true) // 是否使用地理位置
        if (!TextUtils.isEmpty(webUrl)) {
            webView?.setInitialScale(100)//最小缩放等级
        }
        webView?.fitsSystemWindows = true
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
        webView?.loadUrl(WEB_URL)
    }

    //设置返回键的监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView!!.canGoBack()) {
                webView!!.goBack()//返回上一个页面
                return true
            } else {
                if (canGoDesktop) {
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        var exitMsg = "Press again to exit the program"
                        try {
                            val conf: Configuration = resources.configuration
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                conf.setLocale(Locale(mLanguage))
                            } else {
                                conf.locale = Locale(mLanguage)
                            }
                            val metrics = DisplayMetrics()
                            windowManager.defaultDisplay.getMetrics(metrics)
                            val resources = Resources(assets, metrics, conf)
                            exitMsg = resources.getString(R.string.exit_app)
                        } catch (e: Exception) {
                            Log.d("BerryGame[exit]", e.toString())
                            if ("zh".equals(mLanguage)) {
                                exitMsg = "再按一次退出程序"
                            } else if ("en".equals(mLanguage)) {
                                exitMsg = "Press again to exit the program"
                            } else if ("pt".equals(mLanguage)) {
                                exitMsg = "Pressione novamente para sair do programa"
                            }
                        }
                        Toast.makeText(this, exitMsg, Toast.LENGTH_SHORT).show()
                        exitTime = System.currentTimeMillis()
                    } else {
                        val startMain = Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        System.exit(0);
                    }
                } else {
                    finish()
                }
            }
        }
        return false
    }
}