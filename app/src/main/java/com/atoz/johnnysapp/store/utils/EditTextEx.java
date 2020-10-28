package com.atoz.johnnysapp.store.utils;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;

public class EditTextEx extends AppCompatEditText {
    ArrayList<TextWatcher> mListeners = new ArrayList<>();
//    public EditTextEx(Context context) {
//        super(context, (AttributeSet)null);
//    }

    public EditTextEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public EditTextEx(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    public void removeTextChangedListeners() {
        try {
            if (mListeners != null) {
                for (int j = mListeners.size(), i = j - 1; i >= 0; i--) {
                    super.removeTextChangedListener(mListeners.get(i));
                }
                mListeners.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        super.removeTextChangedListener(watcher);
        try {
            if (mListeners != null) {
                mListeners.remove(watcher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
        try {
            if (mListeners == null) {
                mListeners= new ArrayList<>();
            }
            mListeners.add(watcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
