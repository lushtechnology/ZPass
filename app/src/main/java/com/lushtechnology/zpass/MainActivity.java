package com.lushtechnology.zpass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setClassName("com.lushtechnology.zpass",
                "com.lushtechnology.zpass.XRPAccountService");

        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Supabutton");

                try{
                    double x = xrpService.getAccountValue();
                    Toast.makeText(getApplicationContext(), "account is " + x, Toast.LENGTH_SHORT);
                } catch(RemoteException re) {
                    re.printStackTrace();
                }

            }
        });
    }

    IXRPAccountService xrpService = null;
    boolean isBound;
    private ServiceConnection myConnection =
            new ServiceConnection() {
                public void onServiceConnected(
                        ComponentName className,
                        IBinder service) {
                    xrpService = IXRPAccountService.Stub.asInterface(service);
                    isBound = true;
                }

                public void onServiceDisconnected(
                        ComponentName className) {
                    xrpService = null;
                    isBound = false;
                }
            };
}