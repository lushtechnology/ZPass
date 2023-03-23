package com.lushtechnology.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.lushtechnology.demo.databinding.ActivityMainBinding;
import com.lushtechnology.zpass.IXRPAccountService;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // bind to XRP account service
        Intent intent = new Intent();
        intent.setClassName("com.lushtechnology.zpass",
                "com.lushtechnology.zpass.XRPAccountService");

        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    public void sendMessage()
    {
        if (!isBound) return;

        try {
            double amount = xrpService.getAccountValue();
            Toast.makeText(getApplicationContext(), "Account value is " + amount,
                    Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}