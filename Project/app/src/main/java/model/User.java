package model;

import com.google.gson.annotations.SerializedName;
import java.util.regex.Pattern;
import exceptions.InvalidDataException;

public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @SerializedName("_id")
    private String id;
    private String name;
    private String email;
    private String passwordHash;

    public User() {}

    public User(String name, String email, String passwordHash) {
        setName(name);
        setEmail(email);
        setPasswordHash(passwordHash);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isNameValid(name)) {
            this.name = name;
        } else {
            throw new InvalidDataException("User: nome inválido \"" + name + "\"");
        }
    }

    private boolean isNameValid(String name) {
        return name != null && name.trim().length() >= 3;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (isEmailValid(email)) {
            this.email = email.trim().toLowerCase();
        } else {
            throw new InvalidDataException("User: email inválido \"" + email + "\"");
        }
    }

    private boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new InvalidDataException("User: passwordHash vazio");
        }
        this.passwordHash = passwordHash;
    }

    public boolean hasId() {
        return id != null && !id.trim().isEmpty();
    }

    public boolean isEqualTo(User obj) {
        if (obj == null) {
            return false;
        }
        if (this.id != null && obj.id != null) {
            return this.id.equals(obj.id);
        }
        return this.email != null && this.email.equals(obj.email);
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }
}
