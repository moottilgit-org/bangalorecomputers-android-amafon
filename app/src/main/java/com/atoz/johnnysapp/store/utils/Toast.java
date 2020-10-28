package com.atoz.johnnysapp.store.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.view.Gravity;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;

public class Toast {
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;

    public static android.widget.Toast makeText(Context context, CharSequence text, int duration) {
        android.widget.Toast toast= android.widget.Toast.makeText(context, text, duration);
        toast.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        toast.getView().setPadding(10, 10, 10, 10);
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    public static android.widget.Toast makeText(Context context, int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }
}
