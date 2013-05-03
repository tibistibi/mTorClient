package nl.bhit.mtor.client.provider;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.model.Status;
import nl.bhit.mtor.client.properties.MTorProperties;

import org.apache.log4j.Logger;

@MTorMessageProvider
public final class FreeMemoryMTorMessageProvider {
	private static final transient Logger LOG = Logger.getLogger(FreeMemoryMTorMessageProvider.class);
    
    private static long warnLimit = MTorProperties.getFreememoryWarnlimit();
    private static long errorLimit = MTorProperties.getFreememoryErrorlimit();
    
    private static final String WARN_MSG = "The free memory is running low.";
    private static final String ERROR_MSG = "The free memory is too low!";

    /**
     * this method will return a warning message when the WARN_LIMIT is reached and an error message when the
     * ERROR_LIMIT is reached. Null when all is fine.
     * 
     * @return
     */
    @MTorMessage
    public static ClientMessage getFreeMemoryMessage() {
    	ClientMessage message = new ClientMessage();
        final long free = Runtime.getRuntime().freeMemory();
        LOG.trace("free memory is: " + free);
        if (free < errorLimit) {
            LOG.trace(ERROR_MSG);
            return createMessage(message, ERROR_MSG, Status.ERROR);
        }
        if (free < warnLimit) {
            LOG.trace(WARN_MSG);
            return createMessage(message, WARN_MSG, Status.WARN);
        }
        return null;
    }

    protected static ClientMessage createMessage(ClientMessage message, String errorMessage, Status status) {
        LOG.warn(errorMessage);
        message.setContent(errorMessage);
        message.setStatus(status);
        return message;
    }

	public static long getWarnLimit() {
		return warnLimit;
	}

	public static void setWarnLimit(long warnLimit) {
		FreeMemoryMTorMessageProvider.warnLimit = warnLimit;
	}

	public static long getErrorLimit() {
		return errorLimit;
	}

	public static void setErrorLimit(long errorLimit) {
		FreeMemoryMTorMessageProvider.errorLimit = errorLimit;
	}

	private FreeMemoryMTorMessageProvider() {
	}
}
