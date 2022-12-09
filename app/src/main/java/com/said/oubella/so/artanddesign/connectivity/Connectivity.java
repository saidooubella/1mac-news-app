package com.said.oubella.so.artanddesign.connectivity;

import com.said.oubella.so.artanddesign.helpers.Cancellable;

public interface Connectivity {

    Cancellable addListener(boolean notifyNow, NetworkCallback callback);

    Cancellable addListener(NetworkCallback callback);

    boolean isAvailable();
}
