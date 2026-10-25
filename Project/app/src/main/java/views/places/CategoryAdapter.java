package views.places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.triptrail.R;
import java.util.ArrayList;
import java.util.List;
import model.Category;
public class CategoryAdapter extends BaseAdapter {

    private final Context context;
    private final List<Category> items = new ArrayList<>();

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        setItems(categories);
    }
    public void setItems(List<Category> categories) {
        items.clear();
        if (categories != null) {
            items.addAll(categories);
        }
        notifyDataSetChanged();
    }
    public int indexOf(String categoryId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() != null && items.get(i).getId().equals(categoryId)) {
                return i;
            }
        }
        return 0;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Category getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return row(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return row(position, convertView, parent);
    }
    private View row(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            view.setPadding(dp(8), dp(8), dp(8), dp(8));
            ImageView icon = view.findViewById(R.id.imageItem);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(24), dp(24));
            iconParams.setMarginEnd(dp(8));
            icon.setLayoutParams(iconParams);
            icon.setVisibility(View.VISIBLE);
            TextView title = view.findViewById(R.id.textItemTitle);
            TextView subtitle = view.findViewById(R.id.textItemSubtitle);
            TextView meta = view.findViewById(R.id.textItemMeta);
            RatingBar rating = view.findViewById(R.id.ratingItem);
            ImageButton action = view.findViewById(R.id.buttonItemAction);
            subtitle.setVisibility(View.GONE);
            meta.setVisibility(View.GONE);
            rating.setVisibility(View.GONE);
            action.setVisibility(View.GONE);
            title.setTextSize(16);
        }
        Category category = getItem(position);
        ImageView icon = view.findViewById(R.id.imageItem);
        TextView name = view.findViewById(R.id.textItemTitle);
        icon.setImageResource(iconFor(category.getIcon()));
        name.setText(category.getName());
        return view;
    }
    private int dp(int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    static int iconFor(String icon) {
        if (icon == null) {
            return R.drawable.icon_category_other;
        }
        switch (icon) {
            case "restaurant":
                return R.drawable.icon_category_restaurant;
            case "monument":
                return R.drawable.icon_category_monument;
            case "nature":
                return R.drawable.icon_category_nature;
            case "lodging":
                return R.drawable.icon_category_lodging;
            case "beach":
                return R.drawable.icon_category_beach;
            case "museum":
                return R.drawable.icon_category_museum;
            default:
                return R.drawable.icon_category_other;
        }
    }
}
