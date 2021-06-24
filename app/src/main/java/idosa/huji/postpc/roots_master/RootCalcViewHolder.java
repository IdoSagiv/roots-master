package idosa.huji.postpc.roots_master;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RootCalcViewHolder extends RecyclerView.ViewHolder {
    View view;
    TextView descriptionTextView;
    ImageView deleteRootBtn;
    ImageView stopCalcBtn;
    ProgressBar calcProgressBar;

    public RootCalcViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.descriptionTextView = itemView.findViewById(R.id.textViewRootCalculationDescription);
        this.deleteRootBtn = itemView.findViewById(R.id.buttonDeleteRoot);
        this.stopCalcBtn = itemView.findViewById(R.id.buttonStopRootCalculation);
        this.calcProgressBar = itemView.findViewById(R.id.progressBarCalculationProgress);
    }
}
