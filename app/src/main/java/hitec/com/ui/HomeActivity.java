package hitec.com.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.UserAdapter;
import hitec.com.event.GetUsersEvent;
import hitec.com.model.UserItem;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUsersTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUsersResponseVO;

public class HomeActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.user_list)
    RecyclerView branchList;

    private ProgressDialog progressDialog;
    private UserAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        branchList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        branchList.setLayoutManager(mLinearLayoutManager);
        branchList.addItemDecoration(new DividerItemDecoration(HomeActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new UserAdapter(HomeActivity.this);
        branchList.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);

        getUsers();
    }

    @Subscribe
    public void onGetUserEvent(GetUsersEvent event) {
        hideProgressDialog();
        GetUsersResponseVO responseVo = event.getResponse();
        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                String users = responseVo.users;
                refreshList(users);
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

    private void getUsers() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUsersTask task = new GetUsersTask();
        String username = SharedPrefManager.getInstance(this).getUsername();
        String customerID = SharedPrefManager.getInstance(this).getCustomerID();
        String usertype = String.valueOf(SharedPrefManager.getInstance(this).getUserType());

        task.execute(username, customerID, usertype);
    }

    private void refreshList(String users) {
        ArrayList<UserItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(users);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String username = json.getString("username");
                UserItem item = new UserItem();
                item.setUserName(username);
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
        ApplicationContext.showToastMessage(HomeActivity.this, getResources().getString(R.string.network_error));
    }
}