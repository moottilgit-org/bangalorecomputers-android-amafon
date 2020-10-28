package com.atoz.johnnysapp.store.ui.order_details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.data.Table_Items;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;
import com.atoz.johnnysapp.store.utils.Toast;
import com.mixiaoxiao.fastscroll.FastScrollRecyclerView;
import com.mixiaoxiao.fastscroll.helper.RecyclerListAdapter;

import java.util.ArrayList;

public class ItemSelection extends ActivityEx implements View.OnClickListener {
    EditText edSearchFor;
    ImageButton btnClearSearch;
    LinearLayout llNoItems;
    Button btnNew;
    FastScrollRecyclerView rvItems;
    ArrayList<Table_Items> alAllItems, alItems;
    RecyclerListAdapter<Table_Items> raItems;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_selection);
        Toolbar toolbar= findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        edSearchFor= findViewById(R.id.edSearchFor);
        btnClearSearch= findViewById(R.id.btnClearSearch);
        llNoItems= findViewById(R.id.llNoItems);
        btnNew= findViewById(R.id.btnNew);
        rvItems= findViewById(R.id.rvItems);

        btnClearSearch.setOnClickListener(this);
        btnNew.setOnClickListener(this);

        alAllItems= Table_Items.getList(DB);
        alAllItems= alAllItems==null?new ArrayList<>():alAllItems;
        alItems= new ArrayList<>();

        if(getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            edSearchFor.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
        edSearchFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                refreshList();
            }
        });

        raItems= new RecyclerListAdapter<>(this, rvItems, R.layout.lv_attachment_small, alItems, null, new RecyclerListAdapter.Binder() {
            @Override
            public void onBindView(RecyclerListAdapter mAdapter, RecyclerListAdapter.ItemViewHolder holder, int mPosition) {
                try {
                    TextView tvName = holder.mItemView.findViewById(R.id.tvName);
                    ImageButton btnRemove = holder.mItemView.findViewById(R.id.btnRemove);

                    tvName.setText(alItems.get(mPosition).getField(Table_Items.FIELD_NAME));
                    tvName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    btnRemove.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onClick(RecyclerListAdapter mAdapter, View view, int position) {
                Intent intent= new Intent();
                intent.putExtra("ITEM_NAME", alItems.get(position).getField(Table_Items.FIELD_NAME));
                intent.putExtra("ITEM_ID", alItems.get(position).getFieldInt(Table_Items.FIELD_ID));
                intent.putExtra("POSITION", getIntent().getIntExtra("POSITION", -1));
                setResult(RESULT_OK, intent);
                finish();
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

        refreshList();
    }

    private void refreshList() {
        try {
            String searchString= edSearchFor.getText().toString().trim();
            btnClearSearch.setVisibility(searchString.isEmpty()?View.GONE:View.VISIBLE);

            alItems.clear();
            for(Table_Items item: alAllItems) {
                String name= item.getField(Table_Items.FIELD_NAME);
                if(name.equalsIgnoreCase(searchString) || name.contains(searchString) ||
                        name.startsWith(searchString) || name.endsWith(searchString)) {
                    alItems.add(item);
                }
            }

            raItems.notifyDataSetChanged();
            rvItems.setVisibility(alItems.size()==0?View.GONE:View.VISIBLE);
            llNoItems.setVisibility(alItems.size()>0?View.GONE:View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnClearSearch:
                    edSearchFor.setText("");
                    refreshList();
                break;
                case R.id.btnNew:
                    if(edSearchFor.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "Please enter Item Name", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent= new Intent();
                        intent.putExtra("ITEM_NAME", edSearchFor.getText().toString().trim());
                        intent.putExtra("ITEM_ID", 0);
                        intent.putExtra("POSITION", getIntent().getIntExtra("POSITION", -1));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
