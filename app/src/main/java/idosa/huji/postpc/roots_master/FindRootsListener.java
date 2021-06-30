package idosa.huji.postpc.roots_master;

public interface FindRootsListener {
    boolean isStopped();

    void onStopped();

    void onProgress(int progress);

    void onResult(long root1, long root2, double calc_time_sec);

    void onFailure(String msg);

    void onTimeout(long current, double calc_time_sec);

}
