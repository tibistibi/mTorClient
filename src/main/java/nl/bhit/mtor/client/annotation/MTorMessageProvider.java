package nl.bhit.mtor.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * classes which are annotated with the @mTor will be read by the mTor client.
 * The mTor message provider will provide hold methods which will provide the mTor client with the messages wich will be send to the mTor server.
 * 
 * @author tibi
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MTorMessageProvider {
}
