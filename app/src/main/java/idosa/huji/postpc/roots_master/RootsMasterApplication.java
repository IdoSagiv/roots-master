package idosa.huji.postpc.roots_master;

import android.app.Application;

public class RootsMasterApplication extends Application {
    private static RootsMasterApplication instance;
    private LocalDb itemsDb;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        itemsDb = new LocalDb(this);
    }

    public static RootsMasterApplication getInstance() {
        return instance;
    }

    public LocalDb getItemsDb() {
        return itemsDb;
    }
}
