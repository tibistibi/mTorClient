package nl.bhit.mtor.client.provider;

import junit.framework.TestCase;
import nl.bhit.mtor.client.model.ClientMessage;
import nl.bhit.mtor.client.model.Status;

import org.junit.Assert;

public class FreeMemoryMTorMessageProviderTest extends TestCase {

    public void testVirtualMemory() throws Exception {
        long free = Runtime.getRuntime().freeMemory();
        
        //enters (free > ERROR_LIMIT, WARN_LIMIT)
        FreeMemoryMTorMessageProvider.setErrorLimit(free - 52428800L);
        FreeMemoryMTorMessageProvider.setWarnLimit(free - 52428800L);
        
        ClientMessage message = FreeMemoryMTorMessageProvider.getFreeMemoryMessage();
        Assert.assertNull(message);
        
        //enters (free < ERROR_LIMIT)
        FreeMemoryMTorMessageProvider.setErrorLimit(free + 52428800L);
        FreeMemoryMTorMessageProvider.setWarnLimit(free + 52428800L);
        
        message = FreeMemoryMTorMessageProvider.getFreeMemoryMessage();
        Assert.assertNotNull(message);
        Assert.assertEquals(Status.ERROR, message.getStatus());
        
        FreeMemoryMTorMessageProvider.setErrorLimit(free - 52428800L);
        //enters (free < WARN_LIMIT)
        FreeMemoryMTorMessageProvider.setWarnLimit(free + 52428800L);
        
        message = FreeMemoryMTorMessageProvider.getFreeMemoryMessage();
        Assert.assertNotNull(message);
        Assert.assertEquals(Status.WARN, message.getStatus());

    }
    
}
