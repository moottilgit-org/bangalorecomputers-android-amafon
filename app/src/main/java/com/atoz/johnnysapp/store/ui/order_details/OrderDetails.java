package com.atoz.johnnysapp.store.ui.order_details;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.data.DatabaseHandler;
import com.atoz.johnnysapp.store.data.Table_OrderItems;
import com.atoz.johnnysapp.store.data.Table_Orders;
import com.atoz.johnnysapp.store.data.Table_Stages;
import com.atoz.johnnysapp.store.online.AsyncListener;
import com.atoz.johnnysapp.store.online.ProgressHelper;
import com.atoz.johnnysapp.store.online.Reader;
import com.atoz.johnnysapp.store.online.Writer;
import com.atoz.johnnysapp.store.ui.Main;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.DateUtils;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.ImageUtils;

import java.util.ArrayList;

public class OrderDetails extends ActivityEx implements FragmentListener, View.OnClickListener {
    public static OrderDetails mInstance;
    ImageView imgAvatar;
    TextView tvName, tvNumber, tvDate, tvStatus, tvOrderID, tvNotes, tvPaidInfo;
    Button btnCall, btnAccept, btnReject, btnMessage, btnUpdate, btnComplete, btnPaid, btnAddress;

    LinearLayout llAddressContainer;
    TextView tvAddress;
    ImageButton imgLocate;

    TabLayout tabs;
    ViewPager vpContainer;
    SectionsPagerAdapter mPagerAdapter;

    int ID;
    Table_Orders mOrder;
    boolean mIsContentChanged= false;

    ChatHelper mChatHelper;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance= this;

        setContentView(R.layout.activity_order_details);
        Toolbar toolbar= findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        imgAvatar= findViewById(R.id.imgAvatar);
        tvName= findViewById(R.id.tvName);
        tvNumber= findViewById(R.id.tvNumber);
        tvDate= findViewById(R.id.tvDate);
        tvStatus= findViewById(R.id.tvStatus);
        tvOrderID= findViewById(R.id.tvOrderID);
        tvNotes= findViewById(R.id.tvNotes);
        tvPaidInfo= findViewById(R.id.tvPaidInfo);

        btnCall= findViewById(R.id.btnCall);
        btnAccept= findViewById(R.id.btnAccept);
        btnReject= findViewById(R.id.btnReject);
        btnMessage= findViewById(R.id.btnMessage);
        btnUpdate= findViewById(R.id.btnUpdate);
        btnComplete= findViewById(R.id.btnComplete);
        btnPaid= findViewById(R.id.btnPaid);
        btnAddress= findViewById(R.id.btnAddress);

        llAddressContainer= findViewById(R.id.llAddressContainer);
        tvAddress= findViewById(R.id.tvAddress);
        imgLocate= findViewById(R.id.imgLocate);

        mChatHelper= new ChatHelper(this);

        ID= getIntent().getIntExtra("ID", 0);
        refreshData(false);

        tvStatus.setPaintFlags(tvStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tabs= findViewById(R.id.tabs);
        vpContainer= findViewById(R.id.vpContainer);
        mPagerAdapter= new SectionsPagerAdapter(this, getSupportFragmentManager());

        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(vpContainer);
        vpContainer.setOffscreenPageLimit(3);
        vpContainer.setAdapter(mPagerAdapter);

        mIsContentChanged= false;

        btnCall.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        btnPaid.setOnClickListener(this);
        btnAddress.setOnClickListener(this);
        tvNotes.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        imgLocate.setOnClickListener(this);
        tvStatus.setOnClickListener(this);

        if(getIntent().getBooleanExtra("MSGS", false)) {
            tabs.getTabAt(1).select();
            vpContainer.setCurrentItem(1);
        }
    }

