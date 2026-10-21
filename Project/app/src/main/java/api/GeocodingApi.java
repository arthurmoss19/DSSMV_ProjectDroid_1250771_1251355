package api;

import api.dto.GeocodingResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingApi {
    @GET("v1/reverse")
    Call<GeocodingResponse> reverse(
            @Query("access_key") String accessKey,
            @Query("query") String coordinates,
            @Query("limit") int limit);
}
