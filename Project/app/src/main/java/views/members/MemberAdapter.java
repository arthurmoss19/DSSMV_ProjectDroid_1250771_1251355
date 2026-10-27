package views.members;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.TripMember;
import model.User;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    public interface Listener {
        void onRemoveMember(TripMember member);
    }
    private final List<TripMember> items = new ArrayList<>();
    private final Map<String, User> users = new HashMap<>();
    private final Listener listener;
    public MemberAdapter(Listener listener) {
        this.listener = listener;
    }
    public void submitList(List<TripMember> members, Map<String, User> usersById) {
        items.clear();
        users.clear();
        if (members != null) {
            items.addAll(members);
        }
        if (usersById != null) {
            users.putAll(usersById);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        TripMember member = items.get(position);
        User user = users.get(member.getUserId());
        if (user == null) {
            holder.name.setText(member.getUserId() == null
                    ? holder.itemView.getContext().getString(R.string.members_unknown)
                    : member.getUserId());
            holder.email.setText("");
        } else {
            holder.name.setText(user.getName());
            holder.email.setText(user.getEmail());
        }
        holder.role.setText(member.isOwner()
                ? R.string.members_role_owner
                : R.string.members_role_guest);

        boolean owner = member.isOwner();
        holder.remove.setVisibility(owner ? View.GONE : View.VISIBLE);
        holder.remove.setOnClickListener(view -> {
            if (listener != null) {
                listener.onRemoveMember(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    
    static class MemberViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView email;
        final TextView role;
        final ImageButton remove;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageView image = itemView.findViewById(R.id.imageItem);
            image.setImageResource(R.drawable.icon_account);
            image.setVisibility(View.VISIBLE);
            name = itemView.findViewById(R.id.textItemTitle);
            email = itemView.findViewById(R.id.textItemSubtitle);
            role = itemView.findViewById(R.id.textItemMeta);
            RatingBar rating = itemView.findViewById(R.id.ratingItem);
            rating.setVisibility(View.GONE);
            remove = itemView.findViewById(R.id.buttonItemAction);
            remove.setContentDescription(itemView.getContext()
                    .getString(R.string.action_delete));
        }
    }
}