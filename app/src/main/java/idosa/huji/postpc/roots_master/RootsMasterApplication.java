package idosa.huji.postpc.roots_master;

import android.app.Application;

public class RootsMasterApplication extends Application {
    private static RootsMasterApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static RootsMasterApplication getInstance() {
        return instance;
    }
}
