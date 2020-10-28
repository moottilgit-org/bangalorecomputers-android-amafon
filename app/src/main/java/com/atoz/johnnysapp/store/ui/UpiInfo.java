package com.atoz.johnnysapp.store.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.barcodereader.BarcodeCaptureActivity;
import com.atoz.johnnysapp.store.online.AsyncListener;
import com.atoz.johnnysapp.store.online.ProgressHelper;
import com.atoz.johnnysapp.store.online.Writer;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.Pref;
import com.atoz.johnnysapp.store.utils.Toast;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class UpiInfo extends ActivityEx implements View.OnClickListener {
    EditText edUPIID;
    String UPI_URI= "";
    Button btnClear, btnScan, btnSave;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_info);
        Toolbar toolbar= findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        edUPIID= findViewById(R.id.edUPIID);
        btnClear= findViewById(R.id.btnClear);
        btnScan= findViewById(R.id.btnScan);
        btnSave= findViewById(R.id.btnSave);

        btnClear.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        edUPIID.setText(Pref.getUPI(this));
        UPI_URI= Pref.getUPIText(this);
        edUPIID.setEnabled(TextUtils.isEmpty(UPI_URI));
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnClear:
                    edUPIID.setText("");
                    UPI_URI= "";
                    edUPIID.setEnabled(true);
                break;
                case R.id.btnScan:
                    Intent intent= new Intent(this, BarcodeCaptureActivity.class);
                    startActivityForResult(intent, REQ_COLLECT_MORE);
                break;
                case R.id.btnSave:
                    ProgressHelper helper= ProgressHelper.get(mContext);
                    Writer.update_upi(mContext, edUPIID.getText().toString(), UPI_URI, new AsyncListener() {
                        @Override
                        public void onStart() {
                            helper.show("Updating Payment info...");
                        }
                        @Override
                        public void onProgressUpdate(Integer... values) { }
                        @Override
                        public void onFinish() {
                            helper.hide();
                        }
                        @Override
                        public void onResult(Boolean result, Http.Response response) {
                            try {
                                if(result) {
                                    Pref.setUPI(mContext, edUPIID.getText().toString());
                                    Pref.setUPIText(mContext, UPI_URI);
                                    finish();
                                } else {
                                    Toast.makeText(mContext, "Unable to update Payment info, " + response.error(), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQ_COLLECT_MORE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode= data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    UPI_URI= barcode.displayValue;
                    UPI_URI= TextUtils.isEmpty(UPI_URI)?"":UPI_URI;
                    if(UPI_URI.startsWith("upi://")) {
                        String temp= UPI_URI.split("pa=")[1];
                        if(temp.contains("&")) {
                            temp= temp.split("&")[0];
                        }
                        edUPIID.setText(temp);
                        edUPIID.setEnabled(false);
                    } else {
                        edUPIID.setText(UPI_URI);
                        UPI_URI= "";
                        edUPIID.setEnabled(true);
                    }
                } else {
                    finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
