package com.atoz.johnnysapp.store.ui.order_details;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.data.Table_OrderItems;
import com.atoz.johnnysapp.store.data.Table_OrderMessages;
import com.atoz.johnnysapp.store.data.Table_Orders;
import com.atoz.johnnysapp.store.online.AsyncListener;
import com.atoz.johnnysapp.store.online.Reader;
import com.atoz.johnnysapp.store.online.Writer;
import com.atoz.johnnysapp.store.receivers_services.Store;
import com.atoz.johnnysapp.store.utils.DateUtils;
import com.atoz.johnnysapp.store.utils.Http;
import com.mixiaoxiao.fastscroll.FastScrollRecyclerView;
import com.mixiaoxiao.fastscroll.helper.RecyclerListAdapter;
import com.mixiaoxiao.fastscroll.helper.SimpleItemTouchHelperCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MessagesFragment extends FragmentEx {
    FastScrollRecyclerView rvMessages;
    ArrayList<Table_OrderMessages> alMessages;
    RecyclerListAdapter<Table_OrderMessages> raMessages;

    public static MessagesFragment create() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MessagesFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext= activity;
        if (!(activity instanceof FragmentListener)) {
            throw new ClassCastException("Activity must implement Activity Interface");
        }
        mMainActivity= (FragmentListener) activity;
        DB= mMainActivity.getDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.activity_order_details_messages, null);
        init();
        return view;
    }

    public void init() {
        initView();
        initAdapter();
        refreshList();
    }

    public void initView() {
        try {
            rvMessages= view.findViewById(R.id.rvMessages);
        } catch (Exception e) {
            Log.e("initView", e.toString());
        }
    }

    private void initAdapter() {
        try {
            alMessages= new ArrayList<>();
            raMessages= new RecyclerListAdapter<>(mContext, rvMessages, R.layout.lv_messages, alMessages, 1, 1, new SimpleItemTouchHelperCallback.Validator() {
                @Override
                public boolean isMovable(int position) {
                    return false;
                }

                @Override
                public boolean isSwipable(int position) {
                    return true;
                }
            }, new RecyclerListAdapter.Binder() {
                @Override
                public void onBindView(RecyclerListAdapter mAdapter, RecyclerListAdapter.ItemViewHolder holder, int mPosition) {
                    try {
                        TextView tvDateTitle = holder.mItemView.findViewById(R.id.tvDateTitle);
                        LinearLayout llMsgContainer = holder.mItemView.findViewById(R.id.llMsgContainer);
                        LinearLayout llMsg = holder.mItemView.findViewById(R.id.llMsg);

                        TextView tvMessageFrom = holder.mItemView.findViewById(R.id.tvMessageFrom);
                        TextView tvMessage = holder.mItemView.findViewById(R.id.tvMessage);
                        TextView tvTime1 = holder.mItemView.findViewById(R.id.tvTime1);
                        TextView tvTime2 = holder.mItemView.findViewById(R.id.tvTime2);
                        TextView tvChatWarning = holder.mItemView.findViewById(R.id.tvChatWarning);
                        tvChatWarning.setVisibility(View.GONE);

                        tvMessage.setText(alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_MSG_TEXT));

                        String time = alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_SENT_ON);
                        Date date = time == null || time.equals("") ? null : DateUtils.getDateTime(time);
                        tvTime1.setText(date == null ? "" : DateUtils.getLocalTime(date));
                        tvTime2.setText(tvTime1.getText());

                        /*if(alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_SENT_BY).equals("S")) {
                            tvMessageFrom.setText("You");
                        } else {
                            tvMessageFrom.setText(mMainActivity.getOrder().getField(Table_Orders.FIELD_USER_NAME));
                        }*/
                        tvMessageFrom.setVisibility(View.GONE);

                        if (mPosition > 0) {
                            String time1 = alMessages.get(mPosition - 1).getField(Table_OrderMessages.FIELD_SENT_ON);
                            Date date1 = time1 == null || time1.equals("") ? null : DateUtils.getDateTime(time1);
                            if (date1 == null || date == null) {
                                tvDateTitle.setVisibility(View.GONE);
                            } else {
                                if (DateUtils.sameDay(date, date1)) {
                                    tvDateTitle.setVisibility(View.GONE);
                                } else {
                                    tvDateTitle.setVisibility(View.VISIBLE);
                                    tvDateTitle.setText(DateUtils.getLocalDate(date));
                                }
                            }
                        } else {
                            if (date != null) {
                                tvDateTitle.setVisibility(View.VISIBLE);
                                tvDateTitle.setText(DateUtils.getLocalDate(date));
                            } else {
                                tvDateTitle.setVisibility(View.GONE);
                            }
                        }


                        int paddingSize = 50;
                        //Handle Attachments
                        switch (alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_MSG_TEXT)) {
                            case ChatHelper.ATTACHED_AUDIO:
                                paddingSize = 150;
                                tvMessage.setVisibility(View.GONE);
                                ImageButton imgPlay = holder.mItemView.findViewById(R.id.imgPlay);
                                SeekBar sbAudio = holder.mItemView.findViewById(R.id.sbAudio);

                                imgPlay.setOnClickListener(new ChatHelper.OnMediaClick(getActivity(), imgPlay, sbAudio, mPosition,
                                        alMessages, raMessages));
                                File file = new File(ChatHelper.getAudioFileName(mContext, alMessages.get(mPosition).getFieldInt(Table_OrderMessages.FIELD_ID), false));
                                if (file.exists() && file.isFile()) {
                                    if (alMessages.get(mPosition).getData().containsKey("playing") &&
                                            alMessages.get(mPosition).getField("playing").equalsIgnoreCase("T")) {
                                        imgPlay.setImageResource(R.drawable.img_pause);
                                    } else {
                                        imgPlay.setImageResource(R.drawable.img_play);
                                    }
                                } else {
                                    imgPlay.setImageResource(R.drawable.btn_icon_download);
                                }
                                break;
                            default:
                                tvMessage.setVisibility(View.VISIBLE);
                                break;
                        }
                        //Handle Attachments


                        boolean sent = alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_SENT_BY).equals("S");
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llMsg.getLayoutParams();
                        if (sent) {
                            int drawable = R.drawable.receipt_unsent;
                            switch (alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_STATUS)) {
                                case "S":
                                    drawable = R.drawable.receipt_sent;
                                    break;
                                case "D":
                                    drawable = R.drawable.receipt_received;
                                    break;
                                case "R":
                                    drawable = R.drawable.receipt_read;
                                    break;
                            }
                            tvTime2.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tvTime1.setVisibility(View.GONE);
                            tvTime2.setVisibility(View.VISIBLE);
                            llMsg.setBackgroundResource(R.drawable.message_bubble_sent);
                            params.gravity = Gravity.RIGHT;
                            llMsgContainer.setPadding(paddingSize, 0, 0, 0);
                        } else {
                            if (alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_STATUS).equals("D")) {
                                tvTime1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.receipt_new, 0, 0, 0);
                            } else {
                                tvTime1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            }
                            tvTime1.setVisibility(View.VISIBLE);
                            tvTime2.setVisibility(View.GONE);
                            llMsg.setBackgroundResource(R.drawable.message_bubble_received);
                            params.gravity = Gravity.LEFT;
                            llMsgContainer.setPadding(0, 0, paddingSize, 0);
                        }
                        llMsg.setLayoutParams(params);

                        CardView cvMessageRef= holder.mItemView.findViewById(R.id.cvMessageRef);
                        TextView tvMessageRef= holder.mItemView.findViewById(R.id.tvMessageRef);
                        int ref= alMessages.get(mPosition).getFieldInt(Table_OrderMessages.FIELD_REF);
                        String refTxt= alMessages.get(mPosition).getField(Table_OrderMessages.FIELD_REF_SRC);
                        String msgRef= Table_OrderMessages.getMessageRef(DB, ref, refTxt);
                        if(ref > 0 && !TextUtils.isEmpty(msgRef)) {
                            cvMessageRef.setVisibility(View.VISIBLE);
                            tvMessageRef.setText(msgRef);
                            cvMessageRef.setOnClickListener(v -> {
                                try {
                                    if (TextUtils.equals(refTxt, "I")) {
                                        mMainActivity.getActivity().tabs.getTabAt(0).select();
                                        mMainActivity.getActivity().vpContainer.setCurrentItem(0);
                                    } else if (TextUtils.equals(refTxt, "M")) {
                                        int pos= -1;
                                        for(int i=0, j= alMessages.size(); i<j; i++) {
                                            if(alMessages.get(i).getFieldInt(Table_OrderMessages.FIELD_ID)==ref) {
                                                pos= i;
                                                break;
                                            }
                                        }
                                        if(pos >= 0) {
                                            rvMessages.scrollToPosition(pos);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            cvMessageRef.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClick(RecyclerListAdapter mAdapter, View view, int position) {

                }

                @Override
                public boolean onLongClick(RecyclerListAdapter mAdapter, View view, int position) {
                    return false;
                }

                @Override
                public boolean onItemDismiss(RecyclerListAdapter mAdapter, int position) {
                    String text= alMessages.get(position).getField(Table_OrderMessages.FIELD_MSG_TEXT);
                    text= TextUtils.equals(text, ChatHelper.ATTACHED_AUDIO)?"Voice Recording":text;
                    mMainActivity.getActivity().mChatHelper.addMessageRef(alMessages.get(position).getFieldInt(Table_OrderMessages.FIELD_ID),
                            "M", "Msg: " + text);
                    return false;
                }

                @Override
                public boolean onItemMove(RecyclerListAdapter mAdapter, int fromPosition, int toPosition) {
                    return false;
                }
            }, new RecyclerListAdapter.ViewHolderListener() {
                @Override
                public int getItemViewType(int position) {
                    switch (alMessages.get(position).getField(Table_OrderMessages.FIELD_MSG_TEXT)) {
                        case ChatHelper.ATTACHED_AUDIO:
                            return ChatHelper.VIEW_TYPE_AUDIO;
                    }
                    return ChatHelper.VIEW_TYPE_TEXT;
                }

                @Override
                public View onCreateViewHolder(ViewGroup parent, int viewType) {
                    switch (viewType) {
                        case ChatHelper.VIEW_TYPE_AUDIO:
                            return LayoutInflater.from(mContext).inflate(R.layout.lv_messages_audio, null);
                        case ChatHelper.VIEW_TYPE_TEXT:
                        default:
                            return null;
                    }
                }

                @Override
                public RecyclerListAdapter.ItemViewHolder onCreateItemHolder(RecyclerListAdapter adapter, View view, int handlerID) {
                    return null;
                }
            });
            rvMessages.removeItemDecoration(raMessages.mDividerItemDecoration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshList() {
        try {
            alMessages.clear();
            ArrayList<Table_OrderMessages> temp= Table_OrderMessages.getList(DB, mMainActivity.getOrderID());
            if(temp != null && temp.size() > 0) {
                alMessages.addAll(temp);
            }
            raMessages.notifyDataSetChanged();
            rvMessages.scrollToPosition(alMessages.size()-1);

            String ids= "";
            for(Table_OrderMessages message: alMessages) {
                if(TextUtils.equals("S", message.getField(Table_OrderMessages.FIELD_SENT_BY))) {
                    continue;
                }
                if(TextUtils.equals("R", message.getField(Table_OrderMessages.FIELD_STATUS))) {
                    continue;
                }
                ids+= (TextUtils.isEmpty(ids)?"":",") + message.getField(Table_OrderMessages.FIELD_ID);
            }
            Writer.msg_read(mContext, ids, new AsyncListener() {
                @Override
                public void onStart() {}
                @Override
                public void onProgressUpdate(Integer... values) {}
                @Override
                public void onFinish() {}
                @Override
                public void onResult(Boolean result, Http.Response response) {
                    Reader.read(mContext, new AsyncListener() {
                        @Override
                        public void onStart() {}
                        @Override
                        public void onProgressUpdate(Integer... values) { }
                        @Override
                        public void onFinish() { }
                        @Override
                        public void onResult(Boolean result, Http.Response response) {
                            Store.refreshView();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}