package views.places;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.triptrail.R;
import api.ApiCallback;
import controllers.CategoryController;
import controllers.PhotoController;
import controllers.PlaceController;
import model.Category;
import model.Photo;
import model.Place;
import util.Extras;
import util.UiUtils;

public class PlaceDetailActivity extends AppCompatActivity {
    private final PlaceController placeController = new PlaceController();
    private final PhotoController photoController = new PhotoController();
    private final CategoryController categoryController = new CategoryController();
    private Toolbar toolbar;
    private ImageView imagePlacePhoto;
    private TextView textPhotoPlaceholder;
    private TextView textName;
    private TextView textCategory;
    private TextView textAddress;
    private TextView textCountry;
    private TextView textPhotosEmpty;
    private RatingBar rating;
    private PhotoAdapter photoAdapter;
    private String placeId;
    private Place place;
    private List<Category> categories;

    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadPlace();
                }
            });

    public static void start(Context context, String placeId) {
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        intent.putExtra(Extras.PLACE_ID, placeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_details_screen);

        placeId = getIntent().getStringExtra(Extras.PLACE_ID);
        if (placeId == null || placeId.trim().isEmpty()) {
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        imagePlacePhoto = findViewById(R.id.imagePlacePhoto);
        textPhotoPlaceholder = findViewById(R.id.textPhotoPlaceholder);
        textName = findViewById(R.id.textPlaceName);
        textCategory = findViewById(R.id.textPlaceCategory);
        textAddress = findViewById(R.id.textPlaceAddress);
        textCountry = findViewById(R.id.textPlaceCountry);
        textPhotosEmpty = findViewById(R.id.textPhotosEmpty);
        rating = findViewById(R.id.ratingPlace);
        RecyclerView recyclerPhotos = findViewById(R.id.recyclerPhotos);

        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.inflateMenu(R.menu.menu_place_details);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

        photoAdapter = new PhotoAdapter();
        recyclerPhotos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerPhotos.setAdapter(photoAdapter);

        loadCategories();
        loadPlace();
    }

    private boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_place) {
            if (place == null) {
                return true;
            }
            Intent intent = new Intent(this, PlaceFormActivity.class);
            intent.putExtra(Extras.TRIP_ID, place.getTripId());
            intent.putExtra(Extras.PLACE_ID, place.getId());
            editLauncher.launch(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_delete_place) {
            if (place != null) {
                confirmDeletePlace();
            }
            return true;
        }
        return false;
    }

    private void loadCategories() {
        categoryController.getAll(new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categories = data;
                showCategory();
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    private void loadPlace() {
        placeController.getPlace(placeId, new ApiCallback<Place>() {
            @Override
            public void onSuccess(Place data) {
                if (data == null) {
                    finish();
                    return;
                }
                place = data;
                toolbar.setTitle(data.getName());
                textName.setText(data.getName());
                rating.setRating((float) data.getRating());
                textAddress.setText(data.getAddress().isEmpty()
                        ? getString(R.string.place_no_address)
                        : data.getAddress());
                textCountry.setText(data.getCountry());
                showCategory();
                loadPhotos();
            }

            @Override
            public void onError(String message) {
                UiUtils.toast(PlaceDetailActivity.this, message);
                finish();
            }
        });
    }

    private void showCategory() {
        if (place == null || categories == null) {
            return;
        }
        for (Category category : categories) {
            if (category.getId() != null && category.getId().equals(place.getCategoryId())) {
                textCategory.setText(category.getName());
                return;
            }
        }
        textCategory.setText(place.getCategoryId());
    }

    private void loadPhotos() {
        photoController.getPhotos(placeId, new ApiCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                photoAdapter.submitList(photos);
                boolean empty = photos == null || photos.isEmpty();
                textPhotosEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                if (empty) {
                    imagePlacePhoto.setVisibility(View.GONE);
                    textPhotoPlaceholder.setVisibility(View.VISIBLE);
                } else {
                    textPhotoPlaceholder.setVisibility(View.GONE);
                    imagePlacePhoto.setVisibility(View.VISIBLE);
                    Glide.with(PlaceDetailActivity.this)
                            .load(PhotoController.mediaUrl(photos.get(0)))
                            .fitCenter()
                            .placeholder(R.drawable.icon_empty)
                            .error(R.drawable.icon_empty)
                            .into(imagePlacePhoto);
                }
            }

            @Override
            public void onError(String message) {
                UiUtils.toast(PlaceDetailActivity.this, message);
            }
        });
    }

    private void confirmDeletePlace() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage("Quer apagar este local e as suas fotografias?")
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        placeController.delete(placeId, new ApiCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onError(String message) {
                                UiUtils.toast(PlaceDetailActivity.this, message);
                            }
                        }))
                .show();
    }
}