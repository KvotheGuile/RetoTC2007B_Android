package mx.fer.app.api


import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    val BASE_API_URL = "https://"
    val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

    private val gson = GsonBuilder()
        .setDateFormat(DATE_FORMAT)
        .create()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}