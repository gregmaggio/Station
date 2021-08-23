/**
 * 
 */
package ca.datamagic.station.inject;

import java.util.Enumeration;
import java.util.Hashtable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Greg
 *
 */
public class MemoryCacheInterceptor implements MethodInterceptor {
	private static Logger logger = LogManager.getLogger(MemoryCacheInterceptor.class);
	private static Hashtable<String, Object> cache = new Hashtable<String, Object>();
	
	public MemoryCacheInterceptor() {
	}

	public static synchronized void clearCache() {
		cache.clear();
	}
	
	public static synchronized Enumeration<String> getKeys() {
		return cache.keys();
	}
	
	private static String getKey(MethodInvocation invocation) {
		StringBuffer key = new StringBuffer();
		key.append(invocation.getMethod().getName());
		key.append("(");
		Object[] arguments = invocation.getArguments();
		if (arguments != null) {
			for (int ii = 0; ii < arguments.length; ii++) {
				Object argument = arguments[ii];
				if (ii > 0) {
					key.append(",");
				}
				if (argument == null) {
					key.append("null");
				} else {
					key.append(argument.toString());
				}
			}
		}
		key.append(")");
		return key.toString();
	}
	
	public static synchronized Object getValue(String key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		return null;
	}
	
	public static synchronized void setValue(String key, Object newVal) {
		cache.put(key, newVal);
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		logger.debug("invoke");
		String key = getKey(invocation);
		Object value = getValue(key);
		if (value == null) {
			value = invocation.proceed();
			if (value != null) {
				setValue(key, value);
			}
		}
		return value;
	}
}
