package com.jedikv.simpleconverter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import com.facebook.stetho.Stetho;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.jedikv.simpleconverter.model.CurrencyModel;
import com.squareup.leakcanary.LeakCanary;
import timber.log.Timber;

/**
 * Created by Kurian on 03/05/2015.
 */
public class App extends Application {

    private static final String TAG = App.class.getCanonicalName();

    @VisibleForTesting
    public static AppComponent appComponent;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static App get(Activity activity) {
        return (App) activity.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag(TAG);
        LeakCanary.install(this);
        if (BuildConfig.DEBUG) {
            AndroidDevMetrics.initWith(this);
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        Timber.plant(new Timber.DebugTree());

        initDagger();
    }

    private void initDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .build();
    }

    public static AppComponent get() {
        return appComponent;
    }
}
