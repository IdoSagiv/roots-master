package idosa.huji.postpc.roots_master;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FindRootsLogicTests {
    private static final int DEF_RUNTIME_MS = 10 * 60 * 1000; // 10 min

    private void assertCalculationResult(long num, long expRoot1, long expRoot2) {
        final long[] roots = {-1, -1};
        ArrayList<Long> expectedRoots = new ArrayList<Long>() {
            {
                add(expRoot1);
                add(expRoot2);
            }
        };

        FindRootsLogic.findRoots(num, 0, DEF_RUNTIME_MS, new FindRootsListener() {
            @Override
            public boolean isStopped() {
                return false;
            }

            @Override
            public void onStopped() {
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onResult(long root1, long root2, double calc_time_sec) {
                roots[0] = root1;
                roots[1] = root2;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onTimeout(long current, double calc_time_sec) {
            }
        });

        if (expRoot1 != expRoot2) {
            Assert.assertNotEquals(roots[0], roots[1]);
        }
        Assert.assertTrue(expectedRoots.contains(roots[0]));
        Assert.assertTrue(expectedRoots.contains(roots[1]));
    }

    @Test
    public void when_calculatingRootsOfNum_then_RootShouldBeCorrect() {
        assertCalculationResult(9, 3, 3);
    }

    @Test
    public void when_calculatingRootsOfPrime_then_RootShouldBeOne() {
        assertCalculationResult(7, 1, 7);
        assertCalculationResult(999999000001L, 999999000001L, 1);
    }

    @Test
    public void when_calculatingRootsForNegNum_then_calculationShouldFail() {
        final boolean[] isFailed = {false};

        FindRootsLogic.findRoots(-1, 0, DEF_RUNTIME_MS, new FindRootsListener() {
            @Override
            public boolean isStopped() {
                return false;
            }

            @Override
            public void onStopped() {
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onResult(long root1, long root2, double calc_time_sec) {
            }

            @Override
            public void onFailure(String msg) {
                isFailed[0] = true;
            }

            @Override
            public void onTimeout(long current, double calc_time_sec) {
            }
        });

        Assert.assertTrue(isFailed[0]);
    }
}