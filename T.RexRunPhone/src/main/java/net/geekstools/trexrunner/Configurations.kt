package net.geekstools.trexrunner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.Util.Functions.FunctionsClass

class Configurations : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurations)

        val functionsClass = FunctionsClass(this@Configurations, applicationContext)

        playGame.setOnClickListener {
            startActivity(Intent(applicationContext, UnityPlayerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}