package views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

import com.triptrail.R;
import api.ApiCallback;
import controllers.TripController;
import model.Trip;
import util.Extras;
import util.SessionManager;
import util.UiUtils;

public class TripListActivity extends AppCompatActivity {

    private SessionManager session;
    private TripController tripController;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerTrips;
    private LinearLayout emptyView;
    private ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_list_screen);

        session = SessionManager.getInstance(this);
        if (!session.isLoggedIn()) {
            goToLogin();
            return;
        }

        tripController = new TripController();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(session.getUserName());
        toolbar.inflateMenu(R.menu.menu_trips_list);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

        swipeRefresh = findViewById(R.id.swipeRefreshTrips);
        recyclerTrips = findViewById(R.id.recyclerTrips);
        emptyView = findViewById(R.id.textTripsEmpty);
        progress = findViewById(R.id.progressTrips);

        recyclerTrips.setLayoutManager(new LinearLayoutManager(this));
        recyclerTrips.setHasFixedSize(false);

        swipeRefresh.setOnRefreshListener(this::loadTrips);

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddTrip);
        fab.setOnClickListener(view -> openForm());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session != null && session.isLoggedIn()) {
            loadTrips();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Extras.REQUEST_TRIP_FORM && resultCode == RESULT_OK) {
            loadTrips();
        }
    }

    // ações

    private boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            return true;
        }
        return false;
    }

    private void openForm() {
        Intent intent = new Intent(this, TripFormActivity.class);
        startActivityForResult(intent, Extras.REQUEST_TRIP_FORM);
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

    // carregamento

    /** Um pedido por ecrã: nada de polling (secção 11 da proposta). */
    private void loadTrips() {
        showLoading(true);
        tripController.getTrips(session.getUserId(), new ApiCallback<List<Trip>>() {
            @Override
            public void onSuccess(List<Trip> trips) {
                showLoading(false);
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


