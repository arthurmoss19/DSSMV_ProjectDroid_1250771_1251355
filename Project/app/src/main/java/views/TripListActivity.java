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
}

