package org.openiot.security.oauth;

import org.slf4j.LoggerFactory;
import org.springframework.util.SystemPropertyUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * @author Mehdi Riahi
 * 
 */
public abstract class LogbackConfigurer {
	public static void initLogging(String location) throws JoranException {
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
		// assume SLF4J is bound to logback in the current environment
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		// Call context.reset() to clear any previous configuration, e.g. default
		// configuration. For multi-step configuration, omit calling context.reset().
		context.reset();
		configurator.doConfigure(resolvedLocation);
	}
}
