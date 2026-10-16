package views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.viewpager2.widget.ViewPager2;

import com.triptrail.R;
import api.ApiCallback;
import model.Trip;
import controllers.TripController;
import util.DateUtils;
import util.Extras;
import util.UiUtils;

public class TripDetailActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private final TripController tripController = new TripController();

    private String tripId;

    public static void start(Context context, String tripId) {
        Intent intent = new Intent(context, TripDetailActivity.class);
        intent.putExtra(Extras.TRIP_ID, tripId);
        context.startActivity(intent);
    }
}
