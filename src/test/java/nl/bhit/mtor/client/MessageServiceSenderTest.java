package nl.bhit.mtor.client;

import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class MessageServiceSenderTest extends TestCase {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());
    MessageServiceSender client = new MessageServiceSender();

    @Test
    public void testAddMessage() {
        log.trace("start testAddMessage...");
        try {
            client.sendMessages();
            log.info("sending worked");
        } catch (Exception e) {
            log.info("sending failed. this test only works with active soap service");
            log.info("error", e);
        }
    }

    @Test
    public void testGetBasePackages() {
        Set<String> result = client.getBasePackages();
        Assert.assertEquals(1, result.size());
    }
}
