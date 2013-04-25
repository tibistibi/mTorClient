package nl.bhit.mtor.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.util.AnnotationUtil;
import nl.bhit.mtor.client.util.RestUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.web.client.RestClientException;

/**
 * This message Service sender will send all messages it will find. It will look for providers annotated with @MTorMessageProvider
 * and methods with
 * 
 * @MTorMessage.
 * @author tibi
 * @author jgvanrossum
 */
public class MessageServiceSender {
	private static final transient Logger LOG = Logger.getLogger(MessageServiceSender.class);

	private static final String M_TOR_PROJECT_ID = "mTor.project.id";
    private static final String M_TOR_SERVER_URL = "mTor.server.url";
    private static final String M_TOR_SERVER_USERNAME = "mTor.server.username";
    private static final String M_TOR_SERVER_PASSWORD = "mTor.server.password";
    private static final String M_TOR_PACKAGES = "mTor.packages";
    private static final String M_TOR_PROPERTIES = "mTor.properties";
    
    Properties properties;
    private final String defaultBasePackage = "nl.bhit.mtor";

	public MessageServiceSender() {
		properties = new Properties();
		if (!loadProperties(M_TOR_PROPERTIES)) {
			LOG.debug("Will load default properties.");
			loadProperties("default." + M_TOR_PROPERTIES);
		}
		LOG.trace("props loaded");
	}

	protected boolean loadProperties(String propertiesFile) {
		boolean result = false;
		try {
			properties.load(this.getClass().getResourceAsStream("/" + propertiesFile));
			result = true;
		} catch (Exception e) {
			LOG.warn("Properties could not be loaded. Make sure the properties file is on the path: " + M_TOR_PROPERTIES);
			LOG.trace("stacktrace for above error:", e);
		}
		return result;
	}

	/**
	 * Will search for all @MTorMessageProvider classes and invoke the @MTorMessage methods for retrieval of the
	 * messages. These messages will be send via soap
	 * to the mTor server.
	 * Possible errors will be logged but will not influence the project (will be kept quiet).
	 */
	public void sendMessages() {
		LOG.trace("start sending message, will search for MTorMessageProvider classes");

		for (String basePackage : getBasePackages()) {
			LOG.trace("sendMessages for base package: " + basePackage);
			try {
				sendMessages(basePackage);
			} catch (Exception e) {
				LOG.warn("there is an exception with sendMessages. The application is probably not monitorred! " + e.getMessage());
				LOG.trace("exception with which we do nothing. ", e);
			}
		}
	}

	protected Set<String> getBasePackages() {
		Set<String> result = new HashSet<String>();
		result.add(defaultBasePackage);
<<<<<<< HEAD
		String[] pieces = StringUtils.split(getBasePackageFromProperty(), ",");
=======
		String[] pieces = StringUtils.split(getBasePackegeFromProperty(), ",");
>>>>>>> branch 'master' of https://github.com/tibistibi/mTorClient.git
		if (pieces != null) {
			for (int i = 0; i < pieces.length; i++) {
				result.add(StringUtils.trim(pieces[i]));
			}
		}
		return result;
	}

	protected String getBasePackageFromProperty() {
		return properties.getProperty(M_TOR_PACKAGES);
	}

	private void sendMessages(String basePackage) throws Exception {
		try {
			final Set<BeanDefinition> candidates = AnnotationUtil.findProviders(MTorMessageProvider.class, basePackage);
			for (BeanDefinition beanDefinition : candidates) {
				sendMessageForProvider(beanDefinition);
			}
		} catch (Exception e) {
			LOG.warn("There is a problem in sending the mTor messages via soap. Monitoring will not work", e);
			throw e;
		}
	}

	protected void sendMessageForProvider(BeanDefinition beanDefinition) throws IllegalAccessException, InvocationTargetException, RestClientException,
			Exception {
		LOG.debug("found bean: " + beanDefinition);
		for (Method method : AnnotationUtil.findMethods(MTorMessage.class, beanDefinition)) {
			sendMessageForMethod(method);
		}
	}

    protected void sendMessageForMethod(Method method) throws IllegalAccessException, InvocationTargetException, RestClientException, Exception {
        LOG.trace("will invoke method to retrieve a Message: " + method);
        ClientMessage clientMessage = (ClientMessage) method.invoke(null, (Object[]) null); 
        
        if (clientMessage == null) {
            LOG.trace("Client message result is null, no sending needed.");
        } else {
        	clientMessage.setProjectId(getProjectId());
        	String url = getServerUrl() + "/services/api/messages/saveclientmessage";
        	LOG.debug("Saving client message to the server: " + clientMessage);
            RestUtil.putObjectInServer(clientMessage, url, getServerUsername(), getServerPassword());
        }
    }

	protected Long getProjectId() {
		Long projectId = null;
		try {
			String projectIdStr = properties.getProperty(M_TOR_PROJECT_ID);
			projectId = new Long(projectIdStr);
		} catch (Exception e) {
			LOG.warn("could not read the projectId so message can not be send, no monitoring possible!", e);
		}
		LOG.debug("using projectId:" + projectId);
		return projectId;
	}

	protected String getServerUrl() {
		return properties.getProperty(M_TOR_SERVER_URL);
	}
    
    protected String getServerUsername() {
        return properties.getProperty(M_TOR_SERVER_USERNAME);
    }
    
    protected String getServerPassword() {
        return properties.getProperty(M_TOR_SERVER_PASSWORD);
    }

}
