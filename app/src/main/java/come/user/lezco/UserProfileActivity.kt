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
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.utils.CircleTransform
import come.user.lezco.utils.Common
import come.user.lezco.utils.Common.Companion.showMKPanelError
import come.user.lezco.utils.Url
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class UserProfileActivity : AppCompatActivity() {

    internal var txt_profile: TextView? = null
    lateinit var txt_name: TextView
    lateinit var edt_name: EditText
    internal var txt_user_name: TextView? = null
    internal var edt_user_name: EditText? = null
    internal var txt_user_mobile: TextView? = null
    internal var edt_mobile: EditText? = null
    internal var txt_user_email: TextView? = null
    internal var edt_email: EditText? = null
    internal var layout_back_arrow: RelativeLayout? = null
    lateinit var img_add_image: ImageView
    lateinit var txt_date_of_birth: TextView
    lateinit var edt_date_of_birth_val: EditText
    lateinit var spinner_gender: Spinner
    internal var layout_save: RelativeLayout? = null
    internal var txt_save: TextView? = null

    internal var genderString = "gender"

    lateinit var OpenSans_Regular: Typeface
    lateinit var OpenSans_Bold: Typeface
    lateinit var Roboto_Regular: Typeface
    lateinit var Roboto_Medium: Typeface
    lateinit var Roboto_Bold: Typeface
    lateinit var userPref: SharedPreferences

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    lateinit var OpenCameraDialog: Dialog
    private var mCapturedImageURI: Uri? = null
    internal var userImage: File? = null

    lateinit var myCalendar: Calendar
    lateinit var date: DatePickerDialog.OnDateSetListener

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
        setContentView(R.layout.activity_user_profile)

        window.setBackgroundDrawableResource(R.drawable.background)

        //Error Alert
        rlMainView = findViewById(R.id.rlMainView) as RelativeLayout
        tvTitle = findViewById(R.id.tvTitle) as TextView

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, resources.getDimension(R.dimen.height_50).toInt(), 0, 0)
        rlMainView.layoutParams = params

        txt_profile = findViewById(R.id.txt_profile) as TextView?
        txt_name = findViewById(R.id.txt_name) as TextView
        edt_name = findViewById(R.id.edt_name) as EditText
        txt_user_name = findViewById(R.id.txt_user_name) as TextView?
        edt_user_name = findViewById(R.id.edt_user_name) as EditText?
        txt_user_mobile = findViewById(R.id.txt_user_mobile) as TextView?
        edt_mobile = findViewById(R.id.edt_mobile) as EditText?
        txt_user_email = findViewById(R.id.txt_user_email) as TextView?
        edt_email = findViewById(R.id.edt_email) as EditText?
        layout_back_arrow = findViewById(R.id.layout_back_arrow) as RelativeLayout?
        img_add_image = findViewById(R.id.img_add_image) as ImageView
        txt_date_of_birth = findViewById(R.id.txt_date_of_birth) as TextView
        edt_date_of_birth_val = findViewById(R.id.edt_date_of_birth_val) as EditText
        spinner_gender = findViewById(R.id.spinner_gender) as Spinner
        layout_save = findViewById(R.id.layout_save) as RelativeLayout?
        txt_save = findViewById(R.id.txt_save) as TextView?

        ProgressDialog =
            Dialog(this@UserProfileActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        userPref = PreferenceManager.getDefaultSharedPreferences(this@UserProfileActivity)

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        Roboto_Regular = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Medium = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        txt_profile!!.typeface = OpenSans_Bold
        txt_save!!.typeface = Roboto_Regular

        txt_name.typeface = Roboto_Regular
        edt_name.typeface = Roboto_Regular
        txt_user_name!!.typeface = Roboto_Regular
        edt_user_name!!.typeface = Roboto_Regular
        txt_user_mobile!!.typeface = Roboto_Regular
        edt_mobile!!.typeface = Roboto_Regular
        txt_user_email!!.typeface = Roboto_Regular

        edt_date_of_birth_val.typeface = Roboto_Regular
        txt_date_of_birth.typeface = Roboto_Regular

        edt_email!!.typeface = Roboto_Regular

        edt_user_name!!.setText(userPref.getString("username", ""))
        edt_name.setText(userPref.getString("name", ""))
        edt_mobile!!.setText(userPref.getString("mobile", ""))
        edt_email!!.setText(userPref.getString("email", ""))
        edt_date_of_birth_val.setText(userPref.getString("date_of_birth", ""))

        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        val facebook_id = userPref.getString("facebook_id", "")
        Log.d("facebook_id", "facebook_id = " + facebook_id!!)
        if (facebook_id != null && facebook_id != "" && userPref.getString("userImage", "") == "") {
            val facebookImage = Url.FacebookImgUrl + facebook_id + "/picture?type=large"
            Log.d("facebookImage", "facebookImage = $facebookImage")
            Picasso.with(this@UserProfileActivity)
                .load(facebookImage)
                .placeholder(R.drawable.avatar_placeholder)
                .resize(200, 200)
                .transform(CircleTransform())
                .into(img_add_image)
        } else {
            Picasso.with(this@UserProfileActivity)
                .load(Uri.parse(Url.userImageUrl + userPref.getString("userImage", "")!!))
                .placeholder(R.drawable.mail_defoult)
                .transform(CircleTransform())
                .into(img_add_image)
        }

        layout_save!!.setOnClickListener(View.OnClickListener {
            if (edt_name.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@UserProfileActivity,
                    resources.getString(R.string.please_enter_name),
                    rlMainView,
                    tvTitle,
                    Roboto_Regular
                )
                edt_name.requestFocus()
                return@OnClickListener
            } else if (edt_user_name!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@UserProfileActivity,
                    resources.getString(R.string.please_enter_username),
                    rlMainView,
                    tvTitle,
                    Roboto_Regular
                )
                edt_user_name!!.requestFocus()
                return@OnClickListener
            } else if (edt_mobile!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@UserProfileActivity,
                    resources.getString(R.string.please_enter_mobile),
                    rlMainView,
                    tvTitle,
                    Roboto_Regular
                )
                edt_mobile!!.requestFocus()
                return@OnClickListener
            } else if (edt_email!!.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@UserProfileActivity,
                    resources.getString(R.string.please_enter_email),
                    rlMainView,
                    tvTitle,
                    Roboto_Regular
                )
                edt_email!!.requestFocus()
                return@OnClickListener
            } else if (edt_email!!.text.toString().trim { it <= ' ' }.length != 0 && !isValidEmail(
                    edt_email!!.text.toString().trim { it <= ' ' })
            ) {
                showMKPanelError(
                    this@UserProfileActivity,
                    resources.getString(R.string.please_enter_valid_email),
                    rlMainView,
                    tvTitle,
                    Roboto_Regular
                )
                edt_email!!.requestFocus()
                return@OnClickListener
            }

            ProgressDialog.show()
            cusRotateLoading.start()

            Log.d("user id", "user id = " + userPref.getString("id", "")!!)
            val IonObj = Ion.with(this@UserProfileActivity).load(Url.profileUrl).setTimeout(6000)
            //.setJsonObjectBody(json)
            IonObj.setMultipartParameter("name", edt_name.text.toString().trim { it <= ' ' })
                .setMultipartParameter(
                    "username",
                    edt_user_name!!.text.toString().trim { it <= ' ' })
                .setMultipartParameter("email", edt_email!!.text.toString().trim { it <= ' ' })
                .setMultipartParameter("mobile", edt_mobile!!.text.toString().trim { it <= ' ' })
                .setMultipartParameter("uid", userPref.getString("id", ""))
                .setMultipartParameter("dob", edt_date_of_birth_val.text.toString())
                .setMultipartParameter("gender", genderString)
            Log.d("userImage", "UserProfile userImage = " + userImage!!)
            if (userImage != null) {
                IonObj.setMultipartFile("image", userImage)
            } else
                IonObj.setMultipartParameter("image", "")

            IonObj.setMultipartParameter("isdevice", "1")
                .asJsonObject()
                .setCallback { error, result ->
                    // do stuff with the result or error
                    Log.d("UserProfile result", "UserProfile result = $result==$error")
                    ProgressDialog.cancel()
                    cusRotateLoading.stop()
                    if (error == null) {
                        try {
                            val resObj = JSONObject(result.toString())
                            if (resObj.getString("status") == "success") {

                                val userAry = JSONArray(resObj.getString("user_detail"))
                                val userDetilObj = userAry.getJSONObject(0)

                                val id = userPref.edit()
                                id.putString("id", userDetilObj.getString("id").toString())
                                id.commit()

                                val name = userPref.edit()
                                name.putString("name", userDetilObj.getString("name").toString())
                                name.commit()

                                val username = userPref.edit()
                                username.putString(
                                    "username",
                                    userDetilObj.getString("username").toString()
                                )
                                username.commit()

                                val mobile = userPref.edit()
                                mobile.putString(
                                    "mobile",
                                    userDetilObj.getString("mobile").toString()
                                )
                                mobile.commit()

                                val email = userPref.edit()
                                email.putString("email", userDetilObj.getString("email").toString())
                                email.commit()

                                val isLogin = userPref.edit()
                                isLogin.putString("isLogin", "1")
                                isLogin.commit()

                                val userImage = userPref.edit()
                                userImage.putString(
                                    "userImage",
                                    userDetilObj.getString("image").toString()
                                )
                                userImage.commit()

                                val dob = userPref.edit()
                                dob.putString(
                                    "date_of_birth",
                                    userDetilObj.getString("dob").toString()
                                )
                                dob.commit()

                                val gender = userPref.edit()
                                gender.putString("gender", genderString)
                                gender.commit()

                                ProgressDialog.cancel()
                                cusRotateLoading.stop()

                                Common.showMkSucess(
                                    this@UserProfileActivity,
                                    resources.getString(R.string.profile_update_sucess),
                                    "yes"
                                )

                                Handler().postDelayed({ finish() }, 2000)

                            } else if (resObj.getString("status") == "false") {
                                Common.user_InActive = 1
                                Common.InActive_msg = resObj.getString("message")

                                val editor = userPref.edit()
                                editor.clear()
                                editor.commit()

                                val logInt = Intent(
                                    this@UserProfileActivity,
                                    LoginOptionActivity::class.java
                                )
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(logInt)
                            } else if (resObj.getString("status") == "failed") {
                                Common.showMkError(
                                    this@UserProfileActivity,
                                    resObj.getString("error code")
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        Common.ShowHttpErrorMessage(this@UserProfileActivity, error.message)
                    }
                }
        })

        layout_back_arrow!!.setOnClickListener { finish() }

        img_add_image.setOnClickListener {
            OpenCameraDialog =
                Dialog(this@UserProfileActivity, android.R.style.Theme_Translucent_NoTitleBar)
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

        val list = ArrayList<String>()
        list.add("Gender")
        list.add("Male")
        list.add("Female")

        val dataAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, list
        )
        dataAdapter.setDropDownViewResource(R.layout.gender_spinner_layout)
        spinner_gender.adapter = dataAdapter
        for (si in list.indices) {
            if (userPref.getString("gender", "") == list[si]) {
                spinner_gender.setSelection(si)
            }
        }
        spinner_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.white))
                (parent.getChildAt(0) as TextView).textSize = 16f
                (parent.getChildAt(0) as TextView).typeface = OpenSans_Regular
                (parent.getChildAt(0) as TextView).gravity = Gravity.RIGHT
                genderString = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        myCalendar = Calendar.getInstance()


        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

            var catalog_outdated = 0
            try {
                val strDate = sdf.parse(sdf.format(myCalendar.time))
                if (System.currentTimeMillis() < strDate.time) {
                    catalog_outdated = 1
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            Log.d("catalog_outdated", "catalog_outdated = $catalog_outdated")
            if (catalog_outdated == 0)
                edt_date_of_birth_val.setText(sdf.format(myCalendar.time))
            else {
                edt_date_of_birth_val.setText(userPref.getString("date_of_birth", ""))
                Common.showMkError(
                    this@UserProfileActivity,
                    resources.getString(R.string.invalid_birth_date)
                )
            }
        }

        edt_date_of_birth_val.setOnTouchListener { v, event -> false }

        edt_date_of_birth_val.inputType = InputType.TYPE_NULL
        edt_date_of_birth_val.requestFocus()

        edt_date_of_birth_val.setOnClickListener {
            val dpd = DatePickerDialog(
                this@UserProfileActivity,
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

        Common.ValidationGone(this@UserProfileActivity, rlMainView, edt_name)
        Common.ValidationGone(this@UserProfileActivity, rlMainView, edt_user_name!!)
        Common.ValidationGone(this@UserProfileActivity, rlMainView, edt_mobile!!)
        Common.ValidationGone(this@UserProfileActivity, rlMainView, edt_email!!)
        Common.ValidationGone(this@UserProfileActivity, rlMainView, edt_date_of_birth_val)

    }

    public override fun onDestroy() {
        super.onDestroy()

        txt_profile = null
        txt_user_name = null
        edt_user_name = null
        txt_user_mobile = null
        edt_mobile = null
        txt_user_email = null
        edt_email = null
        layout_save = null
        txt_save = null
        layout_back_arrow = null
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

            Picasso.with(this@UserProfileActivity).load(userImage).transform(CircleTransform())
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

    companion object {
        var REQUEST_CAMERA = 1
        var REQUEST_GALLERY = 2

        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

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
    }
}
