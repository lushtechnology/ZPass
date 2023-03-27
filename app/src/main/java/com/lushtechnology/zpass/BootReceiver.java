package com.lushtechnology.zpass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            // start XRP service
            intent.setClassName(XRPAccountService.class.getPackage().getName(),
                    XRPAccountService.class.getName());
            intent.putExtra("address", MainActivity.ADDRESS);
            intent.putExtra("seed", MainActivity.SEED);
            context.startService(intent);
        }
    }
}
