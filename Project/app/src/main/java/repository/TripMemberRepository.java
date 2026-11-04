package repository;

import java.util.List;

import api.ApiCallback;
import api.ApiClient;
import model.TripMember;
import model.User;
import retrofit2.Call;
import java.util.HashMap;
import java.util.Map;

public class TripMemberRepository extends BaseRepository {
    private final UserRepository users = new UserRepository();

    public void getMembers(String tripId, ApiCallback<List<TripMember>> callback) {
        Call<List<TripMember>> call = ApiClient.restDb()
                .getMembers(filter("tripId", tripId));
        enqueue(call, callback);
    }

    public void invite(String tripId, String email, ApiCallback<TripMember> callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError("Indique o email do participante.");
            return;
        }
        users.findByEmail(email.trim(), new ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                getMembers(tripId, new ApiCallback<List<TripMember>>() {
                    @Override
                    public void onSuccess(List<TripMember> members) {
                        if (members != null) {
                            for (TripMember member : members) {
                                if (user.getId() != null && user.getId().equals(member.getUserId())) {
                                    callback.onError("Esse utilizador já participa nesta viagem.");
                                    return;
                                }
                            }
                        }
                        TripMember member = new TripMember(user.getId(), tripId, TripMember.Role.GUEST);
                        member.setStatus(TripMember.Status.PENDING);
                        Call<TripMember> call = ApiClient.restDb().createMember(member);
                        enqueue(call, callback);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void remove(String memberId, ApiCallback<Void> callback) {
        Call<okhttp3.ResponseBody> call = ApiClient.restDb().deleteMember(memberId);
        enqueueVoid(call, callback);
    }

    public void getMemberships(String userId, ApiCallback<List<TripMember>> callback) {
        String query = "{\"userId\":\"" + userId + "\",\"status\":\"ACCEPTED\"}";
        Call<List<TripMember>> call = ApiClient.restDb().getMembers(query);
        enqueue(call, callback);
    }

    public void getPendingInvites(String userId, ApiCallback<List<TripMember>> callback) {
        String query = "{\"userId\":\"" + userId + "\",\"status\":\"PENDING\"}";
        Call<List<TripMember>> call = ApiClient.restDb().getMembers(query);
        enqueue(call, callback);
    }

    public void accept(String memberId, ApiCallback<TripMember> callback) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("status", TripMember.Status.ACCEPTED.name());
        Call<TripMember> call = ApiClient.restDb().updateMember(memberId, fields);
        enqueue(call, callback);
    }
}
