package com.lushtechnology.zpass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.lushtechnology.zpass.store.DownloadWorker;
import com.lushtechnology.zpass.store.StoreApp;

import java.util.List;

public class StoreAppAdapter extends
        RecyclerView.Adapter<StoreAppAdapter.ViewHolder> {

    private List<StoreApp> mStoreApps;

    // Pass in the contact array into the constructor
    public StoreAppAdapter(List<StoreApp> storeapps) {
        mStoreApps = storeapps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View appView = inflater.inflate(R.layout.item_app, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(appView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreApp app = mStoreApps.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(app.getName());
        ImageView button = holder.downloadButton;
        //button.setText(app.isOnline() ? "Message" : "Offline");
        //button.setEnabled(app.isOnline());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.getAbsoluteAdapterPosition());
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mStoreApps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView downloadButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.info_text);
            downloadButton = (ImageView) itemView.findViewById(R.id.download_button);
        }
    }
}
