package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import hitec.com.adapter.UserAdapter;
import hitec.com.event.GetUsersEvent;
import hitec.com.model.UserItem;
import hitec.com.notification.TrackingService;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUsersTask;
import hitec.com.task.SendLocationTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUsersResponseVO;

public class HomeActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.user_list)
    RecyclerView userList;

    private ProgressDialog progressDialog;
    private UserAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            try {
                Geocoder gCoder = new Geocoder(getApplicationContext());
                List<Address> addresses = gCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                String strAdd = strReturnedAddress.toString();
                String sender = SharedPrefManager.getInstance(getApplicationContext()).getUsername();

                SendLocationTask task = new SendLocationTask();
                task.execute(sender, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), strAdd);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, mLocationListener);
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, mLocationListener);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }*/

        ButterKnife.bind(this);

        userList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userList.setLayoutManager(mLinearLayoutManager);
        userList.addItemDecoration(new DividerItemDecoration(HomeActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new UserAdapter(HomeActivity.this);
        userList.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        startService(new Intent(HomeActivity.this, TrackingService.class));
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

    public void sendNotification(final String receiver) {
        //Show Tag Request Dialog
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dlg_input_tag, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText edtTag = (EditText) promptView.findViewById(R.id.edt_tag);

        alertDialogBuilder.setCancelable(false)
                .setTitle(R.string.input_tag)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sender = SharedPrefManager.getInstance(HomeActivity.this).getUsername();
                        String message = edtTag.getText().toString();
                        SendNotificationTask task = new SendNotificationTask();
                        task.execute(sender, receiver, message);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}