package nl.bhit.mtor.client;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MonitorJob implements Job {
	private static final transient Logger LOG = Logger.getLogger(MonitorJob.class);

	private static final String JOB_NAME = "mTorClientMonitor";
	
    /**
     * Starts the MTor client processes
     */
	public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.debug("starting up the client process.");
        MessageServiceSender client = new MessageServiceSender();
        try {
            client.sendMessages();
        } catch (Exception e) {
            LOG.warn("exception occured: ", e);
            throw new JobExecutionException(e);
        }
    }
	
	public static String getJobName() {
		return JOB_NAME;
	}
}
