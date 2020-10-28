package com.atoz.johnnysapp.store.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atoz.johnnysapp.store.data.Table_OrderMessages;
import com.atoz.johnnysapp.store.data.Table_Orders;
import com.atoz.johnnysapp.store.data.Table_Stages;
import com.atoz.johnnysapp.store.online.AsyncListener;
import com.atoz.johnnysapp.store.online.ProgressHelper;
import com.atoz.johnnysapp.store.online.Reader;
import com.atoz.johnnysapp.store.online.Writer;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.ui.error_and_feedback.ActivitySendFeedback;
import com.atoz.johnnysapp.store.ui.order_details.OrderDetails;
import com.atoz.johnnysapp.store.utils.DateUtils;
import com.atoz.johnnysapp.store.utils.DialogInput;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.ImageUtils;
import com.atoz.johnnysapp.store.utils.Pref;
import com.atoz.johnnysapp.store.utils.Toast;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.utils.updater.UpdaterHelper;
import com.mixiaoxiao.fastscroll.FastScrollRecyclerView;
import com.mixiaoxiao.fastscroll.helper.RecyclerListAdapter;

import java.util.ArrayList;

public class Main extends ActivityEx {
    public static Main mInstance= null;

    TextView tvShopName, tvShopNumber;
    ImageButton imgPayment;

    EditText edSearchFor;
    ImageButton btnClearSearch;
    SwipeRefreshLayout swipeLayout;
    TextView tvNoRecord;
    FastScrollRecyclerView rvOrders;
    Button btnReferNumber, btnReferNumberWA;

    ArrayList<Table_Orders> alOrders;
    RecyclerListAdapter<Table_Orders> raOrders;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mInstance= this;

