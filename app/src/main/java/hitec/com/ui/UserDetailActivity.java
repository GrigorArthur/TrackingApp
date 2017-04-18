package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.MessageAdapter;
import hitec.com.adapter.UserAdapter;
import hitec.com.event.GetUserMessagesEvent;
import hitec.com.event.GetUsersEvent;
import hitec.com.model.MessageItem;
import hitec.com.model.UserItem;
import hitec.com.notification.TrackingService;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUserMessagesTask;
import hitec.com.task.GetUsersTask;
import hitec.com.task.SendLocationTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUserMessagesResponseVO;
import hitec.com.vo.GetUsersResponseVO;

public class UserDetailActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.message_list)
    RecyclerView messageList;

    private ProgressDialog progressDialog;
    private MessageAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ButterKnife.bind(this);

        username = getIntent().getStringExtra("username");

        messageList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(UserDetailActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(mLinearLayoutManager);
        messageList.addItemDecoration(new DividerItemDecoration(UserDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new MessageAdapter(UserDetailActivity.this);
        messageList.setAdapter(adapter);

        progressDialog = new ProgressDialog(UserDetailActivity.this);
        getUserMessages();
    }

    @Subscribe
    public void onGetUserMessagesEvent(GetUserMessagesEvent event) {
        hideProgressDialog();
        GetUserMessagesResponseVO responseVo = event.getResponse();
        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                String messages = responseVo.messages;
                refreshList(messages);
            } else {
                networkError();
            }
        } else {
            networkError();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    private void getUserMessages() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUserMessagesTask task = new GetUserMessagesTask();

        task.execute(username);
    }

    private void refreshList(String users) {
        ArrayList<MessageItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(users);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String username = json.getString("to_user");
                String message = json.getString("message");
                String time = json.getString("time");

                MessageItem item = new MessageItem();
                item.setUserName(username);
                item.setMessage(message);
                item.setTime(time);

                items.add(item);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(UserDetailActivity.this, getResources().getString(R.string.network_error));
    }
}