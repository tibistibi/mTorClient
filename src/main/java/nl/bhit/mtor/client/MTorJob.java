package nl.bhit.mtor.client;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MTorJob implements Job {
	private static final transient Logger LOG = Logger.getLogger(MTorJob.class);

    /**
     * Starts the MTor client processes
     * Should be called from an scheduler
     */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
        LOG.debug("starting up the client process.");
        MessageServiceSender client = new MessageServiceSender();
        try {
            client.sendMessages();
        } catch (Exception e) {
            LOG.warn("exception occured: ", e);
            throw new JobExecutionException(e);
        }
    }
}
