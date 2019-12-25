package come.user.lezco

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.gpsLocation.GPSTracker
import come.user.lezco.utils.CircleTransform
import come.user.lezco.utils.Common
import come.user.lezco.utils.Common.Companion.showMKPanelError
import come.user.lezco.utils.Url
import cz.msebera.android.httpclient.HttpEntity
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.entity.mime.content.FileBody
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.params.HttpConnectionParams
import cz.msebera.android.httpclient.util.EntityUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {

    internal var edit_username: EditText? = null
    internal var edit_name: EditText? = null
    internal var edit_mobile: EditText? = null
    internal var edit_email: EditText? = null
    internal var edit_password: EditText? = null
    internal var edit_com_password: EditText? = null
    internal var layout_signup: RelativeLayout? = null
    internal var img_add_image: ImageView? = null
    internal var edit_date_of_birth: EditText? = null
    internal var spinner_gender: Spinner? = null
    lateinit var layout_show_hide: RelativeLayout
    lateinit var txt_hide_show: TextView
    lateinit var layout_info_panel: RelativeLayout
    lateinit var subtitle: TextView
    lateinit var txt_sign_up_logo: TextView
    lateinit var txt_signup: TextView
    lateinit var profile_scrollview: ScrollView

    lateinit var OpenSans_Regular: Typeface
    lateinit var regularRoboto: Typeface
    lateinit var Roboto_Bold: Typeface

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    internal var facebook_id: String? = ""
    internal var twitter_id: String? = ""
    internal var facebook_email = ""
    internal var facebook_name = ""
    internal var genderString = "gender"

    lateinit var OpenCameraDialog: Dialog

    private var mCapturedImageURI: Uri? = null
    internal var userImage: File? = null
    lateinit var userPref: SharedPreferences

    lateinit var myCalendar: Calendar
    lateinit var date: DatePickerDialog.OnDateSetListener

    internal var PickupLongtude: Double = 0.toDouble()
    internal var PickupLatitude: Double = 0.toDouble()

    internal var passwordShowHide = false
    lateinit var letter: Pattern

    internal var digit = Pattern.compile("[0-9]")
    internal var special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")

    internal var newFocuValidation = false

    //Error Alert
    lateinit var rlMainView: RelativeLayout
    lateinit var tvTitle: TextView

    val imageUri: Uri
        get() {
            val file1 = File(Environment.getExternalStorageDirectory().toString() + "/Naqil")
            if (!file1.exists()) {
                file1.mkdirs()
            }
            val file2 =
                File(Environment.getExternalStorageDirectory().toString() + "/Naqil/UserImage")
            if (!file2.exists()) {
                file2.mkdirs()
            }

            val file =
                File(Environment.getExternalStorageDirectory().toString() + "/Naqil/UserImage/" + System.currentTimeMillis() + ".jpg")

            return Uri.fromFile(file)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setBackgroundDrawableResource(R.drawable.background)

        Log.d("Locale", "Locale Language = " + Locale.getDefault().language)
        if (Locale.getDefault().language == "en") {
            letter = Pattern.compile("[a-zA-z]")
            Log.d("Locale", "Locale Language one= " + Locale.getDefault().language)
        } else {
            letter = Pattern.compile("[^x00-x7F]")
        }

        profile_scrollview = findViewById(R.id.profile_scrollview) as ScrollView
        edit_username = findViewById(R.id.edit_username) as EditText?
        edit_name = findViewById(R.id.edit_name) as EditText?
        edit_mobile = findViewById(R.id.edit_mobile) as EditText?
        edit_email = findViewById(R.id.edit_email) as EditText?
        edit_password = findViewById(R.id.edit_password) as EditText?
        edit_com_password = findViewById(R.id.edit_com_password) as EditText?
        layout_signup = findViewById(R.id.layout_signup) as RelativeLayout?
        img_add_image = findViewById(R.id.img_add_image) as ImageView?
        edit_date_of_birth = findViewById(R.id.edit_date_of_birth) as EditText?
        spinner_gender = findViewById(R.id.spinner_gender) as Spinner?
        layout_show_hide = findViewById(R.id.layout_show_hide) as RelativeLayout
        txt_hide_show = findViewById(R.id.txt_hide_show) as TextView
        layout_info_panel = findViewById(R.id.layout_info_panel) as RelativeLayout
        subtitle = findViewById(R.id.subtitle) as TextView
        txt_sign_up_logo = findViewById(R.id.txt_sign_up_logo) as TextView
        txt_signup = findViewById(R.id.txt_signup) as TextView

        //Error Alert
        rlMainView = findViewById(R.id.rlMainView) as RelativeLayout
        tvTitle = findViewById(R.id.tvTitle) as TextView

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        )

        val gpsTracker = GPSTracker(this@SignUpActivity)
        PickupLatitude = gpsTracker.latitude
        PickupLongtude = gpsTracker.longitude

        userPref = PreferenceManager.getDefaultSharedPreferences(this@SignUpActivity)

        ProgressDialog = Dialog(this@SignUpActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        regularRoboto = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        edit_name!!.typeface = OpenSans_Regular
        edit_username!!.typeface = OpenSans_Regular
        edit_mobile!!.typeface = OpenSans_Regular
        edit_email!!.typeface = OpenSans_Regular
        edit_password!!.typeface = OpenSans_Regular
        edit_com_password!!.typeface = OpenSans_Regular
        edit_date_of_birth!!.typeface = OpenSans_Regular
        subtitle.typeface = OpenSans_Regular
        txt_sign_up_logo.typeface = Roboto_Bold
        txt_signup.typeface = Roboto_Bold

        intent?.let {
            facebook_id = intent!!.getStringExtra("facebook_id") ?:""
            facebook_email = intent!!.getStringExtra("facebook_email")?:""
            facebook_name = intent!!.getStringExtra("facebook_name")?:""
            twitter_id = intent!!.getStringExtra("twitter_id")?:""
        }

        edit_name!!.setText(facebook_name)
        edit_email!!.setText(facebook_email)

        //showMkHitMessage(edit_name, getResources().getString(R.string.allow_minimum_four_characters));

        //getResources().getString(R.string.please_enter_name)
        //        edit_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        //            @Override
        //            public void onFocusChange(View v, boolean hasFocus) {
        //            Log.d("hasFocus", "hasFocus password= " + hasFocus+"=="+edit_name.getText().toString().length());
        //                if (!hasFocus) {
        //                    boolean EditValidation = false;
        //
        //                    if(!EditValidation){
        //                        newFocuValidation = false;
        //                    }
        //                }
        //            }
        //        });

        edit_name!!.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        EdittextActonListner(edit_name!!, "name")

        edit_name!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length != 0) {
                    edit_name!!.removeTextChangedListener(this)
                    // change the text

                    val text = s.toString()
                    var upperString = ""
                    if (s.length != 0)
                        upperString =
                            text.substring(0, 1).toUpperCase() + text.substring(1, text.length)

                    Log.d("upperString", "upperString = $upperString")
                    edit_name!!.setText(upperString)
                    // enable it again
                    edit_name!!.addTextChangedListener(this)
                    edit_name!!.setSelection(edit_name!!.text.length)
                }
            }
        })

        EdittextActonListner(edit_username!!, "username")

        EdittextActonListner(edit_mobile!!, "mobile")

        EdittextActonListner(edit_email!!, "email")


        edit_password!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            Log.d("hasFocus", "hasFocus password= $hasFocus")
            if (hasFocus) {

                //                    new Handler().postDelayed(new Runnable() {
                //                        @Override
                //                        public void run() {
                //                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //                            imm.showSoftInput(edit_password, InputMethodManager.SHOW_IMPLICIT);
                //                        }
                //                    }, 500);

                subtitle.text = ""

                Handler().postDelayed({
                    LayoutEnimation(
                        R.color.dialog_hit_color,
                        resources.getString(R.string.password_valid)
                    )
                }, 100)

            }
        }
        EdittextActonListner(edit_password!!, "password")

        EdittextActonListner(edit_com_password!!, "confirm_password")

        layout_signup!!.setOnClickListener {
            val isvalid = ValidationRegister()
            if (isvalid) {
                if (Common.isNetworkAvailable(this@SignUpActivity)) {
                    SighUpUserHttp().execute()
                } else {
                    Common.showInternetInfo(this@SignUpActivity, "")
                }
            }
        }
        Log.d("facebook_id", "facebook_id = " + facebook_id!!)
        if (facebook_id != null && facebook_id != "") {
            val facebookImage = Url.FacebookImgUrl + facebook_id + "/picture?type=large"
            Log.d("facebookImage", "facebookImage = $facebookImage")
            Picasso.with(this@SignUpActivity)
                .load(facebookImage)
                .placeholder(R.drawable.avatar_placeholder)
                .resize(200, 200)
                .transform(CircleTransform())
                .into(img_add_image)
        }

        img_add_image!!.setOnClickListener {
            OpenCameraDialog =
                Dialog(this@SignUpActivity, android.R.style.Theme_Translucent_NoTitleBar)
            OpenCameraDialog.setContentView(R.layout.camera_dialog_layout)

            val layout_open_camera =
                OpenCameraDialog.findViewById(R.id.layout_open_camera) as RelativeLayout
            layout_open_camera.setOnClickListener {
                OpenCameraDialog.cancel()
                val ci = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                mCapturedImageURI = imageUri
                ci.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
                startActivityForResult(ci, REQUEST_CAMERA)
            }

            val layout_open_gallery =
                OpenCameraDialog.findViewById(R.id.layout_open_gallery) as RelativeLayout
            layout_open_gallery.setOnClickListener {
                OpenCameraDialog.cancel()
                val gi = Intent(Intent.ACTION_PICK)
                gi.type = "image/*"
                startActivityForResult(gi, REQUEST_GALLERY)
            }

            val layout_open_cancel =
                OpenCameraDialog.findViewById(R.id.layout_open_cancel) as RelativeLayout
            layout_open_cancel.setOnClickListener { OpenCameraDialog.cancel() }

            OpenCameraDialog.show()
        }

        myCalendar = Calendar.getInstance()


        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

            edit_date_of_birth!!.setText(sdf.format(myCalendar.time))

            spinner_gender!!.performClick()
        }

        edit_date_of_birth!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val dpd = DatePickerDialog(
                    this@SignUpActivity,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.maxDate = System.currentTimeMillis()
                val minCal = Calendar.getInstance()
                minCal.add(Calendar.YEAR, -100)
                val hundredYearsAgo = minCal.timeInMillis
                dpd.datePicker.minDate = hundredYearsAgo
                dpd.show()
            }
        }




        edit_date_of_birth!!.inputType = InputType.TYPE_NULL
        edit_date_of_birth!!.setOnClickListener {
            val dpd = DatePickerDialog(
                this@SignUpActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        val list = ArrayList<String>()
        list.add("Gender")
        list.add("Male")
        list.add("Female")

        val dataAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, list
        )
        dataAdapter.setDropDownViewResource(R.layout.gender_spinner_layout)
        spinner_gender!!.adapter = dataAdapter

        spinner_gender!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.white))
                (parent.getChildAt(0) as TextView).textSize = 16f
                (parent.getChildAt(0) as TextView).typeface = OpenSans_Regular
                genderString = parent.getItemAtPosition(position).toString()
                Log.d("genderString", "genderString = $genderString")
                //                if (!genderString.equals("Gender"))
                //                    edit_password.requestFocus();

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }



        layout_show_hide.setOnClickListener {
            if (passwordShowHide) {
                txt_hide_show.text = resources.getString(R.string.show)
                edit_password!!.transformationMethod = PasswordTransformationMethod()
                passwordShowHide = false
            } else {
                edit_password!!.transformationMethod = HideReturnsTransformationMethod()
                txt_hide_show.text = resources.getString(R.string.hide)
                passwordShowHide = true
            }
            edit_password!!.setSelection(edit_password!!.text.length)
        }

        profile_scrollview.setOnClickListener {
            if (rlMainView.visibility == View.VISIBLE) {
                if (!isFinishing) {
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

        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_name!!)
        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_username!!)
        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_password!!)
        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_com_password!!)
        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_mobile!!)
        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_email!!)
        Common.ValidationGone(this@SignUpActivity, rlMainView, edit_date_of_birth!!)

    }

    fun EdittextActonListner(editText: EditText, EdtTxtName: String) {
        editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val isvalid = ValidationRegister()
                if (isvalid) {
                    if (Common.isNetworkAvailable(this@SignUpActivity)) {
                        SighUpUserHttp().execute()
                    } else {
                        Common.showInternetInfo(this@SignUpActivity, "")
                    }
                }
                return@OnEditorActionListener true
            } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Log.d("Done", "hasFocus Done = " + editText.text.length + "==" + EdtTxtName)
                layout_info_panel.visibility = View.GONE
                val isValidNext = ValidationNextRegister(EdtTxtName)

                return@OnEditorActionListener if (!isValidNext) {
                    true
                } else {
                    false
                }
            }
            false
        })
    }


    inner class SighUpUserHttp : AsyncTask<String, Int, String>() {

        private var content: String? = null
        internal var entity: HttpEntity

        override fun onPreExecute() {
            Log.d("Start", "start")
            ProgressDialog.show()
            cusRotateLoading.start()

        }

        init {

            val entityBuilder = MultipartEntityBuilder.create()
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)

            entityBuilder.addTextBody("name", edit_name!!.text.toString())
            entityBuilder.addTextBody("email", edit_email!!.text.toString())
            entityBuilder.addTextBody("username", edit_username!!.text.toString())
            entityBuilder.addTextBody("mobile", edit_mobile!!.text.toString())
            entityBuilder.addTextBody("password", edit_password!!.text.toString())
            entityBuilder.addTextBody("isdevice", "1")
            if (facebook_id != null && facebook_id != "")
                entityBuilder.addTextBody("facebook_id", facebook_id)
            else
                entityBuilder.addTextBody("facebook_id", "")
            if (twitter_id != null && twitter_id != "")
                entityBuilder.addTextBody("twitter_id", twitter_id)
            else
                entityBuilder.addTextBody("twitter_id", "")
            entityBuilder.addTextBody("dob", edit_date_of_birth!!.text.toString())
            entityBuilder.addTextBody("gender", genderString)
            if (userImage != null) {
                val userFile = File(userImage!!.path)
                entityBuilder.addPart("image", FileBody(userFile))
            } else {
                entityBuilder.addTextBody("image", "")
            }
            entity = entityBuilder.build()
        }

        override fun doInBackground(vararg params: String): String? {

            try {
                val client = DefaultHttpClient()
                val HttpParams = client.params
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000)
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000)

                val post = HttpPost(Url.signupUrl)
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
            Log.d("signupUrl", "signupUrl result= $result")
            val isStatus = Common.ShowHttpErrorMessage(this@SignUpActivity, result)
            if (isStatus) {
                try {
                    val resObj = JSONObject(result)
                    Log.d("signupUrl", "signupUrl two= $resObj")
                    if (resObj.getString("status") == "success") {

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
                        val userDetilArray = JSONArray(resObj.getString("user_Detail"))
                        val userDetilObj = userDetilArray.getJSONObject(0)

                        val id = userPref.edit()
                        id.putString("id", userDetilObj.getString("id").toString())
                        id.commit()

                        val name = userPref.edit()
                        name.putString("name", userDetilObj.getString("name").toString())
                        name.commit()

                        val passwordPre = userPref.edit()
                        passwordPre.putString("password", edit_password!!.text.toString())
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
                        gender.putString("gender", genderString)
                        gender.commit()

                        if (userDetilObj.getString("facebook_id").toString() != "") {
                            val facebook_id = userPref.edit()
                            facebook_id.putString(
                                "facebook_id",
                                userDetilObj.getString("facebook_id").toString()
                            )
                            facebook_id.commit()
                        }

                        if (userDetilObj.getString("twitter_id").toString() != "") {
                            val twitter_id = userPref.edit()
                            twitter_id.putString(
                                "twitter_id",
                                userDetilObj.getString("twitter_id").toString()
                            )
                            twitter_id.commit()
                        }

                        ProgressDialog.cancel()
                        cusRotateLoading.stop()

                        //Common.showMkSucess(SignUpActivity.this, resObj.getString("message"),"no");

                        Handler().postDelayed({
                            val hi = Intent(this@SignUpActivity, HomeActivity::class.java)
                            hi.putExtra("PickupLatitude", PickupLatitude)
                            hi.putExtra("PickupLongtude", PickupLongtude)
                            startActivity(hi)
                            finish()
                        }, 1000)

                        //                        RequestParams loginParams = new RequestParams();
                        //                        loginParams.put("password", edit_password.getText().toString());
                        //                        loginParams.put("email", edit_email.getText().toString());
                        //
                        //                        Common.LoginCall(SignUpActivity.this, loginParams, ProgressDialog, cusRotateLoading, edit_password.getText().toString());

                    } else if (resObj.getString("status") == "failed") {
                        Log.d("signupUrl", "signupUrl status = " + resObj.getString("status"))
                        ProgressDialog.cancel()
                        cusRotateLoading.stop()
                        Common.showLoginRegisterMkError(
                            this@SignUpActivity,
                            resObj.getString("message")
                        )
                    }
                } catch (e: JSONException) {
                    Log.d("signupUrl", "signupUrl error = " + e.message)
                    e.printStackTrace()
                }

            }
        }
    }

    fun ValidationNextRegister(EditTextString: String): Boolean {


        Log.d("Passwors", "Password = " + PasswordValidaton(edit_password!!.text.toString()))
        if (EditTextString == "name") {
            if (edit_name!!.text.toString().trim { it <= ' ' }.length == 0) {
                //layout_info_panel.setVisibility(View.GONE);
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_name),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_name!!.requestFocus()
                return false
            } else if (edit_name!!.text.toString().trim { it <= ' ' }.length < 4) {
                //layout_info_panel.setVisibility(View.GONE);
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_min_name),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_name!!.requestFocus()
                return false
            }
        } else if (EditTextString == "username") {
            if (edit_username!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_username),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_username!!.requestFocus()
                return false
            } else if (edit_username!!.text.toString().trim { it <= ' ' }.length < 4) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_min_isername),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_username!!.requestFocus()
                return false
            }
        } else if (EditTextString == "password") {
            if (!PasswordValidaton(edit_password!!.text.toString())) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.password_valid),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_password!!.requestFocus()
                return false
            } else if (edit_password!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_password),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_password!!.requestFocus()
                return false
            } else if (edit_password!!.text.toString().trim { it <= ' ' }.length < 6 || edit_password!!.text.toString().trim { it <= ' ' }.length > 32) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.password_length),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_password!!.requestFocus()
                return false
            }
        } else if (EditTextString == "confirm_password") {
            if (edit_com_password!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_confirm_password),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_com_password!!.requestFocus()
                return false
            } else if (edit_password!!.text.toString() != edit_com_password!!.text.toString()) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.password_confirm),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_com_password!!.requestFocus()
                return false
            }
        } else if (EditTextString == "mobile") {
            if (edit_mobile!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_mobile),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_mobile!!.requestFocus()
                return false
            }
        } else if (EditTextString == "email") {
            if (edit_email!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_email),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_email!!.requestFocus()
                return false
            } else if (edit_email!!.text.toString().trim { it <= ' ' }.length != 0 && !isValidEmail(
                    edit_email!!.text.toString().trim { it <= ' ' })
            ) {
                showMKPanelError(
                    this@SignUpActivity,
                    resources.getString(R.string.please_enter_valid_email),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                edit_email!!.requestFocus()
                return false
            }
        }
        return true
    }

    fun ValidationRegister(): Boolean {


        Log.d("Passwors", "Password = " + PasswordValidaton(edit_password!!.text.toString()))
        if (edit_name!!.text.toString().trim { it <= ' ' }.length == 0) {
            //layout_info_panel.setVisibility(View.GONE);
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_name),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_name!!.requestFocus()
            return false
        } else if (edit_name!!.text.toString().trim { it <= ' ' }.length < 4) {
            //layout_info_panel.setVisibility(View.GONE);
            LayoutEnimation(
                R.color.dialog_error_color,
                resources.getString(R.string.please_min_name)
            )
            edit_name!!.requestFocus()
            return false
        } else if (edit_username!!.text.toString().trim { it <= ' ' }.length == 0) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_username),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_username!!.requestFocus()
            return false
        } else if (edit_username!!.text.toString().trim { it <= ' ' }.length < 4) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_min_isername),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_username!!.requestFocus()
            return false
        } else if (edit_password!!.text.toString().trim { it <= ' ' }.length == 0) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_password),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_password!!.requestFocus()
            return false
        } else if (!PasswordValidaton(edit_password!!.text.toString())) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.password_valid),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_password!!.requestFocus()
            return false
        } else if (edit_password!!.text.toString().trim { it <= ' ' }.length < 6 || edit_password!!.text.toString().trim { it <= ' ' }.length > 32) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.password_length),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_password!!.requestFocus()
            return false
        } else if (edit_com_password!!.text.toString().trim { it <= ' ' }.length == 0) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_confirm_password),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_com_password!!.requestFocus()
            return false
        } else if (edit_password!!.text.toString() != edit_com_password!!.text.toString()) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.password_confirm),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_com_password!!.requestFocus()
            return false
        } else if (edit_mobile!!.text.toString().trim { it <= ' ' }.length == 0) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_mobile),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_mobile!!.requestFocus()
            return false
        } else if (edit_email!!.text.toString().trim { it <= ' ' }.length == 0) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_email),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_email!!.requestFocus()
            return false
        } else if (edit_email!!.text.toString().trim { it <= ' ' }.length != 0 && !isValidEmail(
                edit_email!!.text.toString().trim { it <= ' ' })
        ) {
            showMKPanelError(
                this@SignUpActivity,
                resources.getString(R.string.please_enter_valid_email),
                rlMainView,
                tvTitle,
                regularRoboto
            )
            edit_email!!.requestFocus()
            return false
        }

        return true
    }

    fun PasswordValidaton(password: String): Boolean {
        val hasLetter = letter.matcher(password)
        val hasDigit = digit.matcher(password)
        //Matcher hasSpecial = special.matcher(password);

        return hasLetter.find() && hasDigit.find()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestCode = ", "requestCode = $requestCode==$resultCode==$data")
        if (requestCode == REQUEST_CAMERA) {
            CreateUserImage(mCapturedImageURI)
        } else if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                val selImagePath = getPath(data.data)
                mCapturedImageURI = Uri.parse(selImagePath)
                CreateUserImage(mCapturedImageURI)
            }
        }

    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } else
            return null
    }

    fun CreateUserImage(imagePath: Uri?) {
        try {
            Log.d("imagePath", "imagePath = " + imagePath!!)
            //Bitmap bitmap = BitmapFactory.decodeStream(is,null,o2);
            val file = File(imagePath.path!!)
            val exif = ExifInterface(file.path)
            val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation =
                if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL
            var rotationAngle = 0
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270

            val bitmap = resizeBitMapImage1(imagePath.path, 200, 200)

            val RotateBitmap = RotateBitmap(bitmap, rotationAngle.toFloat())

            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
            val currentDateandTime = sdf.format(Date())
            val myFinalImagePath = saveToInternalSorage(RotateBitmap, currentDateandTime)
            Log.d("imagePath1", "imagePath1 = $myFinalImagePath")

            userImage = File(myFinalImagePath)

            Picasso.with(this@SignUpActivity).load(userImage).transform(CircleTransform())
                .into(img_add_image)

        } catch (es: Exception) {
            es.printStackTrace()
            println("==== exceptin in setimage : $es")
        }

    }

    private fun saveToInternalSorage(bitmapImage: Bitmap, imageName: String): String {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        Log.d("imagePath2", "imagePath2 = $directory")
        // Create imageDir
        val mypath = File(directory, "$imageName.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return directory.absolutePath + "/" + imageName + ".jpg"
    }

    fun LayoutEnimation(color: Int, message: String) {

        layout_info_panel.setBackgroundResource(color)
        if (layout_info_panel.visibility == View.GONE) {
            layout_info_panel.visibility = View.VISIBLE
        }
        subtitle.text = message
        val slideUpAnimation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_up_map
        )
        layout_info_panel.startAnimation(slideUpAnimation)

        slideUpAnimation.fillAfter = true
        slideUpAnimation.duration = 2000

        slideUpAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                layout_info_panel.alpha = 1f
                if (layout_info_panel.visibility == View.GONE) {
                    layout_info_panel.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animation) {
                animation.cancel()

                Handler().postDelayed({ layout_info_panel.alpha = 0f }, 1000)

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    public override fun onDestroy() {
        super.onDestroy()

        edit_username = null
        edit_name = null
        edit_mobile = null
        edit_email = null
        edit_password = null
        edit_com_password = null
        layout_signup = null
        img_add_image = null
        edit_date_of_birth = null
        spinner_gender = null
    }

    companion object {

        var REQUEST_CAMERA = 1
        var REQUEST_GALLERY = 2

        fun resizeBitMapImage1(filePath: String?, targetWidth: Int, targetHeight: Int): Bitmap? {
            var bitMapImage: Bitmap? = null
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(filePath, options)
                var sampleSize = 0.0
                val scaleByHeight =
                    Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth - targetWidth)
                if (options.outHeight * options.outWidth * 2 >= 1638) {
                    sampleSize =
                        (if (scaleByHeight) options.outHeight / targetHeight else options.outWidth / targetWidth).toDouble()
                    sampleSize =
                        Math.pow(2.0, Math.floor(Math.log(sampleSize) / Math.log(2.0))).toInt()
                            .toDouble()
                }
                options.inJustDecodeBounds = false
                options.inTempStorage = ByteArray(128)
                while (true) {
                    try {
                        options.inSampleSize = sampleSize.toInt()
                        bitMapImage = BitmapFactory.decodeFile(filePath, options)
                        break
                    } catch (ex: Exception) {
                        try {
                            sampleSize = sampleSize * 2
                        } catch (ex1: Exception) {

                        }

                    }

                }
            } catch (ex: Exception) {

            }

            return bitMapImage
        }

        fun RotateBitmap(source: Bitmap?, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source!!, 0, 0, source.width, source.height, matrix, true)
        }


        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}
