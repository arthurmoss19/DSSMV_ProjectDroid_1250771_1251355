package controllers;

import java.io.File;
import java.util.List;
import api.ApiCallback;
import model.Photo;
import repository.PhotoRepository;
public class PhotoController {
    private final PhotoRepository photos = new PhotoRepository();
    public void getPhotos(String placeId, ApiCallback<List<Photo>> callback) {
        photos.getPhotos(placeId, callback);
    }

    public void upload(File file, String placeId, String caption, ApiCallback<Photo> callback) {
        photos.upload(file, placeId, caption, callback);
    }
    public void delete(String photoId, ApiCallback<Void> callback) {
        photos.delete(photoId, callback);
    }
    public static String mediaUrl(Photo photo) {
        return PhotoRepository.mediaUrl(photo);
    }
}