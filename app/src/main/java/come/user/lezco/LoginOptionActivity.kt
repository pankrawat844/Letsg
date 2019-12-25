package come.user.lezco

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.gpsLocation.GPSTracker
import come.user.lezco.utils.Common
import come.user.lezco.utils.Common.Companion.isNetworkAvailable
import come.user.lezco.utils.Common.Companion.showInternetInfo
import come.user.lezco.utils.Common.Companion.showMKPanelError
import come.user.lezco.utils.Url
import cz.msebera.android.httpclient.HttpEntity
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.params.HttpConnectionParams
import cz.msebera.android.httpclient.util.EntityUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login_option.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.util.*

class LoginOptionActivity : AppCompatActivity() {

    lateinit var img_logo: ImageView
    lateinit var img_background: ImageView
    lateinit var img_facebook: ImageView
    lateinit var img_twitter: ImageView
    lateinit var txt_sign_in_with: TextView
    lateinit var txt_new_user_signup: TextView
    lateinit var txt_signin: TextView
    lateinit var layout_option_main: RelativeLayout
    lateinit var layout_new_user_signup: RelativeLayout
    lateinit var layout_signin: RelativeLayout

    lateinit var OpenSans_Regular: Typeface
    lateinit var regularRoboto: Typeface
    lateinit var Roboto_Bold: Typeface

    lateinit var callbackManager: CallbackManager

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    lateinit var twitterLoginBtn: TwitterLoginButton
    internal var socialFlg: Int = 0
    lateinit var userPref: SharedPreferences

    internal var PickupLongtude: Double = 0.toDouble()
    internal var PickupLatitude: Double = 0.toDouble()

    //Error Alert
    lateinit var rlMainView: RelativeLayout
    lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        FacebookSdk.sdkInitialize(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_option)

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        regularRoboto = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        val gpsTracker = GPSTracker(this@LoginOptionActivity)
        PickupLatitude = gpsTracker.latitude
        PickupLongtude = gpsTracker.longitude

        //Error Alert
        rlMainView = findViewById(R.id.rlMainView) as RelativeLayout
        tvTitle = findViewById(R.id.tvTitle) as TextView

        layout_option_main = findViewById(R.id.layout_option_main) as RelativeLayout
        img_logo = findViewById(R.id.img_logo) as ImageView

        img_facebook = findViewById(R.id.img_facebook) as ImageView
        img_twitter = findViewById(R.id.img_twitter) as ImageView
        txt_sign_in_with = findViewById(R.id.txt_sign_in_with) as TextView
        layout_new_user_signup = findViewById(R.id.layout_new_user_signup) as RelativeLayout
        layout_signin = findViewById(R.id.layout_signin) as RelativeLayout
        txt_new_user_signup = findViewById(R.id.txt_new_user_signup) as TextView
        txt_new_user_signup.typeface = Roboto_Bold
        txt_signin = findViewById(R.id.txt_signin) as TextView
        txt_signin.typeface = Roboto_Bold

        userPref = PreferenceManager.getDefaultSharedPreferences(this@LoginOptionActivity)

