package api.dto;

import java.util.List;

public class GeocodingResponse {
    private List<Result> data;
    private Error error;

    public List<Result> getData() {
        return data;
    }

    public Result first() {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return data.get(0);
    }

    public boolean hasError() {
        return error != null && error.getCode() != null;
    }

    public static class Result {
        private String label;
        private String number;
        private String street;
        private String locality;
        private String country;
        private String country_code;

        public String getLabel() {
            return label;
        }

        public String getLocality() {
            return locality;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryCode() {
            return country_code;
        }

        public String getStreet() {
            return street;
        }

        public String getNumber() {
            return number;
        }
    }

    public static class Error {
        private String code;

        public String getCode() {
            return code;
        }
    }
}

