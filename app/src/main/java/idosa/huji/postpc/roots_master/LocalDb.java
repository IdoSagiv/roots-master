package idosa.huji.postpc.roots_master;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
        for (String key : sp.getAll().keySet()) {
            String itemSerialize = sp.getString(key, null);
            if (itemSerialize == null) continue;
            RootCalcItem itemToAdd = RootCalcItem.parse(itemSerialize);
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
}