        ProgressDialog = Dialog(this@LoginOptionActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        callbackManager = CallbackManager.Factory.create()



        txt_sign_in_with.typeface = OpenSans_Regular

        img_background = findViewById(R.id.img_background) as ImageView
        Picasso.with(this@LoginOptionActivity)
                .load(R.drawable.background)
                .into(img_background)

        Picasso.with(this@LoginOptionActivity)
                .load(R.drawable.logo)
                .into(img_logo)

        Picasso.with(this@LoginOptionActivity)
                .load(R.drawable.facebook_btn)
                .into(img_facebook)


        Picasso.with(this@LoginOptionActivity)
                .load(R.drawable.twitter_btn)
                .into(img_twitter)

        layout_new_user_signup.setOnClickListener {
            val si = Intent(this@LoginOptionActivity, SignUpActivity::class.java)
            startActivity(si)
        }

        layout_signin.setOnClickListener {
            val li = Intent(this@LoginOptionActivity, LoginActivity::class.java)
            startActivity(li)
        }

        layout_guest.setOnClickListener {
            if (isNetworkAvailable(this@LoginOptionActivity)) {

                var loginUrl: String? = null
                try {
                    loginUrl = Url.carType
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                Log.d("loginUrl", "loginUrl " + loginUrl!!)
                Common.CarTypeCallHttp(
                    this@LoginOptionActivity,
                    ProgressDialog,
                    cusRotateLoading,
                    "",

                    loginUrl
                ).execute()
            } else {
                showInternetInfo(this@LoginOptionActivity, "")
            }
        }
        img_facebook.setOnClickListener {
            val loggedIn = AccessToken.getCurrentAccessToken() != null

            if (loggedIn) {
                val accessToken = AccessToken.getCurrentAccessToken()
                // App code
                val request = GraphRequest.newMeRequest(
                        accessToken
                ) { `object`, response ->
                    Log.d("object", "object = $`object`==$response")

                    if (`object` != null) {
                        //facebook get data
                        try {
                            var FbEmail = ""
                            var FbName = ""
                            if (`object`.has("email"))
                                FbEmail = `object`.getString("email")
                            if (`object`.has("name"))
                                FbName = `object`.getString("name")

                            //CheckFacebookUser(Url.facebookLoginUrl,object.getString("id"),"",FbEmail,FbName);
                            CheckFacebookUserHttp(Url.facebookLoginUrl, `object`.getString("id"), "", FbEmail, FbName).execute()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        Toast.makeText(this@LoginOptionActivity, "Something went wrong", Toast.LENGTH_LONG)
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email")
                request.parameters = parameters
                request.executeAsync()


            } else {

                callbackManager = CallbackManager.Factory.create()

                LoginManager.getInstance().logInWithPublishPermissions(this@LoginOptionActivity, Arrays.asList("publish_actions"))

                LoginManager.getInstance().registerCallback(callbackManager,
                        object : FacebookCallback<LoginResult> {
                            override fun onSuccess(loginResult: LoginResult) {

                                Log.d("loginResult", "loginResult = $loginResult")
                                // App code
                                val request = GraphRequest.newMeRequest(
                                        loginResult.accessToken
                                ) { `object`, response ->
                                    Log.d("object", "object = $`object`==$response")

                                    if (`object` != null) {
                                        //facebook get data
                                        //object.getString("id")

                                        try {
                                            var FbEmail = ""
                                            var FbName = ""
                                            if (`object`.has("email"))
                                                FbEmail = `object`.getString("email")
                                            if (`object`.has("name"))
                                                FbName = `object`.getString("name")

                                            val FacebookSocialUrl = Url.facebookLoginUrl + "?facebook_id=" + `object`.getString("id")
                                            //CheckFacebookUser(FacebookSocialUrl,object.getString("id"),"",FbEmail,FbName);
                                            CheckFacebookUserHttp(Url.facebookLoginUrl, `object`.getString("id"), "", FbEmail, FbName).execute()
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }

                                    } else {
                                        Toast.makeText(this@LoginOptionActivity, "Something went wrong", Toast.LENGTH_LONG)
                                    }
                                }
                                val parameters = Bundle()
                                parameters.putString("fields", "id,name,email")
                                request.parameters = parameters
                                request.executeAsync()
                            }

                            override fun onCancel() {
                                Log.d("cancel", "cancel = ")
                            }

                            override fun onError(exception: FacebookException) {
                                Log.d("fb error", "fb error = " + exception.message)
                                println("exception >>" + exception.message)
                            }
                        })
            }
        }

        img_twitter.setOnClickListener {
            if (Common.isNetworkAvailable(this@LoginOptionActivity)) {
                try {

                    socialFlg = 2
                    twitterLoginBtn = TwitterLoginButton(this@LoginOptionActivity)

                    twitterLoginBtn.callback = object : Callback<TwitterSession>() {
                        override fun success(result: Result<TwitterSession>) {
                            Log.d("twitter", "twitter = $result")
                        }

                        override fun failure(exception: TwitterException) {
                            // Do something on failure
                            Log.d("twitter", "twitter erro = " + exception.message)
                        }
                    }

                    val authClient = TwitterAuthClient()
                    authClient.authorize(this@LoginOptionActivity, object : Callback<TwitterSession>() {
                        override fun success(twitterSessionResult: Result<TwitterSession>) {
                            val user = twitterSessionResult.data
                            val session = Twitter.getSessionManager().activeSession
                            val authToken = session.authToken

                            val token = authToken.token
                            val secret = authToken.secret

                            CheckFacebookUserHttp(Url.twitterLoginUrl, "", session.userId.toString(), "", session.userName).execute()
                            //                                TwitterApiClient twitterApiClient = Twitter.getApiClient();
                            //                                StatusesService twapiclient = twitterApiClient.getStatusesService();
                            //                                twapiclient.userTimeline(user.getUserId(), null, null, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                            //                                    @Override
                            //                                    public void success(Result<List<Tweet>> listResult) {
                            //
                            //                                        String twitterPrfImageUrl = listResult.data.get(0).user.profileImageUrl;
                            //                                        twitterPrfImageUrl = twitterPrfImageUrl.replace("_normal","_bigger");
                            //
                            //                                        String twitterUsername="";
                            //                                        if(!listResult.data.get(0).user.name.equals(""))
                            //                                            twitterUsername = listResult.data.get(0).user.name;
                            //
                            //                                        String twitterId = String.valueOf(listResult.data.get(0).user.id);
                            //
                            ////                                        RequestParams socialParams = new RequestParams();
                            ////                                        socialParams.put("twitter_id", twitterId);
                            ////
                            ////                                        //String twitterUrl = Url.twitterUrl + "?sign=" + ss.sign + "&salt=" + ss.salt + "&twitter_id=" + listResult.data.get(0).user.id + "&username=" +listResult.data.get(0).user.name+"&device_token="+common.device_token;
                            ////                                        Log.d("Twitter Url","Twitter Url = "+Url.twitterLoginUrl+"?"+socialParams);
                            ////                                        CheckFacebookUser(Url.twitterLoginUrl, socialParams, "", twitterId, "", twitterUsername);
                            //                                        new CheckFacebookUserHttp(Url.twitterLoginUrl,"",twitterId,"",twitterUsername).execute();
                            //                                    }
                            //
                            //                                    @Override
                            //                                    public void failure(TwitterException e) {
                            //
                            //                                    }
                            //                                });


                        }

                        override fun failure(e: TwitterException) {
                            println("Twitter Auth is failure")
                        }
                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Common.showInternetInfo(this@LoginOptionActivity, "")
            }
        }

        Log.d("user_InActive", "user_InActive = " + Common.user_InActive)
        if (Common.user_InActive == 1) {
            showMKPanelError(this@LoginOptionActivity, resources.getString(R.string.inactive_user), rlMainView, tvTitle, regularRoboto)
            Common.user_InActive = 0
        }

        layout_option_main.setOnClickListener {
            if (rlMainView.visibility == View.VISIBLE) {
                if (!isFinishing) {
                    val slideUp = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -100f)
                    slideUp.duration = 10
                    slideUp.fillAfter = true
                    rlMainView.startAnimation(slideUp)
                    slideUp.setAnimationListener(object : Animation.AnimationListener {

                        override fun onAnimationStart(animation: Animation) {}

                        override fun onAnimationRepeat(animation: Animation) {}

                        override fun onAnimationEnd(animation: Animation) {
                            rlMainView.visibility = View.GONE
                        }
                    })

                }
            }
        }
    }

    internal inner class CheckFacebookUserHttp(var SocialUrl: String, var facebook_id: String, var twitter_id: String, var FbEmail: String, var FbName: String) : AsyncTask<String, Int, String>() {

        var entity: HttpEntity
        private var content: String? = null

        init {

            val entityBuilder = MultipartEntityBuilder.create()
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)

            if (facebook_id != "")
                entityBuilder.addTextBody("facebook_id", facebook_id)
            else if (twitter_id != "")
                entityBuilder.addTextBody("twitter_id", twitter_id)
            entity = entityBuilder.build()
        }

        override fun onPreExecute() {
            ProgressDialog.show()
            cusRotateLoading.start()
        }

        override fun doInBackground(vararg params: String): String? {
            try {
                val client = DefaultHttpClient()
                val HttpParams = client.params
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000)
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000)
                Log.d("SocialUrl", "SocialUrl = $SocialUrl==$facebook_id==$twitter_id")
                val post = HttpPost(SocialUrl)
                post.entity = entity
                client.execute(post) { httpResponse ->
                    val httpEntity = httpResponse.entity
                    content = EntityUtils.toString(httpEntity)
                    Log.d("Result >>>", "Result One" + content!!)

                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Indiaries", "Result error$e")
                return e.message
            }

            return content
        }

        override fun onPostExecute(result: String) {
            cusRotateLoading.stop()
            ProgressDialog.cancel()

            val isStatus = Common.ShowHttpErrorMessage(this@LoginOptionActivity, result)
            if (isStatus) {
                try {
                    val resObj = JSONObject(result)
                    Log.d("Social Register resObj", "Social Register resObj = $resObj")
                    if (resObj.getString("status") == "failed") {

                        AlertDialog.Builder(this@LoginOptionActivity)
                                .setMessage(resources.getString(R.string.facebook_popup_string))
                                .setPositiveButton(resources.getString(R.string.register)) { dialog, which ->
                                    val ri = Intent(this@LoginOptionActivity, SignUpActivity::class.java)
                                    ri.putExtra("facebook_id", facebook_id)
                                    ri.putExtra("twitter_id", twitter_id)
                                    ri.putExtra("facebook_email", FbEmail)
                                    ri.putExtra("facebook_name", FbName)
                                    startActivity(ri)
                                }
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                                    // do nothing
                                }
                                .show()

                    } else if (resObj.getString("status") == "success") {

                        val CabDetAry = JSONArray(resObj.getString("cabDetails"))
                        Common.CabDetail = CabDetAry

                        /*set Start Currency*/

                        val currencyArray = JSONArray(resObj.getString("country_detail"))
                        for (ci in 0 until currencyArray.length()) {
                            val startEndTimeObj = currencyArray.getJSONObject(ci)
                            Common.Currency = startEndTimeObj.getString("currency")
                            Common.Country = startEndTimeObj.getString("country")
                        }

                        /*set Start And End Time*/
                        val startEndTimeArray = JSONArray(resObj.getString("time_detail"))
                        for (si in 0 until startEndTimeArray.length()) {
                            val startEndTimeObj = startEndTimeArray.getJSONObject(si)
                            Common.StartDayTime = startEndTimeObj.getString("day_start_time")
                            Common.EndDayTime = startEndTimeObj.getString("day_end_time")
                        }

                        /*User Detail*/
                        val userDetilObj = JSONObject(resObj.getString("userdetail"))

                        val id = userPref.edit()
                        id.putString("id", userDetilObj.getString("id").toString())
                        id.commit()

                        val name = userPref.edit()
                        name.putString("name", userDetilObj.getString("name").toString())
                        name.commit()

                        val passwordPre = userPref.edit()
                        passwordPre.putString("password", "")
                        passwordPre.commit()

                        val username = userPref.edit()
                        username.putString("username", userDetilObj.getString("username").toString())
                        username.commit()

                        val mobile = userPref.edit()
                        mobile.putString("mobile", userDetilObj.getString("mobile").toString())
                        mobile.commit()

                        val email = userPref.edit()
                        email.putString("email", userDetilObj.getString("email").toString())
                        email.commit()

                        val isLogin = userPref.edit()
                        isLogin.putString("isLogin", "1")
                        isLogin.commit()

                        val userImage = userPref.edit()
                        userImage.putString("userImage", userDetilObj.getString("image").toString())
                        userImage.commit()

                        val dob = userPref.edit()
                        dob.putString("date_of_birth", userDetilObj.getString("dob").toString())
                        dob.commit()


                        val facebook_id = userPref.edit()
                        facebook_id.putString("facebook_id", userDetilObj.getString("facebook_id").toString())
                        facebook_id.commit()

                        val twitter_id = userPref.edit()
                        twitter_id.putString("twitter_id", userDetilObj.getString("twitter_id").toString())
                        twitter_id.commit()

                        val gender = userPref.edit()
                        gender.putString("gender", userDetilObj.getString("gender").toString())
                        gender.commit()

                        //Common.showMkSucess(LoginOptionActivity.this, resObj.getString("message").toString(), "no");

                        Handler().postDelayed({
                            val hi = Intent(this@LoginOptionActivity, HomeActivity::class.java)
                            hi.putExtra("PickupLatitude", PickupLatitude)
                            hi.putExtra("PickupLongtude", PickupLongtude)
                            startActivity(hi)
                            finish()
                        }, 500)


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun CheckFacebookUser(SocialUrl: String, facebook_id: String, twitter_id: String, FbEmail: String, FbName: String) {

        ProgressDialog.show()
        cusRotateLoading.start()
        Log.d("Social Url ", "Social Url = $SocialUrl?facebook_id=$facebook_id")
        Ion.with(this@LoginOptionActivity)
                .load(SocialUrl)
                .setTimeout(10000)
                .asJsonObject()
                .setCallback { error, result ->
                    // do stuff with the result or error

                    if (error == null) {

                        ProgressDialog.cancel()
                        cusRotateLoading.stop()
                        Log.d("Social Register resObj", "Social Register resObj = $result")
                        try {
                            val resObj = JSONObject(result.toString())
                            Log.d("Social Register resObj", "Social Register resObj = $resObj")
                            if (resObj.getString("status") == "failed") {

                                AlertDialog.Builder(this@LoginOptionActivity)
                                        .setMessage(resources.getString(R.string.facebook_popup_string))
                                        .setPositiveButton(resources.getString(R.string.register)) { dialog, which ->
                                            val ri = Intent(this@LoginOptionActivity, SignUpActivity::class.java)
                                            ri.putExtra("facebook_id", facebook_id)
                                            ri.putExtra("twitter_id", twitter_id)
                                            ri.putExtra("facebook_email", FbEmail)
                                            ri.putExtra("facebook_name", FbName)
                                            startActivity(ri)
                                        }
                                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                                            // do nothing
                                        }
                                        .show()

                            } else if (resObj.getString("status") == "success") {

                                Common.showMkSucess(this@LoginOptionActivity, resObj.getString("message").toString(), "yes")

                                Handler().postDelayed({
                                    val hi = Intent(this@LoginOptionActivity, HomeActivity::class.java)
                                    startActivity(hi)
                                    finish()
                                }, 2000)

                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        ProgressDialog.cancel()
                        cusRotateLoading.stop()

                        Common.ShowHttpErrorMessage(this@LoginOptionActivity, error.message)
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (socialFlg == 2) {
            Log.d("requestCode", "requestCode = $requestCode")
            twitterLoginBtn.onActivityResult(requestCode, resultCode, data)
            socialFlg = 0
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

}
