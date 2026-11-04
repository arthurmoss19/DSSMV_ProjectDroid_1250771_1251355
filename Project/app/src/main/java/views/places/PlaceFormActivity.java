package views.places;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.File;
import java.util.List;

import com.triptrail.R;
import api.ApiCallback;
import controllers.CategoryController;
import controllers.PhotoController;
import controllers.PlaceController;
import exceptions.InvalidDataException;
import model.Category;
import model.Place;
import util.DateUtils;
import util.Extras;
import util.PermissionHelper;
import util.UiUtils;
import views.camera.CameraActivity;

import android.content.ActivityNotFoundException;
import android.provider.MediaStore;
import java.util.ArrayList;
import util.ImageUtils;

import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.content.res.ColorStateList;

public class PlaceFormActivity extends AppCompatActivity {

    private final PlaceController placeController = new PlaceController();
    private final CategoryController categoryController = new CategoryController();
    private final PhotoController photoController = new PhotoController();

    private Toolbar toolbar;
    private EditText editName;
    private Spinner spinnerCategory;
    private RatingBar rating;
    private TextView textAddress;
    private EditText editLatitude;
    private EditText editLongitude;
    private TextView textPhotoCount;

    private LinearLayout photoPreviews;
    private ProgressBar progress;

    private CategoryAdapter categoryAdapter;
    private FusedLocationProviderClient locationClient;

    private String tripId;
    private String placeId;
    private Place editingPlace;
    private String pendingCategoryId;
    private static final int MAX_PHOTOS = 3;

