package come.user.lezco.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.*
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.squareup.picasso.Picasso
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.*
import come.user.lezco.gpsLocation.GPSTracker
import cz.msebera.android.httpclient.HttpEntity
import cz.msebera.android.httpclient.client.ClientProtocolException
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.StringEntity
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.params.BasicHttpParams
import cz.msebera.android.httpclient.params.HttpConnectionParams
import cz.msebera.android.httpclient.protocol.HTTP
import cz.msebera.android.httpclient.util.EntityUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*


/**
 * Created by techintegrity on 04/07/16.
 */
class Common {

    lateinit var layout_book_my_trip: RelativeLayout
    lateinit var layout_my_trip: RelativeLayout
    lateinit var layout_rate_card: RelativeLayout
    lateinit var layout_cahnge_password: RelativeLayout
    lateinit var layout_footer_logout: RelativeLayout

    internal var PickupLongtude: Double = 0.toDouble()
    internal var PickupLatitude: Double = 0.toDouble()

    class LoginSocialUserHttp(
        internal var SocialUrl: String,
        internal var facebook_id: String,
        internal var twitter_id: String,
        internal var activity: Activity
    ) : AsyncTask<String, Int, String>() {

        internal var entity: HttpEntity
        private var content: String? = null
        internal var userPref: SharedPreferences
        internal var PickupLongtude: Double = 0.toDouble()
        internal var PickupLatitude: Double = 0.toDouble()

        init {

            val entityBuilder = MultipartEntityBuilder.create()
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)

            if (facebook_id != "")
                entityBuilder.addTextBody("facebook_id", facebook_id)
            else if (twitter_id != "")
                entityBuilder.addTextBody("twitter_id", twitter_id)
            entity = entityBuilder.build()

            userPref = PreferenceManager.getDefaultSharedPreferences(activity)

            val gpsTracker = GPSTracker(activity)
            PickupLatitude = gpsTracker.latitude
            PickupLongtude = gpsTracker.longitude
        }

