package hitec.com.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hitec.com.R;
import hitec.com.util.SharedPrefManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining views
    private Button buttonRegister;
    private EditText editTextEmail;
    private ProgressDialog progressDialog;

    //URL to RegisterDevice.php
    private static final String URL_REGISTER_DEVICE = "http://192.168.2.113/track/RegisterDevice.php";

    private static class MyTimeTask extends TimerTask
    {

        public void run()
        {
            //write your code here
            Log.v("Time Schedule", "Executed");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter.parse("2017-04-13 01:41:40");

            //Now create the time and schedule it
            Timer timer = new Timer();

            //Use this if you want to execute it once
            timer.schedule(new MyTimeTask(), date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        //Get Alarms
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo info = alarm.getNextAlarmClock();
        long time = info.getTriggerTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Log.v("Time:", cal.getTime().toString());

        //getting views from xml
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        //adding listener to view
        buttonRegister.setOnClickListener(this);
    }

    //storing token to mysql server
    private void sendTokenToServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = editTextEmail.getText().toString();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage("Hello");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();*/
            sendTokenToServer();
        }
    }
}