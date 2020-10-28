package com.atoz.johnnysapp.store.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.online.Customer;
import com.atoz.johnnysapp.store.receivers_services.Firebase_InstanceIDService;
import com.atoz.johnnysapp.store.receivers_services.Firebase_MessagingService;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.Pref;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

public class Login extends ActivityEx implements View.OnClickListener {
    private static final String TAG = Login.class.getSimpleName();

    TextInputEditText edUserName, edPassword;
    Button btnCancel, btnSignIn, btnForgot, btnSignUp;
    TextView tvLoginError;
    ProgressBar pbSignInClick;
    boolean autoLogin= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUserName= findViewById(R.id.edUserName);
        edPassword= findViewById(R.id.edPassword);
        btnCancel= findViewById(R.id.btnCancel);
        btnSignIn= findViewById(R.id.btnSignIn);
        tvLoginError= findViewById(R.id.tvLoginError);
        pbSignInClick= findViewById(R.id.pbSignInClick);
        btnForgot= findViewById(R.id.btnForgot);
        btnSignUp= findViewById(R.id.btnSignUp);

        btnCancel.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnForgot.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        edUserName.setText(Pref.getMobile(mContext));
        edPassword.setText(Pref.getPassword(mContext));

        pbSignInClick.setVisibility(View.GONE);

        if(Pref.init(mContext).getBoolean(Pref.IS_LOGGED_IN, false)) {
            autoLogin= true;
            login();
        } else {
            autoLogin= false;
            edPassword.setText("");
            edUserName.requestFocus();
        }
        Firebase_MessagingService.getUniqueID(this);
        Firebase_InstanceIDService.getFCMToken(mContext, () -> {
            Firebase_MessagingService.sendFCMToServer(mContext, FirebaseInstanceId.getInstance().getToken());
            Firebase_InstanceIDService.subscribeToTopic(Firebase_InstanceIDService.TOPIC_APP_UPDATE);
            Firebase_InstanceIDService.subscribeToTopic(Firebase_InstanceIDService.TOPIC_APP_ALERTS);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, Preferences.class), 1);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnCancel:
                    finish();
                break;
                case R.id.btnSignIn:
                    login();
                break;
                case R.id.btnForgot:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://smebusinesssoftware.com/johnnysapp/store/reset-password.html"));
                    startActivity(intent);
                break;
                case R.id.btnSignUp:
                    startActivity(new Intent(this, Register.class));
                    finish();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        try {
            if(edUserName.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_login_id);
                edUserName.requestFocus();
                return;
            }
            if(edPassword.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_password);
                edPassword.requestFocus();
                return;
            }

            if(autoLogin) {
                Intent intent = new Intent(mContext, Main.class);
                if (getIntent().getExtras() != null) {
                    intent.putExtras(getIntent().getExtras());
                }
                startActivity(intent);
                finish();
                return;
            }
            showError("");
            showLoadingSignIn(true);

            Customer.login(this, edUserName.getText().toString(), edPassword.getText().toString(), result -> {
                try {
                    showLoadingSignIn(false);
                    if (result != null) {
                        String resultStr = result.getString("result");
                        if(resultStr.equalsIgnoreCase("success")) {
                            showError("");
                            JSONObject data= result.getJSONObject("data");

                            Pref.setPassword(mContext, edPassword.getText().toString());
                            Pref.setUID(mContext, data.getInt("id"));
                            Pref.setFullName(mContext, data.getString("fullname"));
                            Pref.setEmail(mContext, data.getString("email_id"));
                            Pref.setMobile(mContext, data.getString("phone_number"));
                            Pref.setUPI(mContext, data.getString("upi_id"));
                            Pref.setUPIText(mContext, data.getString("upi_text"));
                            Pref.init(mContext).setBoolean(Pref.IS_LOGGED_IN, true);

                            if(!autoLogin) {
                                Intent intent = new Intent(mContext, Main.class);
                                if (getIntent().getExtras() != null) {
                                    intent.putExtras(getIntent().getExtras());
                                }
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            showError(result.getString("msg"));
                        }
                    } else {
                        showError(R.string.error_connection_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showLoadingSignIn(false);
        }
    }

    private void showLoadingSignIn(boolean show) {
        try {
            int visibility1= show?View.VISIBLE:View.GONE;
            int visibility2= show?View.GONE:View.VISIBLE;
            pbSignInClick.setVisibility(visibility1);
            btnCancel.setVisibility(visibility2);
            btnSignIn.setVisibility(visibility2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(int error) {
        showError(error > 0 ? getString(error) : "");
    }

    private void showError(String error) {
        if(error==null || error.isEmpty()) {
            tvLoginError.setVisibility(View.GONE);
            tvLoginError.setText("");
        } else {
            tvLoginError.setVisibility(View.VISIBLE);
            tvLoginError.setText(error);
        }
    }
}
