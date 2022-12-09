package com.said.oubella.so.artanddesign;

import android.app.Application;
import android.content.Context;

public final class ArtAndDesignApp extends Application {

    private AppContainer container;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        container = new AppContainer(this);
        container.preferences().applyTheme();
    }

    public AppContainer container() {
        return container;
    }
}
