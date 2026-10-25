package views.places;

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
import model.Place;
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    public interface Listener {
        void onPlaceClick(Place place);

        void onPlaceLongClick(Place place);
    }
    private final List<Place> items = new ArrayList<>();

    private final Listener listener;

    public PlaceAdapter(Listener listener) {
        this.listener = listener;
    }
    public void submitList(List<Place> places) {
        items.clear();
        if (places != null) {
            items.addAll(places);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new PlaceViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = items.get(position);
        holder.image.setImageResource(R.drawable.icon_pin);
        holder.title.setText(place.getName());
        holder.rating.setRating(place.getRating());

        String address = place.getAddress();
        holder.subtitle.setText(address == null || address.trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.place_no_address)
                : address);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onPlaceClick(place);
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null) {
                listener.onPlaceLongClick(place);
            }
            return true;
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView subtitle;
        final RatingBar rating;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageItem);
            title = itemView.findViewById(R.id.textItemTitle);
            subtitle = itemView.findViewById(R.id.textItemSubtitle);
            TextView meta = itemView.findViewById(R.id.textItemMeta);
            rating = itemView.findViewById(R.id.ratingItem);
            ImageButton action = itemView.findViewById(R.id.buttonItemAction);
            image.setVisibility(View.VISIBLE);
            meta.setVisibility(View.GONE);
            rating.setVisibility(View.VISIBLE);
            action.setVisibility(View.GONE);
        }
    }
}
