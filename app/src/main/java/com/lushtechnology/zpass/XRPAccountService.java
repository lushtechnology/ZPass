package com.lushtechnology.zpass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class XRPAccountService extends Service {
    public XRPAccountService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //return null;
        return binder;
    }

    private final IXRPAccountService.Stub binder = new IXRPAccountService.Stub() {
        public double getAccountValue() {
            return 5.1;
        }

        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) {
            // nothing here
        }
    };
}