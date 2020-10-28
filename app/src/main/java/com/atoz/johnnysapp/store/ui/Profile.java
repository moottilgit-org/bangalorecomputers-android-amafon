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
import com.atoz.johnnysapp.store.utils.Pref;

import org.json.JSONObject;

public class Profile extends ActivityEx implements View.OnClickListener {
    private static final String TAG = Profile.class.getSimpleName();

    TextInputEditText edFullName, edMobile, edEmail;
    Button btnCancel, btnUpdate, btnPaymentInfo, btnChangePassword;
    TextView tvSignupError;
    ProgressBar pbSignUpClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edFullName= findViewById(R.id.edFullName);
        edMobile= findViewById(R.id.edMobile);
        edEmail= findViewById(R.id.edEmail);
        btnCancel= findViewById(R.id.btnCancel);
        btnUpdate= findViewById(R.id.btnUpdate);
        tvSignupError= findViewById(R.id.tvSignupError);
        pbSignUpClick= findViewById(R.id.pbSignUpClick);
        btnPaymentInfo= findViewById(R.id.btnPaymentInfo);
        btnChangePassword= findViewById(R.id.btnChangePassword);

        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnPaymentInfo.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);

        pbSignUpClick.setVisibility(View.GONE);

        edFullName.setText(Pref.getFullName(mContext));
        edMobile.setText(Pref.getMobile(mContext));
        edEmail.setText(Pref.getEmail(mContext));
        edFullName.post(()->{showFocus(this, edFullName);});
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnPaymentInfo:
                    startActivity(new Intent(this, UpiInfo.class));
                break;
                case R.id.btnChangePassword:
                    startActivity(new Intent(this, ChangePWD.class));
                break;
                case R.id.btnCancel:
                    finish();
                break;
                case R.id.btnUpdate:
                    updateProfile();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProfile() {
        try {
            if(edFullName.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_full_name);
                edFullName.requestFocus();
                return;
            }

            showError("");
            showLoadingSignUp(true);
            Customer.update(this, edFullName.getText().toString(), result -> {
                try {
                    showLoadingSignUp(false);
                    if (result != null) {
                        String resultStr = result.getString("result");
                        if(resultStr.equalsIgnoreCase("success")) {
                            showError("");
                            JSONObject data= result.getJSONObject("data");
                            Pref.setFullName(mContext, data.getString("fullname"));
                            try {
                                if(Main.mInstance != null) {
                                    Main.mInstance.tvShopName.setText(Pref.getFullName(mContext));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
            showLoadingSignUp(false);
        }
    }

    private void showLoadingSignUp(boolean show) {
        try {
            int visibility1= show?View.VISIBLE:View.GONE;
            int visibility2= show?View.GONE:View.VISIBLE;
            pbSignUpClick.setVisibility(visibility1);
            btnCancel.setVisibility(visibility2);
            btnUpdate.setVisibility(visibility2);
            btnPaymentInfo.setVisibility(visibility2);
            btnChangePassword.setVisibility(visibility2);
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
