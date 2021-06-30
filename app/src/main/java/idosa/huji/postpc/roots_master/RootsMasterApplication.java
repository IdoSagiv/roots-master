package idosa.huji.postpc.roots_master;

import android.app.Application;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class RootsMasterApplication extends Application {
    private static RootsMasterApplication instance;
    private LocalDb itemsDb;
    private WorkManager workManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        itemsDb = new LocalDb(this);
        workManager = WorkManager.getInstance(this);
        workManager.pruneWork();
    }

    public static RootsMasterApplication getInstance() {
        return instance;
    }

    public LocalDb getItemsDb() {
        return itemsDb;
    }

    public WorkManager getWorkManager() {
        return workManager;
    }
}
