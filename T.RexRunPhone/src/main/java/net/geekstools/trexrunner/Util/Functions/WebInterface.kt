package net.geekstools.trexrunner.Util.Functions

import android.app.Activity
import android.content.Context
import android.content.Intent
import org.xwalk.core.JavascriptInterface

class WebInterface {

    lateinit var activity: Activity
    lateinit var context: Context

    constructor(activity: Activity, context: Context) {
        this.activity = activity
        this.context = context
    }

    @JavascriptInterface
    fun GameOverShowInterstitialAds() {
        context.sendBroadcast(Intent("SHOW_INTERSTITIAL_ADS"))
    }

    @JavascriptInterface
    fun ForceReloadInterstitialAds() {
        context.sendBroadcast(Intent("LOAD_INTERSTITIAL_ADS"))
    }
}