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
    private List<MessageItem> items = new ArrayList<>();
    private String username;

    public MessageAdapter(UserDetailActivity parent, String username) {
        this.parent = parent;
        this.username = username;
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

        if(username.equals(item.getFromUser())) {
            holder.tvUserName.setText("To " + item.getToUser());
        }
        else {
            holder.tvUserName.setText("From " + item.getFromUser());
        }
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
