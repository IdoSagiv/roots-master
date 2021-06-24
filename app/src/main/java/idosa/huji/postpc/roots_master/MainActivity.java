package idosa.huji.postpc.roots_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocalDb calculationsItemsHolder = null;
    private WorkManager workManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        RootCalcListAdapter adapter = new RootCalcListAdapter();
        workManager = RootsMasterApplication.getInstance().getWorkManager();
        LiveData<List<WorkInfo>> workInfoLd = workManager.getWorkInfosByTagLiveData("calc_roots");

        if (calculationsItemsHolder == null) {
            calculationsItemsHolder = RootsMasterApplication.getInstance().getItemsDb();
        }

        calculationsItemsHolder.itemsLiveData.observe(this, adapter::setItems);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRootsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        workInfoLd.observe(this, workersInfo -> {
            for (WorkInfo workInfo : workersInfo) {
                String itemId = workInfo.getOutputData().getString("calcItemId");

                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    if (workInfo.getOutputData().hasKeyWithValueOfType("stopped_at", Long.class)) {
                        Log.d("MainActivity", "calc paused");
                        long stoppedAt = workInfo.getOutputData().getLong("stopped_at", 0);
                        double calcTimeSec = workInfo.getOutputData().getLong("calc_time_sec", 0);
                        calculationsItemsHolder.calculationPaused(itemId, stoppedAt, calcTimeSec);
                        startCalculation(calculationsItemsHolder.getItem(itemId));
                    } else {
                        Log.d("MainActivity", "calc SUCCEEDED");
                        long root1 = workInfo.getOutputData().getLong("root1", 0);
                        long root2 = workInfo.getOutputData().getLong("root2", 0);
                        double calcTime = workInfo.getOutputData().getDouble("calc_time_sec", 0);
                        calculationsItemsHolder.finishCalculation(itemId, root1, root2, calcTime);
                    }
                } else if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    Data progressData = workInfo.getProgress();
                    String id1 = progressData.getString("calcItemId");
                    int progress = progressData.getInt("progress", -1);
                    if (id1 != null && progress != -1) {
                        calculationsItemsHolder.updateProgress(id1, progress);
                    }
                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    calculationsItemsHolder.calculationFailed(itemId);
                }
            }
            workManager.pruneWork(); // deletes all final state jobs (finished, failed, cancelled) todo: keep?
        });


        // delete and cancel callbacks
        adapter.onDeleteRootCallback = item -> calculationsItemsHolder.deleteRootCalculation(item);
        adapter.onCancelCalcCallback = item -> {
            workManager.cancelWorkById(item.getWorkerId());
            calculationsItemsHolder.calculationCancelled(item.getId());
        };

        // add calculation button on click
        findViewById(R.id.buttonAddRootCalc).setOnClickListener(this::newCalculationPopup);
    }

    public void newCalculationPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Calculation");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("enter number");
        builder.setView(input);

        builder.setPositiveButton("Calc", (dialog, which) -> {
            RootCalcItem item = calculationsItemsHolder.addNewRootCalculation(strToPosLong(input.getText().toString()));
            startCalculation(item);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();


        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(strToPosLong(input.getText().toString()) > 0);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                positiveButton.setEnabled(strToPosLong(input.getText().toString()) > 0);
            }
        });
    }

    private void startCalculation(RootCalcItem item) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CalculateRootsWorker.class)
                .addTag("calc_roots")
                .setInputData(
                        new Data.Builder()
                                .putString("calc_item_id", item.getId())
                                .build()
                )
                .build();
        item.setWorkerId(request.getId());
        workManager.enqueue(request);
    }

    /**
     * @param str string to convert to long
     * @return the long value of the string or -1 if the string is not a positive long
     */
    private long strToPosLong(String str) {
        try {
            long n = Long.parseLong(str);
            return n > 0 ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    finish();
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Close the app?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}