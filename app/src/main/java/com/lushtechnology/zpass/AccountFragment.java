package com.lushtechnology.zpass;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

        Intent intent = new Intent();
        intent.setClassName(XRPAccountService.class.getPackage().getName(),
                XRPAccountService.class.getName());
        intent.putExtra("address", MainActivity.ADDRESS);
        intent.putExtra("seed", MainActivity.SEED);
        //startService(intent);
        getContext().bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        // TODO: fix long background process
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button configure = view.findViewById(R.id.configure);
        configure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureWallet();
            }
        });
    }

    private void configureWallet() {
        Intent intent = new Intent(getActivity(), ConfigureWalletActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    IXRPAccountService xrpService = null;
    final private ServiceConnection myConnection =
            new ServiceConnection() {
                public void onServiceConnected(
                        ComponentName className,
                        IBinder service) {
                    xrpService = IXRPAccountService.Stub.asInterface(service);

                    try {
                        TextView balanceView = getView().findViewById(R.id.balance);
                        long drops = xrpService.getAccountValue();
                        BigDecimal xrp = XrpCurrencyAmount.ofDrops(drops).toXrp();
                        balanceView.setText("" + xrp);
                    } catch (RemoteException rex) {
                        rex.printStackTrace();
                    }
                }

                public void onServiceDisconnected(
                        ComponentName className) {
                    xrpService = null;
                }
            };
}