package idosa.huji.postpc.roots_master;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class CalculateRootsWorker extends Worker {
    private static final int DEFAULT_RUNTIME_SEC = 10 * 60; // 10 min
    private static final long MIN_START_NUM = 3L;

    private final long num;
    private final double prevCalcTimeSec;
    private long prevStopNum;
    private final long timeToRunMs;
    private final String calcItemId;

    public CalculateRootsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.i("CalculateRootsWorker", "Worker created");
        LocalDb db = RootsMasterApplication.getInstance().getItemsDb();

        calcItemId = getInputData().getString("calc_item_id");
        RootCalcItem item = db.getItem(calcItemId);
        num = item.getNumber();
        prevStopNum = Math.max(item.getPrevCalcStopNum(), MIN_START_NUM);
        prevCalcTimeSec = item.getPrevCalcTimeSec();
        int timeToRunSec = getInputData().getInt("time_to_run", DEFAULT_RUNTIME_SEC);
        timeToRunMs = TimeUnit.SECONDS.toMillis(timeToRunSec);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("CalculateRootsWorker", "Worker started doWork()");
        long timeStartMs = System.currentTimeMillis();

        if (num <= 0) {
            Log.i("CalculateRootsWorker", "Worker failed. Can't calculate roots for non-positive input" + num);
            return Result.failure(new Data.Builder()
                    .putString("reason", "can't calculate roots for non-positive input").build());
        }

        // private case for even numbers
        if (num % 2 == 0) {
            return buildComputationEndResult(num, 2, System.currentTimeMillis() - timeStartMs);
        }

        // search for roots
        double runUntil = Math.sqrt(num) + 1;
        if (prevStopNum % 2 == 0) prevStopNum--;
        for (long i = prevStopNum; i < runUntil; i += 2) {
            setProgressAsync(new Data.Builder()
                    .putString("calcItemId", calcItemId)
                    .putInt("progress", (int) Math.ceil(100 * i / runUntil))
                    .build());
            if (num % i == 0) {
                return buildComputationEndResult(num, i, System.currentTimeMillis() - timeStartMs);
            }
            if (System.currentTimeMillis() - timeStartMs > timeToRunMs) {
                // pause calculation
                return buildComputationPauseResult(num, i, System.currentTimeMillis() - timeStartMs);
            }
        }

        // the original number is prime
        return buildComputationEndResult(num, 1, System.currentTimeMillis() - timeStartMs);
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i("CalculateRootsWorker", "Worker stopped");
    }

    private Result buildComputationEndResult(long origNum, long root1, long calculationTimeMilliSec) {
        Log.i("CalculateRootsWorker", "Worker finished successfully");
        return Result.success(
                new Data.Builder()
                        .putString("calcItemId", calcItemId)
                        .putLong("original_number", origNum)
                        .putLong("root1", root1)
                        .putLong("root2", origNum / root1)
                        .putDouble("calc_time_sec", prevCalcTimeSec + milliToRoundedSec(calculationTimeMilliSec))
                        .build()
        );
    }

    private Result buildComputationPauseResult(long origNum, long stoppedAt, long calculationTimeMilliSec) {
        Log.i("CalculateRootsWorker", "Worker paused");
        return Result.success(
                new Data.Builder()
                        .putString("calcItemId", calcItemId)
                        .putLong("original_number", origNum)
                        .putLong("stopped_at", stoppedAt)
                        .putDouble("calc_time_sec", prevCalcTimeSec + milliToRoundedSec(calculationTimeMilliSec))
                        .build()
        );
    }

    private double milliToRoundedSec(long millis) {
        return round3decimals(millis / 1000d);
    }

    private double round3decimals(double n) {
        return Math.round(n * 1000) / 1000d;
    }
}
