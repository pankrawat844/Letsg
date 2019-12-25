package come.user.lezco.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SmsApi {


    companion object{
        operator fun invoke():SmsApi
        {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://instantalerts.co/api/web/")
                .build()
                .create(SmsApi::class.java)
        }
    }

    @GET("send")
     fun otp_send(
        @Query("apikey") apikey:String,
        @Query("sender") sender:String,
        @Query("to") mobile_no:String,
        @Query("message") msg:String,
        @Query("format") format:String
    ):Call<ResponseBody>


}