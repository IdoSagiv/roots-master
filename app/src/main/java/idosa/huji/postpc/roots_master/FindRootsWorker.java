package idosa.huji.postpc.roots_master;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

public class FindRootsWorker extends Worker {
    private static final int RUNTIME_MS = 10 * 60 * 1000; // 10 min
    private static final long MIN_START_NUM = 3L;

    private final long num;
    private final double prevCalcTimeSec;
    private final long prevStopNum;
    private final String calcItemId;
    private boolean isCanceled = false;

    public FindRootsWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        LocalDb db = RootsMasterApplication.getInstance().getItemsDb();

        calcItemId = getInputData().getString("calc_item_id");
        RootCalcItem item = db.getItem(calcItemId);
        num = item.getNumber();
        prevStopNum = Math.max(item.getPrevCalcStopNum(), MIN_START_NUM);
        prevCalcTimeSec = item.getPrevCalcTimeSec();
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        Log.i("CalculateRootsWorker", "Worker " + getId().toString() + " for " + num + " started doWork()");
        final Result[] result = new Result[1];
        result[0] = Result.failure(new Data.Builder()
                .putBoolean("isCanceled", false)
                .putString("reason", "something went wrong").build());

        FindRootsLogic.findRoots(num, prevStopNum, RUNTIME_MS, new FindRootsListener() {
            @Override
            public boolean isStopped() {
                return isCanceled;
            }

            @Override
            public void onStopped() {
                Log.i("CalculateRootsWorker", "Worker " + getId().toString() + " for " + num + " stopped");
                result[0] = Result.failure(new Data.Builder()
                        .putBoolean("isCanceled", true).build());
            }

            @Override
            public void onProgress(int progress) {
                setProgressAsync(new Data.Builder()
                        .putString("calcItemId", calcItemId)
                        .putInt("progress", progress)
                        .build());
            }

            @Override
            public void onResult(long root1, long root2, double calc_time_sec) {
                Log.i("CalculateRootsWorker", "Worker " + getId().toString() + " for " + num + " finished doWork() successfully");
                result[0] = Result.success(
                        new Data.Builder()
                                .putString("calcItemId", calcItemId)
                                .putLong("original_number", num)
                                .putLong("root1", root1)
                                .putLong("root2", root2)
                                .putDouble("calc_time_sec", prevCalcTimeSec + calc_time_sec)
                                .build()
                );
            }

            @Override
            public void onFailure(String msg) {
                result[0] = Result.failure(new Data.Builder()
                        .putBoolean("isCanceled", false)
                        .putString("reason", msg).build());
            }

            @Override
            public void onTimeout(long current, double calc_time_sec) {
                Log.i("CalculateRootsWorker", "Worker " + getId().toString() + " for " + num + " paused");
                result[0] = Result.success(
                        new Data.Builder()
                                .putString("calcItemId", calcItemId)
                                .putLong("original_number", num)
                                .putLong("stopped_at", current)
                                .putDouble("calc_time_sec", prevCalcTimeSec + calc_time_sec)
                                .build()
                );
            }
        });

        return result[0];
    }

    @Override
    public void onStopped() {
        super.onStopped();
        isCanceled = true;
    }
}
