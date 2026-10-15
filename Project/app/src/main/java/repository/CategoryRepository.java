package repository;

import java.util.ArrayList;
import java.util.List;

import api.ApiCallback;
import api.ApiClient;
import model.Category;
import retrofit2.Call;

public class CategoryRepository extends BaseRepository {
    public static final String[] DEFAULT_NAMES = {
            "Restaurante", "Monumento", "Natureza", "Alojamento", "Praia", "Museu", "Outro"
    };

    public static final String[] DEFAULT_ICONS = {
            "restaurant", "monument", "nature", "lodging", "beach", "museum", "other"
    };

    public void getAll(ApiCallback<List<Category>> callback) {
        Call<List<Category>> call = ApiClient.restDb().getCategories("{}");
        enqueue(call, callback);
    }

    public void seedDefaultsIfEmpty() {
        Call<List<Category>> call = ApiClient.restDb().getCategories("{}");
        enqueue(call, new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    return;
                }
                List<Category> defaults = new ArrayList<>();
                for (int i = 0; i < DEFAULT_NAMES.length; i++) {
                    defaults.add(new Category(DEFAULT_NAMES[i], DEFAULT_ICONS[i]));
                }
                ApiClient.restDb().createCategories(defaults).enqueue(
                        new retrofit2.Callback<List<Category>>() {
                            @Override
                            public void onResponse(Call<List<Category>> call,
                                                   retrofit2.Response<List<Category>> response) {
                            }
                            @Override
                            public void onFailure(Call<List<Category>> call, Throwable throwable) {
                                // idem
                            }
                        });
            }
            @Override
            public void onError(String message) {
            }
        });
    }

    public static List<Category> defaults() {
        List<Category> list = new ArrayList<>();
        for (int i = 0; i < DEFAULT_NAMES.length; i++) {
            Category category = new Category(DEFAULT_NAMES[i], DEFAULT_ICONS[i]);
            category.setId("default_cat_" + i);
            list.add(category);
        }
        return list;
    }
}
