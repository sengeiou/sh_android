package gm.mobi.android.task.jobs;


import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public abstract class CancellableJob extends Job {
    private boolean cancelled;

    protected CancellableJob(Params params) {
        super(params);
    }

    /**
     * Set job to cancelled, so it won't return any value or send any event.
     * Useful for avoiding receiving
     * Note: doesn't stops it, just sets a flag. Implementation is responsible for checking it before performing work or returning a response.
     */
    public void cancelJob() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
