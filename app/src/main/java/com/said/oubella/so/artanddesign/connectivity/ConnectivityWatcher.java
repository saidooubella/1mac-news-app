package com.said.oubella.so.artanddesign.connectivity;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.said.oubella.so.artanddesign.AppExecutors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ConnectivityWatcher {

    private final ConnectivityManager manager;
    private final AppExecutors executors;
    private final Application context;

    private final Set<NetworkCallback> callbacks;
    private final AtomicBoolean isRunning;

    ConnectivityWatcher(Application context, AppExecutors executors) {
        this.manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.isRunning = new AtomicBoolean();
        this.callbacks = new HashSet<>();
        this.executors = executors;
        this.context = context;
    }

    static ConnectivityWatcher getInstance(Application context, AppExecutors executors) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new ConnectivityWatcherNougatImpl(context, executors);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new ConnectivityWatcherLollipopImpl(context, executors);
        } else {
            return new ConnectivityWatcherDefaultImpl(context, executors);
        }
    }

    protected abstract void registerCallback();

    protected abstract void unregisterCallback();

    final void notifyListeners(final boolean isConnected) {
        for (NetworkCallback callback : callbacks) {
            executors.main().execute(() -> callback.onChanged(isConnected));
        }
    }

    final ConnectivityManager getManager() {
        return manager;
    }

    final Application getContext() {
        return context;
    }

    final synchronized void addCallback(NetworkCallback callback) {
        callbacks.add(callback);
        if (!isRunning.get() && !callbacks.isEmpty()) {
            registerCallback();
            isRunning.set(true);
        }
    }

    final synchronized void removeCallback(NetworkCallback callback) {
        callbacks.remove(callback);
        if (isRunning.get() && callbacks.isEmpty()) {
            unregisterCallback();
            isRunning.set(false);
        }
    }

    final boolean isAvailable() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            final Network activeNetwork = manager.getActiveNetwork();
            final NetworkCapabilities cap = manager.getNetworkCapabilities(activeNetwork);
            if (cap == null) return false;
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Network[] networks = manager.getAllNetworks();
            for (final Network network : networks) {
                final NetworkInfo info = manager.getNetworkInfo(network);
                if (info != null && info.isConnected()) return true;
            }
        } else {
            final NetworkInfo[] networks = manager.getAllNetworkInfo();
            for (final NetworkInfo networkInfo : networks) {
                if (networkInfo != null && networkInfo.isConnected()) return true;
            }
        }

        return false;
    }
}
