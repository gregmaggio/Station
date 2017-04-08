/**
 * 
 */
package ca.datamagic.station.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Greg
 *
 */
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
public @interface MemoryCache {

}
