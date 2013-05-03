package nl.bhit.mtor.client.provider;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.model.Status;

/**
 * This message provider has one method which will send a message with status INFO and content 'i am alive'
 */

@MTorMessageProvider
public final class HeartBeatMTorMessageProvider {

    @MTorMessage
    public static ClientMessage getHeartBeatMessage() {
    	ClientMessage message = new ClientMessage();
        message.setContent("I am alive!");
        message.setStatus(Status.INFO);
        return message;
    }
    
    private HeartBeatMTorMessageProvider() {
    }
}
