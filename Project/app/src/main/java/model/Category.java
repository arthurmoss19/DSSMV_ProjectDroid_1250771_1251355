package model;

import com.google.gson.annotations.SerializedName;

import exceptions.InvalidDataException;

public class Category {
    @SerializedName("_id")
    private String id;
    private String name;

    private String icon;

    public Category() {}

    public Category(String name, String icon) {
        setName(name);
        this.icon = icon;
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
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Category: nome vazio");
        }
        this.name = name.trim();
    }

    public String getIcon() {
        return icon == null ? "other" : icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isEqualTo(Category obj) {
        if (obj == null) {
            return false;
        }
        if (this.id != null && obj.id != null) {
            return this.id.equals(obj.id);
        }
        return name != null && name.equalsIgnoreCase(obj.name);
    }

    public static Category unknown() {
        return new Category("Outro", "other");
    }

    @Override
    public String toString() {
        return name;
    }
}
