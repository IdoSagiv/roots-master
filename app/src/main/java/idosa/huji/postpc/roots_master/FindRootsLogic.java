package idosa.huji.postpc.roots_master;

public class FindRootsLogic {
    static void findRoots(long num, long startFrom, long timeoutMs, FindRootsListener listener) {
        long timeStartMs = System.currentTimeMillis();

        if (num <= 0) {
            listener.onFailure("non positive num has no roots");
            return;
        }

        if (num % 2 == 0) {
            // found result
            listener.onResult(2,
                    num / 2,
                    milliToRoundedSec(System.currentTimeMillis() - timeStartMs));
            return;
        }

        double runUntil = Math.sqrt(num) + 1;
        if (startFrom % 2 == 0) startFrom--;
        long lastProgressUpdateTime = System.currentTimeMillis();

        for (long i = startFrom; i < runUntil; i += 2) {
            if (listener.isStopped()) {
                // worker stopped
                listener.onStopped();
                return;
            }

            // update progress every 500 ms
            if (System.currentTimeMillis() - lastProgressUpdateTime > 200) {
                lastProgressUpdateTime = System.currentTimeMillis();
                listener.onProgress((int) Math.ceil(100 * i / runUntil));
            }

            if (num % i == 0) {
                // found result
                listener.onResult(i,
                        num / i,
                        milliToRoundedSec(System.currentTimeMillis() - timeStartMs));
                return;
            }
            if (System.currentTimeMillis() - timeStartMs > timeoutMs) {
                // pause calculation
                listener.onTimeout(i, milliToRoundedSec(System.currentTimeMillis() - timeStartMs));
                return;
            }
        }

        // the number is prime
        listener.onResult(1,
                num,
                milliToRoundedSec(System.currentTimeMillis() - timeStartMs));
    }

    private static double milliToRoundedSec(long millis) {
        return round3decimals(millis / 1000d);
    }

    private static double round3decimals(double n) {
        return Math.round(n * 1000) / 1000d;
    }
}
