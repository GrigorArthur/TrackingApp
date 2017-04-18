package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.event.RegisterEvent;
import hitec.com.notification.MyFirebaseInstanceIDService;
import hitec.com.notification.TrackingService;
import hitec.com.proxy.BaseProxy;
import hitec.com.proxy.RegisterProxy;
import hitec.com.task.RegisterTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.util.StringUtil;
import hitec.com.util.URLManager;
import hitec.com.vo.BaseResponseVO;
import hitec.com.vo.RegisterTokenRequestVO;
import hitec.com.vo.RegisterTokenResponseVO;

public class MainActivity extends AppCompatActivity {

    //defining views
    private ProgressDialog progressDialog;

    @Bind(R.id.edt_user_name)
    TextInputEditText edtUserName;
    @Bind(R.id.edt_customer_id)
    TextInputEditText edtCustomerId;
    @Bind(R.id.edt_password)
    TextInputEditText edtPassword;

    private Animation shake;

    private String username;
    private String customerID;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(!SharedPrefManager.getInstance(this).getFirstRun()) {
            startHomeActivity();
        }

        progressDialog = new ProgressDialog(this);
        shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.edittext_shake);
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

    @Subscribe
    public void onRegisterEvent(RegisterEvent event) {
        hideProgressDialog();
        RegisterTokenResponseVO responseVo = event.getResponse();

        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                int usertype = responseVo.usertype;
                SharedPrefManager.getInstance(this).saveUserName(username);
                SharedPrefManager.getInstance(this).saveCustomerID(customerID);
                SharedPrefManager.getInstance(this).saveFirstRun(false);
                SharedPrefManager.getInstance(this).saveUserType(usertype);

                startHomeActivity();
            } else {
                ApplicationContext.showToastMessage(MainActivity.this, getResources().getStringArray(R.array.register_result)[responseVo.error_code]);
            }
        } else {
            networkError();
        }
    }

    @OnClick(R.id.btn_sign_in)
    void onClickBtnSignIn() {
        username = edtUserName.getText().toString();
        customerID = edtCustomerId.getText().toString();
        password = edtPassword.getText().toString();

        if (!checkUserName()) return;
        if (!checkCustomerID()) return;
        if (!checkPassword()) return;

        startSignIn();
    }

    private boolean checkUserName() {
        if (StringUtil.isEmpty(username)) {
            showInfoNotice(edtUserName);
            return false;
        }

        return true;
    }

    private boolean checkCustomerID() {
        if (StringUtil.isEmpty(username)) {
            showInfoNotice(edtUserName);
            return false;
        }

        return true;
    }

    private boolean checkPassword() {
        if (StringUtil.isEmpty(username)) {
            showInfoNotice(edtUserName);
            return false;
        }

        return true;
    }

    private void showInfoNotice(TextInputEditText target) {
        target.startAnimation(shake);
        if (target.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //storing token to mysql server
    private void startSignIn() {
        progressDialog.setMessage(getResources().getString(R.string.signing_in));
        progressDialog.show();

        String token = SharedPrefManager.getInstance(this).getDeviceToken();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        RegisterTask task = new RegisterTask();
        task.execute(username, customerID, password, token);
    }

    //start Home Activity
    private void startHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(MainActivity.this, getResources().getString(R.string.network_error));
    }
}