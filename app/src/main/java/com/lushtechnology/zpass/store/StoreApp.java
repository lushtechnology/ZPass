package com.lushtechnology.zpass.store;

import com.lushtechnology.zpass.StoreAppAdapter;

import org.bouncycastle.util.Store;

import java.util.ArrayList;

public class StoreApp {

    private String mName;
    private String mURL;

    public StoreApp(String name, String url) {
        mName = name;
        mURL = url;
    }

    public String getName() {
        return mName;
    }

    public String getURL() {
        return mURL;
    }

    public static ArrayList<StoreApp> createList(int num) {
        ArrayList<StoreApp> apps = new ArrayList<StoreApp>();

        for (int i = 1; i <= num; i++) {
            apps.add(new StoreApp("Foo App " + i,
                    "https://raw.githubusercontent.com/lushtechnology/ZPass-store/main/apps/foo" + i + "-release.apk"));
        }

        return apps;
    }
}