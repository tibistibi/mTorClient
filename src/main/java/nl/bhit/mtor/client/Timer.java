package nl.bhit.mtor.client;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Timer {
	private static final transient Logger LOG = Logger.getLogger(Timer.class);

    /**
     * is called from the timer.
     */
    public void process() {
        LOG.debug("starting up the timed processor.");
        MessageServiceSender client = new MessageServiceSender();
        try {
            client.sendMessages();
        } catch (Exception e) {
            LOG.warn("exception occured: ", e);
        }
    }
}
