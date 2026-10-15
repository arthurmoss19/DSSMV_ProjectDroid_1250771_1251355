package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import com.triptrail.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final Gson GSON = new GsonBuilder().create();

    private static RestDbApi restDbApi;

    private ApiClient() {
    }

    public static synchronized RestDbApi restDb() {
        if (restDbApi == null) {
            restDbApi = build(BuildConfig.RESTDB_URL).create(RestDbApi.class);
        }
        return restDbApi;
    }
    public static Gson gson() {
        return GSON;
    }
    public static boolean isKeyMissing(String key) {
        return key == null || key.trim().isEmpty() || "X".equalsIgnoreCase(key.trim());
    }

    public static synchronized void reset() {
        restDbApi = null;
    }
    private static Retrofit build(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .build();
    }
    private static OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        builder.addInterceptor(chain -> {
            okhttp3.Request original = chain.request();
            okhttp3.Request.Builder request = original.newBuilder();
            if (original.url().host().endsWith("restdb.io")) {
                request.addHeader("x-apikey", BuildConfig.RESTDB_KEY);
            }
            return chain.proceed(request.build());
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(logging);
        }
        return builder.build();
    }
}