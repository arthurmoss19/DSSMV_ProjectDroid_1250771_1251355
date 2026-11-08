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
import java.util.ArrayList;
import java.util.Collections;
import controllers.TripMemberController;
import model.TripMember;
import android.os.Handler;
import android.os.Looper;
import java.util.HashSet;
import java.util.Set;

public class TripListActivity extends AppCompatActivity implements TripAdapter.Listener {
    private SessionManager session;
    private TripController tripController;
    private TripAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerTrips;
    private LinearLayout emptyView;
    private ProgressBar progress;

    private TripMemberController memberController;

    private static final long INVITE_POLL_MS = 60000;

    private final Set<String> shownInvites = new HashSet<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable invitePoll = new Runnable() {
        @Override
        public void run() {
            loadInvites();
            handler.postDelayed(this, INVITE_POLL_MS);
        }
    };

    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {}
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
        memberController = new TripMemberController();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_trips_list);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);
        UiUtils.applyTopBarInsets(toolbar);
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
            handler.removeCallbacks(invitePoll);
            handler.post(invitePoll);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(invitePoll);
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
        if (!isOwner(trip)) {
            UiUtils.toast(this, getString(R.string.trips_owner_only));
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
    private boolean isOwner(Trip trip) {
        String myId = session == null ? null : session.getUserId();
        return myId != null && trip.getOwnerId() != null && myId.equals(trip.getOwnerId());
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
            public void onSuccess(List<Trip> owned) {
                loadSharedTrips(owned);
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                UiUtils.toast(TripListActivity.this, message);
            }
        });
    }

    private void loadInvites() {
        memberController.getPendingInvites(session.getUserId(),
                new ApiCallback<List<TripMember>>() {
                    @Override
                    public void onSuccess(List<TripMember> invites) {
                        if (invites == null || invites.isEmpty()) {
                            return;
                        }
                        List<TripMember> fresh = new ArrayList<>();
                        List<String> ids = new ArrayList<>();
                        for (TripMember invite : invites) {
                            if (invite.getId() != null && shownInvites.contains(invite.getId())) {
                                continue;
                            }
                            fresh.add(invite);
                            shownInvites.add(invite.getId());
                            if (invite.getTripId() != null && !ids.contains(invite.getTripId())) {
                                ids.add(invite.getTripId());
                            }
                        }
                        if (fresh.isEmpty()) {
                            return;
                        }
                        tripController.getTripsByIds(ids, new ApiCallback<List<Trip>>() {
                            @Override
                            public void onSuccess(List<Trip> trips) {
                                List<TripMember> valid = new ArrayList<>();
                                List<String> seen = new ArrayList<>();
                                for (TripMember invite : fresh) {
                                    if (invite.getTripId() == null
                                            || seen.contains(invite.getTripId())) {
                                        continue;
                                    }
                                    Trip trip = findTrip(trips, invite.getTripId());
                                    if (trip != null && session.getUserId() != null
                                            && session.getUserId().equals(trip.getOwnerId())) {
                                        continue;
                                    }
                                    seen.add(invite.getTripId());
                                    valid.add(invite);
                                }
                                showNextInvite(valid, trips, 0);
                            }

                            @Override
                            public void onError(String message) {
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                    }
                });
    }

    private void showNextInvite(List<TripMember> invites, List<Trip> trips, int index) {
        if (index >= invites.size()) {
            return;
        }
        TripMember invite = invites.get(index);
        Trip trip = findTrip(trips, invite.getTripId());
        String title = trip == null ? invite.getTripId() : trip.getTitle();
        new AlertDialog.Builder(this)
                .setTitle(R.string.invites_title)
                .setMessage(getString(R.string.invites_message, title))
                .setCancelable(false)
                .setNegativeButton(R.string.invites_decline, (dialog, which) ->
                        memberController.remove(invite.getId(), new ApiCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                showNextInvite(invites, trips, index + 1);
                            }

                            @Override
                            public void onError(String message) {
                                shownInvites.remove(invite.getId());
                                UiUtils.toast(TripListActivity.this, message);
                                showNextInvite(invites, trips, index + 1);
                            }
                        }))
                .setPositiveButton(R.string.invites_accept, (dialog, which) ->
                        memberController.accept(invite.getId(), new ApiCallback<TripMember>() {
                            @Override
                            public void onSuccess(TripMember member) {
                                loadTrips();
                                showNextInvite(invites, trips, index + 1);
                            }

                            @Override
                            public void onError(String message) {
                                shownInvites.remove(invite.getId());
                                UiUtils.toast(TripListActivity.this, message);
                                showNextInvite(invites, trips, index + 1);
                            }
                        }))
                .show();
    }

    private Trip findTrip(List<Trip> trips, String id) {
        for (Trip trip : trips) {
            if (id != null && id.equals(trip.getId())) {
                return trip;
            }
        }
        return null;
    }

    private void loadSharedTrips(List<Trip> owned) {
        memberController.getMemberships(session.getUserId(),
                new ApiCallback<List<TripMember>>() {
                    @Override
                    public void onSuccess(List<TripMember> memberships) {
                        List<String> ids = new ArrayList<>();
                        if (memberships != null) {
                            for (TripMember member : memberships) {
                                String tripId = member.getTripId();
                                if (tripId != null && !ids.contains(tripId)) {
                                    ids.add(tripId);
                                }
                            }
                        }
                        tripController.getTripsByIds(ids, new ApiCallback<List<Trip>>() {
                            @Override
                            public void onSuccess(List<Trip> shared) {
                                showTrips(mergeTrips(owned, shared));
                            }

                            @Override
                            public void onError(String message) {
                                showTrips(owned);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        showTrips(owned);
                    }
                });
    }

    private List<Trip> mergeTrips(List<Trip> owned, List<Trip> shared) {
        List<Trip> result = new ArrayList<>();
        if (owned != null) {
            result.addAll(owned);
        }
        if (shared != null) {
            for (Trip trip : shared) {
                boolean exists = false;
                for (Trip item : result) {
                    if (item.getId() != null && item.getId().equals(trip.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    result.add(trip);
                }
            }
        }
        Collections.sort(result, (a, b) -> {
            String dateA = a.getStartDate() == null ? "" : a.getStartDate();
            String dateB = b.getStartDate() == null ? "" : b.getStartDate();
            return dateB.compareTo(dateA);
        });
        return result;
    }

    private void showTrips(List<Trip> trips) {
        showLoading(false);
        adapter.submitList(trips);
        boolean empty = trips == null || trips.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerTrips.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (!loading) {
            swipeRefresh.setRefreshing(false);
        }
    }
}