    public void refreshData(boolean refreshTabs) {
        try {
            mOrder= Table_Orders.get(DB, ID);
            tvName.setText(mOrder.getField(Table_Orders.FIELD_USER_NAME));
            tvNumber.setText(mOrder.getField(Table_Orders.FIELD_USER_NUMBER));
            tvDate.setText(DateUtils.getLocalDateTime(mOrder.getField(Table_Orders.FIELD_DATE)));
            tvStatus.setText(Table_Stages.getName(DB, mOrder.getFieldInt(Table_Orders.FIELD_STAGE)));
            tvOrderID.setText("#" + mOrder.getFieldInt(Table_Orders.FIELD_REF));
            tvNotes.setText(mOrder.getField(Table_Orders.FIELD_NOTES));
            tvNotes.setVisibility(tvNotes.getText().toString().isEmpty()?View.GONE:View.VISIBLE);
            ImageUtils.applyAvatar(OrderDetails.this, imgAvatar, mOrder.getField(Table_Orders.FIELD_USER_AVATAR));
            boolean disabled= TextUtils.equals("FINISH", mOrder.getField(Table_Orders.FIELD_STATUS))||
                                TextUtils.equals("CANCEL", mOrder.getField(Table_Orders.FIELD_STATUS));
            if(disabled) {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                btnUpdate.setVisibility(View.GONE);
                mChatHelper.hide();
            } else {
                btnAccept.setVisibility(TextUtils.equals("REJECT", mOrder.getField(Table_Orders.FIELD_STATUS)) ? View.VISIBLE : View.GONE);
                btnReject.setVisibility(TextUtils.equals("REJECT", mOrder.getField(Table_Orders.FIELD_STATUS)) ? View.GONE : View.VISIBLE);
                btnComplete.setVisibility(TextUtils.equals("REJECT", mOrder.getField(Table_Orders.FIELD_STATUS)) ? View.GONE : View.VISIBLE);
                btnUpdate.setVisibility(TextUtils.equals("REJECT", mOrder.getField(Table_Orders.FIELD_STATUS)) ? View.GONE : View.VISIBLE);
                mChatHelper.show();
            }
            if(TextUtils.equals("T", mOrder.getField(Table_Orders.FIELD_PAYMENT_DONE))) {
                btnPaid.setVisibility(View.GONE);
                tvPaidInfo.setVisibility(View.VISIBLE);
                tvPaidInfo.setText("Paid on " + DateUtils.getLocalDateTime(mOrder.getField(Table_Orders.FIELD_PAYMENT_TIME)));
            } else {
                btnPaid.setVisibility(View.VISIBLE);
                tvPaidInfo.setVisibility(View.GONE);
            }

            if(TextUtils.isEmpty(mOrder.getField(Table_Orders.FIELD_ADDRESS))) {
                btnAddress.setVisibility(View.GONE);
            } else {
                tvAddress.setText(mOrder.getField(Table_Orders.FIELD_ADDRESS));
                imgLocate.setVisibility(mOrder.getFieldDouble(Table_Orders.FIELD_LAT)==0?View.GONE:View.VISIBLE);
                btnAddress.setVisibility(View.VISIBLE);
            }

            if(refreshTabs) {
                mPagerAdapter.mItems.refreshList();
                mPagerAdapter.mMessages.refreshList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tvStatus:
                    changeStage();
                break;
                case R.id.btnCall:
                    Uri call_uri = Uri.parse("tel:" + mOrder.getField(Table_Orders.FIELD_USER_NUMBER));
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, call_uri);
                    mContext.startActivity(callIntent);
                break;
                case R.id.btnAccept:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.accept_the_order)
                            .setNegativeButton(R.string.action_cancel, null)
                            .setPositiveButton(R.string.accept, (dialog, which) -> {
                                acceptOrder();
                            }).show();
                break;
                case R.id.btnReject:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.reject_the_order)
                            .setNegativeButton(R.string.action_cancel, null)
                            .setPositiveButton(R.string.reject, (dialog, which) -> {
                                rejectOrder();
                            }).show();
                break;
                /*case R.id.btnMessage:
                    sendAMessage();
                break;*/
                case R.id.btnUpdate:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.send_the_estimate)
                            .setNegativeButton(R.string.action_cancel, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                updateOrder();
                            }).show();
                break;
                case R.id.btnComplete:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.complete_the_order)
                            .setNegativeButton(R.string.action_cancel, null)
                            .setPositiveButton(R.string.label_completed, (dialog, which) -> {
                                completeOrder();
                            }).show();
                break;
                case R.id.btnPaid:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.update_payment_received)
                            .setNegativeButton(R.string.action_cancel, null)
                            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                paymentReceived();
                            }).show();
                break;

                case R.id.tvNotes:
                    if(tvNotes.getMaxLines() > 1) {
                        tvNotes.setMaxLines(1);
                        tvNotes.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more,0);
                    } else {
                        tvNotes.setMaxLines(20);
                        tvNotes.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more_up,0);
                    }
                break;
                case R.id.tvAddress:
                    if(tvAddress.getMaxLines() > 3) {
                        tvAddress.setMaxLines(3);
                        tvAddress.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more,0);
                    } else {
                        tvAddress.setMaxLines(20);
                        tvAddress.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more_up,0);
                    }
                break;
                case R.id.imgLocate:
                    double lat= mOrder.getFieldDouble(Table_Orders.FIELD_LAT);
                    double lon= mOrder.getFieldDouble(Table_Orders.FIELD_LNG);
                    String title= mOrder.getField(Table_Orders.FIELD_USER_NAME);
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat + "," + lon + "(" + title + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                break;
                case R.id.btnAddress:
                    if(btnAddress.getVisibility()==View.VISIBLE) {
                        if(llAddressContainer.getVisibility()==View.VISIBLE) {
                            llAddressContainer.setVisibility(View.GONE);
                        } else {
                            llAddressContainer.setVisibility(View.VISIBLE);
                        }
                    } else {
                        llAddressContainer.setVisibility(View.GONE);
                    }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mChatHelper.stop();
        mInstance= null;
        super.onDestroy();
    }

    @Override
    public DatabaseHandler getDatabase() {
        return DB;
    }

    @Override
    public OrderDetails getActivity() {
        return this;
    }

    @Override
    public int getOrderID() {
        return ID;
    }

    @Override
    public Table_Orders getOrder() {
        return mOrder;
    }

    private void acceptOrder() {
        try {
            ProgressHelper helper= ProgressHelper.get(this);
            Writer.accepted(mContext, ID, new AsyncListener() {
                @Override
                public void onStart() {
                    helper.show(R.string.updating_order);
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
                        mOrder.setField(Table_Orders.FIELD_STATUS, "ACCEPT");
                        mOrder.save(DB);
                        refreshData(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rejectOrder() {
        try {
            ProgressHelper helper= ProgressHelper.get(this);
            Writer.reject(mContext, ID, new AsyncListener() {
                @Override
                public void onStart() {
                    helper.show(R.string.updating_order);
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
                        mOrder.setField(Table_Orders.FIELD_STATUS, "REJECT");
                        mOrder.setFieldInt(Table_Orders.FIELD_STAGE, 5);
                        mOrder.save(DB);
                        refreshData(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void completeOrder() {
        try {
            ProgressHelper helper= ProgressHelper.get(this);
            Writer.finish(mContext, ID, new AsyncListener() {
                @Override
                public void onStart() {
                    helper.show(R.string.completing_order);
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
                        mOrder.setField(Table_Orders.FIELD_STATUS, "FINISH");
                        mOrder.setFieldInt(Table_Orders.FIELD_STAGE, 7);
                        mOrder.save(DB);
                        refreshData(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paymentReceived() {
        try {
            ProgressHelper helper= ProgressHelper.get(this);
            Writer.paymentReceived(mContext, ID, new AsyncListener() {
                @Override
                public void onStart() {
                    helper.show(R.string.updating_order);
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
                        mOrder.setField(Table_Orders.FIELD_PAYMENT_DONE, "T");
                        mOrder.setField(Table_Orders.FIELD_PAYMENT_TIME, DateUtils.getDateTimeStr());
                        mOrder.save(DB);
                        refreshData(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOrder() {
        try {
            ArrayList<Table_OrderItems> alItems= mPagerAdapter.mItems.alItems;
            ProgressHelper helper= ProgressHelper.get(this);
            Writer.update(mContext, ID, alItems, new AsyncListener() {
                @Override
                public void onStart() {
                    helper.show(R.string.sending_estimate);
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
                        Reader.read(mContext, new AsyncListener() {
                            @Override
                            public void onStart() {}
                            @Override
                            public void onProgressUpdate(Integer... values) {}
                            @Override
                            public void onFinish() {}
                            @Override
                            public void onResult(Boolean result, Http.Response response) {
                                refreshData(true);
                            }
                        });
                        mOrder.setFieldInt(Table_Orders.FIELD_STAGE, 2);
                        mOrder.save(DB);
                        refreshData(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeStage() {
        try {
            ArrayList<Table_Stages> stages= Table_Stages.getList(DB);
            String[] items= new String[stages.size()];
            for(int i=0, j=stages.size(); i<j; i++) {
                items[i]= stages.get(i).getField(Table_Stages.FIELD_NAME);
            }
            new AlertDialog.Builder(mContext)
                    .setItems(items, (dialog, which) -> {
                        ProgressHelper helper= ProgressHelper.get(this);
                        int stageID= stages.get(which).getFieldInt(Table_Stages.FIELD_ID);
                        Writer.updateStage(mContext, ID, stageID, new AsyncListener() {
                            @Override
                            public void onStart() {
                                helper.show(R.string.updating_stage);
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
                                    mOrder.setFieldInt(Table_Orders.FIELD_STAGE, stageID);
                                    mOrder.save(DB);
                                    refreshData(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private void sendAMessage() {
        sendAMessage("");
    }
    private void sendAMessage(String oldMessage) {
        try {
            DialogInput.showStringMulti(mContext, oldMessage, "Send a Message", newMessage -> {
                ProgressHelper helper= ProgressHelper.get(mContext);
                Writer.message(mContext, ID, newMessage, null, new AsyncListener() {
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
                                    messages.save(DB);
                                }
                                refreshData(true);
                            } else {
                                sendAMessage(newMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
