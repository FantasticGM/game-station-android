# Generate Signed Bundle or APK
keystore.jks

Password:qwz_berry_game

Alias:key

First and Last Name:QianWanZheng

Organization Unit:QianWanZheng

Organization:QianWanZheng

City or Locality:Xiamen

State or Province:FuJian

Country Code(XX):ZH


# 360加固
https://jiagu.360.cn/#/global/index


# H5跟Android交互说明
桥名:JSBerryGame

H5统一通过window.JSBerryGame.postMessage(params参数)调用android原生方法

H5调用例子:
val params = {
    action: 'jumpWebView',
    type: 'self',
    url: 'https://xxxx.com'，
    extend: '{xxx: xxx}'// 后期如果涉及复杂业务统一将额外参数放到extend里，将对象转为String作为extend的值
}
window.JSBerryGame.postMessage(params)

1、jumpWebView(新开WebActivity页面)
参数说明:
    type: 'self'//新开WebActivity页面
    type: 'outer'//打开手机浏览器

2、changeLanguage(切换网页语言)
参数说明:
    type: 'en'//en英文 zh中文 pt葡萄牙语

3、appDownload(下载apk)
参数说明:
    type: 'android'//android IOS

4、getAppVersion(获取当前使用的app版本信息:versionCode+versionName)
参数说明:
    原生回调：
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
