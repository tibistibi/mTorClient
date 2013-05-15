package nl.bhit.mtor.client;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.properties.MTorProperties;
import nl.bhit.mtor.client.util.RestUtil;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

public class MessageServiceSenderTest extends TestCase {
	private static final transient Logger LOG = Logger.getLogger(MessageServiceSenderTest.class);
	
    MessageServiceSender client = new MessageServiceSender();

    @Test
    public void testAddMessage() {
        LOG.trace("start testAddMessage...");
        try {
            client.sendMessages();
            LOG.info("sending worked");
        } catch (Exception e) {
            LOG.info("sending failed. this test only works with active soap service");
            LOG.info("error", e);
        }
    }

    @Test
    public void testGetBasePackages() {
        Set<String> result = MTorProperties.getPackages();
        Assert.assertEquals(1, result.size());
    }
    
    @Test
    public void testGetMessages() {
    	String url = MTorProperties.getServerUrlBase() + "/services/api/messages/-1.json";
    	try {
			List<ClientMessage> clientMessages = RestUtil.getObjectsFromServer(ClientMessage[].class, url, MTorProperties.getServerUsername(), MTorProperties.getServerPassword());
			Assert.assertTrue(clientMessages.size() > 0);
		} catch (RestClientException e) {
			LOG.info("Getting textmessages test failed because of RestClientException: " + e.getMessage());
		} catch (Exception e) {
			LOG.info("Getting textmessages test failed because of a General Exception: " + e.getMessage());
		}
    }
    
}
