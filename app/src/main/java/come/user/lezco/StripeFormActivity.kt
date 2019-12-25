package come.user.lezco

import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.utils.Common

class StripeFormActivity : AppCompatActivity() {

    lateinit var txt_stripe_form: TextView
    internal var expYear: EditText? = null
    internal var number: EditText? = null
    internal var expMonth: EditText? = null
    internal var cvc: EditText? = null
    internal var save: RelativeLayout? = null
    internal var layout_back_arrow: RelativeLayout? = null

    lateinit var OpenSans_Regular: Typeface
    lateinit var OpenSans_Bold: Typeface

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_form)

        txt_stripe_form = findViewById(R.id.txt_stripe_form) as TextView
        expYear = findViewById(R.id.expYear) as EditText?
        number = findViewById(R.id.number) as EditText?
        expMonth = findViewById(R.id.expMonth) as EditText?
        cvc = findViewById(R.id.cvc) as EditText?
        save = findViewById(R.id.save) as RelativeLayout?
        layout_back_arrow = findViewById(R.id.layout_back_arrow) as RelativeLayout?

        ProgressDialog = Dialog(this@StripeFormActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")

        txt_stripe_form.typeface = OpenSans_Bold

        number!!.typeface = OpenSans_Regular
        expYear!!.typeface = OpenSans_Regular
        expMonth!!.typeface = OpenSans_Regular
        cvc!!.typeface = OpenSans_Regular

        save!!.setOnClickListener(View.OnClickListener {
            if (number!!.text.toString().length == 0) {
                Common.showMkError(this@StripeFormActivity, resources.getString(R.string.please_enter_card_number))
                return@OnClickListener
            } else if (expYear!!.text.toString().length == 0) {
                Common.showMkError(this@StripeFormActivity, resources.getString(R.string.please_enter_year))
                return@OnClickListener
            } else if (expMonth!!.text.toString().length == 0) {
                Common.showMkError(this@StripeFormActivity, resources.getString(R.string.please_enter_month))
                return@OnClickListener
            } else if (cvc!!.text.toString().length == 0) {
                Common.showMkError(this@StripeFormActivity, resources.getString(R.string.please_enter_cvc))
                return@OnClickListener
            }

            ProgressDialog.show()
            cusRotateLoading.start()

            Log.d("Data", "Data = " + expYear!!.text.toString() + "==" + expMonth!!.text.toString())
            val card = Card(number!!.text.toString(),
                    Integer.parseInt(expMonth!!.text.toString()),
                    Integer.parseInt(expYear!!.text.toString()),
                    cvc!!.text.toString())

            //card.setCurrency("USD");

            val validation = card.validateCard()

            if (validation) {

                Stripe().createToken(
                        card,
                        PUBLISHABLE_KEY,
                        object : TokenCallback() {
                            override fun onSuccess(token: Token) {
                                //getTokenList().addToList(token);

                                ProgressDialog.cancel()
                                cusRotateLoading.stop()

                                Log.d("token", "token = " + token.id)
                                val ri = Intent()
                                ri.putExtra("stripe_id", token.id)
                                setResult(2, ri)
                                finish()
                            }

                            override fun onError(error: Exception) {
                                ProgressDialog.cancel()
                                cusRotateLoading.stop()
                                Common.showMkError(this@StripeFormActivity, error.localizedMessage)
                            }
                        })
            } else if (!card.validateNumber()) {
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                Common.showMkError(this@StripeFormActivity, "The card number that you entered is invalid")
            } else if (!card.validateExpiryDate()) {
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                Common.showMkError(this@StripeFormActivity, "The expiration date that you entered is invalid")
            } else if (!card.validateCVC()) {
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                Common.showMkError(this@StripeFormActivity, "The CVC code that you entered is invalid")
            } else {
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                Common.showMkError(this@StripeFormActivity, "The card details that you entered are invalid")
            }
        })

        layout_back_arrow!!.setOnClickListener { finish() }
    }

    public override fun onDestroy() {
        super.onDestroy()

        expYear = null
        number = null
        expMonth = null
        cvc = null
        save = null
        layout_back_arrow = null

    }

    companion object {
        /*Stripe integration variable*/
        val PUBLISHABLE_KEY = "pk_test_6pRNASCoBOKtIshFeQd4XMUh"
    }
}
