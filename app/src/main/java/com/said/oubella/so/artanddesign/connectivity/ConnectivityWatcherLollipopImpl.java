package com.said.oubella.so.artanddesign.connectivity;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.said.oubella.so.artanddesign.AppExecutors;

final class ConnectivityWatcherLollipopImpl extends ConnectivityWatcher {

    private final ConnectivityManager.NetworkCallback connectivityCallback;

    private final NetworkRequest networkRequest;

    ConnectivityWatcherLollipopImpl(Application context, AppExecutors executors) {
        super(context, executors);

        this.networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();

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
    protected void registerCallback() {
        getManager().registerNetworkCallback(networkRequest, connectivityCallback);
    }

    @Override
    protected void unregisterCallback() {
        getManager().unregisterNetworkCallback(connectivityCallback);
    }
}
