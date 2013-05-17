package nl.bhit.mtor.client;

import junit.framework.Assert;
import nl.bhit.mtor.client.properties.MTorProperties;
import nl.bhit.mtor.client.util.SchedulerUtil;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.quartz.SchedulerException;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MTorJobSchedulerTest {
	private static final transient Logger LOG = Logger.getLogger(MTorJobSchedulerTest.class);
	
	SchedulerJob scheduler = new SchedulerJob();
	
    @Test
    public void testScheduler() {
    	
    	try {
			SchedulerUtil.initialize();
			
			scheduler.execute(null);
			SchedulerUtil.listJobs();
			
			Assert.assertEquals(MTorProperties.getSchedulerCron(), SchedulerUtil.getCurrentCronExpression(MonitorJob.getJobName()));
			
			scheduler.execute(null);
			SchedulerUtil.listJobs();
			
	    	Assert.assertEquals(MTorProperties.getSchedulerCron(), SchedulerUtil.getCurrentCronExpression(MonitorJob.getJobName()));
	    	
	    	SchedulerUtil.rescheduleExistingJob(MonitorJob.getJobName(), "15 * * * * ?");
	    	SchedulerUtil.listJobs();
	    	
	    	assertThat(MTorProperties.getSchedulerCron(), not(equalTo(SchedulerUtil.getCurrentCronExpression(MonitorJob.getJobName()))));
	    	
	    	scheduler.execute(null);
	    	SchedulerUtil.listJobs();
	    	
	    	Assert.assertEquals(MTorProperties.getSchedulerCron(), SchedulerUtil.getCurrentCronExpression(MonitorJob.getJobName()));
			
	    	SchedulerUtil.shutdown();
	    	
		} catch (SchedulerException e) {
			LOG.warn("Problem while starting scheduler! " + e.getMessage());
		}
    }
    
}
