package net.geekstools.trexrunner

import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.Util.Ads.AdsInterface
import net.geekstools.trexrunner.Util.Ads.FunctionsClassAds
import net.geekstools.trexrunner.Util.Functions.PublicVariable

fun EntryConfigurations.setupAds() {

    val functionsClassAds: FunctionsClassAds = FunctionsClassAds(applicationContext)
    functionsClassAds.initialLoadingAds(object : AdsInterface {
        override fun enableRewardedVideo() {

            val rewardedPromotionCode = functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
            if ((rewardedPromotionCode >= 33) && functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

                rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                        "<big>Please Click to See Video Ads to<br/>" +
                        "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                        + "</font>")
            } else {
                rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                        "<big>Click to See Video Ads to Get<br/>" +
                        "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                        + "</font>")
                rewardVideo.append("$rewardedPromotionCode / 33" + Html.fromHtml("<br/>"))
            }

            rewardVideo.visibility = View.VISIBLE
        }

        override fun reloadRewardedVideo() {

            rewardVideo.visibility = View.INVISIBLE
        }

        override fun rewardedPromotionCode() {

            rewardVideo.setTextColor(getColor(R.color.light))

            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big>"
                    + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                    + "</font>")

            rewardVideo.visibility = View.VISIBLE
        }
    })

    rewardVideo.setOnClickListener {
        if ((functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0) >= 33)
                && !functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

            try {
                val emailTextMessage = ("\n\n\n\n\n"
                        + "[Essential Information]" + "\n"
                        + getString(R.string.app_name) + " | " + functionsClassCheckpoint.appVersionName(getPackageName()) + "\n"
                        + functionsClassCheckpoint.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClassCheckpoint.getCountryIso().toUpperCase())

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rewardedPromotionCodeTitle))
                    putExtra(Intent.EXTRA_TEXT, emailTextMessage)
                    type = "text/*"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage("com.google.android.gm")
                }
                startActivity(Intent.createChooser(emailIntent, getString(R.string.rewardedPromotionCodeTitle)))

                functionsClassCheckpoint.savePreference(".NoAdsRewardedInfo", "Requested", true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            if (functionsClassAds.rewardedVideoAdInstance.isLoaded) {
                functionsClassAds.rewardedVideoAdInstance.show()
            }
        }
    }
}

fun EntryConfigurations.adsCheckpoint() {
    PublicVariable.eligibleLoadShowAds = true

    val rewardedPromotionCode = functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
    if (rewardedPromotionCode >= 33 && functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

        rewardVideo.visibility = View.INVISIBLE

    } else if (rewardedPromotionCode >= 33 && !functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

        rewardVideo.setTextColor(getColor(R.color.light))

        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big>"
                + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                + "</font>")

        rewardVideo.visibility = View.VISIBLE
    }
}