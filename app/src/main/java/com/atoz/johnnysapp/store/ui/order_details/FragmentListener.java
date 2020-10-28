package com.atoz.johnnysapp.store.ui.order_details;

import com.atoz.johnnysapp.store.data.DatabaseHandler;
import com.atoz.johnnysapp.store.data.Table_Orders;

public interface FragmentListener {
    DatabaseHandler getDatabase();
    OrderDetails getActivity();
    int getOrderID();
    Table_Orders getOrder();
}
