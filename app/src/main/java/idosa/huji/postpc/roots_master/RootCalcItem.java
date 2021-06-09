package idosa.huji.postpc.roots_master;

import com.google.gson.Gson;

import java.util.UUID;

public class RootCalcItem {
    private final static int ROOT_NOT_FOUND_YET = -1;
    private final static int MIN_PROGRESS = 0;
    public final static int MAX_PROGRESS = 100;
    private final String id;
    private final long number;
    private long root1;
    private long root2;
    private int calculationProgress;
    private int prevCalcTimeSec;
    private int prevCalcStopNum;


    public RootCalcItem(long number) {
        this.id = UUID.randomUUID().toString();
        this.number = number;
        this.root1 = ROOT_NOT_FOUND_YET;
        this.root2 = ROOT_NOT_FOUND_YET;
        this.prevCalcStopNum = 3; // first number to check
        this.prevCalcTimeSec = 0;

        this.calculationProgress = MIN_PROGRESS;
    }

    public void setCalculationProgress(int progress) {
        this.calculationProgress = Math.min(MAX_PROGRESS, Math.max(MIN_PROGRESS, progress));
    }

    public void setRoots(long root1, long root2) {
        this.root1 = root1;
        this.root2 = root2;
        // if found roots than the calculation is done
        this.calculationProgress = MAX_PROGRESS;
    }

    public String getId() {
        return id;
    }

    public long getNumber() {
        return number;
    }

    public long getRoot1() {
        return root1;
    }

    public long getRoot2() {
        return root2;
    }

    public int getCalculationProgress() {
        return calculationProgress;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static RootCalcItem parse(String serialize) {
        Gson gson = new Gson();
        return gson.fromJson(serialize, RootCalcItem.class);
    }
}