        Toolbar toolbar= findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }
        tvShopName= findViewById(R.id.tvShopName);
        tvShopNumber= findViewById(R.id.tvShopNumber);
        imgPayment= findViewById(R.id.imgPayment);

        tvShopName.setText(Pref.getFullName(this));
        tvShopNumber.setText(Pref.getMobile(this));
        imgPayment.setOnClickListener(v -> {
            startActivity(new Intent(this, UpiInfo.class));
        });
        tvShopName.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });
        tvShopNumber.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        edSearchFor= findViewById(R.id.edSearchFor);
        btnClearSearch= findViewById(R.id.btnClearSearch);
        swipeLayout= findViewById(R.id.swipeLayout);
        tvNoRecord= findViewById(R.id.tvNoRecord);
        rvOrders= findViewById(R.id.rvOrders);
        btnReferNumber= findViewById(R.id.btnReferNumber);
        btnReferNumberWA= findViewById(R.id.btnReferNumberWA);

        btnReferNumberWA.setVisibility(hasWhatsApp(this)?View.VISIBLE:View.GONE);

        swipeLayout.setOnRefreshListener(this::readContents);
        btnReferNumber.setOnClickListener(v -> {
            referANumber(this);
        });
        btnReferNumberWA.setOnClickListener(v -> {
            referANumberWA(this);
        });
        initAdapter();
        refreshList();
        readContents();
    }
    private void initAdapter() {
        try {
            alOrders= new ArrayList<>();
            raOrders= new RecyclerListAdapter<>(this, rvOrders, R.layout.lv_orders, alOrders,  null, new RecyclerListAdapter.Binder() {
                @Override
                public void onBindView(RecyclerListAdapter mAdapter, RecyclerListAdapter.ItemViewHolder holder, int mPosition) {
                    try {
                        ImageView imgAvatar = holder.mItemView.findViewById(R.id.imgAvatar);
                        TextView tvName = holder.mItemView.findViewById(R.id.tvName);
                        TextView tvNumber = holder.mItemView.findViewById(R.id.tvNumber);
                        TextView tvDate = holder.mItemView.findViewById(R.id.tvDate);
                        TextView tvStatus = holder.mItemView.findViewById(R.id.tvStatus);
                        TextView tvOrderID = holder.mItemView.findViewById(R.id.tvOrderID);

                        Button btnCall = holder.mItemView.findViewById(R.id.btnCall);
                        Button btnView = holder.mItemView.findViewById(R.id.btnView);
                        RelativeLayout rlMessageCount = holder.mItemView.findViewById(R.id.rlMessageCount);
                        TextView tvMessageCount = holder.mItemView.findViewById(R.id.tvMessageCount);

                        ImageUtils.applyAvatar(Main.this, imgAvatar, alOrders.get(mPosition).getField(Table_Orders.FIELD_USER_AVATAR));
                        tvName.setText(alOrders.get(mPosition).getField(Table_Orders.FIELD_USER_NAME));
                        tvNumber.setText(alOrders.get(mPosition).getField(Table_Orders.FIELD_USER_NUMBER));
                        tvDate.setText(DateUtils.getLocalDateTime(alOrders.get(mPosition).getField(Table_Orders.FIELD_DATE)));
                        tvStatus.setText(Table_Stages.getName(DB, alOrders.get(mPosition).getFieldInt(Table_Orders.FIELD_STAGE)));
                        tvOrderID.setText("#" + alOrders.get(mPosition).getFieldInt(Table_Orders.FIELD_REF));

                        btnCall.setOnClickListener(v -> {
                            try {
                                Uri call_uri = Uri.parse("tel:" + alOrders.get(mPosition).getField(Table_Orders.FIELD_USER_NUMBER));
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, call_uri);
                                mContext.startActivity(callIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        ArrayList<Table_OrderMessages> msgs= Table_OrderMessages.getUnread(DB, alOrders.get(mPosition).getFieldInt(Table_Orders.FIELD_ID));
                        if(msgs == null || msgs.size()==0) {
                            rlMessageCount.setVisibility(View.GONE);
                        } else {
                            tvMessageCount.setText(msgs.size()>9?"9+":""+msgs.size());
                            rlMessageCount.setVisibility(View.VISIBLE);
                        }

                        btnView.setOnClickListener(v -> onClick(mAdapter, holder.mItemView, mPosition));
                        rlMessageCount.setOnClickListener(v -> onClick(mAdapter, null, mPosition));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClick(RecyclerListAdapter mAdapter, View view, int position) {
                    try {
                        Intent intent = new Intent(mContext, OrderDetails.class);
                        intent.putExtra("ID", alOrders.get(position).getFieldInt(Table_Orders.FIELD_ID));
                        if(view == null) {
                            intent.putExtra("MSGS", true);
                        }
                        startActivityForResult(intent, REQ_EDIT_ENQUIRY);
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
    public void refreshList() {
        try {
            alOrders.clear();
            ArrayList<Table_Orders> temp= Table_Orders.getActive(DB);
            if(temp != null && temp.size() > 0) {
                alOrders.addAll(temp);
            }

            raOrders.notifyDataSetChanged();
            tvNoRecord.setVisibility(alOrders.size()>0?View.GONE:View.VISIBLE);
            rvOrders.setVisibility(alOrders.size()==0?View.GONE:View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdaterHelper.CheckForUpdatesIfRequired(this, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private static final int TIME_INTERVAL = 2000;// # milliseconds, desired time passed between two back presses.
    private long mBackPressed;// # milliseconds, time when back pressed.
    private android.widget.Toast mToastExit;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            if (mToastExit != null) {
                mToastExit.cancel();
            }
            super.onBackPressed();
            return;
        } else {
            mToastExit = Toast.makeText(getBaseContext(), "Press back once more to exit.", Toast.LENGTH_SHORT);
            mToastExit.show();
        }
        mBackPressed = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enquiries, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem item= menu.findItem(R.id.action_feedback);
            SpannableStringBuilder title = new SpannableStringBuilder(item.getTitle());
            StyleSpan styleSpan= new StyleSpan(Typeface.BOLD);
            title.setSpan(styleSpan, 0, title.length(), 0);
            item.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                readContents();
            return true;
            case R.id.action_profile:
                startActivity(new Intent(this, Profile.class));
            return true;
            case R.id.action_update:
                UpdaterHelper.CheckForUpdates(this, true);
            return true;
            case R.id.action_feedback:
                Intent intent2= new Intent(this, ActivitySendFeedback.class);
                startActivity(intent2);
            return true;
            case R.id.action_logout:
                Pref.init(this).setBoolean(Pref.IS_LOGGED_IN, false);
                startActivity(new Intent(this, Login.class));
                finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(mReferActivityResult != null) {
            mReferActivityResult.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        mInstance= null;
        super.onDestroy();
    }

    private void readContents() {
        try {
            Reader.read(this, new AsyncListener() {
                @Override
                public void onStart() {
                    swipeLayout.setRefreshing(true);
                }
                @Override
                public void onProgressUpdate(Integer... values) { }
                @Override
                public void onFinish() {
                    swipeLayout.setRefreshing(false);
                }
                @Override
                public void onResult(Boolean result, Http.Response response) {
                    refreshList();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DialogInput.ActivityResult mReferActivityResult= null;
    public static void referANumber(ActivityEx context) {
        referANumber(context, "", "");
    }
    public static void referANumber(ActivityEx context, String oldNumber, String oldName) {
        try {
            mReferActivityResult= DialogInput.showRefer(context, oldNumber, oldName,context.getResources().getString(R.string.enter_customer_details),
                    (newNumber, newName) -> {
                try {
                    mReferActivityResult= null;
                    ProgressHelper helper= ProgressHelper.get(context);
                    Writer.refer(context, newNumber, newName, new AsyncListener() {
                        @Override
                        public void onStart() {
                            helper.show(R.string.inviting_you_customer);
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
                                if(result && response.success()) {
                                    Toast.makeText(context, R.string.invitation_sent_to_customer, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, context.getResources().getString(R.string.invitation_failed_try) + (response==null?"":response.error()), Toast.LENGTH_LONG).show();
                                    referANumber(context, newNumber, newName);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void referANumberWA(Context context) {
        try {
            String name = Pref.init(context).getString("local_user_name", Pref.getFullName(context));
            DialogInput.showString(context, name, "Enter your name (optional)", result -> {
                Pref.init(context).setString("local_user_name", result);
                referANumberWA(context, result);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void referANumberWA(Context context, String name) {
        try {
            name= TextUtils.isEmpty(name)?"This is your your neighborhood shop " + Pref.getFullName(context) + ". ":
                    "I am " + name + " from your neighborhood shop " + Pref.getFullName(context) + ". ";
            String message = name + "In these difficult days of the global covid-19 pandemic,  we are open, serving you, operating as per the guidelines of the civic authority. Though we do take many precautions, we understand the risk we are taking; risk to ourselves and risk to our community. While we were quite concerned about it all, we found a mobile app, AMAfON, that can reduce the risk to us and to our community, helping us to minimise contacts.\n" +
                    "\n" +
                    "If we use AMAfON, you can send your orders to us digitally. We can process your order, send you an estimate, communicate with you easily to finalise the transaction. Once you confirm/pay we can pack your items, all these, without any physical contact. To facilitate this, YOU need to use a companion app, Johnnysapp, the app for every Johnny. The link to learn more about JohnnysApp and to download it:\n" +
                    "https://amafon.in/johnnysapp.html\n" +
                    "\n" +
                    "While we use AMAfON to process your orders, you use JohnnysApp to maintain your shopping list and send us the orders.\n" +
                    "\n" +
                    "The updated version of JonnysApp is not put on Google Play-Store as they are not able to process new requests because of the pandemic. When you install an app from outside play-store, android(Google) gives you many warnings. Please ignore those wanings if you want to use all the features of JohnnysApp.\n" +
                    "\n" +
                    "JohnnysApp, the app for every Johnny, helps you in over a 100 different ways. Maintaining a shopping list and sending the order to us is one of those 100+ features.\n" +
                    "\n" +
                    "Permissions in JohnnysApp:\n" +
                    "If you want to make full use of it, you have to give many permissions at the time of installation, some of which could be MISUSED by others. JohnnysApp, however, is not a security threat since the resident program works as per your selections and your data is not compromised as your data is maintained entirely in your mobile itself. If you give all those permissions, the app can be more useful to you. The fact that all your data is usable even when you aren't connected to internet is proof that the data is entirely on your mobile.\n" +
                    "\n" +
                    "JohnnysApp accesses and uses, with your permission, your call records, your text messages, your contacts, your media files etc to automate many of your mobile usage. For example, you can setup a reminder to call your last incoming caller tomorrow at 10:30am, without copying or typing the mobile number today or even tomorrow when the call is due.\n" +
                    "Another example: The voice command 'Get my id card' can open your aadhaar card, scanned in and saved as one of 'My documents'.\n" +
                    "All these features are possible only if you give all the requested permissions.";

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=&text=" + Uri.encode(message);
            sendIntent.setData(Uri.parse(url));
            context.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean hasWhatsApp(Context context) {
        return getWAIntent(context) != null;
    }
    public static Intent getWAIntent(Context context) {
        PackageManager pm= context.getPackageManager();
        try {
            Intent intent= pm.getLaunchIntentForPackage("com.whatsapp");
            if(intent == null) {
                intent= pm.getLaunchIntentForPackage("com.whatsapp.w4b");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
