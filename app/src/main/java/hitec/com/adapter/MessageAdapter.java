package hitec.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.R;
import hitec.com.model.MessageItem;
import hitec.com.model.UserItem;
import hitec.com.ui.UserDetailActivity;
import hitec.com.util.SharedPrefManager;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private UserDetailActivity parent;
    private int usertype;
    private List<MessageItem> items = new ArrayList<>();

    public MessageAdapter(UserDetailActivity parent) {
        this.parent = parent;
        this.usertype = SharedPrefManager.getInstance(parent).getUserType();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_messages, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        MessageItem item = items.get(position);

        holder.tvUserName.setText(item.getUsername());
        holder.tvMessage.setText(item.getMessage());
        holder.tvTime.setText(item.getTIme());
    }

    public MessageItem getItem(int pos) {
        return items.get(pos);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(MessageItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<MessageItem> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @Bind(R.id.tv_username)
        TextView tvUserName;
        @Bind(R.id.tv_message)
        TextView tvMessage;
        @Bind(R.id.tv_time)
        TextView tvTime;

        public MessageViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
