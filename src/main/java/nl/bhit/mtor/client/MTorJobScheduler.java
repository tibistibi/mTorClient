package nl.bhit.mtor.client;

import java.util.Date;
import java.util.List;

import nl.bhit.mtor.client.properties.MTorProperties;
import nl.bhit.mtor.client.properties.MTorPropertiesException;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

@Component
public class MTorJobScheduler {
	private static final transient Logger LOG = Logger.getLogger(MTorJobScheduler.class);
	
	private static final String TRIGGER_NAME = "mTorClientCronTrigger";
	private static final String JOB_NAME = "mTorClientJob";
	private static final String GROUP_NAME = "mTorClient";
	
	private String cron;
	private Scheduler scheduler = null;

    public MTorJobScheduler() {
    }
 
	public void run() {
		try {
			MTorProperties.initialize();
			cron = MTorProperties.getSchedulerCron();
			
			scheduler = new StdSchedulerFactory().getScheduler();
			if (scheduler.isShutdown()) {
				LOG.debug("Scheduler not yet started, starting now....");
				scheduler.start();
				
			} else if (getCurrentCronExpression() == null) {
				LOG.debug("First time run, scheduling new job");
				scheduleNewJob();
			} else {
				LOG.debug("Scheduler already started. Will check if rescheduling monitoring need to be done");
				if (!cron.equals(getCurrentCronExpression())) {
					LOG.debug("Cron schedule has changed, rescheduling monitoring job!");
					rescheduleExistingJob(cron);
				}
			}
			
			listJobs();
			
		} catch (MTorPropertiesException e) {
			LOG.warn("Properties of mTor client are not properly loaded. The application is probably not monitored! " + e.getMessage());
		} catch (SchedulerException e) {
			LOG.warn("Problem while creating scheduler. The application is probably not monitored! " + e.getMessage());
		}
	}
	
	public void scheduleNewJob() {
		try {
			JobDetail job = newJob(MTorJob.class)
				      .withIdentity(JOB_NAME, GROUP_NAME)
				      .build();
	
			CronTrigger trigger = newTrigger()
				    .withIdentity(TRIGGER_NAME, GROUP_NAME)
				    .withSchedule(cronSchedule(cron))
				    .build();
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			
		} catch (SchedulerException e) {
			LOG.warn("Problem while scheduling monitoring job. The application is probably not monitored! " + e.getMessage());
		}
	}
	
	public String getCurrentCronExpression () {
		String cronExpression = null;
		try {
			for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP_NAME))) {
				if (triggerKey.getName().equals(TRIGGER_NAME)) {
					CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
					cronExpression = trigger.getCronExpression();
				}
			}
		} catch (SchedulerException e) {
			LOG.warn("Problem while getting schedule of current job. The application might not be monitored! " + e.getMessage());
		}
		return cronExpression;
	}
	
	public void rescheduleExistingJob(String cron) {
		try {
			for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP_NAME))) {
				if (triggerKey.getName().equals(TRIGGER_NAME)) {
					CronTrigger trigger = newTrigger()
						    .withIdentity(TRIGGER_NAME, GROUP_NAME)
						    .withSchedule(cronSchedule(cron))
						    .build();
	
					scheduler.rescheduleJob(triggerKey, trigger);
					scheduler.start();
				}
			}
		} catch (SchedulerException e) {
			LOG.warn("Problem while rescheduling monitoring job. The application might not be monitored! " + e.getMessage());
		}
	}
	
	public void listJobs() {
		try {
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
		} catch (SchedulerException e) {
			LOG.warn("Problem while listing jobs! " + e.getMessage());
		}
	}
	
    public void shutdown() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
        	LOG.warn("Problem while shutting down scheduler! " + e.getMessage());
        }
    }
	
}
