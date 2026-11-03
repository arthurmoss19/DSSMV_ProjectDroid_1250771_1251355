package api;

import api.dto.ImgBbResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ImgBbApi {
    @Multipart
    @POST("1/upload")
    Call<ImgBbResponse> upload(@Query("key") String key, @Part MultipartBody.Part image);
}
