package idosa.huji.postpc.roots_master;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalDb {
    private final HashMap<String, RootCalcItem> items = new HashMap<>();
    private final SharedPreferences sp;

    private final MutableLiveData<List<RootCalcItem>> itemsMutableLiveData = new MutableLiveData<>();
    public final LiveData<List<RootCalcItem>> itemsLiveData = itemsMutableLiveData;

    public LocalDb(Context context) {
        this.sp = context.getSharedPreferences("local_items_db", Context.MODE_PRIVATE);
        initFromSp();
    }

    private void initFromSp() {
        Gson gson = new Gson();
        for (String key : sp.getAll().keySet()) {
            String itemSerialize = sp.getString(key, null);
            if (itemSerialize == null) continue;
            RootCalcItem itemToAdd = gson.fromJson(itemSerialize, RootCalcItem.class);
            if (itemToAdd != null) items.put(itemToAdd.getId(), itemToAdd);
        }
        itemsMutableLiveData.setValue(getCurrentItems());
    }

    public List<RootCalcItem> getCurrentItems() {
        return new ArrayList<>(items.values());
    }

    public RootCalcItem getItem(String id) {
        if (items.containsKey(id)) return items.get(id);
        return null;
    }

    public RootCalcItem addNewRootCalculation(long number) {
        RootCalcItem calcItem = new RootCalcItem(number);
        items.put(calcItem.getId(), calcItem);

        updateItemInSp(calcItem);

        itemsMutableLiveData.setValue(getCurrentItems());
        return calcItem;
    }

    public void deleteRootCalculation(RootCalcItem item) {
        items.remove(item.getId());

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(item.getId());
        editor.apply();

        itemsMutableLiveData.setValue(getCurrentItems());
    }

    public void changeCalculationProgress(RootCalcItem item, int newProgress) {
        item.setCalculationProgress(newProgress);

        updateItemInSp(item);

        itemsMutableLiveData.setValue(getCurrentItems());
    }

    public void finishCalculation(RootCalcItem item, long root1, long root2, double calculationTime) {
        item.setRoots(root1, root2);
        item.setPrevCalcTimeSec(calculationTime);

        updateItemInSp(item);
        itemsMutableLiveData.setValue(getCurrentItems());
    }

    public void finishCalculation(String itemId, long root1, long root2, double calculationTime) {
        RootCalcItem item = getItem(itemId);
        if (item != null) {
            finishCalculation(item, root1, root2, calculationTime);
        }
    }

    public void updateProgress(String itemId, int progress) {
        RootCalcItem item = getItem(itemId);
        if (item != null) {
            item.setCalculationProgress(progress);

            updateItemInSp(item);
            itemsMutableLiveData.setValue(getCurrentItems());
        }
    }

    public void calculationStopped(String itemId, long stoppedAt, double calculationTime) {
        RootCalcItem item = getItem(itemId);
        if (item != null) {
            item.setPrevCalcStopNum(stoppedAt);
            item.setPrevCalcTimeSec(calculationTime);

            updateItemInSp(item);
            itemsMutableLiveData.setValue(getCurrentItems());
        }
    }

    public void calculationCancelled(String itemId) {
        RootCalcItem item = getItem(itemId);
        if (item != null) {
            item.cancel();

            updateItemInSp(item);
            itemsMutableLiveData.setValue(getCurrentItems());
        }
    }

    public void calculationFailed(String itemId) {
        RootCalcItem item = getItem(itemId);
        if (item != null) {
            item.failed();

            updateItemInSp(item);
            itemsMutableLiveData.setValue(getCurrentItems());
        }
    }

    private void updateItemInSp(RootCalcItem item) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(item.getId(), gson.toJson(item));
        editor.apply();
    }
}
