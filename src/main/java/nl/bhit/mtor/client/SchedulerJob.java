package nl.bhit.mtor.client;

import nl.bhit.mtor.client.properties.MTorProperties;
import nl.bhit.mtor.client.properties.MTorPropertiesException;
import nl.bhit.mtor.client.util.SchedulerUtil;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class SchedulerJob implements Job  {

	private static final transient Logger LOG = Logger.getLogger(SchedulerJob.class);

	private static final String JOB_NAME = "mTorClientScheduler";
	private static final String CRON_EXPRESSION = "0 0/2 * * * ?";
	private static final int START_MONITORING_DELAY_IN_MINUTES = 4;
	
    public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			MTorProperties.initialize();
			String cronExpression = MTorProperties.getSchedulerCron();
			String currentCronExpression = SchedulerUtil.getCurrentCronExpression(MonitorJob.getJobName());
			
			if (currentCronExpression == null) {
				LOG.debug("First time run, scheduling new job. Monitoring will begin in " + START_MONITORING_DELAY_IN_MINUTES + " minutes!");
				SchedulerUtil.scheduleNewJob(MonitorJob.class, MonitorJob.getJobName(), cronExpression, START_MONITORING_DELAY_IN_MINUTES);
			} else {
				LOG.debug("Monitoring job already started. Will check if cron schedule needs to be changed...");
				if (!cronExpression.equals(currentCronExpression)) {
					LOG.debug("Cron schedule has changed, rescheduling monitoring job!");
					SchedulerUtil.rescheduleExistingJob(MonitorJob.getJobName(), cronExpression);
				}
			}
			
			SchedulerUtil.listJobs();
			
		} catch (MTorPropertiesException e) {
			LOG.warn("Properties of mTor client are not properly loaded. The application is probably not monitored! " + e.getMessage());
			throw new JobExecutionException(e);
		} catch (SchedulerException e) {
			LOG.warn("Problem while creating scheduler. The application is probably not monitored! " + e.getMessage());
			throw new JobExecutionException(e);
		}
	}
	
	public static String getJobName() {
		return JOB_NAME;
	}
	
	public static String getCronExpression() {
		return CRON_EXPRESSION;
	}
}
