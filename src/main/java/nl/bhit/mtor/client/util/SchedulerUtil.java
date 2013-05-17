package nl.bhit.mtor.client.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public final class SchedulerUtil {
	private static final transient Logger LOG = Logger.getLogger(SchedulerUtil.class);
	
	private static final String GROUP_NAME = "mTorClient";
	static final long MINUTE_IN_MILLISECONDS=60000;
	
	private static Scheduler scheduler = null;
	
	public static void initialize() throws SchedulerException {
		scheduler = new StdSchedulerFactory().getScheduler();
	}
	
	public static void scheduleNewJob(Class<? extends Job> clazz, String jobName, String cron, Integer delayInMinutes) throws SchedulerException {
		JobDetail job = newJob(clazz)
			      .withIdentity(jobName, GROUP_NAME)
			      .build();

		Date startDate = new Date();
		if (delayInMinutes != null) {
			long t = startDate.getTime();
			startDate = new Date(t + (Long.valueOf(delayInMinutes) * MINUTE_IN_MILLISECONDS));
		}
		
		CronTrigger trigger = newTrigger()
			    .withIdentity(jobName, GROUP_NAME)
			    .withSchedule(cronSchedule(cron))
			    .startAt(startDate)
			    .build();
		
		scheduler.scheduleJob(job, trigger);
		scheduler.start();
	}
	
	public static String getCurrentCronExpression(String jobName) throws SchedulerException {
		String triggerName = jobName;
		String cronExpression = null;
		for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP_NAME))) {
			if (triggerKey.getName().equals(triggerName)) {
				CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
				cronExpression = trigger.getCronExpression();
			}
		}
		return cronExpression;
	}
	
	public static void rescheduleExistingJob(String jobName, String cron) throws SchedulerException {
		String triggerName = jobName;
		for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP_NAME))) {
			if (triggerKey.getName().equals(triggerName)) {
				CronTrigger trigger = newTrigger()
					    .withIdentity(triggerName, GROUP_NAME)
					    .withSchedule(cronSchedule(cron))
					    .build();

				scheduler.rescheduleJob(triggerKey, trigger);
				scheduler.start();
			}
		}
	}
	
	public static void listJobs() throws SchedulerException {
		for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(GROUP_NAME))) {
			String jobName = jobKey.getName();
			String jobGroup = jobKey.getGroup();

			@SuppressWarnings("unchecked")
			List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
			Date nextFireTime = triggers.get(0).getNextFireTime();

			LOG.debug("[jobName] " + jobName +
					" [groupName] " + jobGroup + 
					" [nextFireTime] "+ nextFireTime);
		}
	}
	
	public static void shutdown() throws SchedulerException {
		scheduler.shutdown();
	}
	
	private SchedulerUtil() {
	}
}
