package net.geekstools.trexrunner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.ConfirmationOverlay
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.Util.Functions.FunctionsClass

class Configurations : WearableActivity() {

    lateinit var functionsClass: FunctionsClass

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurations)
        FirebaseApp.initializeApp(applicationContext);

        functionsClass = FunctionsClass(this@Configurations, applicationContext)

        playGame.setOnClickListener {
            startActivity(Intent(applicationContext, UnityPlayerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

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
                                .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                                .setMessage(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLinkDialogue)))
                                .setDuration(1500 * 1)
                                .showOn(this@Configurations)

                        functionsClass.savePreference(".UserState", "FirstTime", false)
                    }
                } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                    println("RemoteIntent.RESULT_FAILED")
                } else {
                    println("Unexpected Result $resultCode")
                }
            }
        }

        reward.setOnClickListener {
            val intentPlayStore = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(Uri.parse(getString(R.string.play_store_link) + packageName))
            RemoteIntent.startRemoteActivity(
                    applicationContext,
                    intentPlayStore,
                    object : ResultReceiver(Handler()) {
                        override protected fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                            if (resultCode == RemoteIntent.RESULT_OK) {
                                println("RemoteIntent.RESULT_OK")
                                if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {
                                    ConfirmationOverlay()
                                            .setType(ConfirmationOverlay.OPEN_ON_PHONE_ANIMATION)
                                            .setMessage(getString(R.string.playOnPhone))
                                            .setDuration((1000 * 1.9).toInt())
                                            .showOn(this@Configurations)
                                }
                            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                                println("RemoteIntent.RESULT_FAILED")
                            } else {
                                println("Unexpected Result $resultCode")
                            }
                        }
                    })
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@Configurations, OnCompleteListener<Void> { task ->
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
}