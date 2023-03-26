package com.lushtechnology.zpass;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static void initXrpService(Context context, ServiceConnection myConnection, HashMap<String, String> params) {
        Intent intent = new Intent();
        intent.setClassName(XRPAccountService.class.getPackage().getName(),
                XRPAccountService.class.getName());
        intent.putExtra("address", MainActivity.ADDRESS);
        intent.putExtra("seed", MainActivity.SEED);

        if(params != null)
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                intent.putExtra(key, value);
            }
        //startService(intent);
        context.bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }
}
