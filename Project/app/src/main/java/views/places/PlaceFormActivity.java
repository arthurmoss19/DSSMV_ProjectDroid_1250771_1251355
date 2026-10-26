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
import util.GeoUtils;
import util.PermissionHelper;
import util.UiUtils;
import views.camera.CameraActivity;

public class PlaceFormActivity extends AppCompatActivity {

    private final PlaceController placeController = new PlaceController();
    private final CategoryController categoryController = new CategoryController();
    private final PhotoController photoController = new PhotoController();

    private Toolbar toolbar;
    private EditText editName;
    private Spinner spinnerCategory;
    private RatingBar rating;
    private TextView textCoordinates;
    private TextView textAddress;
    private EditText editLatitude;
    private EditText editLongitude;
    private TextView textPhotoCount;
    private ProgressBar progress;

    private CategoryAdapter categoryAdapter;
    private FusedLocationProviderClient locationClient;

    private String tripId;
    private String placeId;
    private Place editingPlace;
    private String pendingCategoryId;
    private String pendingPhotoPath;
    private String enrichedAddress = "";
    private String enrichedCountry = "";

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    pendingPhotoPath = result.getData().getStringExtra(Extras.PHOTO_FILE);
                    if (pendingPhotoPath != null) {
                        textPhotoCount.setText("1 fotografia pronta a enviar.");
                    }
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
        textCoordinates = findViewById(R.id.textPlaceCoordinates);
        textAddress = findViewById(R.id.textPlaceAddress);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        textPhotoCount = findViewById(R.id.textPhotoCount);
        progress = findViewById(R.id.progressPlaceForm);

        Button getLocation = findViewById(R.id.buttonGetLocation);
        Button takePhoto = findViewById(R.id.buttonTakePhoto);
        Button save = findViewById(R.id.buttonSavePlace);

        toolbar.setNavigationOnClickListener(view -> finish());
        getLocation.setOnClickListener(view -> requestLocation());
        takePhoto.setOnClickListener(view -> cameraLauncher.launch(CameraActivity.intent(this)));
        save.setOnClickListener(view -> save());

        categoryAdapter = new CategoryAdapter(this, CategoryController.defaults());
        spinnerCategory.setAdapter(categoryAdapter);
        loadCategories();

        if (placeId == null || placeId.trim().isEmpty()) {
            toolbar.setTitle(R.string.place_form_title_new);
            rating.setRating(3);
            textCoordinates.setText(R.string.place_form_coordinates_empty);
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
                editName.setText(place.getName());
                rating.setRating(place.getRating());
                editLatitude.setText(String.valueOf(place.getLatitude()));
                editLongitude.setText(String.valueOf(place.getLongitude()));
                textCoordinates.setText(GeoUtils.formatCoordinates(
                        place.getLatitude(), place.getLongitude()));
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
                    textCoordinates.setText(GeoUtils.formatCoordinates(
                            location.getLatitude(), location.getLongitude()));
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
            transientPlace.setRating(Math.max(1, (int) rating.getRating()));
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
            target.setRating(Math.max(1, (int) rating.getRating()));
            target.setLatitude(parse(editLatitude));
            target.setLongitude(parse(editLongitude));
            target.setVisitedAt(DateUtils.today());
            target.setAddress(enrichedAddress);
            target.setCountry(enrichedCountry);

            showLoading(true);
            ApiCallback<Place> callback = new ApiCallback<Place>() {
                @Override
                public void onSuccess(Place place) {
                    uploadPendingPhoto(place);
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

    private void uploadPendingPhoto(Place place) {
        if (pendingPhotoPath == null || place == null || !place.hasId()) {
            finishWithResult();
            return;
        }
        photoController.upload(new File(pendingPhotoPath), place.getId(), "",
                new ApiCallback<model.Photo>() {
                    @Override
                    public void onSuccess(model.Photo photo) {
                        finishWithResult();
                    }

                    @Override
                    public void onError(String message) {
                        UiUtils.toast(PlaceFormActivity.this, message);
                        finishWithResult();
                    }
                });
    }

    private void finishWithResult() {
        showLoading(false);
        setResult(RESULT_OK);
        finish();
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
