package idosa.huji.postpc.roots_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RootCalcListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new RootCalcListAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRootsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        ArrayList<RootCalcItem> items = new ArrayList<>();
        RootCalcItem item1 = new RootCalcItem(400);
        item1.setCalculationProgress(75);


        RootCalcItem item2 = new RootCalcItem(123400);
        item2.setCalculationProgress(15);

        RootCalcItem item3 = new RootCalcItem(30);
        item3.setCalculationProgress(75);
        item3.setRoots(15, 2);

        items.add(item1);
        items.add(item2);
        items.add(item3);

        adapter.setItems(items);
    }
}