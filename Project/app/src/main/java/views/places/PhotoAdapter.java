package views.places;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.triptrail.R;
import java.util.ArrayList;
import java.util.List;
import controllers.PhotoController;
import model.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public interface Listener {
        void onDeletePhoto(Photo photo);
    }
    private final List<Photo> items = new ArrayList<>();
    private final Listener listener;

    public PhotoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Photo> photos) {
        items.clear();
        if (photos != null) {
            items.addAll(photos);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new PhotoViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = items.get(position);
        Glide.with(holder.image.getContext())
                .load(PhotoController.mediaUrl(photo))
                .centerCrop()
                .into(holder.image);

        String caption = photo.getCaption();
        holder.caption.setText(caption == null ? "" : caption);
        holder.delete.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeletePhoto(photo);
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    private static int dp(View view, int value) {
        return Math.round(value * view.getResources().getDisplayMetrics().density);
    }
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView caption;
        final ImageButton delete;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            LinearLayout root = itemView.findViewById(R.id.listItemRoot);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(0, 0, 0, dp(itemView, 8));
            root.setLayoutParams(new RecyclerView.LayoutParams(dp(itemView, 160),
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            image = itemView.findViewById(R.id.imageItem);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(itemView, 120));
            imageParams.setMarginEnd(0);
            image.setLayoutParams(imageParams);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageTintList(null);
            image.setVisibility(View.VISIBLE);

            LinearLayout textBlock = itemView.findViewById(R.id.textBlock);
            textBlock.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textBlock.setPadding(dp(itemView, 8), 0, dp(itemView, 8), 0);

            TextView title = itemView.findViewById(R.id.textItemTitle);
            title.setVisibility(View.GONE);
            caption = itemView.findViewById(R.id.textItemSubtitle);
            caption.setTextColor(itemView.getContext().getColor(R.color.tt_text));
            TextView meta = itemView.findViewById(R.id.textItemMeta);
            meta.setVisibility(View.GONE);
            RatingBar rating = itemView.findViewById(R.id.ratingItem);
            rating.setVisibility(View.GONE);

            delete = itemView.findViewById(R.id.buttonItemAction);
            LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                    dp(itemView, 32), dp(itemView, 32));
            actionParams.gravity = Gravity.END;
            delete.setLayoutParams(actionParams);
            delete.setVisibility(View.VISIBLE);
        }
    }
}
