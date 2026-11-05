package api;

import org.junit.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import okhttp3.MultipartBody;
import model.Trip;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApiContractTest {

    @Test
    public void restDbUsersUsesExpectedPathAndQuery() throws Exception {
        Method method = RestDbApi.class.getMethod("getUsers", String.class);

        assertEquals("rest/users", method.getAnnotation(GET.class).value());

        Query query = null;
        for (Annotation annotation : method.getParameterAnnotations()[0]) {
            if (annotation instanceof Query) {
                query = (Query) annotation;
            }
        }
        assertNotNull(query);
        assertEquals("q", query.value());
    }

    @Test
    public void restDbTripsUsesExpectedPaths() throws Exception {
        Method create = RestDbApi.class.getMethod("createTrip", Trip.class);
        assertEquals("rest/trips", create.getAnnotation(POST.class).value());

        Method delete = RestDbApi.class.getMethod("deleteTrip", String.class);
        assertEquals("rest/trips/{id}", delete.getAnnotation(DELETE.class).value());

        Method get = RestDbApi.class.getMethod("getTrip", String.class);
        assertEquals("rest/trips/{id}", get.getAnnotation(GET.class).value());

        Path path = null;
        for (Annotation annotation : get.getParameterAnnotations()[0]) {
            if (annotation instanceof Path) {
                path = (Path) annotation;
            }
        }
        assertNotNull(path);
        assertEquals("id", path.value());
    }

    @Test
    public void restDbPhotosUsesExpectedPath() throws Exception {
        Method method = RestDbApi.class.getMethod("getPhotos", String.class, String.class);
        assertEquals("rest/photos", method.getAnnotation(GET.class).value());
    }

    @Test
    public void uploadUsesMultipartMediaEndpoint() throws Exception {
        Method method = RestDbApi.class.getMethod("uploadMedia", MultipartBody.Part.class);

        assertNotNull(method.getAnnotation(Multipart.class));
        assertEquals("media", method.getAnnotation(POST.class).value());
    }

    @Test
    public void geocodingUsesReverseEndpoint() throws Exception {
        Method method = GeocodingApi.class.getMethod(
                "reverse", String.class, String.class, int.class);

        assertEquals("v1/reverse", method.getAnnotation(GET.class).value());

        Query accessKey = null;
        for (Annotation annotation : method.getParameterAnnotations()[0]) {
            if (annotation instanceof Query) {
                accessKey = (Query) annotation;
            }
        }
        assertNotNull(accessKey);
        assertEquals("access_key", accessKey.value());
    }
}