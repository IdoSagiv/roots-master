package idosa.huji.postpc.roots_master;

public class RootCalcItem {
    private final static int ROOT_NOT_FOUND_YET = -1;
    private final static int MIN_PROGRESS = 0;
    public final static int MAX_PROGRESS = 100;
    private final long number;
    private long root1;
    private long root2;
    private int calculationProgress;


    public RootCalcItem(long number) {
        this.number = number;
        this.root1 = ROOT_NOT_FOUND_YET;
        this.root2 = ROOT_NOT_FOUND_YET;
        this.calculationProgress = MIN_PROGRESS;
    }

    public void setCalculationProgress(int progress) {
        this.calculationProgress = Math.min(MAX_PROGRESS, Math.max(MIN_PROGRESS, progress));
    }

    public void setRoots(long root1, long root2) {
        this.root1 = root1;
        this.root2 = root2;
        // if found roots than calculation done
        this.calculationProgress = MAX_PROGRESS;
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
}
