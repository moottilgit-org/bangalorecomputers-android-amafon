package com.atoz.johnnysapp.store.ui.order_details;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.data.Table_OrderMessages;
import com.atoz.johnnysapp.store.online.AsyncListener;
import com.atoz.johnnysapp.store.online.ProgressHelper;
import com.atoz.johnnysapp.store.online.Reader;
import com.atoz.johnnysapp.store.online.Writer;
import com.atoz.johnnysapp.store.utils.FileUtils;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.PermissionManager;
import com.atoz.johnnysapp.store.utils.Toast;
import com.mixiaoxiao.fastscroll.helper.RecyclerListAdapter;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class ChatHelper implements View.OnClickListener {
    public static final String ATTACHED_AUDIO= "***voice***attachment***message***";

    public static final int VIEW_TYPE_TEXT= 0;
    public static final int VIEW_TYPE_AUDIO= 1;

    private final OrderDetails mContext;

    private LinearLayout llCompose;
    private CardView cvMessageRef;
    private TextView tvMessageRef;
    private ImageButton imgMessageRef;

    private ImageButton imgAudioRecord, imgImage, imgRecPlay, imgRecDelete, btnSendMessage;
    private EditText edCompose;
    private TextView tvTextInfo;

    private LinearLayout llRecording;
    private TextView tvRecording;
    private Chronometer cmRecording;

    String mSavePath;
    String mFileName;
    MediaPlayer mMediaPlayer;
    MediaRecorder mMediaRecorder;

    private int mMessageRefID= 0;
    private String mMessageRefSrc= "I";

    public ChatHelper(OrderDetails context) {
        mContext= context;
        try {
            File path= new File(mContext.getExternalCacheDir(), "audio");
            path.mkdirs();
            mSavePath= path.getAbsolutePath();

            llCompose= findViewById(R.id.llCompose);
            cvMessageRef= findViewById(R.id.cvMessageRef);
            tvMessageRef= findViewById(R.id.tvMessageRef);
            imgMessageRef= findViewById(R.id.imgMessageRef);

            imgAudioRecord= findViewById(R.id.imgAudioRecord);
            imgImage= findViewById(R.id.imgImage);
            imgRecPlay= findViewById(R.id.imgRecPlay);
            imgRecDelete= findViewById(R.id.imgRecDelete);
            btnSendMessage= findViewById(R.id.btnSendMessage);
            edCompose= findViewById(R.id.edCompose);
            tvTextInfo= findViewById(R.id.tvTextInfo);
            llRecording= findViewById(R.id.llRecording);
            tvRecording= findViewById(R.id.tvRecording);
            cmRecording= findViewById(R.id.cmRecording);

            imgMessageRef.setOnClickListener(this);
            imgImage.setOnClickListener(this);
            imgRecPlay.setOnClickListener(this);
            imgRecDelete.setOnClickListener(this);
            btnSendMessage.setOnClickListener(this);

            edCompose.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override
                public void afterTextChanged(Editable s) {
                    tvTextInfo.setText(String.valueOf(s.length()));
                }
            });

            imgAudioRecord.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            if(!PermissionManager.hasSelfPermission(mContext, PermissionManager.PERMISSION_RECORD)) {
                                promptPermission();
                                return false;
                            }
                            cancelRecording();
                            mFileName= "audio_"+ System.currentTimeMillis()+".amr";
                            File file= new File(mSavePath, mFileName);
                            file.createNewFile();

                            mMediaRecorder = new MediaRecorder();
                            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            mMediaRecorder.setOutputFile(file.getAbsolutePath());
                            try {
                                mMediaRecorder.prepare();
                                mMediaRecorder.start();
                            } catch (IllegalStateException | IOException e) {
                                Log.d("recorder", e.toString());
                            }

                            edCompose.setVisibility(View.GONE);
                            imgImage.setVisibility(View.GONE);
                            imgRecPlay.setVisibility(View.GONE);
                            imgRecDelete.setVisibility(View.GONE);

                            llRecording.setVisibility(View.VISIBLE);
                            tvRecording.setText("Recording...");
                            cmRecording.setBase(SystemClock.elapsedRealtime());
                            cmRecording.start();
                        } catch (Exception e) {
                            Log.d("ACTION_DOWN err", e.toString());
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        try {
                            if (mMediaRecorder!=null){
                                try { mMediaRecorder.stop(); } catch (Exception e) {/**/}
                                try { mMediaRecorder.release(); } catch (Exception e) {/**/}
                            }
                            imgRecPlay.setVisibility(View.VISIBLE);
                            imgRecDelete.setVisibility(View.VISIBLE);
                            imgAudioRecord.setVisibility(View.GONE);
                            tvRecording.setText("Voice Record.");
                            cmRecording.stop();
                        } catch (Exception e) {
                            Log.d("ACTION_UP err", e.toString());
                        }
                        break;

                    default:
                        break;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mContext.findViewById(id);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.imgMessageRef:
                    clearMessageRef();
                break;
                case R.id.imgRecPlay:
                    togglePlay();
                break;
                case R.id.imgRecDelete:
                    cancelRecording();
                break;
                case R.id.btnSendMessage:
                    sendAMessage();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        llCompose.setVisibility(View.GONE);
    }
    public void show() {
        llCompose.setVisibility(View.VISIBLE);
    }

    private void clearMessageRef() {
        try {
            mMessageRefID= 0;
            mMessageRefSrc= "";
            cvMessageRef.setVisibility(View.GONE);
        } catch (Exception e)  {
            e.printStackTrace();
        }
    }

    public void addMessageRef(int refID, String refSrc, String title) {
        try {
            mMessageRefID= refID;
            mMessageRefSrc= refSrc;
            tvMessageRef.setText(title);
            cvMessageRef.setVisibility(View.VISIBLE);
        } catch (Exception e)  {
            e.printStackTrace();
        }
    }

    private void togglePlay() {
        try {
            if((mMediaPlayer!=null) && (mMediaPlayer.isPlaying())){
                mMediaPlayer.pause();
                imgRecPlay.setImageResource(R.drawable.img_play);

            }else{
                mMediaPlayer = new MediaPlayer();
                File mf=new File(mSavePath + "/" + mFileName);
                if(!(mf.exists()||mf.canRead())){
                    Toast.makeText(mContext, "Audio File Missing. " + mf.toString(), Toast.LENGTH_LONG).show();
                    return ;
                }

                try {
                    mMediaPlayer.setDataSource(mf.getPath().toString());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();

                    imgRecPlay.setImageResource(R.drawable.img_pause);
                    mMediaPlayer.setOnCompletionListener(mp -> imgRecPlay.setImageResource(R.drawable.img_play));
                } catch (IOException e) {
                    Log.d("RecPlay err", e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void promptPermission() {
        try {
            PermissionManager.getInstance(mContext).request(PermissionManager.PERMISSION_RECORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void cancelRecording() {
        try {
            if(!TextUtils.isEmpty(mFileName)) {
                File file= new File(mSavePath, mFileName);
                if(file.exists()) {
                    file.delete();
                }
            }
            imgRecPlay.setVisibility(View.GONE);
            imgRecDelete.setVisibility(View.GONE);
            llRecording.setVisibility(View.GONE);
            imgAudioRecord.setVisibility(View.VISIBLE);
            edCompose.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAMessage() {
        try {
            if(edCompose.getVisibility()==View.VISIBLE && edCompose.getText().toString().trim().isEmpty()) {
                edCompose.requestFocus();
                return;
            }
            ProgressHelper helper= ProgressHelper.get(mContext);
            String message= edCompose.getText().toString().trim();
            File file= llRecording.getVisibility()==View.VISIBLE?new File(mSavePath, mFileName):null;
            if(file != null) {
                message= "***voice***attachment***message***";
            }
            Writer.message(mContext, mContext.ID, message, file, mMessageRefID, mMessageRefSrc, new AsyncListener() {
                @Override
                public void onStart() {
                    helper.show(R.string.sending_message);
                }
                @Override
                public void onProgressUpdate(Integer... values) {}
                @Override
                public void onFinish() {
                    helper.hide();
                }
                @Override
                public void onResult(Boolean result, Http.Response response) {
                    try {
                        if(result && response.success()) {
                            JSONObject msg_data= response.data().getJSONObject("msg_data");
                            if(msg_data != null) {
                                Table_OrderMessages messages= new Table_OrderMessages();
                                messages.setFieldInt(Table_OrderMessages.FIELD_LID, 0);
                                messages.setFieldInt(Table_OrderMessages.FIELD_ID, Reader.getJInt(msg_data,"id"));
                                messages.setFieldInt(Table_OrderMessages.FIELD_OID, Reader.getJInt(msg_data,"order_id"));
                                messages.setField(Table_OrderMessages.FIELD_SENT_BY, Reader.getJString(msg_data,"sent_by"));
                                messages.setField(Table_OrderMessages.FIELD_SENT_ON, Reader.getJString(msg_data,"sent_on"));
                                messages.setField(Table_OrderMessages.FIELD_MSG_TEXT, Reader.getJString(msg_data,"msg_text"));
                                messages.setField(Table_OrderMessages.FIELD_STATUS, "S");
                                messages.setFieldInt(Table_OrderMessages.FIELD_REF, mMessageRefID);
                                messages.setField(Table_OrderMessages.FIELD_REF_SRC, mMessageRefSrc);
                                messages.save(mContext.DB);
                            }
                            mContext.refreshData(true);
                            if(file != null) {
                                File newFile= new File(ChatHelper.getAudioFileName(mContext, Reader.getJInt(msg_data,"id"), false));
                                FileUtils.moveFile(file, newFile, true);
                            }
                            clearMessageRef();
                            cancelRecording();
                            edCompose.setText("");
                            edCompose.requestFocus();
                        } else {
                            Toast.makeText(mContext, "Unable to send message. " + (response==null?"":response.errorMessage()), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelRecording();
    }

    public static String getAudioFileName(Context context, int ID, boolean online) {
        try {
            if(online) {
                //https://smebusinesssoftware.com/johnnysapp/assets/audios/
                return "voice" + ID + ".amr";
            } else {
                File path= context.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC);
                path.mkdirs();
                return new File(path, "voice" + ID + ".amr").getAbsolutePath();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static int mLastPlayedIndex= -1;
    public static MediaPlayer MEDIA_PLAYER_OBJ= null;
    public static class OnMediaClick implements View.OnClickListener {
        Activity mContext;
        ImageView mImgPlay;
        SeekBar mSBAudio;
        int mPosition;
        ArrayList<Table_OrderMessages> mItems;
        RecyclerListAdapter mAdapter;

        public OnMediaClick(Activity context, ImageView imgPlay, SeekBar sbAudio, int position,
                            ArrayList<Table_OrderMessages> items, RecyclerListAdapter adapter) {
            mContext= context;
            mImgPlay= imgPlay;
            mSBAudio= sbAudio;
            mPosition= position;
            mItems= items;
            mAdapter= adapter;
        }

        @Override
        public void onClick(View btn) {
            File file= new File(ChatHelper.getAudioFileName(mContext, mItems.get(mPosition).getFieldInt(Table_OrderMessages.FIELD_ID), false));
            if(!file.exists() || !file.isFile()) {
                String url= ChatHelper.getAudioFileName(mContext, mItems.get(mPosition).getFieldInt(Table_OrderMessages.FIELD_ID), true);
                Reader.downloadAudio(mContext, url, file, () -> {
                    mAdapter.notifyDataSetChanged();
                    if(file.exists()) {
                        onClick(btn);
                    }
                });
            }
            if((mLastPlayedIndex>=0) && (mLastPlayedIndex!=mPosition)) {
                mItems.get(mLastPlayedIndex).getData().put("playing", "F");
                mAdapter.notifyItemChanged(mLastPlayedIndex);
            }

            if(mItems.get(mPosition).getData().containsKey("playing") &&
                    mItems.get(mPosition).getData().getAsString("playing").equals("T")) {
                if (MEDIA_PLAYER_OBJ!=null){
                    MEDIA_PLAYER_OBJ.pause();
                }
                mLastPlayedIndex= -1;
                mItems.get(mPosition).getData().put("playing", "F");
                mImgPlay.setImageResource(R.drawable.img_play);
                mAdapter.notifyItemChanged(mPosition);
                return;
            }

            try {
                try {
                    if(MEDIA_PLAYER_OBJ != null) {
                        MEDIA_PLAYER_OBJ.pause();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MEDIA_PLAYER_OBJ = new MediaPlayer();
                MEDIA_PLAYER_OBJ.setDataSource(file.getAbsolutePath());
                MEDIA_PLAYER_OBJ.prepare();
                mSBAudio.setMax(MEDIA_PLAYER_OBJ.getDuration());

                listenProgress(mContext, mSBAudio);
                MEDIA_PLAYER_OBJ.start();
                MEDIA_PLAYER_OBJ.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer arg0) {
                        mLastPlayedIndex= -1;
                        mItems.get(mPosition).setField("playing", "F");
                        mImgPlay.setImageResource(R.drawable.img_play);
                        mSBAudio.setProgress(0);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mLastPlayedIndex= mPosition;
                mItems.get(mPosition).setField("playing", "T");
                mImgPlay.setImageResource(R.drawable.img_pause);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void listenProgress(final Activity context, SeekBar sbAudio) {
        Handler mHandler= new Handler();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if( (MEDIA_PLAYER_OBJ != null) && MEDIA_PLAYER_OBJ.isPlaying() ){
                        sbAudio.setProgress(MEDIA_PLAYER_OBJ.getCurrentPosition());
                    }
                    mHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    Log.d("mHandler run err", e.toString());
                }
            }
        });
    }

    /*
    public static class AttachDownloader extends AsyncTask<Void,Void,Boolean> {
        private WeakReference<Context> mContext;
        private String mUrlString;
        private File mDestination;
        private Runnable mAfterDownload;
        private ProgressHelper progressHelper;
        public static void download(Context context, String urlString, File destination, Runnable afterDownload) {
            try {
                AttachDownloader attachDownloader= new AttachDownloader();
                attachDownloader.mContext= new WeakReference<>(context);
                attachDownloader.mUrlString= urlString;
                attachDownloader.mDestination= destination;
                attachDownloader.mAfterDownload= afterDownload;
                attachDownloader.executeOnExecutor(Executors.newSingleThreadExecutor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            progressHelper= ProgressHelper.get(mContext.get());
            progressHelper.show("Downloading Audio. Please wait...");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return Http.saveUrl(mUrlString, mDestination, null);
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressHelper.hide();
            if(aBoolean && mAfterDownload != null) {
                mAfterDownload.run();
            }
        }
    }
 */
}

