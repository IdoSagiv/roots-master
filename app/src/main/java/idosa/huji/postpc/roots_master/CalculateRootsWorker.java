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
    private final long num;
    private final double prevCalcTimeSec;
    private final long prevStopNum;
    private final long timeToRunMs;

    public CalculateRootsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        num = getInputData().getLong("number_to_calc", 0L);
        prevStopNum = getInputData().getLong("last_stop_num", 3L);
        prevCalcTimeSec = getInputData().getDouble("prev_calc_time_sec", 0d);
        int timeToRunSec = getInputData().getInt("time_to_run", DEFAULT_RUNTIME_SEC);
        timeToRunMs = TimeUnit.SECONDS.toMillis(timeToRunSec);
    }

    @NonNull
    @Override
    public Result doWork() {
        long timeStartMs = System.currentTimeMillis();

        if (num <= 0) {
            Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + num);
            return Result.failure(new Data.Builder()
                    .putString("reason", "can't calculate roots for non-positive input").build());
        }

        // private case for even numbers
        if (num % 2 == 0) {
            return buildComputationEndResult(num, 2, System.currentTimeMillis() - timeStartMs);
        }

        // search for roots
        double runUntil = Math.sqrt(num) + 1;
        for (long i = prevStopNum; i < runUntil; i += 2) {
            setProgressAsync(new Data.Builder().putDouble("progress", runUntil / i).build());
            if (num % i == 0) {
                setProgressAsync(new Data.Builder().putDouble("progress", 100d).build()); // todo: remove?
                return buildComputationEndResult(num, i, System.currentTimeMillis() - timeStartMs);
            }
            if (System.currentTimeMillis() - timeStartMs > timeToRunMs) {
                // pause calculation
                return buildComputationPauseResult(num, i, System.currentTimeMillis() - timeStartMs);
            }
        }

        // the original number is prime
        setProgressAsync(new Data.Builder().putDouble("progress", 100d).build()); // todo: remove?
        return buildComputationEndResult(num, 1, System.currentTimeMillis() - timeStartMs);
    }

    private Result buildComputationEndResult(long origNum, long root1, long calculationTimeMilliSec) {
        return Result.success(
                new Data.Builder()
                        .putLong("original_number", origNum)
                        .putLong("root1", root1)
                        .putLong("root2", origNum / root1)
                        .putDouble("calc_time_sec", prevCalcTimeSec + milliToRoundedSec(calculationTimeMilliSec))
                        .build()
        );
    }

    private Result buildComputationPauseResult(long origNum, long stoppedAt, long calculationTimeMilliSec) {
        return Result.success(
                new Data.Builder()
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
