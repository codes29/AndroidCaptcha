package com.codes29.androidcaptcha

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    var SITE_KEY= "6Le-v2YUAAAAAEuwPmHtqZTcceOra8tvk5NEcGcw"
    var SECRET_KEY= "6Le-v2YUAAAAADSZ5V3iI7PUAdLwGyChSWW7DCXq"
    val TAG = "Main Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this@MainActivity,CaptchaActivity::class.java))

    }
}
