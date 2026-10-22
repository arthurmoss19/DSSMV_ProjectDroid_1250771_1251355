package views.trips;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.triptrail.R;
import java.util.ArrayList;
import java.util.List;
import model.Trip;
import util.DateUtils;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    public interface Listener {
        void onTripClick(Trip trip);
        void onTripLongClick(Trip trip);
    }
    private final List<Trip> items = new ArrayList<>();
    private final Listener listener;
    public TripAdapter(Listener listener) {
        this.listener = listener;
    }
    public void submitList(List<Trip> trips) {
        items.clear();
        if (trips != null) {
            items.addAll(trips);
        }
        notifyDataSetChanged();
    }
    @NonNull

    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = items.get(position);
        holder.image.setImageResource(R.drawable.icon_pin);
        holder.image.setContentDescription(holder.itemView.getContext()
                .getString(R.string.trips_cd_trip_icon));
        holder.title.setText(trip.getTitle());
        holder.subtitle.setText(DateUtils.formatRange(trip.getStartDate(), trip.getEndDate()));

        int days = trip.getDurationInDays();
        String duration = holder.itemView.getResources()
                .getQuantityString(R.plurals.trips_duration, days, days);
        String description = trip.getDescription();
        if (description == null || description.trim().isEmpty()) {
            holder.meta.setText(duration);
        } else {
            holder.meta.setText(description + " · " + duration);
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onTripClick(trip);
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null) {
                listener.onTripLongClick(trip);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    static class TripViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView subtitle;
        final TextView meta;
        TripViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageItem);
            title = itemView.findViewById(R.id.textItemTitle);
            subtitle = itemView.findViewById(R.id.textItemSubtitle);
            meta = itemView.findViewById(R.id.textItemMeta);
            RatingBar rating = itemView.findViewById(R.id.ratingItem);
            ImageButton action = itemView.findViewById(R.id.buttonItemAction);
            image.setVisibility(View.VISIBLE);
            rating.setVisibility(View.GONE);
            action.setVisibility(View.GONE);
        }
    }
}

