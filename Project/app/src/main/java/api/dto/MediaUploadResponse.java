package api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MediaUploadResponse {
    @SerializedName("_id")
    private String id;

    @SerializedName("ids")
    private List<String> ids;

    public String getId() {
        if (id != null) {
            return id;
        }
        if (ids != null && !ids.isEmpty()) {
            return ids.get(0);
        }
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIds() {
        return ids;
    }
}
