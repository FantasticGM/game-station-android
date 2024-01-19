package com.skyx.berrygame.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import java.util.Locale

object LanguageHelper {

    private var mLocal: Locale = Locale("en")
    private var mChangeLanguage: String = ""
    /**
     * changeLanguage 默认是跟系统语言，如果手动切换浏览器语言，那更新语言
     * */
    fun getAttachBaseContext(context: Context, changeLanguage: String = ""): Context {
        if (!TextUtils.isEmpty(changeLanguage)) {
            mChangeLanguage = changeLanguage
            mLocal = Locale(changeLanguage)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return setAppLanguageApi24(context)
        } else {
            setAppLanguage(context)
        }
        return context
    }

    /**
     * 获取当前系统语言，如未包含则默认英文
     * Locale类包含语言、国家等属性
     */
    private fun getSystemLocale(): Locale {

        val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        return when (systemLocale.language) {
            Locale.CHINA.language -> {
                Locale.CHINA
            }
            Locale.ENGLISH.language -> {
                Locale.ENGLISH
            }
            else -> {
                Locale.ENGLISH
            }
        }
    }

    /**
     * 兼容 7.0 及以上
     */
    @TargetApi(Build.VERSION_CODES.N)
    private fun setAppLanguageApi24(context: Context): Context {
        val locale = getSystemLocale()
        val resource = context.resources
        val configuration = resource.configuration
        if (!TextUtils.isEmpty(mChangeLanguage)) {
            configuration.setLocale(mLocal)
            configuration.setLocales(LocaleList(mLocal))
        } else {
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
        }
        return context.createConfigurationContext(configuration)
    }

    /**
     * 设置应用语言
     */
    private fun setAppLanguage(context: Context) {
        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        // 获取当前系统语言，默认设置跟随系统
        val locale = getSystemLocale()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!TextUtils.isEmpty(mChangeLanguage)) {
                configuration.setLocale(mLocal)
            } else {
                configuration.setLocale(locale)
            }
        } else {
            if (!TextUtils.isEmpty(mChangeLanguage)) {
                configuration.locale = mLocal
            } else {
                configuration.locale = locale
            }
        }
        resources.updateConfiguration(configuration, displayMetrics)
    }
}