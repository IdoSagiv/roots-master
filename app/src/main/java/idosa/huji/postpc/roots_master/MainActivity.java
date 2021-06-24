package idosa.huji.postpc.roots_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocalDb calculationsItemsHolder = null;
    private RootCalcListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new RootCalcListAdapter(this);
        WorkManager workManager = RootsMasterApplication.getInstance().getWorkManager();
        LiveData<List<WorkInfo>> workInfoLd = workManager.getWorkInfosByTagLiveData("calc_roots");

        if (calculationsItemsHolder == null) {
            calculationsItemsHolder = RootsMasterApplication.getInstance().getItemsDb();
        }

        calculationsItemsHolder.itemsLiveData.observe(this, rootCalcItems -> {
            adapter.setItems(rootCalcItems);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRootsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        workInfoLd.observe(this, workersInfo -> {
            Log.d("debug", "observe workInfo");
            for (WorkInfo workInfo : workersInfo) {
                Data progressData = workInfo.getProgress();
                String id1 = progressData.getString("calcItemId");
                int progress = progressData.getInt("progress", -1);
                Log.d("debug", "id: " + id1 + " progress: " + progress);
                if (id1 != null && progress != -1) {
                    calculationsItemsHolder.updateProgress(id1, progress);
                }
                String itemId = workInfo.getOutputData().getString("calcItemId");

                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    long root1 = workInfo.getOutputData().getLong("root1", 0);
                    long root2 = workInfo.getOutputData().getLong("root2", 0);
                    double calcTime = workInfo.getOutputData().getDouble("calc_time_sec", 0);
                    calculationsItemsHolder.finishCalculation(itemId, root1, root2, calcTime);
                } else if (workInfo.getState() == WorkInfo.State.RUNNING) {
//                    Data progressData = workInfo.getProgress();
//                    String id1 = progressData.getString("calcItemId");
//                    int progress = progressData.getInt("progress", -1);
//                    Log.d("debug", "id: " + id1 + " progress: " + progress);
//                    if (id1 != null && progress != -1) {
//                        calculationsItemsHolder.updateProgress(id1, progress);
//                    }
                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    calculationsItemsHolder.calculationFailed(itemId);
                }
            }
            workManager.pruneWork(); // deletes all final state jobs (finished, failed, cancelled)
        });


        adapter.onDeleteRootCallback = item -> calculationsItemsHolder.deleteRootCalculation(item);
        adapter.onCancelCalcCallback = item -> {
            // todo: cancel worker
            workManager.cancelWorkById(item.getWorkerId());
            calculationsItemsHolder.calculationCancelled(item.getId());
        };


        FloatingActionButton newCalcFab = findViewById(R.id.buttonAddRootCalc);
        newCalcFab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Calculation");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("enter number");
            builder.setView(input);

            builder.setPositiveButton("Calc", (dialog, which) -> {
                RootCalcItem item = calculationsItemsHolder.addNewRootCalculation(strToPosLong(input.getText().toString()));
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
        });
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
}