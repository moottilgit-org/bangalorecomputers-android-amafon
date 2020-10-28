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

public class ChangePWD extends ActivityEx implements View.OnClickListener {
    private static final String TAG = ChangePWD.class.getSimpleName();

    TextInputEditText edPasswordO, edPassword, edPasswordC;
    Button btnCancel, btnChangePwd;
    TextView tvSignupError;
    ProgressBar pbSignUpClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        edPasswordO= findViewById(R.id.edPasswordO);
        edPassword= findViewById(R.id.edPassword);
        edPasswordC= findViewById(R.id.edPasswordC);
        btnCancel= findViewById(R.id.btnCancel);
        btnChangePwd= findViewById(R.id.btnChangePwd);
        tvSignupError= findViewById(R.id.tvSignupError);
        pbSignUpClick= findViewById(R.id.pbSignUpClick);

        btnCancel.setOnClickListener(this);
        btnChangePwd.setOnClickListener(this);

        pbSignUpClick.setVisibility(View.GONE);

        edPasswordO.post(()->edPasswordO.requestFocus());
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnCancel:
                    finish();
                break;
                case R.id.btnChangePwd:
                    signup();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signup() {
        try {
            if(edPasswordO.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_opassword);
                edPasswordO.requestFocus();
                return;
            }

            if(!edPasswordO.getText().toString().equals(Pref.getPassword(mContext))) {
                showError(R.string.error_invalid_opassword);
                edPasswordO.requestFocus();
                return;
            }

            if(edPassword.getText().toString().isEmpty()) {
                showError(R.string.error_please_enter_password);
                edPassword.requestFocus();
                return;
            }
            if(!edPasswordC.getText().toString().equals(edPassword.getText().toString())) {
                showError(R.string.error_confirm_password_is_not_matching);
                edPassword.requestFocus();
                return;
            }

            showError("");
            showLoadingSignUp(true);
            Customer.changePassword(this, edPassword.getText().toString(), result -> {
                try {
                    showLoadingSignUp(false);
                    if (result != null) {
                        String resultStr = result.getString("result");
                        if(resultStr.equalsIgnoreCase("success")) {
                            showError("");
                            JSONObject data= result.getJSONObject("data");

                            Pref.setPassword(mContext, edPassword.getText().toString());

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
            btnChangePwd.setVisibility(visibility2);
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
