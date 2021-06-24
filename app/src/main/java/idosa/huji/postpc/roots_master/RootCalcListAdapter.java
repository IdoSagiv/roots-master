package idosa.huji.postpc.roots_master;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RootCalcListAdapter extends RecyclerView.Adapter<RootCalcViewHolder> {
    private final ArrayList<RootCalcItem> items;
    public OnClickCallback onDeleteRootCallback = null;
    public OnClickCallback onCancelCalcCallback = null;


    public RootCalcListAdapter() {
        super();
        this.items = new ArrayList<>();
    }

    public void setItems(List<RootCalcItem> newItems) {
        items.clear();
        items.addAll(newItems);
        Collections.sort(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RootCalcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.root_calc_item, parent, false);
        return new RootCalcViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RootCalcViewHolder holder, int position) {
        RootCalcItem item = items.get(position);

        switch (item.getStatus()) {
            case IN_PROGRESS: {
                setHolderToInProgressMode(holder, item);
                break;
            }
            case DONE: {
                setHolderToDoneMode(holder, item);
                break;
            }
            case CANCELED: {
                setHolderToCancelMode(holder, item);
                break;
            }

            case FAILED: {
                setHolderToFailedMode(holder, item);
                break;
            }
        }
    }

    private void setHolderToInProgressMode(RootCalcViewHolder holder, RootCalcItem item) {
        holder.stopCalcBtn.setVisibility(View.VISIBLE);
        holder.calcProgressBar.setVisibility(View.VISIBLE);
        holder.deleteRootBtn.setVisibility(View.GONE);

        holder.descriptionTextView.setText(String.format("Calculating roots for %d", item.getNumber()));

        holder.calcProgressBar.setProgress(item.getCalculationProgress());

        holder.stopCalcBtn.setOnClickListener(v -> {
            if (onCancelCalcCallback == null) return;
            onCancelCalcCallback.onClick(item);
        });
    }

    private void setHolderToDoneMode(RootCalcViewHolder holder, RootCalcItem item) {
        holder.deleteRootBtn.setVisibility(View.VISIBLE);
        holder.stopCalcBtn.setVisibility(View.GONE);
        holder.calcProgressBar.setVisibility(View.GONE);

        if (item.getRoot1() == 1 || item.getRoot2() == 1) {
            holder.descriptionTextView.setText(String.format("%d is prime", item.getNumber()));
        } else {
            holder.descriptionTextView.setText(String.format("%d=%dx%d", item.getNumber(), item.getRoot1(), item.getRoot2()));
        }

        holder.deleteRootBtn.setOnClickListener(v -> {
            if (onDeleteRootCallback == null) return;
            onDeleteRootCallback.onClick(item);
        });
    }

    private void setHolderToCancelMode(RootCalcViewHolder holder, RootCalcItem item) {
        holder.deleteRootBtn.setVisibility(View.VISIBLE);
        holder.stopCalcBtn.setVisibility(View.GONE);
        holder.calcProgressBar.setVisibility(View.GONE);

        holder.descriptionTextView.setText(String.format("Calculating roots for %d cancelled", item.getNumber()));

        holder.deleteRootBtn.setOnClickListener(v -> {
            if (onDeleteRootCallback == null) return;
            onDeleteRootCallback.onClick(item);
        });
    }

    private void setHolderToFailedMode(RootCalcViewHolder holder, RootCalcItem item) {
        holder.deleteRootBtn.setVisibility(View.VISIBLE);
        holder.stopCalcBtn.setVisibility(View.GONE);
        holder.calcProgressBar.setVisibility(View.GONE);

        holder.descriptionTextView.setText(String.format("Calculating roots for %d failed", item.getNumber()));

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
