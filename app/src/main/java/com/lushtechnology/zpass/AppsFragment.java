package com.lushtechnology.zpass;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lushtechnology.zpass.store.DownloadWorker;
import com.lushtechnology.zpass.store.StoreApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppsFragment extends Fragment
        implements StoreAppAdapter.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<StoreApp> apps;

    ProgressBar progressBar;
    StoreAppAdapter adapter;

    public AppsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppsFragment newInstance(String param1, String param2) {
        AppsFragment fragment = new AppsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        // Lookup the recyclerview in activity layout
        RecyclerView rvStoreApps = (RecyclerView) view.findViewById(R.id.rvStoreApps);
        progressBar = view.findViewById(R.id.progress_bar);

        // Initialize contacts
        final String base_url = "https://raw.githubusercontent.com/lushtechnology/ZPass-store/main/apps/";

        apps = new ArrayList<>();
        apps.add(new StoreApp("Demo Payment App", base_url + "demo-release.apk"));
        apps.add(new StoreApp("Foo App 1", base_url + "foo1.apk"));
        apps.add(new StoreApp("Foo App 2", base_url + "foo2.apk"));
        apps.add(new StoreApp("Foo App 3", base_url + "foo3.apk"));
        apps.add(new StoreApp("Foo App 4", base_url + "foo4.apk"));
        apps.add(new StoreApp("Foo App 5", base_url + "foo5.apk"));

        //StoreApp.createList(20);
        // Create adapter passing in the sample user data
        adapter = new StoreAppAdapter(apps);
        adapter.setOnItemClickListener(this);

        // Attach the adapter to the recyclerview to populate items
        rvStoreApps.setAdapter(adapter);
        // Set layout manager to position the items
        rvStoreApps.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onItemClick(int position) {

        StoreApp app = apps.get(position);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putString("url", app.getURL())
                .putString("filename", app.getName() + ".apk")
                .build();

        OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(getContext()).enqueue(downloadRequest);

        LiveData<WorkInfo> workInfoLiveData = WorkManager.getInstance(getContext()).
                    getWorkInfoByIdLiveData(downloadRequest.getId());

       workInfoLiveData.observe(this, workInfo -> {
                    if (workInfo.getState() == WorkInfo.State.RUNNING) {
                        progressBar.setVisibility(View.VISIBLE);
                        int progress = workInfo.getProgress().getInt("progress",0);
                        progressBar.setProgress(progress);
                    } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        progressBar.setVisibility(View.GONE);
                        // File download succeeded

                        DialogInterface.OnClickListener dilistner = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        String filePath = workInfo.getOutputData().getString("filePath");
                                        installAPK(filePath);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                .setMessage("Proceed to install the app?")
                                .setPositiveButton("Yes", dilistner)
                                .setNegativeButton("No", dilistner);
                        builder.show();

                    } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                        progressBar.setVisibility(View.GONE);
                        // File download failed
                    }
                });

    }

    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_INSTALL = 2;

    void installAPK(String filePath) {
        File apkFile = new File(filePath);

        if (apkFile.exists()) {
            Uri apkUri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider", apkFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, REQUEST_INSTALL);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), "APK installed successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "APK installation failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mInstallButton.setEnabled(true);
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
