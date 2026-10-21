package repository;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.triptrail.BuildConfig;
import api.ApiCallback;
import api.ApiClient;
import api.dto.MediaUploadResponse;
import model.Photo;
import util.DateUtils;
import retrofit2.Call;

public class PhotoRepository extends BaseRepository {
    public void getPhotos(String placeId, ApiCallback<List<Photo>> callback) {
        Call<List<Photo>> call = ApiClient.restDb()
                .getPhotos(filter("placeId", placeId), orderBy("takenAt", false));
        enqueue(call, callback);
    }

    public void upload(File file, String placeId, String caption, ApiCallback<Photo> callback) {
        if (file == null || !file.exists()) {
            callback.onError("Não foi possível ler o ficheiro da fotografia.");
            return;
        }

        RequestBody body = RequestBody.create(file, MediaType.get("image/jpeg"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);

        Call<MediaUploadResponse> call = ApiClient.restDb().uploadMedia(part);
        enqueue(call, new ApiCallback<MediaUploadResponse>() {
            @Override
            public void onSuccess(MediaUploadResponse response) {
                if (response == null || response.getId() == null) {
                    callback.onError("O servidor não devolveu o identificador da fotografia.");
                    return;
                }
                createPhotoRecord(placeId, response.getId(), caption, callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private void createPhotoRecord(String placeId, String mediaId, String caption,
                                   ApiCallback<Photo> callback) {
        Photo photo = new Photo(placeId, mediaId, caption, DateUtils.now());
        Call<Photo> call = ApiClient.restDb().createPhoto(photo);
        enqueue(call, callback);
    }

    public void delete(String photoId, ApiCallback<Void> callback) {
        Call<okhttp3.ResponseBody> call = ApiClient.restDb().deletePhoto(photoId);
        enqueueVoid(call, callback);
    }

    public void deleteByPlace(String placeId, ApiCallback<Void> callback) {
        Call<List<Photo>> call = ApiClient.restDb().getPhotos(filter("placeId", placeId), null);
        enqueue(call, new ApiCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                deleteNext(photos, 0, callback);
            }

            @Override
            public void onError(String message) {
                callback.onSuccess(null);
            }
        });
    }

    private void deleteNext(List<Photo> photos, int index, ApiCallback<Void> callback) {
        if (photos == null || index >= photos.size()) {
            callback.onSuccess(null);
            return;
        }
        Call<okhttp3.ResponseBody> call = ApiClient.restDb().deletePhoto(photos.get(index).getId());
        enqueueVoid(call, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                deleteNext(photos, index + 1, callback);
            }

            @Override
            public void onError(String message) {
                deleteNext(photos, index + 1, callback);
            }
        });
    }

    public static String mediaUrl(Photo photo) {
        if (photo == null || photo.getMediaId() == null) {
            return null;
        }
        String mediaId = photo.getMediaId();
        if (mediaId.startsWith("http://") || mediaId.startsWith("https://")) {
            return mediaId;
        }
        String base = BuildConfig.RESTDB_URL.endsWith("/")
                ? BuildConfig.RESTDB_URL
                : BuildConfig.RESTDB_URL + "/";
        return base + "media/" + mediaId + "?s=w&apikey=" + BuildConfig.RESTDB_KEY;
    }
}
