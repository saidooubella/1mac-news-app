package com.said.oubella.so.artanddesign.connectivity;

import android.app.Application;

import com.said.oubella.so.artanddesign.AppExecutors;
import com.said.oubella.so.artanddesign.helpers.Cancellable;

public final class ConnectivityImpl implements Connectivity {

    private final ConnectivityWatcher watcher;

    public ConnectivityImpl(Application context, AppExecutors executors) {
        this.watcher = ConnectivityWatcher.getInstance(context, executors);
    }

    @Override
    public Cancellable addListener(NetworkCallback callback) {
        return addListener(false, callback);
    }

    @Override
    public Cancellable addListener(boolean notifyNow, final NetworkCallback callback) {

        if (notifyNow) callback.onChanged(isAvailable());

        watcher.addCallback(callback);

        return new Cancellable() {
            @Override
            public void cancel() {
                watcher.removeCallback(callback);
            }
        };
    }

    @Override
    public boolean isAvailable() {
        return watcher.isAvailable();
    }
}
