package views.trips;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.triptrail.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import api.ApiCallback;
import controllers.CategoryController;
import controllers.PlaceController;
import controllers.TripController;
import controllers.TripMemberController;
import model.Category;
import model.Place;
import model.Trip;
import model.TripMember;
import model.TripStatistics;
import model.User;
import repository.UserRepository;
import util.Extras;
import util.GeoUtils;
import util.UiUtils;
import views.members.MemberAdapter;
import views.places.PlaceAdapter;
import views.places.PlaceDetailActivity;
import views.places.PlaceFormActivity;

public class TripDetailActivity extends AppCompatActivity
        implements PlaceAdapter.Listener, MemberAdapter.Listener {

    private final TripController tripController = new TripController();
    private final PlaceController placeController = new PlaceController();
    private final CategoryController categoryController = new CategoryController();
    private final TripMemberController memberController = new TripMemberController();
    private final UserRepository users = new UserRepository();

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private View sectionPlaces;
    private View sectionMap;
    private View sectionMembers;
    private View sectionStats;

    private RecyclerView recyclerPlaces;
    private PlaceAdapter placeAdapter;
    private TextView textPlacesEmpty;
    private ProgressBar progressPlaces;
    private EditText editSearchPlaces;
    private Spinner spinnerFilter;

    private MapView mapView;
    private TextView textMapEmpty;

    private RecyclerView recyclerMembers;
    private MemberAdapter memberAdapter;
    private TextView textMembersEmpty;
    private ProgressBar progressMembers;
    private EditText editInviteEmail;

    private TextView textStatsPlaces;
    private TextView textStatsCountries;
    private TextView textStatsDistance;
    private TextView textStatsRating;
    private TextView textStatsDays;
    private TextView textStatsEmpty;

    private String tripId;
    private Trip trip;
    private final List<Place> allPlaces = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();

    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadTrip();
                }
            });

    private final ActivityResultLauncher<Intent> placeFormLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadPlaces();
                }
            });

    public static void start(Context context, String tripId) {
        Intent intent = new Intent(context, TripDetailActivity.class);
        intent.putExtra(Extras.TRIP_ID, tripId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details_screen);

        tripId = getIntent().getStringExtra(Extras.TRIP_ID);
        if (tripId == null || tripId.trim().isEmpty()) {
            finish();
            return;
        }

        bindViews();
        setupTabs();
        setupPlaces();
        setupMembers();
        setupMap();

        loadTrip();
        loadCategories();
        loadMembers();
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle(R.string.trip_detail_default_title);
        toolbar.inflateMenu(R.menu.menu_trip_details);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

        tabLayout = findViewById(R.id.tabLayout);
        sectionPlaces = findViewById(R.id.sectionPlaces);
        sectionMap = findViewById(R.id.sectionMap);
        sectionMembers = findViewById(R.id.sectionMembers);
        sectionStats = findViewById(R.id.sectionStats);

        recyclerPlaces = findViewById(R.id.recyclerPlaces);
        textPlacesEmpty = findViewById(R.id.textPlacesEmpty);
        progressPlaces = findViewById(R.id.progressPlaces);
        editSearchPlaces = findViewById(R.id.editSearchPlaces);
        spinnerFilter = findViewById(R.id.spinnerFilterCategory);

        mapView = findViewById(R.id.mapView);
        textMapEmpty = findViewById(R.id.textMapEmpty);

        recyclerMembers = findViewById(R.id.recyclerMembers);
        textMembersEmpty = findViewById(R.id.textMembersEmpty);
        progressMembers = findViewById(R.id.progressMembers);
        editInviteEmail = findViewById(R.id.editInviteEmail);

        textStatsPlaces = findViewById(R.id.textStatsPlaces);
        textStatsCountries = findViewById(R.id.textStatsCountries);
        textStatsDistance = findViewById(R.id.textStatsDistance);
        textStatsRating = findViewById(R.id.textStatsRating);
        textStatsDays = findViewById(R.id.textStatsDays);
        textStatsEmpty = findViewById(R.id.textStatsEmpty);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_places));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_map));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_members));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_stats));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showSection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        showSection(0);
    }

    private void showSection(int position) {
        sectionPlaces.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        sectionMap.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        sectionMembers.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        sectionStats.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
        if (position == 1 && mapView != null) {
            mapView.invalidate();
        }
    }

    private void setupPlaces() {
        placeAdapter = new PlaceAdapter(this);
        recyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlaces.setAdapter(placeAdapter);

        Button add = findViewById(R.id.fabAddPlace);
        add.setOnClickListener(view -> openPlaceForm(null));
        editSearchPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupMembers() {
        memberAdapter = new MemberAdapter(this);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(memberAdapter);
        Button invite = findViewById(R.id.buttonInvite);
        invite.setOnClickListener(view -> invite());
    }

    private void setupMap() {
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(10.0);
    }

    private boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_trip) {
            if (trip == null) {
                return true;
            }
            openForm();
            return true;
        }
        if (item.getItemId() == R.id.action_delete_trip) {
            confirmDelete();
            return true;
        }
        return false;
    }

    private void loadTrip() {
        tripController.getTrip(tripId, new ApiCallback<Trip>() {
            @Override
            public void onSuccess(Trip data) {
                if (data == null) {
                    finish();
                    return;
                }
                trip = data;
                toolbar.setTitle(data.getTitle());
                updateStats();
            }

            @Override
            public void onError(String message) {
                UiUtils.toast(TripDetailActivity.this, message);
            }
        });
    }

    private void loadCategories() {
        categoryController.getAll(new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categories.clear();
                if (data != null) {
                    categories.addAll(data);
                }
                List<String> names = new ArrayList<>();
                names.add(getString(R.string.places_filter_all));
                for (Category category : categories) {
                    names.add(category.getName());
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(TripDetailActivity.this,
                        android.R.layout.simple_spinner_item, names);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilter.setAdapter(spinnerAdapter);
                applyFilter();
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    private void loadPlaces() {
        progressPlaces.setVisibility(View.VISIBLE);
        placeController.getPlaces(tripId, new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(List<Place> places) {
                progressPlaces.setVisibility(View.GONE);
                allPlaces.clear();
                if (places != null) {
                    allPlaces.addAll(places);
                }
                applyFilter();
                updateMap();
                updateStats();
            }

            @Override
            public void onError(String message) {
                progressPlaces.setVisibility(View.GONE);
                UiUtils.toast(TripDetailActivity.this, message);
            }
        });
    }

    private void applyFilter() {
        String query = editSearchPlaces.getText() == null
                ? "" : editSearchPlaces.getText().toString().trim().toLowerCase();
        int position = spinnerFilter.getSelectedItemPosition();
        String categoryId = null;
        if (position > 0 && position - 1 < categories.size()) {
            categoryId = categories.get(position - 1).getId();
        }

        List<Place> filtered = new ArrayList<>();
        for (Place place : allPlaces) {
            boolean matchesName = query.isEmpty()
                    || place.getName().toLowerCase().contains(query);
            boolean matchesCategory = categoryId == null
                    || categoryId.equals(place.getCategoryId());
            if (matchesName && matchesCategory) {
                filtered.add(place);
            }
        }
        placeAdapter.submitList(filtered);
        boolean empty = filtered.isEmpty();
        textPlacesEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerPlaces.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void updateMap() {
        mapView.getOverlays().clear();
        boolean empty = allPlaces.isEmpty();
        textMapEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        mapView.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) {
            mapView.invalidate();
            return;
        }

        for (Place place : allPlaces) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(place.getLatitude(), place.getLongitude()));
            marker.setTitle(place.getName());
            marker.setSnippet(place.getAddress());
            marker.setOnMarkerClickListener((m, map) -> {
                PlaceDetailActivity.start(this, place.getId());
                return true;
            });
            mapView.getOverlays().add(marker);
        }

        Place first = allPlaces.get(0);
        mapView.getController().setCenter(new GeoPoint(first.getLatitude(), first.getLongitude()));
        mapView.getController().setZoom(11.0);
        mapView.invalidate();
    }

    private void updateStats() {
        if (trip == null) {
            return;
        }
        TripStatistics statistics = new TripStatistics(allPlaces, trip);
        textStatsPlaces.setText(String.valueOf(statistics.getPlaceCount()));
        textStatsCountries.setText(String.valueOf(statistics.getCountryCount()));
        textStatsDistance.setText(GeoUtils.formatDistance(statistics.getTotalDistanceKm()));
        textStatsRating.setText(String.format(Locale.getDefault(), "%.1f",
                statistics.getAverageRating()));
        int days = statistics.getDayCount();
        textStatsDays.setText(getResources().getQuantityString(R.plurals.trips_duration, days, days));
        textStatsEmpty.setVisibility(statistics.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadMembers() {
        progressMembers.setVisibility(View.VISIBLE);
        memberController.getMembers(tripId, new ApiCallback<List<TripMember>>() {
            @Override
            public void onSuccess(List<TripMember> members) {
                users.getAll(new ApiCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        progressMembers.setVisibility(View.GONE);
                        Map<String, User> byId = new HashMap<>();
                        if (data != null) {
                            for (User user : data) {
                                byId.put(user.getId(), user);
                            }
                        }
                        memberAdapter.submitList(members, byId);
                        boolean empty = members == null || members.isEmpty();
                        textMembersEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                        recyclerMembers.setVisibility(empty ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onError(String message) {
                        progressMembers.setVisibility(View.GONE);
                        memberAdapter.submitList(members, null);
                        boolean empty = members == null || members.isEmpty();
                        textMembersEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                        recyclerMembers.setVisibility(empty ? View.GONE : View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                progressMembers.setVisibility(View.GONE);
                UiUtils.toast(TripDetailActivity.this, message);
            }
        });
    }

    private void invite() {
        String email = editInviteEmail.getText() == null
                ? "" : editInviteEmail.getText().toString().trim();
        progressMembers.setVisibility(View.VISIBLE);
        memberController.invite(tripId, email, new ApiCallback<TripMember>() {
            @Override
            public void onSuccess(TripMember member) {
                progressMembers.setVisibility(View.GONE);
                editInviteEmail.setText("");
                loadMembers();
            }

            @Override
            public void onError(String message) {
                progressMembers.setVisibility(View.GONE);
                UiUtils.toast(TripDetailActivity.this, message);
            }
        });
    }

    @Override
    public void onPlaceClick(Place place) {
        PlaceDetailActivity.start(this, place.getId());
    }

    @Override
    public void onPlaceLongClick(Place place) {
        new AlertDialog.Builder(this)
                .setTitle(place.getName())
                .setItems(new CharSequence[]{
                        getString(R.string.trips_edit_action),
                        getString(R.string.trips_delete_action)
                }, (dialog, which) -> {
                    if (which == 0) {
                        openPlaceForm(place.getId());
                    } else {
                        confirmDeletePlace(place);
                    }
                })
                .show();
    }

    private void confirmDeletePlace(Place place) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage("Quer apagar este local e as suas fotografias?")
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        placeController.delete(place.getId(), new ApiCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                loadPlaces();
                            }

                            @Override
                            public void onError(String message) {
                                UiUtils.toast(TripDetailActivity.this, message);
                            }
                        }))
                .show();
    }

    @Override
    public void onRemoveMember(TripMember member) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage("Quer remover este participante da viagem?")
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        memberController.remove(member.getId(), new ApiCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                loadMembers();
                            }

                            @Override
                            public void onError(String message) {
                                UiUtils.toast(TripDetailActivity.this, message);
                            }
                        }))
                .show();
    }

    private void openForm() {
        Intent intent = new Intent(this, TripFormActivity.class);
        intent.putExtra(Extras.TRIP_ID, tripId);
        formLauncher.launch(intent);
    }

    private void openPlaceForm(String placeId) {
        Intent intent = new Intent(this, PlaceFormActivity.class);
        intent.putExtra(Extras.TRIP_ID, tripId);
        if (placeId != null) {
            intent.putExtra(Extras.PLACE_ID, placeId);
        }
        placeFormLauncher.launch(intent);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.trips_delete_title)
                .setMessage(getString(R.string.trips_delete_message,
                        trip == null ? "" : trip.getTitle()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.trips_delete_action, (dialog, which) ->
                        tripController.delete(tripId, new ApiCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onError(String message) {
                                UiUtils.toast(TripDetailActivity.this, message);
                            }
                        }))
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        if (tripId != null) {
            loadPlaces();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onDetach();
        }
        super.onDestroy();
    }
}