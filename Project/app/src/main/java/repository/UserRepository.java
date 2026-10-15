package repository;

import java.util.List;
import api.ApiCallback;
import api.ApiClient;
import exceptions.DuplicatedDataException;
import exceptions.InvalidDataException;
import model.User;
import util.PasswordUtils;
import retrofit2.Call;
public class UserRepository extends BaseRepository {
    public void login(String email, String password, ApiCallback<User> callback) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            callback.onError("Preencha o email e a palavra-passe.");
            return;
        }
        Call<List<User>> call = ApiClient.restDb()
                .getUsers(filter("email", email.trim().toLowerCase()));

        enqueue(call, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if (users == null || users.isEmpty()) {
                    callback.onError("Não existe nenhuma conta com esse email.");
                    return;
                }
                User user = users.get(0);
                if (!PasswordUtils.matches(password, user.getPasswordHash())) {
                    callback.onError("Palavra-passe incorreta.");
                    return;
                }
                callback.onSuccess(user);
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
    public void register(String name, String email, String password, ApiCallback<User> callback) {
        try {
            User user = new User(name, email, PasswordUtils.hash(password));
            checkPassword(password);

            Call<List<User>> lookup = ApiClient.restDb()
                    .getUsers(filter("email", user.getEmail()));

            enqueue(lookup, new ApiCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    if (users != null && !users.isEmpty()) {
                        callback.onError(new DuplicatedDataException(
                                "Já existe uma conta com o email " + user.getEmail()).what());
                        return;
                    }
                    createUser(user, callback);
                }
                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });
        } catch (InvalidDataException e) {
            callback.onError(e.what());
        }
    }
    public void getAll(ApiCallback<List<User>> callback) {
        Call<List<User>> call = ApiClient.restDb().getUsers("{}");
        enqueue(call, callback);
    }
    public void findByEmail(String email, ApiCallback<User> callback) {
        Call<List<User>> call = ApiClient.restDb()
                .getUsers(filter("email", email.trim().toLowerCase()));

        enqueue(call, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if (users == null || users.isEmpty()) {
                    callback.onError("Não existe nenhum utilizador com o email " + email + ".");
                    return;
                }
                callback.onSuccess(users.get(0));
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
    private void createUser(User user, ApiCallback<User> callback) {
        Call<User> call = ApiClient.restDb().createUser(user);
        enqueue(call, callback);
    }
    private void checkPassword(String password) {
        if (!PasswordUtils.isValid(password)) {
            throw new InvalidDataException("A palavra-passe tem de ter pelo menos 6 caracteres.");
        }
    }
}

