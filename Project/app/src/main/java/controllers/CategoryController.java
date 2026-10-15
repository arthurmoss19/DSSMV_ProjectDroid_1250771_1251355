package controllers;
import java.util.List;

import api.ApiCallback;
import model.Category;
import repository.CategoryRepository;
public class CategoryController {
    private final CategoryRepository categories = new CategoryRepository();
    public void getAll(ApiCallback<List<Category>> callback) {
        categories.getAll(callback);
    }
    public void seedDefaultsIfEmpty() {
        categories.seedDefaultsIfEmpty();
    }
    public static List<Category> defaults() {
        return CategoryRepository.defaults();
    }
}