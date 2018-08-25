package ch.itmed.radcentre.job;

import java.io.IOException;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

    private static final long STARTUP_DELAY = 5000; // 5 seconds delay for first run
    protected static final long JOB_INTERVAL = 10000; // Job should run every 60 seconds

    public void earlyStartup() {
    	System.out.println("Starting Background Job");
        Job updateJob;
		try {
			updateJob = new DirectoryWatchJob("Refreshing data in the background");
			updateJob.schedule(STARTUP_DELAY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*
        updateJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                super.done(event);
                updateJob.schedule(JOB_INTERVAL);
            }
        });*/
    }
}
