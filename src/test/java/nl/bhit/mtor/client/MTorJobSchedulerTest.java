package nl.bhit.mtor.client;

import junit.framework.Assert;
import nl.bhit.mtor.client.properties.MTorProperties;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MTorJobSchedulerTest {
	
	MTorJobScheduler scheduler = new MTorJobScheduler();
	
    @Test
    public void testScheduler() {
    	scheduler.run();
    	
    	Assert.assertEquals(MTorProperties.getSchedulerCron(), scheduler.getCurrentCronExpression());
    	
    	scheduler.run();
    	
    	Assert.assertEquals(MTorProperties.getSchedulerCron(), scheduler.getCurrentCronExpression());
    	
    	scheduler.rescheduleExistingJob("15 * * * * ?");
    	
    	assertThat(MTorProperties.getSchedulerCron(), not(equalTo(scheduler.getCurrentCronExpression())));
    	
    	scheduler.run();
    	
    	Assert.assertEquals(MTorProperties.getSchedulerCron(), scheduler.getCurrentCronExpression());
    	
    	scheduler.shutdown();
    	
    }
    
    public void testChangingPropertiesFile() {
    	scheduler.run();
    	
    	try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	scheduler.run();
    	
    	try {
			Thread.sleep(20000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	scheduler.run();
    	
    	try {
			Thread.sleep(1000000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	scheduler.shutdown();
    }
}
