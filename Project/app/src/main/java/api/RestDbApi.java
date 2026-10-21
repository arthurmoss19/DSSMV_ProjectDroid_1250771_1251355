package api;

import java.util.List;
import java.util.Map;

import api.dto.MediaUploadResponse;
import model.*;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.*;

public interface RestDbApi {

    @GET("rest/users")
    Call<List<User>> getUsers(@Query("q") String query);

    @POST("rest/users")
    Call<User> createUser(@Body User user);

    @GET("rest/trips")
    Call<List<Trip>> getTrips(@Query("q") String query, @Query("h") String hints);

    @GET("rest/trips/{id}")
    Call<Trip> getTrip(@Path("id") String id);

    @POST("rest/trips")
    Call<Trip> createTrip(@Body Trip trip);

    @PUT("rest/trips/{id}")
    Call<Trip> updateTrip(@Path("id") String id, @Body Trip trip);

    @DELETE("rest/trips/{id}")
    Call<ResponseBody> deleteTrip(@Path("id") String id);

    @GET("rest/categories")
    Call<List<Category>> getCategories(@Query("q") String query);

    @POST("rest/categories")
    Call<List<Category>> createCategories(@Body List<Category> categories);

    @GET("rest/places")
    Call<List<Place>> getPlaces(@Query("q") String query, @Query("h") String hints);

    @GET("rest/places/{id}")
    Call<Place> getPlace(@Path("id") String id);

    @POST("rest/places")
    Call<Place> createPlace(@Body Place place);

    @PATCH("rest/places/{id}")
    Call<Place> updatePlace(@Path("id") String id, @Body Map<String, Object> fields);

    @DELETE("rest/places/{id}")
    Call<ResponseBody> deletePlace(@Path("id") String id);

    @GET("rest/photos")
    Call<List<Photo>> getPhotos(@Query("q") String query, @Query("h") String hints);

    @POST("rest/photos")
    Call<Photo> createPhoto(@Body Photo photo);

    @DELETE("rest/photos/{id}")
    Call<ResponseBody> deletePhoto(@Path("id") String id);

    @Multipart
    @POST("media")
    Call<MediaUploadResponse> uploadMedia(@Part MultipartBody.Part file);
}



