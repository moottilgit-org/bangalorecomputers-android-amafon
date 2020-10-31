package com.atoz.johnnysapp.store.utils;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import java.util.Locale;

public class PhoneNumberCleaner {
    public static String whatsappNet(String number) {
        if(TextUtils.isEmpty(number)) {
            return "";
        }
        number= number.replace("@s.whatsapp.net", "");
        return whatsapp(number) + "@s.whatsapp.net";
    }
    public static String whatsapp(String number) {
        if(TextUtils.isEmpty(number)) {
            return "";
        }
        number= format(number);
        if (number.startsWith("+")) {
            number= number.substring(1);
        }
        return number;
    }

    private static String cleanup(String number) {
        if(TextUtils.isEmpty(number)) {
            return "";
        }
        String[] invalidChars= new String[] {" ", "(", ")", "-", "[", "]"};
        for(String str: invalidChars) {
            if (number.contains(str)) {
                number = number.replace(str, "");
            }
        }
        return number;
    }
    private static String cc(String number) {
        if(TextUtils.isEmpty(number)) {
            return "+91";
        }
        return number.contains(" ")?number.split(" ")[0]:"+91";
    }

    public static String format(String number) {
        //input 00971565725467, change to +971565725467 and +971 56 572 5467
        //input +16128142085/0016128142085, change to +16128142085 and +1 612-814-2085
        //input +919035289381/09035289381, change to +919035289381 and +91 90352 89381
        if(TextUtils.isEmpty(number)) {
            return "";
        }
        number= cleanup(number);
        if(PermissionManager.is21()) {
            number = PhoneNumberUtils.formatNumberToE164(number, Locale.getDefault().getCountry());
            //number= PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
        }
        return number;
    }
}
