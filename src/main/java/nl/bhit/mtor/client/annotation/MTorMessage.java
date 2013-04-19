package nl.bhit.mtor.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation will only be used with a class which is annotated with the @MTorMessageProvider.
 * All mesthods with the @MTorMessage annotation will be called by the client and the returned message will be send to the mTor Server.
 * 
 * @author tibi
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MTorMessage {
}
