package com.said.oubella.so.artanddesign;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public final class AppExecutors {

    private final Executor mainExecutor;

    AppExecutors() {
        this.mainExecutor = new MainExecutor();
    }

    public Executor main() {
        return mainExecutor;
    }

    private static final class MainExecutor implements Executor {

        private Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            mainHandler.post(runnable);
        }
    }
}
