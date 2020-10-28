package com.atoz.johnnysapp.store.ui.error_and_feedback;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.FileUtils;
import com.atoz.johnnysapp.store.utils.ImageUtils;
import com.atoz.johnnysapp.store.utils.PermissionManager;
import com.atoz.johnnysapp.store.utils.Toast;
import com.mixiaoxiao.fastscroll.helper.RecyclerListAdapter;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivitySendFeedback extends ActivityEx implements View.OnClickListener {
	private EditText edFeedback;
	private Button btnClose, btnSendFeedback;
	private RelativeLayout rlSender;
	private ImageButton btnAttach;
	private com.mixiaoxiao.fastscroll.FastScrollRecyclerView rvAttachments;
	private ArrayList<Bundle> alAttachments;
	private RecyclerListAdapter<Bundle> raAttachments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false); // prevent users from dismissing the dialog by tapping outside

		setContentView(R.layout.activity_send_feedback);

        DisplayMetrics metrics= getResources().getDisplayMetrics();
        getWindow().setLayout((int) (metrics.widthPixels * 0.96), (int) (metrics.heightPixels * 0.80));

		edFeedback= findViewById(R.id.edFeedback);
		btnClose= findViewById(R.id.btnClose);
		btnSendFeedback= findViewById(R.id.btnSendFeedback);
		rlSender= findViewById(R.id.rlSender);
		rvAttachments= findViewById(R.id.rvAttachments);
		btnAttach= findViewById(R.id.btnAttach);
		rlSender.setVisibility(View.GONE);

		setupButtons();
		setupAttachments();
	}

	private void setupButtons() {
		try {
			btnAttach.setOnClickListener(this);
			btnClose.setOnClickListener(this);
			btnSendFeedback.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupAttachments() {
		try {
			alAttachments= new ArrayList<>();
			raAttachments= new RecyclerListAdapter<>(this, rvAttachments, R.layout.lv_attachment_small, alAttachments,
					0, 2, null, new RecyclerListAdapter.Binder() {
				@Override
				public void onBindView(RecyclerListAdapter mAdapter, RecyclerListAdapter.ItemViewHolder holder, int mPosition) {
					try {
						TextView tvName= holder.mItemView.findViewById(R.id.tvName);
						ImageButton btnRemove= holder.mItemView.findViewById(R.id.btnRemove);

						tvName.setText(alAttachments.get(mPosition).getString("NAME"));
						btnRemove.setTag(""+mPosition);
						btnRemove.setOnClickListener(v -> {
							try {
								int position= Integer.parseInt(v.getTag().toString());
								alAttachments.remove(position);
								raAttachments.notifyDataSetChanged();
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onClick(RecyclerListAdapter mAdapter, View view, int position) {
					try {
						FileUtils.openAFile(ActivitySendFeedback.this, alAttachments.get(position).getString("FILE"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public boolean onLongClick(RecyclerListAdapter mAdapter, View view, int position) {
					return false;
				}
				@Override
				public boolean onItemDismiss(RecyclerListAdapter mAdapter, int position) {
					return false;
				}
				@Override
				public boolean onItemMove(RecyclerListAdapter mAdapter, int fromPosition, int toPosition) {
					return false;
				}
			});
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
				case R.id.btnSendFeedback:
					sendFeedback();
				break;
				case R.id.btnAttach:
					PermissionManager.getInstance(this).set(result -> {
						if(PermissionManager.hasSelfPermission(this, PermissionManager.PERMISSION_STORAGE)) {
							Intent i = new Intent(Intent.ACTION_GET_CONTENT);
							i.addCategory(Intent.CATEGORY_OPENABLE);
							i.setType("image/*");
							startActivityForResult(Intent.createChooser(i,"File Chooser"), ActivityEx.REQ_ATTACH);
						}
					}).request(PermissionManager.PERMISSION_STORAGE);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if(requestCode== ActivityEx.REQ_ATTACH && resultCode==RESULT_OK && data != null) {
			try {
				Uri uri = data.getData();
				//Bitmap CurrentBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				File file= getResizedFile(this, uri, false);
				if(file != null) {
					Bundle bundle= new Bundle();
					bundle.putString("NAME", file.getName());
					bundle.putString("FILE", file.getAbsolutePath());
					bundle.putLong("SIZE", file.length());
					alAttachments.add(bundle);
					raAttachments.notifyItemInserted(alAttachments.size() - 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		if(rlSender.getVisibility()==View.VISIBLE) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onPause() {
		hideFocus(edFeedback);
		super.onPause();
	}

	private File createImageFile() throws IOException {
		return createImageFile(mContext);
	}

	private static File createImageFile(Context context) throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "img_" + timeStamp + "_";
		File storageDir = context.getExternalCacheDir();
		return File.createTempFile(imageFileName, ".jpg", storageDir);
	}
	public static File getResizedFile(Context context, Uri uri, boolean isFile) {
		try {
			File file= createImageFile(context);
			File source= null;
			Bitmap bitmap;
			if(isFile) {
				source= new File(uri.getPath());
				bitmap= getBitmapFromFile(context, source.getAbsolutePath());
			} else {
				bitmap= getBitmapFromUri(context, uri);
				if(bitmap==null) {
					bitmap= MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
				}
			}
			if(ImageUtils.resizeBitmap(bitmap, file, 576, 768)) {
				if(isFile) {
					source.delete();
				}
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Bitmap getBitmapFromUri(Uri imageUri) {
		return getBitmapFromUri(mContext, imageUri);
	}
	private static Bitmap getBitmapFromUri(Context context, Uri imageUri) {
		try {
			String[] filePathColumn = {MediaStore.Images.Media.DATA};
			try(Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null)) {
				if (cursor != null) {
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					return getBitmapFromFile(context, picturePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private Bitmap getBitmapFromFile(String picturePath) {
		return getBitmapFromFile(mContext, picturePath);
	}
	private static Bitmap getBitmapFromFile(Context context, String picturePath) {
		try {
			Bitmap loadedBitmap= BitmapFactory.decodeFile(picturePath);

			ExifInterface exif = null;
			try {
				File pictureFile = new File(picturePath);
				exif = new ExifInterface(pictureFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}

			int orientation = ExifInterface.ORIENTATION_NORMAL;
			if (exif != null) {
				orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			}
			Bitmap newBitmap= null;
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					newBitmap = rotateBitmap(loadedBitmap, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					newBitmap = rotateBitmap(loadedBitmap, 180);
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					newBitmap = rotateBitmap(loadedBitmap, 270);
					break;
			}
			if(newBitmap==null) {
				newBitmap= loadedBitmap;
			} else if(!loadedBitmap.isRecycled()) {
				loadedBitmap.recycle();
			}
			return newBitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BitmapFactory.decodeFile(picturePath);
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(degrees);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private void sendFeedback() {
		try {
			RetrofitApiClass.feedback(this, edFeedback.getText().toString(), alAttachments, new RetrofitApiClass.AsyncListener() {
				@Override
				public void onStart() {
					btnClose.setEnabled(false);
					btnSendFeedback.setEnabled(false);
					btnAttach.setEnabled(false);
					rlSender.setVisibility(View.VISIBLE);
				}
				@Override
				public void onFinish() {
					btnClose.setEnabled(true);
					btnSendFeedback.setEnabled(true);
					btnAttach.setEnabled(true);
					rlSender.setVisibility(View.GONE);
				}
				@Override
				public void onResult(boolean result) {
					if(result) {
						Toast.makeText(ActivitySendFeedback.this, R.string.send_feedback_success, Toast.LENGTH_LONG).show();
						finish();
					} else {
						Toast.makeText(ActivitySendFeedback.this, R.string.send_feedback_error, Toast.LENGTH_LONG).show();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
