package come.user.lezco

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import come.user.lezco.utils.Common
import come.user.lezco.utils.Common.Companion.isNetworkAvailable
import come.user.lezco.utils.Common.Companion.showInternetInfo
import come.user.lezco.utils.RegistrationIntentService
import come.user.lezco.utils.Url
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class SplashActivity : AppCompatActivity() {

    lateinit var img_splash_screen: ImageView
    lateinit var img_location: ImageView

    lateinit var userPref: SharedPreferences

    internal var common = Common()

    lateinit var translation: TranslateAnimation

    val displayHeight: Int
        get() {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        img_splash_screen = findViewById(R.id.img_splash_screen) as ImageView
        img_location = findViewById(R.id.img_location) as ImageView

        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)


        userPref = PreferenceManager.getDefaultSharedPreferences(this@SplashActivity)

        Picasso.with(this@SplashActivity)
            .load(R.drawable.logo1)
            .into(img_splash_screen)

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        //boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        Log.d("gps_enabled", "gps_enabled = $gps_enabled")
        if (!gps_enabled) {
            // notify user
            val dialog = AlertDialog.Builder(this@SplashActivity)
            dialog.setTitle("Improve location accurancy?")
            dialog.setMessage("This app wants to change your device setting:")
            dialog.setPositiveButton(getString(R.string.ok)) { paramDialogInterface, paramInt ->
                // TODO Auto-generated method stub
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(myIntent, 1)
                //get gps
            }
            dialog.setNegativeButton(getString(R.string.cancel)) { paramDialogInterface, paramInt -> finish() }
            dialog.show()
        } else {


            translation = TranslateAnimation(0f, 0f, 0f, displayHeight * 0.50f)
            translation.startOffset = 500
            translation.duration = 2000
            translation.fillAfter = true

            translation.interpolator = BounceInterpolator()
            img_location.startAnimation(translation)

            if (userPref.getString("isLogin", "") == "1") {
                Handler().postDelayed({
                    if (isNetworkAvailable(this@SplashActivity)) {

                        if (userPref.getString(
                                "facebook_id",
                                ""
                            ) != "" || userPref.getString("twitter_id", "") != ""
                        ) {
                            Log.d(
                                "facebook id",
                                "facebook id = " + userPref.getString("facebook_id", "")!!
                            )

                            var SocialUrl = ""
                            if (userPref.getString("facebook_id", "") != "") {
                                SocialUrl = Url.facebookLoginUrl
                            } else if (userPref.getString("twitter_id", "") != "") {
                                SocialUrl = Url.twitterLoginUrl
                            }

                            Common.LoginSocialUserHttp(
                                SocialUrl,
                                userPref.getString("facebook_id", "")!!,
                                userPref.getString("twitter_id", "")!!,
                                this@SplashActivity
                            ).execute()
                        } else {
                            var loginUrl: String? = null
                            try {
                                loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(
                                    userPref.getString(
                                        "email",
                                        ""
                                    ), "UTF-8"
                                ) + "&password=" + userPref.getString("password", "")
                            } catch (e: UnsupportedEncodingException) {
                                e.printStackTrace()
                            }

                            Log.d("loginUrl", "loginUrl " + loginUrl!!)

                            Common.LoginCallHttp(
                                this@SplashActivity,
                                null,
                                null,
                                userPref.getString("password", "")!!,
                                "SplashScreen",
                                loginUrl
                            ).execute()
                        }
                    } else {
                        showInternetInfo(this@SplashActivity, "")
                    }
                }, 100)

            } else {
                Handler().postDelayed({
                    startActivity(Intent(this@SplashActivity, LoginOptionActivity::class.java))
                    finish()
                }, 2500)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            if (isNetworkAvailable(this@SplashActivity)) {

                var loginUrl: String? = null
                try {
                    loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(
                        userPref.getString(
                            "email",
                            ""
                        ), "UTF-8"
                    ) + "&password=" + userPref.getString("password", "")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                Log.d("loginUrl", "loginUrl " + loginUrl!!)
                Common.LoginCallHttp(
                    this@SplashActivity,
                    null,
                    null,
                    userPref.getString("password", "")!!,
                    "SplashScreen",
                    loginUrl
                ).execute()
            } else {
                showInternetInfo(this@SplashActivity, "")
            }
        }
    }
}
