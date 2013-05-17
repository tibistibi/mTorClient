package nl.bhit.mtor.client;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.bhit.mtor.client.util.SchedulerUtil;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

/**
 * <p>StartupListener class used to initialize and database settings
 * and populate any application-wide drop-downs.
 * <p/>
 * <p>Keep in mind that this listener is executed outside of OpenSessionInViewFilter,
 * so if you're using Hibernate you'll have to explicitly initialize all loaded data at the
 * GenericDao or service level to avoid LazyInitializationException. Hibernate.initialize() works
 * well for doing this.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener implements ServletContextListener {
	
	private static final transient Logger LOG = Logger.getLogger(StartupListener.class);

    /**
     * {@inheritDoc}
     */
	@Override
    public void contextInitialized(ServletContextEvent event) {
		LOG.debug("Starting up mTor client scheduler");
		try {
			SchedulerUtil.initialize();
			SchedulerUtil.scheduleNewJob(SchedulerJob.class, SchedulerJob.getJobName(), SchedulerJob.getCronExpression(), null);
			
		} catch (SchedulerException e) {
			LOG.warn("Problem while starting scheduler! " + e.getMessage());
		}
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
		LOG.debug("Shutting down mTor client scheduler");
        try {
        	SchedulerUtil.shutdown();
        } catch (SchedulerException e) {
        	LOG.warn("Problem while shutting down scheduler! " + e.getMessage());
        }
    }
	


}