    private final List<String> pendingPhotoPaths = new ArrayList<>();
    private final List<model.Photo> existingPhotos = new ArrayList<>();
    private int existingPhotoCount = 0;
    private String enrichedAddress = "";
    private String enrichedCountry = "";

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String path = result.getData().getStringExtra(Extras.PHOTO_FILE);
                    if (path != null) {
                        addPendingPhoto(new File(path));
                    }
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    addPendingPhoto(ImageUtils.fromUri(this, result.getData().getData()));
                }
            });

    public static void start(Context context, String tripId, String placeId) {
        Intent intent = new Intent(context, PlaceFormActivity.class);
        intent.putExtra(Extras.TRIP_ID, tripId);
        if (placeId != null) {
            intent.putExtra(Extras.PLACE_ID, placeId);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_place_screen);

        tripId = getIntent().getStringExtra(Extras.TRIP_ID);
        placeId = getIntent().getStringExtra(Extras.PLACE_ID);
        if (tripId == null || tripId.trim().isEmpty()) {
            finish();
            return;
        }

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        toolbar = findViewById(R.id.toolbar);
        editName = findViewById(R.id.editPlaceName);
        spinnerCategory = findViewById(R.id.spinnerPlaceCategory);
        rating = findViewById(R.id.ratingPlace);
        textAddress = findViewById(R.id.textPlaceAddress);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        textPhotoCount = findViewById(R.id.textPhotoCount);
        photoPreviews = findViewById(R.id.photoPreviews);
        progress = findViewById(R.id.progressPlaceForm);

        Button getLocation = findViewById(R.id.buttonGetLocation);
        Button takePhoto = findViewById(R.id.buttonTakePhoto);
        Button save = findViewById(R.id.buttonSavePlace);

        toolbar.setNavigationOnClickListener(view -> finish());
        getLocation.setOnClickListener(view -> requestLocation());
        takePhoto.setOnClickListener(view -> cameraLauncher.launch(CameraActivity.intent(this)));
        Button pickPhoto = findViewById(R.id.buttonPickPhoto);
        pickPhoto.setOnClickListener(view -> openGallery());
        save.setOnClickListener(view -> save());

        categoryAdapter = new CategoryAdapter(this, CategoryController.defaults());
        spinnerCategory.setAdapter(categoryAdapter);
        loadCategories();

        if (placeId == null || placeId.trim().isEmpty()) {
            toolbar.setTitle(R.string.place_form_title_new);
            rating.setRating(3);
        } else {
            toolbar.setTitle(R.string.place_form_title_edit);
            loadPlace();
        }
    }

    private void loadCategories() {
        categoryController.getAll(new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories == null || categories.isEmpty()) {
                    categories = CategoryController.defaults();
                }
                categoryAdapter.setItems(categories);
                selectCategory();
            }

            @Override
            public void onError(String message) {
                categoryAdapter.setItems(CategoryController.defaults());
                selectCategory();
            }
        });
    }

    private void loadPlace() {
        showLoading(true);
        placeController.getPlace(placeId, new ApiCallback<Place>() {
            @Override
            public void onSuccess(Place place) {
                showLoading(false);
                if (place == null) {
                    finish();
                    return;
                }
                editingPlace = place;
                loadPhotoCount();
                editName.setText(place.getName());
                rating.setRating((float) place.getRating());
                editLatitude.setText(String.valueOf(place.getLatitude()));
                editLongitude.setText(String.valueOf(place.getLongitude()));
                enrichedAddress = place.getAddress();
                enrichedCountry = place.getCountry();
                showAddress();
                pendingCategoryId = place.getCategoryId();
                selectCategory();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                UiUtils.toast(PlaceFormActivity.this, message);
                finish();
            }
        });
    }

    private void selectCategory() {
        if (pendingCategoryId != null) {
            spinnerCategory.setSelection(categoryAdapter.indexOf(pendingCategoryId));
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        if (!PermissionHelper.hasLocationPermission(this)) {
            PermissionHelper.requestLocation(this);
            return;
        }
        showLoading(true);
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    showLoading(false);
                    if (location == null) {
                        UiUtils.toast(this, "Não foi possível obter a posição.");
                        return;
                    }
                    editLatitude.setText(String.valueOf(location.getLatitude()));
                    editLongitude.setText(String.valueOf(location.getLongitude()));
                    enrich(location.getLatitude(), location.getLongitude());
                })
                .addOnFailureListener(error -> {
                    showLoading(false);
                    UiUtils.toast(this, "Não foi possível obter a posição.");
                });
    }

    private void enrich(double latitude, double longitude) {
        Category category = (Category) spinnerCategory.getSelectedItem();
        String categoryId = category == null ? null : category.getId();
        String name = text(editName);
        try {
            Place transientPlace = new Place(tripId, name.isEmpty() ? "Local" : name,
                    latitude, longitude);
            transientPlace.setCategoryId(categoryId == null
                    ? CategoryController.defaults().get(0).getId()
                    : categoryId);
            transientPlace.setRating(Math.max(1.0f, rating.getRating()));
            showLoading(true);
            placeController.enrich(transientPlace, new ApiCallback<Place>() {
                @Override
                public void onSuccess(Place place) {
                    showLoading(false);
                    enrichedAddress = place.getAddress();
                    enrichedCountry = place.getCountry();
                    showAddress();
                }

                @Override
                public void onError(String message) {
                    showLoading(false);
                }
            });
        } catch (InvalidDataException e) {
            UiUtils.toast(this, e.what());
        }
    }

    private void showAddress() {
        String country = enrichedCountry == null ? "" : enrichedCountry.trim();
        String address = enrichedAddress == null ? "" : enrichedAddress.trim();
        if (address.isEmpty() && country.isEmpty()) {
            textAddress.setText(R.string.place_form_address_empty);
        } else if (country.isEmpty()) {
            textAddress.setText(address);
        } else if (address.isEmpty()) {
            textAddress.setText(country);
        } else {
            textAddress.setText(address + " · " + country);
        }
    }

    private void save() {
        Category category = (Category) spinnerCategory.getSelectedItem();
        if (category == null || category.getId() == null) {
            UiUtils.toast(this, "Escolha uma categoria.");
            return;
        }
        try {
            Place target = editingPlace == null ? new Place() : editingPlace;
            target.setTripId(tripId);
            target.setName(text(editName));
            target.setCategoryId(category.getId());
            target.setRating(Math.max(1.0f, rating.getRating()));
            target.setLatitude(parse(editLatitude));
            target.setLongitude(parse(editLongitude));
            target.setVisitedAt(DateUtils.today());
            target.setAddress(enrichedAddress);
            target.setCountry(enrichedCountry);

            showLoading(true);
            ApiCallback<Place> callback = new ApiCallback<Place>() {
                @Override
                public void onSuccess(Place place) {
                    uploadPendingPhotos(place, 0);
                }

                @Override
                public void onError(String message) {
                    showLoading(false);
                    UiUtils.toast(PlaceFormActivity.this, message);
                }
            };
            if (editingPlace == null) {
                placeController.createEnriched(target, callback);
            } else {
                placeController.update(target, callback);
            }
        } catch (InvalidDataException e) {
            UiUtils.toast(this, e.what());
        }
    }

    private void uploadPendingPhotos(Place place, int index) {
        if (place == null || !place.hasId() || index >= pendingPhotoPaths.size()) {
            finishWithResult();
            return;
        }
        photoController.upload(new File(pendingPhotoPaths.get(index)), place.getId(), "",
                new ApiCallback<model.Photo>() {
                    @Override
                    public void onSuccess(model.Photo photo) {
                        uploadPendingPhotos(place, index + 1);
                    }

                    @Override
                    public void onError(String message) {
                        UiUtils.toast(PlaceFormActivity.this, message);
                        uploadPendingPhotos(place, index + 1);
                    }
                });
    }

    private void finishWithResult() {
        showLoading(false);
        setResult(RESULT_OK);
        finish();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            galleryLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            UiUtils.toast(this, "Não foi encontrada uma aplicação de galeria.");
        }
    }

    private void addPendingPhoto(File photo) {
        if (photo == null) {
            return;
        }
        if (existingPhotoCount + pendingPhotoPaths.size() >= MAX_PHOTOS) {
            UiUtils.toast(this, getString(R.string.place_form_photos_limit));
            return;
        }
        pendingPhotoPaths.add(photo.getAbsolutePath());
        updatePhotoText();
        refreshPhotoPreviews();
    }

    private void updatePhotoText() {
        int total = existingPhotoCount + pendingPhotoPaths.size();
        if (total == 0) {
            textPhotoCount.setText("");
        } else {
            textPhotoCount.setText(total + " de " + MAX_PHOTOS + " fotografias.");
        }
    }

    private void refreshPhotoPreviews() {
        photoPreviews.removeAllViews();
        for (model.Photo photo : existingPhotos) {
            photoPreviews.addView(preview(PhotoController.mediaUrl(photo),
                    () -> confirmDeleteExistingPhoto(photo)));
        }
        for (int i = 0; i < pendingPhotoPaths.size(); i++) {
            final int index = i;
            photoPreviews.addView(preview(new File(pendingPhotoPaths.get(index)), () -> {
                pendingPhotoPaths.remove(index);
                updatePhotoText();
                refreshPhotoPreviews();
            }));
        }
    }

    private View preview(Object source, Runnable onDelete) {
        FrameLayout box = new FrameLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(80), dp(80));
        params.setMarginEnd(dp(8));
        box.setLayoutParams(params);

        ImageView image = new ImageView(this);
        image.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setContentDescription(getString(R.string.cd_photo));
        Glide.with(this).load(source).fitCenter().into(image);
        box.addView(image);

        ImageView delete = new ImageView(this);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(dp(24), dp(24));
        deleteParams.gravity = Gravity.TOP | Gravity.END;
        delete.setLayoutParams(deleteParams);
        delete.setImageResource(R.drawable.icon_delete);
        delete.setImageTintList(ColorStateList.valueOf(getColor(R.color.tt_error)));
        delete.setContentDescription(getString(R.string.action_delete));
        delete.setOnClickListener(view -> onDelete.run());
        box.addView(delete);

        return box;
    }

    private void confirmDeleteExistingPhoto(model.Photo photo) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage("Quer apagar esta fotografia?")
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        photoController.delete(photo.getId(), new ApiCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                existingPhotos.remove(photo);
                                existingPhotoCount = existingPhotos.size();
                                updatePhotoText();
                                refreshPhotoPreviews();
                            }

                            @Override
                            public void onError(String message) {
                                UiUtils.toast(PlaceFormActivity.this, message);
                            }
                        }))
                .show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void loadPhotoCount() {
        photoController.getPhotos(placeId, new ApiCallback<List<model.Photo>>() {
            @Override
            public void onSuccess(List<model.Photo> photos) {
                existingPhotos.clear();
                if (photos != null) {
                    existingPhotos.addAll(photos);
                }
                existingPhotoCount = existingPhotos.size();
                updatePhotoText();
                refreshPhotoPreviews();
            }

            @Override
            public void onError(String message) {
                updatePhotoText();
            }
        });
    }

    private double parse(EditText field) {
        String value = text(field);
        if (value.isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Coordenada inválida \"" + value + "\"");
        }
    }

    private String text(EditText field) {
        if (field == null || field.getText() == null) {
            return "";
        }
        return field.getText().toString().trim();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.REQUEST_LOCATION
                && PermissionHelper.hasLocationPermission(this)) {
            requestLocation();
        }
    }

    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
