package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.event.SendAdminNotificationEvent;
import hitec.com.event.SendNotificationEvent;
import hitec.com.proxy.SendAdminNotificationProxy;
import hitec.com.proxy.SendNotificationProxy;
import hitec.com.task.SendAdminNotificationTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.util.StringUtil;
import hitec.com.vo.SendAdminNotificationResponseVO;
import hitec.com.vo.SendNotificationResponseVO;

public class PostStatusActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Bind(R.id.edt_users)
    TextInputEditText edtUsers;
    @Bind(R.id.edt_message)
    TextInputEditText edtMessage;

    private Animation shake;

    private static final int REQUEST_SELECT_USER = 1;
    private String selectedUsers = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_status);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtUsers.setKeyListener(null);
        edtUsers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Intent intent = new Intent(PostStatusActivity.this, SelectUserActivity.class);
                        startActivityForResult(intent, REQUEST_SELECT_USER);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        shake = AnimationUtils.loadAnimation(PostStatusActivity.this, R.anim.edittext_shake);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onSendNotificationEvent(SendNotificationEvent event) {
        hideProgressDialog();
        SendNotificationResponseVO responseVO = event.getResponse();
        if(responseVO != null && responseVO.success == SendNotificationProxy.RESPONSE_SUCCESS) {
            postSuccess();
        } else {
            networkError();
        }
    }

    @Subscribe
    public void onSendAdminNotificationEvent(SendAdminNotificationEvent event) {
        hideProgressDialog();
        SendAdminNotificationResponseVO responseVO = event.getResponse();
        if(responseVO != null && responseVO.success == SendAdminNotificationProxy.RESPONSE_SUCCESS) {
            postSuccess();
        } else {
            networkError();
        }
    }

    private boolean checkMessage() {
        if (StringUtil.isEmpty(edtMessage.getText().toString())) {
            showInfoNotice(edtMessage);
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

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @OnClick(R.id.btn_post)
    void onClickBtnPost() {
        if(!checkMessage())
            return;

        progressDialog.setMessage(getResources().getString(R.string.posting));
        progressDialog.show();
        if(selectedUsers.isEmpty()) {
            //Send to Admins.
            String sender = SharedPrefManager.getInstance(PostStatusActivity.this).getUsername();
            String customerID = SharedPrefManager.getInstance(PostStatusActivity.this).getCustomerID();
            String message = edtMessage.getText().toString();

            SendAdminNotificationTask task = new SendAdminNotificationTask();
            task.execute(sender, customerID, message);
        } else {
            //Send to Users
            String sender = SharedPrefManager.getInstance(PostStatusActivity.this).getUsername();
            String message = edtMessage.getText().toString();
            SendNotificationTask task = new SendNotificationTask();
            task.execute(sender, selectedUsers, message);
        }
    }

    private void postSuccess() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PostStatusActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.status_posted));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(PostStatusActivity.this, getResources().getString(R.string.network_error));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_SELECT_USER && resultCode == RESULT_OK) {
            selectedUsers = intent.getStringExtra("users");
            edtUsers.setText(selectedUsers);
        }
    }
}