package com.atoz.johnnysapp.store.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.ui.base.ActivityEx;

import java.util.Timer;
import java.util.TimerTask;

public class DialogInput {
    public interface AfterDialog {
        void run(String result);
    }
    public interface ActivityResult {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
    public interface AfterDialogEx {
        void run(String newNumber, String newName);
    }
    public static void showInt(final Context context, final String oldInput, final String title, final AfterDialog mAfterDialog) {
        show(context, R.layout.ja_dialog_input_int, oldInput, title, null, mAfterDialog);
    }
    public static void showString(final Context context, final String oldInput, final String title, final AfterDialog mAfterDialog) {
        show(context, R.layout.ja_dialog_input_text, oldInput, title, null, mAfterDialog);
    }
    public static void showAutoString(final Context context, final String oldInput, final String title,
                                      final ArrayAdapter<String> adapter, final AfterDialog mAfterDialog) {
        show(context, R.layout.ja_dialog_input_text, oldInput, title, adapter, mAfterDialog);
    }
    public static void showStringMulti(final Context context, final String oldInput, final String title, final AfterDialog mAfterDialog) {
        show(context, R.layout.ja_dialog_input_text_multiline, oldInput, title, null, mAfterDialog);
    }
    public static void show(final Context context, int input_layout, final String oldInput, final String title,
                            final ArrayAdapter<String> adapter, final AfterDialog mAfterDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Html.fromHtml(title));
        View viewInflated = LayoutInflater.from(context).inflate(input_layout, null);
        final EditText input= (EditText) viewInflated.findViewById(R.id.input);
        input.setText(oldInput);
        if(adapter!=null) {
            ((AutoCompleteTextView) input).setAdapter(adapter);
        }
        builder.setView(viewInflated);
        builder.setCancelable(false);
        builder.setOnDismissListener((dialog)->{
            ActivityEx.hideFocus(context, input);
        }).setOnCancelListener((dialog)->{
            ActivityEx.hideFocus(context, input);
        });
        builder.setPositiveButton(R.string.action_ok, (dialog,which)->{
            ActivityEx.hideFocus(context, input);
            dialog.dismiss();
            if(mAfterDialog!=null) {
                mAfterDialog.run(input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.action_cancel, (dialog,which)->{
            ActivityEx.hideFocus(context, input);
            dialog.cancel();
        });
        builder.show();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)context).runOnUiThread(()->{
                    input.requestFocus();
                    ActivityEx.showFocus(context, input);
                });
            }
        }, 100);
        input.requestFocus();
        ActivityEx.showFocus(context, input);
    }


    public static ActivityResult showRefer(final ActivityEx context, final String number, final String name, final String title, final AfterDialogEx mAfterDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Html.fromHtml(title));
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ja_dialog_input_refer, null);
        final EditText edNumber= viewInflated.findViewById(R.id.edNumber);
        final ImageButton imgbtnContact= viewInflated.findViewById(R.id.imgbtnContact);
        final EditText edNickName= viewInflated.findViewById(R.id.edNickName);
        edNumber.setText(number);
        edNickName.setText(name);

        imgbtnContact.setOnClickListener(v -> {
            Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            context.startActivityForResult(intentContact, ActivityEx.REQ_PICK_CONTACT);
        });

        builder.setView(viewInflated);
        builder.setCancelable(false);
        builder.setOnDismissListener((dialog)->{
            ActivityEx.hideFocus(context, edNumber);
        }).setOnCancelListener((dialog)->{
            ActivityEx.hideFocus(context, edNumber);
        });
        builder.setPositiveButton(R.string.action_ok, (dialog,which)->{
            ActivityEx.hideFocus(context, edNumber);
            dialog.dismiss();
            if(mAfterDialog!=null) {
                mAfterDialog.run(edNumber.getText().toString(), edNickName.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.action_cancel, (dialog,which)->{
            ActivityEx.hideFocus(context, edNumber);
            dialog.cancel();
        });
        builder.show();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)context).runOnUiThread(()->{
                    edNumber.requestFocus();
                    ActivityEx.showFocus(context, edNumber);
                });
            }
        }, 100);
        edNumber.requestFocus();
        ActivityEx.showFocus(context, edNumber);

        return (requestCode, resultCode, data) -> {
            if(requestCode==ActivityEx.REQ_PICK_CONTACT && resultCode==Activity.RESULT_OK) {
                try (Cursor c= context.getContentResolver().query(data.getData(), null, null, null, null)) {
                    if(c != null && c.getCount() > 0 && c.moveToFirst()) {
                        edNickName.setText(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        edNumber.setText(cleanupANumberForSearch(c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.DATA1))));
                    }
                }
            }
        };
    }

    public static String cleanupANumber(String number) {
        if(number==null) {
            return "";
        }
        String[] invalidChars= new String[] {" ", "(", ")", "-", "[", "]"};
        for(int i=0; i<invalidChars.length; i++) {
            if (number.contains(invalidChars[i])) {
                number = number.replace(invalidChars[i], "");
            }
        }
        return number;
    }

    public static String cleanupANumberForSearch(String number) {
        if(number==null) {
            return "";
        }
        number= cleanupANumber(number);
        if(number.startsWith("0")) {
            number= number.substring(1);
        } else if(number.startsWith("+")){
            number= number.substring(3 + (number.length()>13?1:0));
        }
        return number;
    }
}
