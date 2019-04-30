package net.geekstools.trexrunner

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.ConfirmationOverlay
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.geekstools.trexrunner.Util.Functions.FunctionsClass
import org.xwalk.core.XWalkInitializer
import org.xwalk.core.XWalkView


class DinoRunnerActivity : WearableActivity(), XWalkInitializer.XWalkInitListener {

    lateinit var functionsClass: FunctionsClass

    lateinit private var tRexRun: XWalkView
    lateinit private var xWalkInitializer: XWalkInitializer

    lateinit private var splashScreen: RelativeLayout

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onXWalkInitStarted() {

    }

    override fun onXWalkInitCancelled() {

    }

    override fun onXWalkInitFailed() {

    }

    override fun onXWalkInitCompleted() {
        val xWalkSettings = tRexRun!!.settings
        xWalkSettings.javaScriptEnabled = true
        xWalkSettings.domStorageEnabled = true
        xWalkSettings.databaseEnabled = true
        tRexRun.loadUrl("file:///android_asset/trexrun/index.html")

        val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
        Handler().postDelayed({
            splashScreen!!.startAnimation(animation)
        }, 1777)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                splashScreen.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xWalkInitializer = XWalkInitializer(this@DinoRunnerActivity, applicationContext)
        xWalkInitializer.initAsync()
        if (resources.configuration.isScreenRound) {
            setContentView(R.layout.dino_run_view_circle)
        } else {
            setContentView(R.layout.dino_run_view_square)
        }
        functionsClass = FunctionsClass(this@DinoRunnerActivity, applicationContext)

        tRexRun = findViewById<View>(R.id.tRexRun) as XWalkView
        splashScreen = findViewById<View>(R.id.splashScreen) as RelativeLayout

        setAmbientEnabled()
    }

    override fun onStart() {
        super.onStart()

        val resultReceiver = object : ResultReceiver(Handler()) {
            override protected fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == RemoteIntent.RESULT_OK) {
                    println("RemoteIntent.RESULT_OK")
                    if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {
                        ConfirmationOverlay()
                                .setMessage(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLinkDialogue)))
                                .setDuration(1000 * 1)
                                .showOn(this@DinoRunnerActivity)

                        functionsClass.savePreference(".UserState", "FirstTime", false)
                    }
                } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                    println("RemoteIntent.RESULT_FAILED")
                } else {
                    println("Unexpected Result $resultCode")
                }
            }
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@DinoRunnerActivity, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClass.appVersionCode(packageName)) {
                                Toast.makeText(applicationContext, getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()
                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            }
                            if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanPlayStoreLink))) {
                                val intentPlayStore = Intent(Intent.ACTION_VIEW)
                                        .addCategory(Intent.CATEGORY_BROWSABLE)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setData(Uri.parse(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLink))))
                                RemoteIntent.startRemoteActivity(
                                        applicationContext,
                                        intentPlayStore,
                                        resultReceiver)
                            }
                        }
                    } else {

                    }
                })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            println("*** " + keyCode)

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
