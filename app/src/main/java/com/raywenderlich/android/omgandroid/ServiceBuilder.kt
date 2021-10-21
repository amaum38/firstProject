import com.raywenderlich.android.omgandroid.Books
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//test comment
interface Endpoints {
    @GET("search.json")
    fun getBooks(@Query("q") key: String): Call<Books>
}

fun provideGson(): GsonConverterFactory {
    return GsonConverterFactory.create()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl("http://openlibrary.org/")
        .client(okHttpClient)
        .addConverterFactory(provideGson())
        .build()
}

fun provideApi(retrofit: Retrofit): Endpoints {
    return retrofit.create(Endpoints::class.java)
}

//module used by koin for injection
val networkModule = module {
    single { provideGson() }
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideApi(get())}
}