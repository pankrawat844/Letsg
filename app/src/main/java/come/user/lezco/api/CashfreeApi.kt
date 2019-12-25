package come.user.lezco.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import come.user.lezco.utils.Url
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface CashfreeApi {

    companion object {
        val gson= GsonBuilder()
            .setLenient()
            .create()
        operator fun invoke():CashfreeApi
        {
            return Retrofit.Builder()
                .baseUrl(Url.cashfree_test_token)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(CashfreeApi::class.java)
        }
    }
    @Headers(
        "Content-Type: application/json",
        "x-client-id: 8602ff415d26ae4a703315422068",
        "x-client-secret: da263f8df85578d21eac48501a64925a011fa9b7"
    )
    @POST("order")
    fun getToken(@Body body:RequestBody):Call<ResponseBody>
}