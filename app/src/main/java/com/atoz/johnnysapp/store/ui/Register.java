package com.atoz.johnnysapp.store.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.online.Customer;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.PhoneNumberCleaner;
import com.atoz.johnnysapp.store.utils.Pref;

import org.json.JSONObject;

public class Register extends ActivityEx implements View.OnClickListener {
    private static final String TAG = Register.class.getSimpleName();

    TextInputEditText edFullName, edMobile, edEmail, edPassword;
    Button btnCancel, btnSignUp, btnSignIn;
    TextView tvSignupError;
    ProgressBar pbSignUpClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edFullName= findViewById(R.id.edFullName);
        edMobile= findViewById(R.id.edMobile);
        edEmail= findViewById(R.id.edEmail);
        edPassword= findViewById(R.id.edPassword);
        btnSignIn= findViewById(R.id.btnSignIn);
        btnCancel= findViewById(R.id.btnCancel);
        btnSignUp= findViewById(R.id.btnSignUp);
        tvSignupError= findViewById(R.id.tvSignupError);
        pbSignUpClick= findViewById(R.id.pbSignUpClick);

        btnSignIn.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        pbSignUpClick.setVisibility(View.GONE);

        if(Pref.getUID(this) > 0) {
            startActivity(new Intent(this, Login.class));
            finish();
        } else {
            edFullName.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnSignIn:
                    startActivity(new Intent(this, Login.class));
                    finish();
                break;
                case R.id.btnCancel:
                    finish();
                break;
                case R.id.btnSignUp:
                    signup();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signup() {
        try {
            if(edFullName.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_full_name);
                edFullName.requestFocus();
                return;
            }
            if(edMobile.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_mobile_number);
                edMobile.requestFocus();
                return;
            }
            if(edEmail.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_email_id);
                edEmail.requestFocus();
                return;
            }
            if(edPassword.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_password);
                edPassword.requestFocus();
                return;
            }

            showError("");
            showLoadingSignUp(true);
            Customer.register(this, edFullName.getText().toString(),
                    PhoneNumberCleaner.format(edMobile.getText().toString()), edEmail.getText().toString(),
                    edPassword.getText().toString(), result -> {
                try {
                    showLoadingSignUp(false);
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


                            Intent intent = new Intent(mContext, Main.class);
                            if (getIntent().getExtras() != null) {
                                intent.putExtras(getIntent().getExtras());
                            }
                            startActivity(intent);
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
            showLoadingSignUp(false);
        }
    }

    private void showLoadingSignUp(boolean show) {
        try {
            int visibility1= show?View.VISIBLE:View.GONE;
            int visibility2= show?View.GONE:View.VISIBLE;
            pbSignUpClick.setVisibility(visibility1);
            btnCancel.setVisibility(visibility2);
            btnSignUp.setVisibility(visibility2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(int error) {
        showError(error > 0 ? getString(error) : "");
    }

    private void showError(String error) {
        if(error==null || error.isEmpty()) {
            tvSignupError.setVisibility(View.GONE);
            tvSignupError.setText("");
        } else {
            tvSignupError.setVisibility(View.VISIBLE);
            tvSignupError.setText(error);
        }
    }
}
