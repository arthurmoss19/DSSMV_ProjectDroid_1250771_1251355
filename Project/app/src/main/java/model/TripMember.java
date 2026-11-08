package model;

import com.google.gson.annotations.SerializedName;
import exceptions.InvalidDataException;


public class TripMember {
    public enum Role {
        OWNER,
        GUEST
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @SerializedName("_id")
    private String id;
    private String userId;
    private String tripId;
    private Role role;
    private Status status = Status.PENDING;

    public TripMember() {
    }
    public TripMember(String userId, String tripId, Role role) {
        setUserId(userId);
        setTripId(tripId);
        setRole(role);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidDataException("TripMember: userId = NULL");
        }
        this.userId = userId;
    }
    public String getTripId() {
        return tripId;
    }
    public void setTripId(String tripId) {
        if (tripId == null || tripId.trim().isEmpty()) {
            throw new InvalidDataException("TripMember: tripId = NULL");
        }
        this.tripId = tripId;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        if (role == null) {
            throw new InvalidDataException("TripMember: role = NULL");
        }
        this.role = role;
    }
    public boolean isOwner() {
        return role == Role.OWNER;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status == null ? Status.PENDING : status;
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    @Override
    public String toString() {
        return "TripMember{" + userId + " -> " + tripId + " (" + role + ")}";
    }
}
