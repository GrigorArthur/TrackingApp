package hitec.com.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.R;
import hitec.com.model.UserItem;
import hitec.com.ui.HomeActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private HomeActivity parent;
    private List<UserItem> items = new ArrayList<>();

    public UserAdapter(HomeActivity parent) {
        this.parent = parent;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        UserItem item = items.get(position);

        holder.tvUserName.setText(item.getUsername());

        holder.view.setTag(position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int)view.getTag();
                UserItem item = items.get(position);
                Log.v("Selected User", item.getUsername());
                /*Intent intent = new Intent(parent, BranchDetailActivity.class);
                intent.putExtra("item", item);
                parent.startActivity(intent);*/
            }
        });
    }

    public UserItem getItem(int pos) {
        return items.get(pos);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(UserItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<UserItem> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @Bind(R.id.tv_username)
        TextView tvUserName;

        public UserViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