        override fun onPreExecute() {

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

            val isStatus = Common.ShowHttpErrorMessage(activity, result)
            if (isStatus) {
                try {
                    val resObj = JSONObject(result)
                    Log.d("Social Register resObj", "Social Register resObj = $resObj")
                    if (resObj.getString("status") == "success") {

                        val cabDtlAry = JSONArray(resObj.getString("cabDetails"))
                        Common.CabDetail = cabDtlAry

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
                        username.putString(
                            "username",
                            userDetilObj.getString("username").toString()
                        )
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
                        facebook_id.putString(
                            "facebook_id",
                            userDetilObj.getString("facebook_id").toString()
                        )
                        facebook_id.commit()

                        val twitter_id = userPref.edit()
                        twitter_id.putString(
                            "twitter_id",
                            userDetilObj.getString("twitter_id").toString()
                        )
                        twitter_id.commit()

                        val gender = userPref.edit()
                        gender.putString("gender", userDetilObj.getString("gender").toString())
                        gender.commit()

                        //Common.showMkSucess(activity, resObj.getString("message").toString(), "no");

                        Handler().postDelayed({
                            val hi = Intent(activity, HomeActivity::class.java)
                            hi.putExtra("PickupLatitude", PickupLatitude)
                            hi.putExtra("PickupLongtude", PickupLongtude)
                            activity.startActivity(hi)
                            activity.finish()
                        }, 2000)


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    class LoginCallHttp(
        internal var activity: Activity,
        internal var ProgressDialog: Dialog?,
        internal var cusRotateLoading: RotateLoading?,
        internal var password: String,
        internal var activityName: String,
        internal var LoginUrl: String
    ) : AsyncTask<String, Int, String>() {
        private var content: String? = null
        internal var userPref: SharedPreferences
        internal var PickupLongtude: Double = 0.toDouble()
        internal var PickupLatitude: Double = 0.toDouble()

        init {

            userPref = PreferenceManager.getDefaultSharedPreferences(activity)

            val gpsTracker = GPSTracker(activity)
            PickupLatitude = gpsTracker.latitude
            PickupLongtude = gpsTracker.longitude

        }

        override fun onPreExecute() {
            Log.d("Start", "start")
            if (ProgressDialog != null) {
                ProgressDialog!!.show()
                cusRotateLoading!!.start()
            }

        }

        override fun doInBackground(vararg params: String): String? {

            try {
                val client = DefaultHttpClient()
                val HttpParams = client.params
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000)
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000)
                Log.d("LoginUrl", "LoginUrl = $LoginUrl")
                val post = HttpPost(LoginUrl)

                client.execute(post) { httpResponse ->
                    val httpEntity = httpResponse.entity
                    content = EntityUtils.toString(httpEntity)
                    Log.d("Result >>>", "Result One" + content!!)

                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Naquil", "Result error$e")
                return e.message
            }

            return content
        }

        override fun onPostExecute(result: String) {
            if (ProgressDialog != null) {
                cusRotateLoading!!.stop()
                ProgressDialog!!.cancel()
            }

            val isStatus = Common.ShowHttpErrorMessage(activity, result)
            Log.d("LoginUrl", "LoginUrl result= $result==$isStatus")
            if (isStatus) {
                try {
                    Log.d("loginUrl", "loginUrl two= $result")
                    val resObj = JSONObject(result)
                    Log.d("loginUrl", "loginUrl two= $resObj")
                    if (ProgressDialog != null) {
                        ProgressDialog!!.cancel()
                        cusRotateLoading!!.stop()
                    }
                    Log.d("loginUrl Status", "loginUrl Status" + resObj.getString("status"))

                    if (resObj.getString("status") == "success") {

                        val cabDtlAry = JSONArray(resObj.getString("cabDetails"))
                        Common.CabDetail = cabDtlAry

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
                        passwordPre.putString("password", password)
                        passwordPre.commit()

                        val username = userPref.edit()
                        username.putString(
                            "username",
                            userDetilObj.getString("username").toString()
                        )
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

                        val gender = userPref.edit()
                        gender.putString("gender", userDetilObj.getString("gender").toString())
                        gender.commit()

                        //                        if (!activityName.equals("SplashScreen")) {
                        //                            Common.showMkSucess(activity, resObj.getString("message"),"no");
                        //                        }

//                        Handler().postDelayed({
                        val hi = Intent(activity, HomeActivity::class.java)
                        hi.putExtra("PickupLatitude", PickupLatitude)
                        hi.putExtra("PickupLongtude", PickupLongtude)
                        activity.startActivity(hi)
                        activity.finish()
//                        }, 2000)
                    } else if (resObj.getString("status") == "failed") {
                        Common.LoginMkError(
                            activity,
                            resObj.getString("error code"),
                            resObj.getString("code")
                        )
                        if (activityName == "SplashScreen") {
//                            Handler().postDelayed({
                            val hi = Intent(activity, LoginActivity::class.java)
                            activity.startActivity(hi)
                            activity.finish()
//                            }, 2000)
                        }
                    } else if (resObj.getString("status") == "false") {
                        Log.d("Result", "Result failed" + resObj.getString("status"))
                        if (resObj.getString("Isactive") == "Inactive") {

                            //Common.showLoginRegisterMkError(activity, resObj.getString("message"));
                            Common.user_InActive = 1
                            Common.InActive_msg = resObj.getString("message")
                            //if (activityName.equals("SplashScreen")) {
                            val editor = userPref.edit()
                            editor.clear()
                            editor.commit()

                            Handler().postDelayed({
                                val logInt = Intent(activity, LoginOptionActivity::class.java)
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                activity.startActivity(logInt)
                            }, 500)
                            //}
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    class CarTypeCallHttp(
        internal var activity: Activity,
        internal var ProgressDialog: Dialog?,
        internal var cusRotateLoading: RotateLoading?,
        internal var activityName: String,
        internal var LoginUrl: String
    ) : AsyncTask<String, Int, String>() {
        private var content: String? = null

        override fun onPreExecute() {
            Log.d("Start", "start")
            if (ProgressDialog != null) {
                ProgressDialog!!.show()
                cusRotateLoading!!.start()
            }

        }

        override fun doInBackground(vararg params: String): String? {

            try {
                val client = DefaultHttpClient()
                val HttpParams = client.params
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000)
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000)
                Log.d("LoginUrl", "LoginUrl = $LoginUrl")
                val post = HttpPost(LoginUrl)

                client.execute(post) { httpResponse ->
                    val httpEntity = httpResponse.entity
                    content = EntityUtils.toString(httpEntity)
                    Log.d("Result >>>", "Result One" + content!!)

                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Naquil", "Result error$e")
                return e.message
            }

            return content
        }

        override fun onPostExecute(result: String) {
            if (ProgressDialog != null) {
                cusRotateLoading!!.stop()
                ProgressDialog!!.cancel()
            }

            val isStatus = Common.ShowHttpErrorMessage(activity, result)
            Log.d("LoginUrl", "LoginUrl result= $result==$isStatus")
            if (isStatus) {
                try {
                    Log.d("loginUrl", "loginUrl two= $result")
                    val resObj = JSONObject(result)
                    Log.d("loginUrl", "loginUrl two= $resObj")
                    if (ProgressDialog != null) {
                        ProgressDialog!!.cancel()
                        cusRotateLoading!!.stop()
                    }
                    Log.d("loginUrl Status", "loginUrl Status" + resObj.getString("status"))

                    if (resObj.getString("status") == "success") {

                        val cabDtlAry = JSONArray(resObj.getString("cabDetails"))
                        Common.CabDetail = cabDtlAry

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


//                        Handler().postDelayed({
                        val hi = Intent(activity, HomeActivity::class.java)
//                            hi.putExtra("PickupLatitude", PickupLatitude)
//                            hi.putExtra("PickupLongtude", PickupLongtude)
                        activity.startActivity(hi)
                        activity.finish()
//                        }, 2000)
                    } else if (resObj.getString("status") == "failed") {
                        Common.LoginMkError(
                            activity,
                            resObj.getString("error code"),
                            resObj.getString("code")
                        )
                        if (activityName == "SplashScreen") {

                            val hi = Intent(activity, LoginActivity::class.java)
                            activity.startActivity(hi)
                            activity.finish()

                        }
                    } else if (resObj.getString("status") == "false") {
                        Log.d("Result", "Result failed" + resObj.getString("status"))

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun SlideMenuDesign(slidingMenu: SlidingMenu, activity: Activity, clickMenu: String) {

        val userPref = PreferenceManager.getDefaultSharedPreferences(activity)

        val Roboto_Regular =
            Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Regular_0.ttf")
        val Roboto_Bold = Typeface.createFromAsset(activity.assets, "fonts/Roboto-Bold.ttf")

        val txt_user_name = slidingMenu.findViewById(R.id.txt_user_name) as TextView
        txt_user_name.typeface = Roboto_Regular
        txt_user_name.text = userPref.getString("username", "")
        val txt_user_number = slidingMenu.findViewById(R.id.txt_user_number) as TextView
        txt_user_number.typeface = Roboto_Regular
        txt_user_number.text = userPref.getString("mobile", "")

        val txt_book_my_trip = slidingMenu.findViewById(R.id.txt_book_my_trip) as TextView
        txt_book_my_trip.typeface = Roboto_Bold
        val txt_my_trip = slidingMenu.findViewById(R.id.txt_my_trip) as TextView
        txt_my_trip.typeface = Roboto_Bold
        val txt_rate_card = slidingMenu.findViewById(R.id.txt_rate_card) as TextView
        txt_rate_card.typeface = Roboto_Bold
        val txt_cahnge_password = slidingMenu.findViewById(R.id.txt_cahnge_password) as TextView
        txt_cahnge_password.typeface = Roboto_Bold
        val txt_sign_out = slidingMenu.findViewById(R.id.txt_sign_out) as TextView
        txt_sign_out.typeface = Roboto_Bold


        layout_book_my_trip = slidingMenu.findViewById(R.id.layout_book_my_trip) as RelativeLayout
        layout_my_trip = slidingMenu.findViewById(R.id.layout_my_trip) as RelativeLayout
        layout_rate_card = slidingMenu.findViewById(R.id.layout_rate_card) as RelativeLayout
        layout_cahnge_password =
            slidingMenu.findViewById(R.id.layout_cahnge_password) as RelativeLayout
        layout_footer_logout = slidingMenu.findViewById(R.id.layout_footer_logout) as RelativeLayout

        val layout_user = slidingMenu.findViewById(R.id.layout_user) as RelativeLayout
        if (Common.ActiveActivity == "book my trips") {
            layout_book_my_trip.setBackgroundResource(R.drawable.active_opt_bg)
        }
        layout_book_my_trip.setOnClickListener {
            slidingMenu.toggle()

            layout_my_trip.setBackgroundResource(0)
            layout_my_trip.setBackgroundResource(0)
            layout_cahnge_password.setBackgroundResource(0)
            Common.ActiveActivity = "book my trips"
            if (clickMenu != "home") {
                val mi = Intent(activity, HomeActivity::class.java)
                activity.startActivity(mi)
                activity.finish()
            }
        }

        if (Common.ActiveActivity == "my trips") {
            layout_my_trip.setBackgroundResource(R.drawable.active_opt_bg)
        }
        layout_my_trip.setOnClickListener {
            slidingMenu.toggle()
            if (userPref.contains("isLogin")) {
                layout_book_my_trip.setBackgroundResource(0)
                layout_rate_card.setBackgroundResource(0)
                layout_cahnge_password.setBackgroundResource(0)
                Common.ActiveActivity = "my trips"
                if (clickMenu != "all trip") {
                    val mi = Intent(activity, AllTripActivity::class.java)
                    activity.startActivity(mi)
                    activity.finish()
                }
            } else
                Toast.makeText(activity, "Please Login First.", Toast.LENGTH_SHORT).show()
        }

        if (Common.ActiveActivity == "rate card") {
            layout_rate_card.setBackgroundResource(R.drawable.active_opt_bg)
        }
        layout_rate_card.setOnClickListener {
            Common.ActiveActivity = "rate card"
            slidingMenu.toggle()
            layout_my_trip.setBackgroundResource(0)
            layout_book_my_trip.setBackgroundResource(0)
            layout_cahnge_password.setBackgroundResource(0)

            if (clickMenu != "rate card") {
                val ri = Intent(activity, RateCardActivity::class.java)
                activity.startActivity(ri)
                activity.finish()
            }
        }

        if (Common.ActiveActivity == "change password") {
            layout_cahnge_password.setBackgroundResource(R.drawable.active_opt_bg)
        }
        layout_cahnge_password.setOnClickListener {
            slidingMenu.toggle()
            if (userPref.contains("isLogin")) {
                Common.ActiveActivity = "change password"
                layout_rate_card.setBackgroundResource(0)
                layout_my_trip.setBackgroundResource(0)
                layout_book_my_trip.setBackgroundResource(0)

                if (clickMenu != "change password") {
                    val mi = Intent(activity, ChangePasswordActivity::class.java)
                    activity.startActivity(mi)
                    activity.finish()
                }
            } else
                Toast.makeText(activity, "Please Login First.", Toast.LENGTH_SHORT).show()
        }


        layout_footer_logout.setOnClickListener {
            slidingMenu.toggle()
            if (userPref.contains("isLogin")) {
                AlertDialog.Builder(activity)
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(activity.resources.getString(R.string.ok)) { dialog, which ->
                        val editor = userPref.edit()
                        editor.clear()
                        editor.commit()
                        val logInt = Intent(activity, LoginOptionActivity::class.java)
                        logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activity.startActivity(logInt)
                    }
                    .setNegativeButton(activity.resources.getString(R.string.cancel)) { dialog, which -> dialog.cancel() }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } else
                Toast.makeText(activity, "Please Login First.", Toast.LENGTH_SHORT).show()
        }

        layout_user.setOnClickListener {
            if (userPref.contains("isLogin")) {
                slidingMenu.toggle()
                val mi = Intent(activity, UserProfileActivity::class.java)
                activity.startActivity(mi)
            } else
                Toast.makeText(activity, "Please Login First.", Toast.LENGTH_SHORT).show()
        }

        val img_user = slidingMenu.findViewById(R.id.img_user) as ImageView
        val facebook_id = userPref.getString("facebook_id", "")
        Log.d("facebook_id", "facebook_id = " + facebook_id!!)
        if (facebook_id != null && facebook_id != "" && userPref.getString("userImage", "") == "") {
            val facebookImage = Url.FacebookImgUrl + facebook_id + "/picture?type=large"
            Log.d("facebookImage", "facebookImage = $facebookImage")
            Picasso.with(activity)
                .load(facebookImage)
                .placeholder(R.drawable.avatar_placeholder)
                .resize(200, 200)
                .transform(CircleTransform())
                .into(img_user)
        } else {
            Log.d(
                "userImage",
                "user Image = " + Url.userImageUrl + userPref.getString("userImage", "")
            )
            Picasso.with(activity)
                .load(Uri.parse(Url.userImageUrl + userPref.getString("userImage", "")!!))
                .transform(CircleTransform())
                .placeholder(R.drawable.mail_defoult)
                .into(img_user)
        }


    }

    class CallUnSubscribeTaken(internal var activity: Activity, internal var DeviceToken: String) :
        AsyncTask<String, Void, String>() {


        private val userPref: SharedPreferences

        init {
            userPref = PreferenceManager.getDefaultSharedPreferences(activity)
        }

        override fun doInBackground(vararg args: String): String {
            // Create a new HttpClient and Post Header
            val httpclient = DefaultHttpClient()
            val myParams = BasicHttpParams()
            HttpConnectionParams.setConnectionTimeout(myParams, 10000)
            HttpConnectionParams.setSoTimeout(myParams, 10000)

            val JSONResponse: JSONObject? = null
            var contentStream: InputStream? = null
            var resultString = ""

            try {

                val passObj = JSONObject()
                passObj.put("user", "u_" + userPref.getString("id", "")!!)
                passObj.put("type", "android")
                passObj.put("token", DeviceToken)

                Log.d("passObj", "response passObj = $passObj")

                val httppost = HttpPost(Url.unsubscribeUrl)
                httppost.setHeader("Content-Type", "application/json")
                httppost.setHeader("Accept", "application/json")

                //                StringEntity se = new StringEntity(passObj.toString());
                //                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                //                httppost.setEntity(se);
                httppost.entity = StringEntity(passObj.toString(), HTTP.UTF_8)

                for (i in 0 until httppost.allHeaders.size) {
                    Log.v("set header", httppost.allHeaders[i].value)
                }

                val response = httpclient.execute(httppost)

                // Do some checks to make sure that the request was processed properly
                val headers = response.allHeaders
                val entity = response.entity
                contentStream = entity.content

                Log.d("response", "response = $response==$entity==$contentStream")
                resultString = response.toString()
            } catch (e: ClientProtocolException) {
                e.printStackTrace()
                Log.d("Error", "response Error one = " + e.message)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("Error", "response Error two = " + e.message)
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("Error", "response Error three = " + e.message)
            }


            return resultString
        }

        override fun onPostExecute(result: String) {
            //Toast.makeText(activity,"sucess = "+result,Toast.LENGTH_LONG).show();

            if (result.contains("HTTP/1.1 200 OK")) {
                Common.CallDeviceTaken(activity, Common.device_token).execute()

            }
        }

    }

    class CallDeviceTaken(activity: Activity, internal var DeviceToken: String) :
        AsyncTask<String, Void, String>() {


        private val userPref: SharedPreferences

        init {
            userPref = PreferenceManager.getDefaultSharedPreferences(activity)
        }

        override fun doInBackground(vararg args: String): String {
            // Create a new HttpClient and Post Header
            val httpclient = DefaultHttpClient()
            val myParams = BasicHttpParams()
            HttpConnectionParams.setConnectionTimeout(myParams, 10000)
            HttpConnectionParams.setSoTimeout(myParams, 10000)

            val JSONResponse: JSONObject? = null
            var contentStream: InputStream? = null
            var resultString = ""

            try {

                val passObj = JSONObject()
                passObj.put("user", "u_" + userPref.getString("id", "")!!)
                passObj.put("type", "android")
                passObj.put("token", DeviceToken)

                Log.d("passObj", "response passObj = $passObj")

                val httppost = HttpPost(Url.subscribeUrl)
                httppost.setHeader("Content-Type", "application/json")
                httppost.setHeader("Accept", "application/json")

                //                StringEntity se = new StringEntity(passObj.toString());
                //                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                //                httppost.setEntity(se);
                httppost.entity = StringEntity(passObj.toString(), HTTP.UTF_8)

                for (i in 0 until httppost.allHeaders.size) {
                    Log.v("set header", httppost.allHeaders[i].value)
                }

                val response = httpclient.execute(httppost)

                // Do some checks to make sure that the request was processed properly
                val headers = response.allHeaders
                val entity = response.entity
                contentStream = entity.content

                Log.d("response", "response = $response==$entity==$contentStream")
                resultString = response.toString()
            } catch (e: ClientProtocolException) {
                e.printStackTrace()
                Log.d("Error", "response Error one = " + e.message)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("Error", "response Error two = " + e.message)
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("Error", "response Error three = " + e.message)
            }


            return resultString
        }

        override fun onPostExecute(result: String) {

            if (result.contains("HTTP/1.1 200 OK")) {
                val isDeviceToken = userPref.edit()
                isDeviceToken.putString("id_device_token", "1")
                isDeviceToken.commit()
            }
        }
    }

    companion object {

        var device_token = ""
        var CabDetail: JSONArray? = null
        var Currency = ""
        var Country = ""
        var StartDayTime = ""
        var EndDayTime = ""
        var allTripFeeds: AllTripFeed? = null
        var ActiveActivity = "book my trips"
        var is_pusnotification = 0
        var user_InActive = 0
        var InActive_msg = ""

        fun showLoginRegisterMkError(act: Activity, message: String) {
            if (!act.isFinishing) {

                val slideUpAnimation: Animation

                val MKInfoPanelDialog = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)

                MKInfoPanelDialog.setContentView(R.layout.mkinfopanel)
                MKInfoPanelDialog.show()
                slideUpAnimation = AnimationUtils.loadAnimation(
                    act.applicationContext,
                    R.anim.slide_up_map
                )

                val layout_info_panel =
                    MKInfoPanelDialog.findViewById(R.id.layout_info_panel) as RelativeLayout
                layout_info_panel.startAnimation(slideUpAnimation)

                val subtitle = MKInfoPanelDialog.findViewById(R.id.subtitle) as TextView
                subtitle.text = message

                Handler().postDelayed({
                    try {
                        if (MKInfoPanelDialog.isShowing && !act.isFinishing)
                            MKInfoPanelDialog.cancel()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 2000)

            }
        }

        fun LoginMkError(act: Activity, message: String, code: String) {

            val OpenSans_Bold = Typeface.createFromAsset(act.assets, "fonts/OpenSans-Bold_0.ttf")
            val OpenSans_Regular =
                Typeface.createFromAsset(act.assets, "fonts/OpenSans-Regular_0.ttf")

            val LoginErrorDialog = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)
            LoginErrorDialog.setContentView(R.layout.login_error_dialog)
            val txt_invalid_login =
                LoginErrorDialog.findViewById(R.id.txt_invalid_login) as TextView
            val txt_error_msg = LoginErrorDialog.findViewById(R.id.txt_error_msg) as TextView
            Log.d("code", "code = $code")
            if (code.toLowerCase(Locale.getDefault()) == "invalid login") {
                txt_invalid_login.text = act.resources.getString(R.string.invalid_login)
                txt_error_msg.text = act.resources.getString(R.string.correct_login_detail)
            } else {
                txt_invalid_login.text =
                    act.resources.getString(R.string.recheck_your_login_detail_title)
                txt_error_msg.text = act.resources.getString(R.string.recheck_your_login_detail)
            }
            txt_invalid_login.typeface = OpenSans_Bold
            txt_error_msg.typeface = OpenSans_Regular

            val layout_ok = LoginErrorDialog.findViewById(R.id.layout_ok) as RelativeLayout
            layout_ok.setOnClickListener { LoginErrorDialog.cancel() }

            LoginErrorDialog.show()
        }

        fun showMkError(act: Activity, error_code: String) {
            var message = ""
            if (!act.isFinishing) {
                Log.d("error_code", "error_code = $error_code")
                if (error_code == "2") {
                    message = act.resources.getString(R.string.enter_correct_login_detail)
                } else if (error_code == "7") {
                    message = act.resources.getString(R.string.email_username_mobile_exit)
                } else if (error_code == "8") {
                    message = act.resources.getString(R.string.email_username_exit)
                } else if (error_code == "9") {
                    message = act.resources.getString(R.string.email_mobile_exit)
                } else if (error_code == "10") {
                    message = act.resources.getString(R.string.mobile_username_exit)
                } else if (error_code == "11") {
                    message = act.resources.getString(R.string.email_exit)
                } else if (error_code == "12") {
                    message = act.resources.getString(R.string.user_exit)
                } else if (error_code == "13") {
                    message = act.resources.getString(R.string.mobile_exit)
                } else if (error_code == "14") {
                    message = act.resources.getString(R.string.somthing_worng)
                } else if (error_code == "15" || error_code == "16") {
                    message = act.resources.getString(R.string.data_not_found)
                } else if (error_code == "19") {
                    message = act.resources.getString(R.string.vehicle_numbet_exits)
                } else if (error_code == "20") {
                    message = act.resources.getString(R.string.license_numbet_exits)
                } else if (error_code == "22") {
                    message = act.resources.getString(R.string.dublicate_booking)
                } else {
                    message = error_code
                }

                val userPref = PreferenceManager.getDefaultSharedPreferences(act)

                val slideUpAnimation: Animation

                val MKInfoPanelDialog = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)

                MKInfoPanelDialog.setContentView(R.layout.mkinfopanel)
                MKInfoPanelDialog.show()
                slideUpAnimation = AnimationUtils.loadAnimation(
                    act.applicationContext,
                    R.anim.slide_up_map
                )

                val layout_info_panel =
                    MKInfoPanelDialog.findViewById(R.id.layout_info_panel) as RelativeLayout
                layout_info_panel.startAnimation(slideUpAnimation)

                val buttonLayoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    act.resources.getDimension(R.dimen.height_40).toInt()
                )
                buttonLayoutParams.setMargins(
                    0,
                    act.resources.getDimension(R.dimen.height_50).toInt(),
                    0,
                    0
                )
                layout_info_panel.layoutParams = buttonLayoutParams

                val subtitle = MKInfoPanelDialog.findViewById(R.id.subtitle) as TextView
                subtitle.text = message

                Handler().postDelayed({
                    try {
                        if (MKInfoPanelDialog.isShowing && !act.isFinishing)
                            MKInfoPanelDialog.cancel()

                        if (error_code == "1" || error_code == "5") {
                            val editor = userPref.edit()
                            editor.clear()
                            editor.commit()

                            val logInt = Intent(act, LoginOptionActivity::class.java)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            act.startActivity(logInt)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 2000)

            }
        }

        fun showMkSucess(act: Activity, message: String, isHeader: String) {
            if (!act.isFinishing) {

                val slideUpAnimation: Animation

                val MKInfoPanelDialog = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)

                MKInfoPanelDialog.setContentView(R.layout.mkinfopanel)
                MKInfoPanelDialog.show()
                slideUpAnimation = AnimationUtils.loadAnimation(
                    act.applicationContext,
                    R.anim.slide_up_map
                )
                slideUpAnimation.fillAfter = true
                slideUpAnimation.duration = 2000

                val layout_info_panel =
                    MKInfoPanelDialog.findViewById(R.id.layout_info_panel) as RelativeLayout
                layout_info_panel.setBackgroundResource(R.color.sucess_color)
                layout_info_panel.startAnimation(slideUpAnimation)

                if (isHeader == "yes") {
                    val buttonLayoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        act.resources.getDimension(R.dimen.height_50).toInt()
                    )
                    buttonLayoutParams.setMargins(
                        0,
                        act.resources.getDimension(R.dimen.height_50).toInt(),
                        0,
                        0
                    )
                    layout_info_panel.layoutParams = buttonLayoutParams
                }

                val subtitle = MKInfoPanelDialog.findViewById(R.id.subtitle) as TextView
                subtitle.text = message

                Handler().postDelayed({
                    try {
                        if (MKInfoPanelDialog.isShowing && !act.isFinishing)
                            MKInfoPanelDialog.cancel()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 2000)

            }
        }

        fun isNetworkAvailable(act: Activity): Boolean {

            val connMgr = act.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnected) {
                // fetch data
                true
            } else {
                // display error
                false
            }

        }

        fun showInternetInfo(act: Activity, message: String) {
            if (!act.isFinishing) {
                val mk = InternetInfoPanel(
                    act,
                    InternetInfoPanel.InternetInfoPanelType.MKInfoPanelTypeInfo,
                    "SUCCESS!",
                    message,
                    2000
                )
                mk.show()
                mk.iv_ok.setOnClickListener {
                    try {
                        if (mk.isShowing && !act.isFinishing)
                            mk.cancel()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun ShowHttpErrorMessage(activity: Activity, ErrorMessage: String?): Boolean {

            Log.d("ErrorMessage", "ErrorMessage = " + ErrorMessage!!)
            var Status = true
            if (ErrorMessage != null && ErrorMessage != "") {
                if (ErrorMessage.contains("Connect to")) {
                    Common.showInternetInfo(activity, "")
                    Status = false
                } else if (ErrorMessage.contains("failed to connect to")) {
                    Common.showInternetInfo(activity, "network not available")
                    Status = false
                } else if (ErrorMessage.contains("Internal Server Error")) {
                    Common.showMkError(activity, "Internal Server Error")
                    Status = false
                } else if (ErrorMessage.contains("Request Timeout")) {
                    Common.showMkError(activity, "Request Timeout")
                    Status = false
                }
            } else {
                Toast.makeText(activity, "Server Time Out", Toast.LENGTH_LONG).show()
                Status = false
            }
            return Status
        }


        fun getTotalPrice(
            intailrate: String,
            FirstKm: Float,
            distance: Float?,
            fromintailrate: String,
            ride_time_rate: String,
            totalTime: Int
        ): Float {
//            var fromintailrate = fromintailrate
//            val totlePrice: Float?


//            val firstPrice = java.lang.Float.parseFloat(intailrate)
//            var secoundPrice: Float? = null
//            Log.d("fromintailrate", "fromintailrate FirstKm= $FirstKm==$distance")
//            if (FirstKm < distance!!) {
//                val afterkm = distance!! - FirstKm
//                Log.d("fromintailrate", "fromintailrate distance= $fromintailrate==$afterkm")
//                if (fromintailrate == "")
//                    fromintailrate = "0"
//                secoundPrice = java.lang.Float.parseFloat(fromintailrate) * afterkm
//                Log.d("total price", "total price = $distance==$FirstKm==$afterkm")
//            }
//
//            Log.d("totalTime", "totalTime = $totalTime==$ride_time_rate")
//            val driverprice = java.lang.Float.parseFloat(ride_time_rate) * totalTime
//
//            if (secoundPrice != null)
//                totlePrice = firstPrice + secoundPrice + driverprice
//            else
//                totlePrice = firstPrice + driverprice

//            return totlePrice
            return intailrate.toFloat() * distance!!
        }

        fun getDisplayHeight(activity: Activity): Int {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

        //    public static void HintFunction(final Activity activity, final EditText editText, final String HitMessage){
        //        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        //            @Override
        //            public void onFocusChange(View v, boolean hasFocus) {
        //                if (hasFocus)
        //                {
        //                    if(editText.getText().length() == 0)
        //                        Common.showMkHitMessage(activity, HitMessage);
        //                }
        //            }
        //        });
        //    }

        fun showMkHitMessage(act: Activity, message: String) {
            if (!act.isFinishing) {

                val slideUpAnimation: Animation

                val MKInfoPanelDialog = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)

                MKInfoPanelDialog.setContentView(R.layout.mkinfopanel)
                MKInfoPanelDialog.show()
                slideUpAnimation = AnimationUtils.loadAnimation(
                    act.applicationContext,
                    R.anim.slide_up_map
                )

                val layout_info_panel =
                    MKInfoPanelDialog.findViewById(R.id.layout_info_panel) as RelativeLayout
                layout_info_panel.startAnimation(slideUpAnimation)

                layout_info_panel.setBackgroundResource(R.color.yellow)

                val subtitle = MKInfoPanelDialog.findViewById(R.id.subtitle) as TextView
                subtitle.text = message

                Handler().postDelayed({
                    try {
                        if (MKInfoPanelDialog.isShowing && !act.isFinishing)
                            MKInfoPanelDialog.cancel()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 2000)

            }
        }

        fun showMKPanelError(
            act: Activity,
            message: String,
            rlMainView: RelativeLayout,
            tvTitle: TextView,
            typeface: Typeface
        ) {
            if (!act.isFinishing && rlMainView.visibility == View.GONE) {

                Log.d("rlMainView", "rlMainView = " + rlMainView.visibility + "==" + View.GONE)
                if (rlMainView.visibility == View.GONE) {
                    rlMainView.visibility = View.VISIBLE
                }

                rlMainView.setBackgroundResource(R.color.dialog_error_color)
                tvTitle.text = message

                tvTitle.typeface = typeface
                val slideUpAnimation =
                    AnimationUtils.loadAnimation(act.applicationContext, R.anim.slide_up_map)
                rlMainView.startAnimation(slideUpAnimation)

            }
        }

        fun ValidationGone(
            activity: Activity,
            rlMainView: RelativeLayout,
            edt_reg_username: EditText
        ) {
            edt_reg_username.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    Log.d(
                        "charSequence",
                        "charSequence = " + charSequence.length + "==" + rlMainView.visibility + "==" + View.VISIBLE
                    )
                    if (charSequence.length > 0 && rlMainView.visibility == View.VISIBLE) {
                        if (!activity.isFinishing) {
                            val slideUp = TranslateAnimation(
                                Animation.RELATIVE_TO_SELF,
                                0f,
                                Animation.RELATIVE_TO_SELF,
                                0f,
                                Animation.RELATIVE_TO_SELF,
                                0f,
                                Animation.RELATIVE_TO_SELF,
                                -100f
                            )
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

                override fun afterTextChanged(editable: Editable) {

                }
            })
        }
    }

}
