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
    public Error getError() {
        return error;
    }

    public boolean hasError() {
        return error != null && error.getCode() != null;
    }

    public static class Result {
        private String label;
        private String name;
        private String number;
        private String street;
        private String locality;
        private String region;
        private String country;
        private String country_code;
        private String postal_code;

        public String getLabel() {
            return label;
        }

        public String getLocality() {
            return locality;
        }

        public String getRegion() {
            return region;
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
        private String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}

