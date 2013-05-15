package nl.bhit.mtor.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.properties.MTorProperties;
import nl.bhit.mtor.client.properties.MTorPropertiesException;
import nl.bhit.mtor.client.util.AnnotationUtil;
import nl.bhit.mtor.client.util.RestUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * This message Service sender will send all messages it will find. It will look for providers annotated with @MTorMessageProvider
 * and methods with
 * 
 * @MTorMessage.
 * @author tibi
 * @author jgvanrossum
 */
public class MessageServiceSender {
	private static final String GENERAL_ERROR_MESSAGE = "There was an error while sending messages. The application is probably not monitored! ";
	private static final transient Logger LOG = Logger.getLogger(MessageServiceSender.class);

	public MessageServiceSender() {
		try {
			MTorProperties.initialize();
		} catch (MTorPropertiesException e) {
			LOG.warn("Properties of mTor client are not properly loaded. The application is probably not monitored! " + e.getMessage());
		}
	}

	/**
	 * Will search for all @MTorMessageProvider classes and invoke the @MTorMessage methods for retrieval of the
	 * messages. These messages will be send via soap
	 * to the mTor server.
	 * Possible errors will be logged but will not influence the project (will be kept quiet).
	 */
	public void sendMessages() {
		LOG.trace("Start sending message, will search for MTorMessageProvider classes");

		for (String basePackage : MTorProperties.getPackages()) {
			LOG.trace("sendMessages for base package: " + basePackage);
			try {
				sendMessages(basePackage);
			} catch (Exception e) {
				LOG.warn(GENERAL_ERROR_MESSAGE + e.getMessage());
				LOG.trace("Exception with which we do nothing.", e);
			}
		}
	}

	private void sendMessages(String basePackage) throws IllegalAccessException, InvocationTargetException {
		final Set<BeanDefinition> candidates = AnnotationUtil.findProviders(MTorMessageProvider.class, basePackage);
		for (BeanDefinition beanDefinition : candidates) {
			sendMessageForProvider(beanDefinition);
		}
	}

	protected void sendMessageForProvider(BeanDefinition beanDefinition) throws IllegalAccessException, InvocationTargetException {
		LOG.debug("Found bean: " + beanDefinition);
		for (Method method : AnnotationUtil.findMethods(MTorMessage.class, beanDefinition)) {
			sendMessageForMethod(method);
		}
	}

    protected void sendMessageForMethod(Method method) throws IllegalAccessException, InvocationTargetException {
        LOG.trace("Will invoke method to retrieve a Message: " + method);
        ClientMessage clientMessage = (ClientMessage) method.invoke(null, (Object[]) null); 
        
        if (clientMessage == null) {
            LOG.trace("Client message result is null, no sending needed.");
        } else {
        	clientMessage.setProjectId(MTorProperties.getProjectId());
        	String url = MTorProperties.getServerUrlBase() + MTorProperties.getServerUrlSaveclientmessage();
        	LOG.debug("Saving client message to the server: " + clientMessage);
            RestUtil.putObjectInServer(clientMessage, url, MTorProperties.getServerUsername(), MTorProperties.getServerPassword());
        }
    }

}
