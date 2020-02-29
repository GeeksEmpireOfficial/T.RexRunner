package net.geekstools.trexrunner.Util.Ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import net.geekstools.trexrunner.BuildConfig
import net.geekstools.trexrunner.R
import net.geekstools.trexrunner.Util.Functions.FunctionsClassCheckpoint

class FunctionsClassAds(var context: Context) {

    val functionsClassPreferences = FunctionsClassCheckpoint(context)

    val rewardedVideoAdInstance: RewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
    val adsRequest: AdRequest = AdRequest.Builder()
            .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
            .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
            .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
            .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
            .addTestDevice("F54D998BCE077711A17272B899B44798")
            .build()

    fun initialLoadingAds(adsInterface: AdsInterface) {
        MobileAds.initialize(context, context.getString(R.string.AdAppId))

        rewardedVideoAdInstance.setImmersiveMode(true)
        rewardedVideoAdInstance.loadAd(context.getString(R.string.adUnitReward), adsRequest)
        rewardedVideoAdInstance.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdLoaded() {
                adsInterface.enableRewardedVideo()
            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewardedVideoStarted() {

            }

            override fun onRewardedVideoAdClosed() {
                val rewardedPromotionCode = functionsClassPreferences.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                if (rewardedPromotionCode >= 33 && !functionsClassPreferences.readPreference(".NoAdsRewardedInfo", "Requested", false)) {
                    adsInterface.rewardedPromotionCode()
                } else {
                    adsInterface.reloadRewardedVideo()

                    rewardedVideoAdInstance.loadAd(context.getString(R.string.adUnitReward), adsRequest)
                }
            }

            override fun onRewarded(rewardItem: RewardItem) {
                val rewardedPromotionCode = functionsClassPreferences.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                functionsClassPreferences.savePreference(".NoAdsRewardedInfo", "RewardedPromotionCode", rewardedPromotionCode + rewardItem.amount)

                functionsClassPreferences.savePreference(".NoAdsRewardedInfo", "RewardedTime", System.currentTimeMillis())
                functionsClassPreferences.savePreference(".NoAdsRewardedInfo", "RewardedAmount", rewardItem.amount)
                functionsClassPreferences.saveFile(".NoAdsRewarded", context.packageName)
            }

            override fun onRewardedVideoAdLeftApplication() {}

            override fun onRewardedVideoAdFailedToLoad(failedCode: Int) {
                if (BuildConfig.DEBUG) {
                    println("Ad Failed $failedCode | RewardedAds")
                }
                rewardedVideoAdInstance.loadAd(context.getString(R.string.adUnitReward), adsRequest)
            }

            override fun onRewardedVideoCompleted() {

            }
        }
    }
}