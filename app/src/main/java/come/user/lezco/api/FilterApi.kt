package come.user.lezco.api

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import come.user.lezco.model.Driver
import come.user.lezco.model.NeareastDriver
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FilterApi {

    companion object
    {
        val gson=GsonBuilder().setLenient().create()
        operator fun invoke():FilterApi
        {
            return Retrofit.Builder()
                .baseUrl("https://digitaldwarka.com/taxiapp/api/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(FilterApi::class.java)
        }
    }

    @POST("api-filter.php")
    fun getFilter(@Body body: Driver):Call<ResponseBody>

    @FormUrlEncoded
    @POST("nearest-driver.php")
    fun getNearestDriver(@Field("user_city") user_city: String,
                         @Field ("user_lat") user_lat:Double,
                         @Field("user_lon") user_lon:Double,
                         @Field("cab_id") cab_id:Int):Call<ResponseBody>
}