package views.trips;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;
import com.triptrail.R;
import api.ApiCallback;
import controllers.TripController;
import model.Trip;
import util.Extras;
import util.SessionManager;
import util.UiUtils;
import views.login.LoginActivity;

public class TripListActivity extends AppCompatActivity implements TripAdapter.Listener {
    private SessionManager session;
    private TripController tripController;
    private TripAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerTrips;
    private LinearLayout emptyView;
    private ProgressBar progress;

    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadTrips();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_list_screen);

        session = SessionManager.getInstance(this);
        if (!session.isLoggedIn()) {
            goToLogin();
            return;
        }
        tripController = new TripController();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(session.getUserName());
        toolbar.inflateMenu(R.menu.menu_trips_list);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);
        swipeRefresh = findViewById(R.id.swipeRefreshTrips);
        recyclerTrips = findViewById(R.id.recyclerTrips);
        emptyView = findViewById(R.id.textTripsEmpty);
        progress = findViewById(R.id.progressTrips);
        adapter = new TripAdapter(this);
        recyclerTrips.setLayoutManager(new LinearLayoutManager(this));
        recyclerTrips.setHasFixedSize(false);
        recyclerTrips.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this::loadTrips);
        Button fab = findViewById(R.id.fabAddTrip);
        fab.setOnClickListener(view -> openForm());
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (session != null && session.isLoggedIn()) {
            loadTrips();
        }
    }
    private boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.logout();
            goToLogin();
            return true;
        }
        return false;
    }
    @Override
    public void onTripClick(Trip trip) {
        openTrip(trip);
    }
    @Override
    public void onTripLongClick(Trip trip) {
        if (trip == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(trip.getTitle())
                .setItems(new CharSequence[]{
                        getString(R.string.trips_edit_action),
                        getString(R.string.trips_delete_action)
                }, (dialog, which) -> {
                    if (which == 0) {
                        openForm(trip);
                    } else {
                        confirmDelete(trip);
                    }
                })
                .show();
    }
    private void openForm() {
        openForm(null);
    }

    private void openForm(Trip trip) {
        Intent intent = new Intent(this, TripFormActivity.class);
        if (trip != null && trip.hasId()) {
            intent.putExtra(Extras.TRIP_ID, trip.getId());
        }
        formLauncher.launch(intent);
    }
    private void openTrip(Trip trip) {
        if (trip == null || !trip.hasId()) {
            return;
        }
        Intent intent = new Intent(this, TripDetailActivity.class);
        intent.putExtra(Extras.TRIP_ID, trip.getId());
        startActivity(intent);
    }
    private void confirmDelete(Trip trip) {
        if (trip == null || !trip.hasId()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.trips_delete_title)
                .setMessage(getString(R.string.trips_delete_message, trip.getTitle()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.trips_delete_action,
                        (dialog, which) -> deleteTrip(trip))
                .show();
    }
    private void deleteTrip(Trip trip) {
        showLoading(true);
        tripController.delete(trip.getId(), new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                loadTrips();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                UiUtils.toast(TripListActivity.this, message);
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void loadTrips() {
        showLoading(true);
        tripController.getTrips(session.getUserId(), new ApiCallback<List<Trip>>() {
            @Override
            public void onSuccess(List<Trip> trips) {
                showLoading(false);
                adapter.submitList(trips);
                boolean empty = trips == null || trips.isEmpty();
                emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
                recyclerTrips.setVisibility(empty ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                UiUtils.toast(TripListActivity.this, message);
            }
        });
    }
    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (!loading) {
            swipeRefresh.setRefreshing(false);
        }
    }
}


