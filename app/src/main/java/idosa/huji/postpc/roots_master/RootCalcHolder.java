package idosa.huji.postpc.roots_master;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RootCalcHolder extends RecyclerView.ViewHolder {
    View view;
    TextView headerTextView;
    TextView calcResultTextView;
    ImageView deleteRootBtn;
    ImageView stopCalcBtn;
    ProgressBar calcProgressBar;

    public RootCalcHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.headerTextView = itemView.findViewById(R.id.textViewRootCalculationHeader);
        this.calcResultTextView = itemView.findViewById(R.id.textViewRootCalculationResult);
        this.deleteRootBtn = itemView.findViewById(R.id.buttonDeleteRoot);
        this.stopCalcBtn = itemView.findViewById(R.id.buttonStopRootCalculation);
        this.calcProgressBar = itemView.findViewById(R.id.progressBarCalculationProgress);
    }
}