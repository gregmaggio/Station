/**
 * 
 */
package ca.datamagic.station.inject;

import ca.datamagic.station.inject.MemoryCache;
import ca.datamagic.station.inject.MemoryCacheInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * @author Greg
 *
 */
public class DAOModule extends AbstractModule {
	public DAOModule() {
	}

	@Override
	protected void configure() {
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(MemoryCache.class), new MemoryCacheInterceptor());
	}
}
