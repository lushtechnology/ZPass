package com.lushtechnology.zpass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.lushtechnology.zpass.store.DownloadWorker;
import com.lushtechnology.zpass.store.StoreApp;

import java.io.File;
import java.util.List;

public class StoreAppAdapter extends
        RecyclerView.Adapter<StoreAppAdapter.ViewHolder> implements LifecycleOwner {

    private List<StoreApp> apps;

    private Context context;

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);


    // Pass in the contact array into the constructor
    public StoreAppAdapter(List<StoreApp> storeapps, LifecycleOwner owner) {
        apps = storeapps;

        owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {

            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
            }

            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View appView = inflater.inflate(R.layout.item_app, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(appView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreApp app = apps.get(position);

        holder.nameTextView.setText(app.getName());

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onDownloadApkClick(holder);
            }
        });
    }

    public void onDownloadApkClick(ViewHolder holder) {

        StoreApp app = apps.get(holder.getAbsoluteAdapterPosition());

        holder.downloadButton.setVisibility(View.INVISIBLE);
        holder.progressIndicator.setVisibility(View.VISIBLE);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putString("url", app.getURL())
                .build();

        OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueue(downloadRequest);

        LiveData<WorkInfo> workInfoLiveData = WorkManager.getInstance(context).
                getWorkInfoByIdLiveData(downloadRequest.getId());

        workInfoLiveData.observe(this, workInfo -> {
            if (workInfo.getState() == WorkInfo.State.RUNNING) {

                holder.progressIndicator.setVisibility(View.VISIBLE);
                int progress = workInfo.getProgress().getInt("progress",0);
                holder.progressIndicator.setProgress(progress);
            } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                holder.downloadButton.setVisibility(View.VISIBLE);
                holder.progressIndicator.setVisibility(View.INVISIBLE);
                // File download succeeded
                String filePath = workInfo.getOutputData().getString("filePath");
                userConfirmProceedInstallation(filePath);
            } if(workInfo.getState() == WorkInfo.State.FAILED) {
                holder.downloadButton.setVisibility(View.VISIBLE);
                holder.progressIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void userConfirmProceedInstallation(String apkfile) {

        /*
        Snackbar.make(this.getView(), "Message Text", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        installAPK(apkfile);
                    }
                })
                .show();
        */
        DialogInterface.OnClickListener dilistner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        installAPK(apkfile);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("Proceed to install the app?")
                .setPositiveButton("Yes", dilistner)
                .setNegativeButton("No", dilistner);
        builder.show();
    }

    void installAPK(String filePath) {

        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (userManager.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY)) {
            System.out.println("Disallow install globally");
        }

        File apkFile = new File(filePath);

        if (apkFile.exists()) {
            Uri apkUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", apkFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(intent);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button downloadButton;

        public CircularProgressIndicator progressIndicator;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.info_text);
            downloadButton = (Button) itemView.findViewById(R.id.button);
            progressIndicator = (CircularProgressIndicator) itemView.findViewById(R.id.progressIndicator);
        }
    }
}
