package model;

import com.google.gson.annotations.SerializedName;
import exceptions.InvalidDataException;

public class Photo {
    @SerializedName("_id")
    private String id;
    private String placeId;
    private String mediaId;
    private String caption;
    private String takenAt;

    public Photo() {
    }
    public Photo(String placeId, String mediaId, String caption, String takenAt) {
        setPlaceId(placeId);
        setMediaId(mediaId);
        setCaption(caption);
        this.takenAt = takenAt;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        if (placeId == null || placeId.trim().isEmpty()) {
            throw new InvalidDataException("Photo: placeId = NULL");
        }
        this.placeId = placeId;
    }

    public String getMediaId() {
        return mediaId;
    }
    public void setMediaId(String mediaId) {
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new InvalidDataException("Photo: mediaId = NULL");
        }
        this.mediaId = mediaId;
    }
    public String getCaption() {
        return caption == null ? "" : caption;
    }
    public void setCaption(String caption) {
        this.caption = caption == null ? "" : caption.trim();
    }
    public String getTakenAt() {
        return takenAt;
    }
    public void setTakenAt(String takenAt) {
        this.takenAt = takenAt;
    }

    public boolean hasId() {
        return id != null && !id.trim().isEmpty();
    }

    public boolean isEqualTo(Photo obj) {
        if (obj == null) {
            return false;
        }
        if (this.id != null && obj.id != null) {
            return this.id.equals(obj.id);
        }
        return mediaId != null && mediaId.equals(obj.mediaId);
    }

    @Override
    public String toString() {
        return "Photo{" + mediaId + "}";
    }
}
