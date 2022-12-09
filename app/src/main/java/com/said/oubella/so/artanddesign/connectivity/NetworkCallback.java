package com.said.oubella.so.artanddesign.connectivity;

import androidx.annotation.MainThread;

public interface NetworkCallback {
    @MainThread
    void onChanged(boolean isConnected);
}
