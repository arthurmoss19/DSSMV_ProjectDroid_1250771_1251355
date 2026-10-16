package views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.triptrail.R;
import model.Trip;
import util.DateUtils;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public interface OnTripClickListener {

        void onTripClick(Trip trip);

        void onTripLongClick(Trip trip);
    }

    private final Context context;
    private final OnTripClickListener listener;
    private final List<Trip> trips = new ArrayList<>();

    public TripAdapter(Context context, OnTripClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setTrips(List<Trip> trips) {
        this.trips.clear();
        if (trips != null) {
            this.trips.addAll(trips);
        }
        notifyDataSetChanged();
    }

    public Trip getTripAt(int position) {
        if (position < 0 || position >= trips.size()) {
            return null;
        }
        return trips.get(position);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.line_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        if (position < 0 || position >= trips.size()) {
            return;
        }
        Trip trip = trips.get(position);

        holder.title.setText(trip.getTitle());
        holder.dates.setText(DateUtils.formatRange(trip.getStartDate(), trip.getEndDate()));
        holder.duration.setText(context.getResources().getQuantityString(
                R.plurals.trips_duration, trip.getDurationInDays(), trip.getDurationInDays()));

        String description = trip.getDescription();
        if (description == null || description.trim().isEmpty()) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(description);
        }

        holder.itemView.setOnClickListener(view -> listener.onTripClick(trip));
        holder.itemView.setOnLongClickListener(view -> {
            listener.onTripLongClick(trip);
            return true;
        });
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView dates;
        final TextView description;
        final TextView duration;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTripTitle);
            dates = itemView.findViewById(R.id.textTripDates);
            description = itemView.findViewById(R.id.textTripDescription);
            duration = itemView.findViewById(R.id.textTripDuration);
        }
    }
}
