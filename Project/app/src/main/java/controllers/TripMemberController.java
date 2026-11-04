package controllers;

import java.util.List;

import api.ApiCallback;
import model.TripMember;
import repository.TripMemberRepository;

public class TripMemberController {
    private final TripMemberRepository members = new TripMemberRepository();

    public void getMembers(String tripId, ApiCallback<List<TripMember>> callback) {
        members.getMembers(tripId, callback);
    }

    public void invite(String tripId, String email, ApiCallback<TripMember> callback) {
        members.invite(tripId, email, callback);
    }

    public void remove(String memberId, ApiCallback<Void> callback) {
        members.remove(memberId, callback);
    }

    public void getMemberships(String userId, ApiCallback<List<TripMember>> callback) {
        members.getMemberships(userId, callback);
    }

    public void getPendingInvites(String userId, ApiCallback<List<TripMember>> callback) {
        members.getPendingInvites(userId, callback);
    }

    public void accept(String memberId, ApiCallback<TripMember> callback) {
        members.accept(memberId, callback);
    }
}
