package nl.bhit.mtor.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.client.util.AnnotationUtil;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.model.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * This message Service sender will send all messages it will find. It will look for providers annotated with @MTorMessageProvider
 * and methods with
 * 
 * @MTorMessage.
 * @author tibi
 */
public class MessageServiceSender {

    private static final String M_TOR_PROJECT_ID = "mTor.project.id";
    private static final String M_TOR_SERVER_URL = "mTor.server.url";
    private static final String M_TOR_PACKAGES = "mTor.packages";
    private static final String M_TOR_PROPERTIES = "mTor.properties";
    protected final Log log = LogFactory.getLog(MessageServiceSender.class);
    Properties properties;
    private final String defaultBasePackage = "nl.bhit.mtor";

    public MessageServiceSender() {
        properties = new Properties();
        if (!loadProperties(M_TOR_PROPERTIES)) {
            log.debug("Will load default properties.");
            loadProperties("default." + M_TOR_PROPERTIES);
        }
        log.trace("props loaded");
    }

    protected boolean loadProperties(String propertiesFile) {
        boolean result = false;
        try {
            properties.load(this.getClass().getResourceAsStream("/" + propertiesFile));
            result = true;
        } catch (Exception e) {
            log.error("Properties could not be loaded. Make sure the properties file is on the path: "
                    + M_TOR_PROPERTIES);
            log.trace("stacktrace for above error:", e);
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
        log.trace("start sending message, will search for MTorMessageProvider classes");

        for (String basePackage : getBasePackages()) {
            log.trace("sendMessages for base package: " + basePackage);
            sendMessages(basePackage);
        }
    }

    protected Set<String> getBasePackages() {
        Set<String> result = new HashSet<String>();
        result.add(defaultBasePackage);
        String[] pieces = StringUtils.split(getBasePackegeFromProperty(), ",");
        for (int i = 0; i < pieces.length; i++) {
            result.add(StringUtils.trim(pieces[i]));
        }
        return result;
    }

    protected String getBasePackegeFromProperty() {
        return properties.getProperty(M_TOR_PACKAGES);
    }

    private void sendMessages(String basePackage) {
        try {
            final Set<BeanDefinition> candidates = AnnotationUtil.findProviders(MTorMessageProvider.class, basePackage);
            for (BeanDefinition beanDefinition : candidates) {
                sendMessageForThisProvider(beanDefinition);
            }
        } catch (Exception e) {
            log.warn("There is a problem in sending the mTor messages via soap. Monitoring will not work", e);
        }
    }

    protected void sendMessageForThisProvider(BeanDefinition beanDefinition) throws IllegalAccessException,
            InvocationTargetException {
        log.debug("found bean: " + beanDefinition);
        for (Method method : AnnotationUtil.findMethods(MTorMessage.class, beanDefinition)) {
            sendMessageForThisMehtod(method);
        }
    }

    protected void sendMessageForThisMehtod(Method method) throws IllegalAccessException, InvocationTargetException {
        log.trace("will invoke method to retrieve a soapMessage: " + method);
        ClientMessage soapMessage = (ClientMessage) method.invoke(null,
                (Object[]) null);
        if (soapMessage == null) {
            log.trace("soapMessage result is null, no sending needed.");
        } else {
            sendMessage(soapMessage);
        }
    }

    protected void sendMessage(ClientMessage soapMessage) {
        log.debug("trying to add a message to the soap service: " + soapMessage);
        //MessageServiceStub stub = createMessageServiceStub();
        //MessageServiceStub.SaveSoapMessageE req = addSoapMessageToStub(soapMessage);
        //sendSoapMessage(stub, req);
    }

    protected String getConnectionUrl() {
        return properties.getProperty(M_TOR_SERVER_URL);
    }

    private static Status getStatus(nl.bhit.mtor.client.model.Status status) {
        Status msgStatus;

        switch (status) {
            case INFO:
                msgStatus = Status.INFO;
                break;
            case WARN:
                msgStatus = Status.WARN;
                break;
            default:
                msgStatus = Status.ERROR;
                break;
        }

        return msgStatus;
    }

    protected Long getPorjectId() {
        Long projectId = null;
        try {
            String projectIdStr = properties.getProperty(M_TOR_PROJECT_ID);
            projectId = new Long(projectIdStr);
        } catch (Exception e) {
            log.error("could not read the projectId so message can not be send, no monitoring possible!", e);
        }
        log.debug("using projectId:" + projectId);
        return projectId;
    }

}
