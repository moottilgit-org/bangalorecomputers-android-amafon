package com.atoz.johnnysapp.store.ui.error_and_feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.utils.Toast;

public class ActivitySendError extends AppCompatActivity implements View.OnClickListener {
	private TextView tvErrorDetails;
	private ScrollView svErrorDetails;
	private Button btnClose, btnSendReport;
	private RelativeLayout rlSender;

	private String mErrorString= "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false); // prevent users from dismissing the dialog by tapping outside

		setContentView(R.layout.activity_send_error_report);

        DisplayMetrics metrics= getResources().getDisplayMetrics();
        getWindow().setLayout((int) (metrics.widthPixels * 0.96), (int) (metrics.heightPixels * 0.80));


		tvErrorDetails= findViewById(R.id.tvErrorDetails);
		svErrorDetails= findViewById(R.id.svErrorDetails);
		btnClose= findViewById(R.id.btnClose);
		btnSendReport= findViewById(R.id.btnSendReport);
		rlSender= findViewById(R.id.rlSender);

		mErrorString= getIntent().getStringExtra(Intent.EXTRA_TEXT);
		mErrorString= TextUtils.isEmpty(mErrorString)?"":(getResources().getString(R.string.error_details)) + "\n" + mErrorString;
		tvErrorDetails.setText(mErrorString);

		rlSender.setVisibility(View.GONE);

		setupTextView();
		setupButtons();
	}

	private void setupTextView() {
		try {
			setupTextView(tvErrorDetails, svErrorDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupButtons() {
		try {
			btnClose.setOnClickListener(this);
			btnSendReport.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
				case R.id.btnClose:
					finish();
				break;
				case R.id.btnSendReport:
					sendErrorReport();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if(rlSender.getVisibility()==View.VISIBLE) {
			return;
		}
		super.onBackPressed();
	}

	private void sendErrorReport() {
		try {
			RetrofitApiClass.errorReport(this, mErrorString, new RetrofitApiClass.AsyncListener() {
				@Override
				public void onStart() {
					btnClose.setEnabled(false);
					btnSendReport.setEnabled(false);
					tvErrorDetails.setEnabled(false);
					rlSender.setVisibility(View.VISIBLE);
				}
				@Override
				public void onFinish() {
					btnClose.setEnabled(true);
					btnSendReport.setEnabled(true);
					tvErrorDetails.setEnabled(true);
					rlSender.setVisibility(View.GONE);
				}
				@Override
				public void onResult(boolean result) {
					if(result) {
						Toast.makeText(ActivitySendError.this, R.string.send_error_success, Toast.LENGTH_LONG).show();
						finish();
					} else {
						Toast.makeText(ActivitySendError.this, R.string.send_error_error, Toast.LENGTH_LONG).show();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setupTextView(TextView textView, View parent) {
		try {
			if(TextUtils.isEmpty(textView.getText().toString().trim())) {
				parent.setVisibility(View.GONE);
			} else {
				parent.setVisibility(View.VISIBLE);
				textView.setMaxLines(1);
				textView.post(() -> {
					if(textView.getLineCount()> 1) {
						textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
						textView.setOnClickListener((v) -> {
							if (textView.getMaxLines() == 1) {
								textView.setMaxLines(100);
								textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_up, 0);
							} else {
								textView.setMaxLines(1);
								textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
							}
						});
					} else {
						textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
