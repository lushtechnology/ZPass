package com.lushtechnology.zpass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lushtechnology.zpass.IXRPAccountService;
import com.lushtechnology.zpass.xrp.XrpHttpWrapper;

public class XRPAccountService extends Service {

    XrpHttpWrapper wrapper = new XrpHttpWrapper();
    String address, seed;

    public XRPAccountService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        address = intent.getStringExtra("address");
        seed = intent.getStringExtra("seed");
        return binder;
    }

    private final IXRPAccountService.Stub binder = new IXRPAccountService.Stub() {
        public long getAccountValue() {

            return wrapper.getAccountInfo(address);
        }

        public String getAccountAddress() {
            return address;
        }

        public void pay(String receiverAdress, long amount) {
            wrapper.pay(seed, receiverAdress, amount);
        }


        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) {
            // nothing here
        }
    };
}