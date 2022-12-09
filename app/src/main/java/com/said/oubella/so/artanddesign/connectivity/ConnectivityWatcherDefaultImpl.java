package com.said.oubella.so.artanddesign.connectivity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.said.oubella.so.artanddesign.AppExecutors;

final class ConnectivityWatcherDefaultImpl extends ConnectivityWatcher {

    private final IntentFilter connectivityFilter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context p1, Intent p2) {
            notifyListeners(isAvailable());
        }
    };

    ConnectivityWatcherDefaultImpl(Application context, AppExecutors executors) {
        super(context, executors);
        this.connectivityFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    protected void registerCallback() {
        getContext().registerReceiver(receiver, connectivityFilter);
    }

    @Override
    protected void unregisterCallback() {
        getContext().unregisterReceiver(receiver);
    }
}
