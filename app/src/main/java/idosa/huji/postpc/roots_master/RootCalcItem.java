package idosa.huji.postpc.roots_master;

import java.util.UUID;

public class RootCalcItem implements Comparable<RootCalcItem> {
    private final static int ROOT_NOT_FOUND_YET = -1;
    private final static int MIN_PROGRESS = 0;
    public final static int MAX_PROGRESS = 100;

    private final String id;
    private final Long number;
    private long root1;
    private long root2;
    private int calculationProgress;
    private CalculationStatus status;
    private double prevCalcTimeSec;
    private long prevCalcStopNum;
    private UUID workerId;


    public RootCalcItem(long number) {
        this.id = UUID.randomUUID().toString();
        this.number = number;
        this.root1 = ROOT_NOT_FOUND_YET;
        this.root2 = ROOT_NOT_FOUND_YET;
        this.prevCalcStopNum = 0;
        this.prevCalcTimeSec = 0;

        this.status = CalculationStatus.IN_PROGRESS;
        this.calculationProgress = MIN_PROGRESS;

        this.workerId = null;
    }

    public void setCalculationProgress(int progress) {
        this.calculationProgress = Math.min(MAX_PROGRESS, Math.max(MIN_PROGRESS, progress));
    }

    public void setPrevCalcTimeSec(double prevCalcTimeSec) {
        this.prevCalcTimeSec = prevCalcTimeSec;
    }

    public void setPrevCalcStopNum(long prevCalcStopNum) {
        this.prevCalcStopNum = prevCalcStopNum;
    }

    public void setRoots(long root1, long root2) {
        this.root1 = root1;
        this.root2 = root2;
        // if found roots than the calculation is done
        this.status = CalculationStatus.DONE;
        this.calculationProgress = MAX_PROGRESS;
    }

    public void cancel() {
        this.status = CalculationStatus.CANCELED;
    }

    public void failed() {
        this.status = CalculationStatus.CANCELED;
    }

    public void setWorkerId(UUID workerId) {
        this.workerId = workerId;
    }

    public String getId() {
        return id;
    }

    public CalculationStatus getStatus() {
        return status;
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

    public long getPrevCalcStopNum() {
        return prevCalcStopNum;
    }

    public double getPrevCalcTimeSec() {
        return prevCalcTimeSec;
    }

    public UUID getWorkerId() {
        return workerId;
    }

    /**
     * done calculations are smaller than in-progress.
     * Two calculations in the same mode are sorted by the number.
     *
     * @param o other calculation to compare to
     * @return a>0 iff this>other, a<0 iff this<other, 0 iff this=other
     */
    @Override
    public int compareTo(RootCalcItem o) {
        if (calculationProgress == MAX_PROGRESS) {
            if (o.calculationProgress < MAX_PROGRESS) {
                return 1;
            }
            return number.compareTo(o.number);
        }
        if (o.calculationProgress == MAX_PROGRESS) {
            return -1;
        }
        return number.compareTo(o.number);
    }
}
