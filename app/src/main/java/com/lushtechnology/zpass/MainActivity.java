package com.lushtechnology.zpass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String ADDRESS = "r4nucSPkeHNo6XNapFwPCpJdYX9XiJB7je";
    private static final String SEED = "sEdV5bYRmQS22UsGMfXy8TPimxokno5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start xrp service
        Intent intent = new Intent();
        intent.setClassName("com.lushtechnology.zpass",
                "com.lushtechnology.zpass.XRPAccountService");
        intent.putExtra("address", ADDRESS);
        intent.putExtra("seed", SEED);
        //startService(intent);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        testButton();
    }

    private void testButton(){

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Supabutton");

                try {
                    long x = exposedService.getAccountValue();
                    Toast.makeText(getApplicationContext(), "account is " + x,
                            Toast.LENGTH_SHORT).show();
                } catch(RemoteException rex) {
                    rex.printStackTrace();
                }

            }
        });
    }

    IXRPAccountService exposedService = null;
    boolean isBound;
    final private ServiceConnection myConnection =
            new ServiceConnection() {
                public void onServiceConnected(
                        ComponentName className,
                        IBinder service) {
                    exposedService = IXRPAccountService.Stub.asInterface(service);
                    isBound = true;
                }

                public void onServiceDisconnected(
                        ComponentName className) {
                    exposedService = null;
                    isBound = false;
                }
            };
}