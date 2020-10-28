package com.atoz.johnnysapp.store.ui.order_details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.data.Table_Items;
import com.atoz.johnnysapp.store.data.Table_OrderItems;
import com.atoz.johnnysapp.store.data.Table_Orders;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.EditTextEx;
import com.atoz.johnnysapp.store.utils.Toast;
import com.mixiaoxiao.fastscroll.FastScrollRecyclerView;
import com.mixiaoxiao.fastscroll.helper.RecyclerListAdapter;
import com.mixiaoxiao.fastscroll.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

public class ItemsFragment extends FragmentEx {
    TextView tvItemCount;
    FastScrollRecyclerView rvItems;
    ArrayList<Table_OrderItems> alItems;
    RecyclerListAdapter<Table_OrderItems> raItems;

    boolean mDisabled= false;
    public static ItemsFragment create() {
        ItemsFragment fragment = new ItemsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ItemsFragment() {
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
        view= inflater.inflate(R.layout.activity_order_details_items, null);
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
            tvItemCount= view.findViewById(R.id.tvItemCount);
            rvItems= view.findViewById(R.id.rvItems);
        } catch (Exception e) {
            Log.e("initView", e.toString());
        }
    }

    private void initAdapter() {
        try {
            alItems= new ArrayList<>();
            raItems= new RecyclerListAdapter<>(mContext, rvItems, R.layout.lv_order_items, alItems, 1, 1, new SimpleItemTouchHelperCallback.Validator() {
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
                        TextView tvNumber = holder.mItemView.findViewById(R.id.tvNumber);
                        TextView tvName = holder.mItemView.findViewById(R.id.tvName);
                        TextView tvLinkName = holder.mItemView.findViewById(R.id.tvLinkName);
                        EditTextEx edQty = holder.mItemView.findViewById(R.id.edQty);
                        EditTextEx edRate = holder.mItemView.findViewById(R.id.edRate);
                        LinearLayout llItemSelection = holder.mItemView.findViewById(R.id.llItemSelection);

                        Button btnStockAvailable = holder.mItemView.findViewById(R.id.btnStockAvailable);
                        Button btnNoStock = holder.mItemView.findViewById(R.id.btnNoStock);

                        try {
                            edQty.removeTextChangedListeners();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            edRate.removeTextChangedListeners();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tvNumber.setText("#" + (mPosition + 1));
                        tvName.setText(alItems.get(mPosition).getField(Table_OrderItems.FIELD_ITEM_NAME));
                        edQty.setText(alItems.get(mPosition).getField(Table_OrderItems.FIELD_QTY));
                        edRate.setText(alItems.get(mPosition).getField(Table_OrderItems.FIELD_RATE));

                        if (!alItems.get(mPosition).getData().containsKey("item_name_linked")) {
                            Table_Items item = alItems.get(mPosition).getFieldInt(Table_OrderItems.FIELD_ITEM_LINK) > 0 ?
                                    Table_Items.get(DB, alItems.get(mPosition).getFieldInt(Table_OrderItems.FIELD_ITEM_LINK)) :
                                    Table_Items.get(DB, alItems.get(mPosition).getField(Table_OrderItems.FIELD_ITEM_NAME));
                            if (item == null) {
                                alItems.get(mPosition).setFieldInt(Table_OrderItems.FIELD_ITEM_LINK, 0);
                                alItems.get(mPosition).setField("item_name_linked", alItems.get(mPosition).getField(Table_OrderItems.FIELD_ITEM_NAME));
                            } else {
                                alItems.get(mPosition).setFieldInt(Table_OrderItems.FIELD_ITEM_LINK, item.getFieldInt(Table_Items.FIELD_ID));
                                alItems.get(mPosition).setField("item_name_linked", item.getField(Table_Items.FIELD_NAME));
                            }
                        }
                        tvLinkName.setText(alItems.get(mPosition).getField("item_name_linked"));

                        if (!mDisabled && TextUtils.equals("AVAIL", alItems.get(mPosition).getField(Table_OrderItems.FIELD_ITEM_STATUS))) {
                            btnStockAvailable.setVisibility(View.VISIBLE);
                            btnNoStock.setVisibility(View.GONE);
                            edQty.setEnabled(true);
                            edRate.setEnabled(true);
                        } else {
                            btnStockAvailable.setVisibility(View.GONE);
                            btnNoStock.setVisibility(View.VISIBLE);
                            edQty.setEnabled(false);
                            edRate.setEnabled(false);
                        }
                        if (mDisabled) {
                            btnStockAvailable.setEnabled(false);
                            btnNoStock.setEnabled(false);
                        } else {
                            btnStockAvailable.setEnabled(true);
                            btnNoStock.setEnabled(true);
                        }

                        edQty.addTextChangedListener(new MyTextWatcher(mPosition, 0));
                        edRate.addTextChangedListener(new MyTextWatcher(mPosition, 1));

                        MyClickListener mListener = new MyClickListener(mPosition);
                        btnStockAvailable.setOnClickListener(mListener);
                        btnNoStock.setOnClickListener(mListener);
                        llItemSelection.setOnClickListener(mListener);

                        edQty.setOnFocusChangeListener(onFocusChangeListener);
                        edRate.setOnFocusChangeListener(onFocusChangeListener);
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
                    mMainActivity.getActivity().mChatHelper.addMessageRef(alItems.get(position).getFieldInt(Table_OrderItems.FIELD_ID),
                            "I", "Item: " + alItems.get(position).getField(Table_OrderItems.FIELD_ITEM_NAME));
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

    private final View.OnFocusChangeListener onFocusChangeListener= (v, hasFocus) -> {
        try {
            if (hasFocus) {
                mMainActivity.getActivity().llAddressContainer.setVisibility(View.GONE);
                mMainActivity.getActivity().tvAddress.setMaxLines(3);
                mMainActivity.getActivity().tvAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                mMainActivity.getActivity().tvNotes.setMaxLines(1);
                mMainActivity.getActivity().tvNotes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private class MyTextWatcher implements TextWatcher {
        private final int mPosition;
        private final int mColumn;
        MyTextWatcher(int position, int column) {
            mPosition= position;
            mColumn= column;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            try {
                switch (mColumn) {
                    case 0: alItems.get(mPosition).setField(Table_OrderItems.FIELD_QTY, s.toString()); break;
                    case 1: alItems.get(mPosition).setField(Table_OrderItems.FIELD_RATE, s.toString()); break;
                }
                mMainActivity.getActivity().mIsContentChanged= true;
                updateSummary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyClickListener implements View.OnClickListener {
        private final int mPosition;
        MyClickListener(int position) {
            mPosition= position;
        }
        @Override
        public void onClick(View v) {
            try {
                if(mDisabled) {
                    return;
                }
                if(v.getId()==R.id.llItemSelection) {
                    selectItem(mPosition);
                } else {
                    if (TextUtils.equals("AVAIL", alItems.get(mPosition).getField(Table_OrderItems.FIELD_ITEM_STATUS))) {
                        alItems.get(mPosition).setField(Table_OrderItems.FIELD_ITEM_STATUS, "EMPTY");
                    } else {
                        alItems.get(mPosition).setField(Table_OrderItems.FIELD_ITEM_STATUS, "AVAIL");
                    }
                    raItems.notifyItemChanged(mPosition);
                    mMainActivity.getActivity().mIsContentChanged = true;
                    updateSummary();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void selectItem(int position) {
        try {
            Intent intent= new Intent(mContext, ItemSelection.class);
            intent.putExtra(Intent.EXTRA_TEXT, alItems.get(position).getField("item_name_linked"));
            intent.putExtra("POSITION", position);
            startActivityForResult(intent, ActivityEx.REQ_PICK_NEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshList() {
        try {
            mDisabled= TextUtils.equals("FINISH", mMainActivity.getOrder().getField(Table_Orders.FIELD_STATUS))||
                    TextUtils.equals("CANCEL", mMainActivity.getOrder().getField(Table_Orders.FIELD_STATUS)) ||
                    TextUtils.equals("REJECT", mMainActivity.getOrder().getField(Table_Orders.FIELD_STATUS));
            alItems.clear();
            ArrayList<Table_OrderItems> temp= Table_OrderItems.getList(DB, mMainActivity.getOrderID());
            if(temp != null && temp.size() > 0) {
                alItems.addAll(temp);
            }

            raItems.notifyDataSetChanged();
            updateSummary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSummary() {
        try {
            double items= 0, qty= 0, rate= 0, missed= 0;
            for(Table_OrderItems item: alItems) {
                if(TextUtils.equals("AVAIL", item.getField(Table_OrderItems.FIELD_ITEM_STATUS))) {
                    double q= item.getFieldDouble(Table_OrderItems.FIELD_QTY);
                    items++;
                    qty+= q;
                    rate+= q * item.getFieldDouble(Table_OrderItems.FIELD_RATE);
                } else {
                    missed++;
                }
            }
            String summary= "Items: " + items;
            summary= summary.endsWith(".0")?summary.substring(0, summary.length()-2):summary;
            summary+= " (qty: " + qty + ")";
            summary= summary.endsWith(".0)")?summary.substring(0, summary.length()-3)+")":summary;
            summary+= ", Amt: " + rate;
            summary= summary.endsWith(".0")?summary.substring(0, summary.length()-2):summary;
            if(missed > 0) {
                summary+= ",  No Stock: " + missed;
                summary= summary.endsWith(".0")?summary.substring(0, summary.length()-2):summary;
            }
            tvItemCount.setText(summary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ActivityEx.REQ_PICK_NEW && resultCode == Activity.RESULT_OK) {
            int position= data.getIntExtra("POSITION", -1);
            if(position >= 0) {
                alItems.get(position).setField("item_name_linked", data.getStringExtra("ITEM_NAME"));
                alItems.get(position).setFieldInt(Table_OrderItems.FIELD_ITEM_LINK, data.getIntExtra("ITEM_ID", 0));
                raItems.notifyItemChanged(position);
                mMainActivity.getActivity().mIsContentChanged= true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}