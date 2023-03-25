package com.lushtechnology.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.lushtechnology.zpass.IXRPAccountService;

import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("New Payment");
        Button payButton = findViewById(R.id.payButton);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText txtAddress = findViewById(R.id.edit_foreignAddress);
                TextInputEditText txtAmount = findViewById(R.id.edit_amountInXRP);

                String foreignAddress = txtAddress.getText().toString();
                Double amount = Double.parseDouble(txtAmount.getText().toString());
                long amountIndrops = XrpCurrencyAmount.ofXrp(new BigDecimal(amount)).value().longValue();

                try {
                    xrpService.pay(foreignAddress, amountIndrops);
                } catch(RemoteException rex) {
                    Toast.makeText(PaymentActivity.this, "Error", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // bind to XRP account service
        Intent intent = new Intent();
        intent.setClassName("com.lushtechnology.zpass",
                "com.lushtechnology.zpass.XRPAccountService");

        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        // TODO: fix long background process
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
    }

    IXRPAccountService xrpService = null;
    private ServiceConnection myConnection =
            new ServiceConnection() {
                public void onServiceConnected(
                        ComponentName className,
                        IBinder service) {
                    xrpService = IXRPAccountService.Stub.asInterface(service);
                }

                public void onServiceDisconnected(
                        ComponentName className) {
                    xrpService = null;
                }
            };

}