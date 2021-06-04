package idosa.huji.postpc.roots_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RootCalcListAdapter extends RecyclerView.Adapter<RootCalcHolder> {
    private final Context context;
    private final ArrayList<RootCalcItem> items;
    public OnClickCallback onDeleteRootCallback = null;
    public OnClickCallback onCancelCalcCallback = null;


    public RootCalcListAdapter(Context context) {
        super();
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void setItems(ArrayList<RootCalcItem> newItems) {
        items.clear();
        items.addAll(newItems);
        //todo: sort items by the order given in the specs

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RootCalcHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.root_calc_item, parent, false);
        return new RootCalcHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RootCalcHolder holder, int position) {
        RootCalcItem item = items.get(position);
        if (item.getCalculationProgress() == RootCalcItem.MAX_PROGRESS) {
            setHolderToCalcDoneMode(holder, item);
        } else {
            setHolderToOnCalcMode(holder, item);
        }
    }

    private void setHolderToOnCalcMode(RootCalcHolder holder, RootCalcItem item) {
        holder.stopCalcBtn.setVisibility(View.VISIBLE);
        holder.calcProgressBar.setVisibility(View.VISIBLE);
        holder.deleteRootBtn.setVisibility(View.GONE);

        holder.descriptionTextView.setText(String.format("Calculating roots for %d...", item.getNumber()));

        // todo: change progress from somewhere else????
        holder.calcProgressBar.setProgress(item.getCalculationProgress());

        holder.stopCalcBtn.setOnClickListener(v -> {
            if (onCancelCalcCallback == null) return;
            onCancelCalcCallback.onClick(item);
        });
    }

    private void setHolderToCalcDoneMode(RootCalcHolder holder, RootCalcItem item) {
        holder.deleteRootBtn.setVisibility(View.VISIBLE);
        holder.stopCalcBtn.setVisibility(View.GONE);
        holder.calcProgressBar.setVisibility(View.GONE);

        if (item.getRoot1() == 1 || item.getRoot2() == 1) {
            holder.descriptionTextView.setText("%d is prime");
        } else {
            holder.descriptionTextView.setText(String.format("%d=%dx%d", item.getNumber(), item.getRoot1(), item.getRoot2()));
        }

        holder.deleteRootBtn.setOnClickListener(v -> {
            if (onDeleteRootCallback == null) return;
            onDeleteRootCallback.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
