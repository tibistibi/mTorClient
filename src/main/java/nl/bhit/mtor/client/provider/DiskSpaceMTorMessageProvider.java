package nl.bhit.mtor.client.provider;

import java.io.File;

import nl.bhit.mtor.client.annotation.MTorMessage;
import nl.bhit.mtor.client.annotation.MTorMessageProvider;
import nl.bhit.mtor.model.Status;
import nl.bhit.mtor.model.soap.SoapMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This message provider has one method which will give a soapMessage about the diskSpace.
 * 
 * @author tibi
 */
@MTorMessageProvider
public class DiskSpaceMTorMessageProvider {

    private final static Log log = LogFactory.getLog(DiskSpaceMTorMessageProvider.class);

    private static long errorLimit = 5000000000L;
    private static long warnLimit = 10000000000L;

    /**
     * this method will return a warning message when the WARN_LIMMI is reached and an error message when the
     * errorLimit is reached. Null when all is fine.
     * 
     * @return
     */
    @MTorMessage
    public static SoapMessage getDiskSpaceMessage() {
        SoapMessage message = new SoapMessage();
        long free = getFreeDiskSpace();
        if (free < errorLimit) {
            return createMessage(message, "The hard drive is almost full!", Status.ERROR);
        }
        if (free < warnLimit) {
            return createMessage(message, "The hard drive is getting full!", Status.WARN);
        }
        return null;
    }

    protected static SoapMessage createMessage(SoapMessage message, String errorMessage, Status status) {
        log.warn(errorMessage);
        message.setContent(errorMessage);
        message.setStatus(status);
        return message;
    }

    protected static long getFreeDiskSpace() {
        File tmp = new File("/");
        long free = tmp.getFreeSpace();
        log.trace("free disk space is: " + free);
        return free;
    }

    /*
     * Getters & Setters
     */
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

}
