package com.veritran.system.bank.app.utils;


import android.content.Context;
import android.widget.Toast;

import com.veritran.system.bank.app.enums.TypeMessagesEnum;

import es.dmoral.toasty.Toasty;

public class MessagesUtil {

    public static void getMessage(Context context, String message, TypeMessagesEnum typeMessagesEnum) {

        switch (typeMessagesEnum) {

            case ERROR:
                Toasty.error(context, message, Toast.LENGTH_LONG, true).show();
                break;

            case INFO:
                Toasty.info(context, message, Toast.LENGTH_LONG, true).show();
                break;

            case SUCCES:
                Toasty.success(context, message, Toast.LENGTH_LONG, true).show();
                break;

            case WARNING:
                Toasty.warning(context, message, Toast.LENGTH_LONG, true).show();
                break;

            default:
                Toasty.normal(context, message, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
