package views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

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

    private MaterialToolbar toolbar;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editStartDate;
    private EditText editEndDate;
    private MaterialButton buttonSave;
    private ProgressBar progress;

    private String startDateIso;
    private String endDateIso;

    private Trip editingTrip;

    private void startNewTrip() {
        toolbar.setTitle(R.string.trips_new_title);
        startDateIso = DateUtils.today();
        endDateIso = DateUtils.today();
        showDates();
    }

    private void showDates() {
        editStartDate.setText(DateUtils.formatDisplayDate(startDateIso));
        editEndDate.setText(DateUtils.formatDisplayDate(endDateIso));
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
    }

    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonSave.setEnabled(!loading);
    }
}
