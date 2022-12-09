package com.said.oubella.so.artanddesign.connectivity;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.said.oubella.so.artanddesign.AppExecutors;

final class ConnectivityWatcherNougatImpl extends ConnectivityWatcher {

    private final ConnectivityManager.NetworkCallback connectivityCallback;

    ConnectivityWatcherNougatImpl(Application context, AppExecutors executors) {
        super(context, executors);

        this.connectivityCallback = new ConnectivityManager.NetworkCallback() {

            public void onAvailable(@NonNull Network network) {
                notifyListeners(isAvailable());
            }

            public void onLost(@NonNull Network network) {
                notifyListeners(isAvailable());
            }
        };
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void registerCallback() {
        getManager().registerDefaultNetworkCallback(connectivityCallback);
    }

    @Override
    protected void unregisterCallback() {
        getManager().unregisterNetworkCallback(connectivityCallback);
    }
}
