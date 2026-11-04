package views.places;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.triptrail.R;
import java.util.ArrayList;
import java.util.List;
import controllers.PhotoController;
import model.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private final List<Photo> items = new ArrayList<>();

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
                .fitCenter()
                .into(holder.image);
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
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setImageTintList(null);
            image.setVisibility(View.VISIBLE);

            LinearLayout textBlock = itemView.findViewById(R.id.textBlock);
            textBlock.setVisibility(View.GONE);
        }
    }
}
