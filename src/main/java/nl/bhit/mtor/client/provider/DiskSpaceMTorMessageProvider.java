package nl.bhit.mtor.client.provider;

import java.io.File;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.model.Status;
import nl.bhit.mtor.client.properties.MTorProperties;

import org.apache.log4j.Logger;

/**
 * This message provider has one method which will give a soapMessage about the diskSpace.
 * 
 * @author tibi
 */
@MTorMessageProvider
public final class DiskSpaceMTorMessageProvider {
	private static final transient Logger LOG = Logger.getLogger(DiskSpaceMTorMessageProvider.class);

    private static long warnLimit = MTorProperties.getDiskspaceWarnlimit();
    private static long errorLimit = MTorProperties.getDiskspaceErrorlimit();
    private static String path = MTorProperties.getDiskspacePath();

    private static final String WARN_MSG = "The hard drive is getting full!";
    private static final String ERROR_MSG = "The hard drive is full!";

    /**
     * this method will return a warning message when the WARN_LIMMI is reached and an error message when the
     * errorLimit is reached. Null when all is fine.
     * 
     * @return
     */
    @MTorMessage
    public static ClientMessage getDiskSpaceMessage() {
    	ClientMessage message = new ClientMessage();
        long free = getFreeDiskSpace();
        if (free < errorLimit) {
            return createMessage(message, ERROR_MSG, Status.ERROR);
        }
        if (free < warnLimit) {
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

    protected static long getFreeDiskSpace() {
        File tmp = new File(path);
        long free = tmp.getFreeSpace();
        LOG.trace("free disk space is: " + free);
        return free;
    }

    public static long getErrorLimit() {
        return errorLimit;
    }

    public static void setErrorLimit(long errorLimit) {
        DiskSpaceMTorMessageProvider.errorLimit = errorLimit;
    }

    public static long getWarnLimit() {
        return warnLimit;
    }

    public static void setWarnLimit(long warnLimit) {
    	DiskSpaceMTorMessageProvider.warnLimit = warnLimit;
    }

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		DiskSpaceMTorMessageProvider.path = path;
	}

    private DiskSpaceMTorMessageProvider() {
    }
    
}
