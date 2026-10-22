package views.trips;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.triptrail.R;
import api.ApiCallback;
import exceptions.InvalidDataException;
import model.Trip;
import controllers.TripController;
import util.DateUtils;
import util.Extras;
import util.SessionManager;
import util.UiUtils;

public class TripFormActivity extends AppCompatActivity {
    private TripController tripController;
    private SessionManager session;
    private Toolbar toolbar;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editStartDate;
    private EditText editEndDate;
    private Button buttonSave;
    private ProgressBar progress;
    private String startDateIso;
    private String endDateIso;
    private Trip editingTrip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trip_screen);

        tripController = new TripController();
        session = SessionManager.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        editTitle = findViewById(R.id.editTripTitle);
        editDescription = findViewById(R.id.editTripDescription);
        editStartDate = findViewById(R.id.editTripStartDate);
        editEndDate = findViewById(R.id.editTripEndDate);
        buttonSave = findViewById(R.id.buttonSaveTrip);
        progress = findViewById(R.id.progressTripForm);

        toolbar.setNavigationOnClickListener(view -> finish());
        buttonSave.setOnClickListener(view -> save());
        editStartDate.setOnClickListener(view -> pickDate(true));
        editEndDate.setOnClickListener(view -> pickDate(false));

        String tripId = getIntent().getStringExtra(Extras.TRIP_ID);
        if (tripId == null || tripId.trim().isEmpty()) {
            startNewTrip();
        } else {
            loadTrip(tripId);
        }
    }

    private void startNewTrip() {
        toolbar.setTitle(R.string.trips_new_title);
        startDateIso = DateUtils.today();
        endDateIso = DateUtils.today();
        showDates();
    }

    private void loadTrip(String tripId) {
        showLoading(true);

        tripController.getTrip(tripId, new ApiCallback<Trip>() {
            @Override
            public void onSuccess(Trip trip) {
                showLoading(false);
                if (trip == null) {
                    finish();
                    return;
                }
                editingTrip = trip;
                editTitle.setText(trip.getTitle());
                editDescription.setText(trip.getDescription());
                startDateIso = trip.getStartDate();
                endDateIso = trip.getEndDate();
                showDates();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                UiUtils.toast(TripFormActivity.this, message);
                finish();
            }
        });
    }

    private void showDates() {
        editStartDate.setText(DateUtils.formatDisplayDate(startDateIso));
        editEndDate.setText(DateUtils.formatDisplayDate(endDateIso));
    }
    private void pickDate(boolean isStart) {
        String current = isStart ? startDateIso : endDateIso;
        Calendar calendar = Calendar.getInstance();
        Date parsed = DateUtils.parseDate(current);
        if (parsed != null) {
            calendar.setTime(parsed);
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String iso = String.format(Locale.US, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);
                    if (isStart) {
                        startDateIso = iso;
                    } else {
                        endDateIso = iso;
                    }
                    showDates();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    private void save() {
        try {
            Trip target = editingTrip == null ? new Trip() : editingTrip.copy();

            target.setTitle(editTitle.getText().toString());
            target.setDescription(editDescription.getText().toString());
            target.setStartDate(startDateIso);
            target.setEndDate(endDateIso);
            target.setOwnerId(session.getUserId());

            submit(target);
        } catch (InvalidDataException e) {
            UiUtils.toast(this, e.what());
        }
    }
    private void submit(Trip target) {
        showLoading(true);
        tripController.save(target, new ApiCallback<Trip>() {
            @Override
            public void onSuccess(Trip trip) {
                showLoading(false);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                UiUtils.toast(TripFormActivity.this, message);
            }
        });
    }
    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonSave.setEnabled(!loading);
    }
}

