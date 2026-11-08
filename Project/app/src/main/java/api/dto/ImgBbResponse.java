package api.dto;

public class ImgBbResponse {
    public boolean success;
    public Data data;

    public static class Data {
        public String url;
    }
}