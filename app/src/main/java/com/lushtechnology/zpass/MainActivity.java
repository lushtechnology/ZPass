package com.lushtechnology.zpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    public static String ADDRESS;
    public static String SEED;

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
                                title = "Accounts";break;
                            case 1:
                                title = "Apps"; break;
                            case 2:
                                title = "NFTs"; break;
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
}