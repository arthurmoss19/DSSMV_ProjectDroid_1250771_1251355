package api;

import java.util.List;
import okhttp3.ResponseBody;

import model.Category;
import model.Trip;
import model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
}



