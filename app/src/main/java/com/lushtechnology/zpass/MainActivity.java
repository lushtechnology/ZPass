package com.lushtechnology.zpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static String ADDRESS;
    public static String SEED;

    public static final String PAYMENT_CONFIRM_INTENT = "com.lushtechnology.PAYMENT_CONFIRM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // elements
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 viewPager2 = findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(MainActivity.this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        String title = "";
                        switch (position) {
                            case 0:
                                title = "Apps"; break;
                            case 1:
                                title = "NFTs"; break;
                            case 2:
                                title = "Accounts";break;
                        }
                        tab.setText(title);
                    }
                }).attach();

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ADDRESS = prefs.getString("address", "");
        SEED = prefs.getString("seed", "");

        Intent intent = new Intent();
        intent.setClassName(XRPAccountService.class.getPackage().getName(),
                XRPAccountService.class.getName());
        intent.putExtra("address", MainActivity.ADDRESS);
        intent.putExtra("seed", MainActivity.SEED);
        startService(intent);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent intent) {
        super.onActivityReenter(resultCode, intent);

        /* Obtain String from Intent  */
        if (intent.getAction().equals(PAYMENT_CONFIRM_INTENT))
        {
            String receiverAddress = getIntent().getStringExtra("receiverAddress");
            String amount = getIntent().getStringExtra("amount");

            new AlertDialog.Builder(this)
                    .setTitle("Payment")
                    .setMessage("Do you want to approve this transaction?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            HashMap<String, String> m = new HashMap<>();
                            m.put("receiverAddress", receiverAddress);
                            m.put("amount", amount);
                            performPayment(m);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    private void performPayment(HashMap<String, String> params) {

        Helper.initXrpService(getApplicationContext(), myConnection, params);
    }

    final private ServiceConnection myConnection =
            new ServiceConnection() {
                public void onServiceConnected(
                        ComponentName className,
                        IBinder service) {

                    IXRPAccountService xrp = IXRPAccountService.Stub.asInterface(service);
                    String receiverAddress = getIntent().getStringExtra("receiverAddress");

                    try {
                        xrp.pay(receiverAddress, 0);
                    } catch (RemoteException rexp) {

                    }

                }

                public void onServiceDisconnected(
                        ComponentName className) {
                }
            };
}