package com.lushtechnology.zpass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import okhttp3.HttpUrl;

public class XRPAccountService extends Service {

    XrplClient xrplClient;

    Seed wallet;

    public XRPAccountService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        init();
        return binder;
    }

    private void init() {
        // TODO: send as parameters
        String classicAddress = "r4nucSPkeHNo6XNapFwPCpJdYX9XiJB7je";
        String seed = "sEdV5bYRmQS22UsGMfXy8TPimxokno5";

        HttpUrl rippledUrl = HttpUrl
                .get("https://s.altnet.rippletest.net:51234/");
        xrplClient = new XrplClient(rippledUrl);

        Seed wallet = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(seed));

    }

    private final IXRPAccountService.Stub binder = new IXRPAccountService.Stub() {
        public double getAccountValue() {

            Address classicAddress = wallet.deriveKeyPair().publicKey().deriveAddress();
            AccountInfoRequestParams requestParams =
                    AccountInfoRequestParams.of(classicAddress);

            double value = -1;
            try {
                AccountInfoResult accountInfoResult =
                        xrplClient.accountInfo(requestParams);

                XrpCurrencyAmount amount = accountInfoResult.accountData().balance();

                value = amount.value().longValue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return value;
        }

        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) {
            // nothing here
        }
    };
}