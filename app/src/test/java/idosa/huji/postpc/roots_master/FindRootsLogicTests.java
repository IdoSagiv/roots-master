package idosa.huji.postpc.roots_master;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.test.core.app.ApplicationProvider;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


@RunWith(RobolectricTestRunner.class)
//@RunWith(JUnit4.class)
@Config(sdk = 28)
public class CalculateRootsWorkerTests {
    private static Context context;
    private static Executor executor;

    @BeforeClass
    public static void init() {
        context = ApplicationProvider.getApplicationContext();
        executor = Executors.newSingleThreadExecutor();
    }

    @Test
    public void when_calculatingRootsWithSuccess_then_resultShouldReturnCorrectly() {
//        RootCalcItem item = new RootCalcItem(12);
//        Data inputData = new Data.Builder()
//                .putString("calc_item_id", item.getId())
//                .build();
//
//        CalculateRootsWorker worker = (CalculateRootsWorker) TestWorkerBuilder.from(context,
//                CalculateRootsWorker.class, executor)
//                .setInputData(inputData)
//                .build();
//
//        ListenableWorker.Result result = worker.doWork();
//        assertEquals(result, ListenableWorker.Result.success());
////        assertThat(result, is(ListenableWorker.Result.success()));

    }

    @Test
    public void when_calculatingRootsForNegNum_then_calculationShouldFail() {
    }
}