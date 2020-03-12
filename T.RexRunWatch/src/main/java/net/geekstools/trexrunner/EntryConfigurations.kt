package net.geekstools.trexrunner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.ConfirmationOverlay
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.GamePlay.UnityPlayerActivity
import net.geekstools.trexrunner.RemoteConfiguration.InitializeFirebaseServerCall
import net.geekstools.trexrunner.Util.Functions.FunctionsClassCheckpoint
import net.geekstools.trexrunner.Util.Functions.FunctionsClassUI

class EntryConfigurations : WearableActivity() {

    lateinit var functionsClassCheckpoint: FunctionsClassCheckpoint
    lateinit var functionsClassUI: FunctionsClassUI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAmbientEnabled()

        setContentView(R.layout.configurations)
        FirebaseApp.initializeApp(applicationContext)

        functionsClassCheckpoint = FunctionsClassCheckpoint(applicationContext)
        functionsClassUI = FunctionsClassUI(applicationContext)

        playGame.setOnClickListener {

            startActivity(Intent(applicationContext, UnityPlayerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun onStart() {
        super.onStart()

        val initializeFirebaseServerCall: InitializeFirebaseServerCall = InitializeFirebaseServerCall(this@EntryConfigurations)
        val firebaseRemoteConfig: FirebaseRemoteConfig = initializeFirebaseServerCall.retrieveFirebaseRemoteConfiguration()

        rewardView.setOnClickListener {

            val intentPlayStore = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(Uri.parse(getString(R.string.play_store_link) + packageName))

            RemoteIntent.startRemoteActivity(
                    applicationContext,
                    intentPlayStore,
                    object : ResultReceiver(Handler()) {
                        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                            if (resultCode == RemoteIntent.RESULT_OK) {

                                ConfirmationOverlay()
                                        .setType(ConfirmationOverlay.OPEN_ON_PHONE_ANIMATION)
                                        .setMessage(getString(R.string.playOnPhone))
                                        .setDuration((1000 * 1.9).toInt())
                                        .showOn(this@EntryConfigurations)

                            } else if (resultCode == RemoteIntent.RESULT_FAILED) {

                            } else {

                            }
                        }
                    })
        }
    }
}