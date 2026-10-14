package model;

import com.google.gson.annotations.SerializedName;

import exceptions.InvalidDataException;
import util.DateUtils;

public class Trip {
    @SerializedName("_id")
    private String id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String ownerId;

    private String baseCurrency;

    public Trip() {}

    public Trip(String title, String description, String startDate, String endDate, String ownerId) {
        setTitle(title);
        setDescription(description);
        setStartDate(startDate);
        setEndDate(endDate);
        setOwnerId(ownerId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && title.trim().length() >= 3) {
            this.title = title.trim();
        } else {
            throw new InvalidDataException("Trip: título inválido \"" + title + "\"");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        if (!DateUtils.isValidDate(startDate)) {
            throw new InvalidDataException("Trip: data de início inválida \"" + startDate + "\"");
        }
        this.startDate = startDate;
        if (this.endDate != null) {
            checkRange();
        }
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        if (!DateUtils.isValidDate(endDate)) {
            throw new InvalidDataException("Trip: data de fim inválida \"" + endDate + "\"");
        }
        String previous = this.endDate;
        this.endDate = endDate;
        try {
            checkRange();
        } catch (InvalidDataException e) {
            this.endDate = previous;
            throw e;
        }
    }

    private void checkRange() {
        if (this.startDate == null || this.endDate == null) {
            return;
        }
        if (!DateUtils.isRangeValid(this.startDate, this.endDate)) {
            throw new InvalidDataException("Trip: a data de fim (" + endDate + ") é anterior à data de início (" + startDate + ")");
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new InvalidDataException("Trip: ownerId = NULL");
        }
        this.ownerId = ownerId;
    }

    public String getBaseCurrency() {
        return baseCurrency == null || baseCurrency.trim().isEmpty() ? "EUR" : baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency == null ? "EUR" : baseCurrency.trim();
    }

    public boolean hasId() {
        return id != null && !id.trim().isEmpty();
    }

    public int getDurationInDays() {
        long days = DateUtils.daysBetween(startDate, endDate);
        return (int) Math.max(1, days + 1);
    }

    public boolean isEqualTo(Trip obj) {
        if (obj == null) {
            return false;
        }
        if (this.id != null && obj.id != null) {
            return this.id.equals(obj.id);
        }
        return title != null && title.equals(obj.title);
    }

    public Trip copy() {
        Trip t = new Trip();
        t.id = this.id;
        t.title = this.title;
        t.description = this.description;
        t.startDate = this.startDate;
        t.endDate = this.endDate;
        t.ownerId = this.ownerId;
        t.baseCurrency = this.baseCurrency;
        return t;
    }

    @Override
    public String toString() {
        return title + " (" + startDate + " -> " + endDate + ")";
    }
}
