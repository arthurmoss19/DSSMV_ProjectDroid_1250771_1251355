package controllers;

import api.ApiCallback;
import model.User;
import repository.UserRepository;
public class UserController {
    private final UserRepository users = new UserRepository();
    private final CategoryController categories = new CategoryController();
    public void login(String email, String password, ApiCallback<User> callback) {
        users.login(email, password, new ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                categories.seedDefaultsIfEmpty();
                callback.onSuccess(user);
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
    public void register(String name, String email, String password, ApiCallback<User> callback) {
        users.register(name, email, password, callback);
    }
}
