package nl.bhit.mtor.client.provider;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.model.Status;
import nl.bhit.mtor.model.soap.SoapMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@MTorMessageProvider
public class FreeMemoryMTorMessageProvider {

	
    private static final Log log = LogFactory.getLog(FreeMemoryMTorMessageProvider.class);
    
    public static final long WARN_LIMIT = 150 /*MB*/ * 1024 /*KB x MB*/ * 1024 /*Byte x KB*/;
    public static final long ERROR_LIMIT = 50 /*MB*/ * 1024 /*KB x MB*/ * 1024 /*Byte x KB*/;
    
    private static final String ERROR_MSG = "The free memory is less then " + ERROR_LIMIT + "!";
    private static final String WARN_MSG = "The free memory is less then " + WARN_LIMIT + "! It is running low.";


    /**
     * this method will return a warning message when the WARN_LIMIT is reached and an error message when the
     * ERROR_LIMIT is reached. Null when all is fine.
     * 
     * @return
     */
    @MTorMessage
    public static SoapMessage getVirtualMemoryMessage() {
        SoapMessage message = new SoapMessage();
        final long free = Runtime.getRuntime().freeMemory();
        log.trace("free memory is: " + free);
        if (free < ERROR_LIMIT) {
            log.trace(ERROR_MSG);
            return createMessage(message, ERROR_MSG, Status.ERROR);
        }
        if (free < WARN_LIMIT) {
            log.trace(WARN_MSG);
            return createMessage(message, WARN_MSG, Status.WARN);
        }
        return null;
    }

    protected static SoapMessage createMessage(SoapMessage message, String errorMessage, Status status) {
        log.warn(errorMessage);
        message.setContent(errorMessage);
        message.setStatus(status);
        return message;
    }

}
