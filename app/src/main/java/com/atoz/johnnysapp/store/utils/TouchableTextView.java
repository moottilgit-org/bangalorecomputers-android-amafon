package com.atoz.johnnysapp.store.utils;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.atoz.johnnysapp.store.R;

public class TouchableTextView extends AppCompatTextView implements View.OnClickListener {
    public TouchableTextView(Context context) {
        super(context, (AttributeSet)null);
        init();
    }

    public TouchableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);//, 16842884);
        init();
    }

    public TouchableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if(getText().toString().isEmpty()) {
                return;
            }
            AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
            builder.setTitle(" ")
                    .setItems(new String[] { getText().toString() }, null)
                    .setNeutralButton(R.string.copy, (dialog, which) -> {
                        if(copyToClipBoard()) {
                            Toast.makeText(getContext(), R.string.copied_to_clipboard, Toast.LENGTH_LONG).show();
                        }
                    }).setPositiveButton(R.string.close, null);
            AlertDialog dlg= builder.create();
            dlg.setOnShowListener(dialog -> {
                ((TextView) ((AlertDialog) dialog).getListView().getChildAt(0)).setTextIsSelectable(true);
            });
            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean copyToClipBoard() {
        try {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard != null) {
                ClipData clip = ClipData.newPlainText("TouchableTextView", getText().toString());
                clipboard.setPrimaryClip(clip);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
