package org.apache.rocketmq.spring.boot.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

/**
 * {@link EventHandler} base that applies itself only to events whose route
 * expression matches one of the configured Ant-style patterns.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public abstract class AbstractPathMatchMessageHandler<T extends RocketmqEvent> extends AbstractAdviceMessageHandler<T>  implements PathProcessor<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractPathMatchMessageHandler.class);

	/** Ant-style path matcher used to test route expressions. */
	protected PathMatcher pathMatcher = new AntPathMatcher();

	/** Ant-style patterns this handler should apply to. */
	protected List<String> appliedPaths = new ArrayList<String>();

	@Override
    /**
     * <p>Process path.</p>
     * @param path
     * @return the process path
     */
	public EventHandler<T> processPath(String path) {
		this.appliedPaths.add(path);
		return this;
	}
	
    /**
     * <p>Returns the path within event.</p>
     * @param event
     * @return the get path within event
     */
	protected String getPathWithinEvent(T event) {
		return event.getRouteExpression();
	}

    /**
     * <p>Paths match.</p>
     * @param path
     * @param event
     * @return the paths match
     */
	protected boolean pathsMatch(String path, T event) {
		String eventExp = getPathWithinEvent(event);
		LOG.trace("Attempting to match pattern '{}' with current Event Expression '{}'...", path, eventExp);
		return pathsMatch(path, eventExp);
	}

    /**
     * <p>Paths match.</p>
     * @param pattern
     * @param path
     * @return the paths match
     */
	protected boolean pathsMatch(String pattern, String path) {
		return pathMatcher.match(pattern, path);
	}
	
	
    /**
     * <p>Pre handle.</p>
     * @param event
     * @return the pre handle
     */
	protected boolean preHandle(T event) throws Exception {

		if (this.appliedPaths == null || this.appliedPaths.isEmpty()) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("appliedPaths property is null or empty.  This Handler will passthrough immediately.");
			}
			return true;
		}

		for (String path : this.appliedPaths) {
			// If the path does match, then pass on to the subclass
			// implementation for specific checks
			// (first match 'wins'):
			if (pathsMatch(path, event)) {
				LOG.trace("Current Event Expression matches pattern '{}'.  Determining handler chain execution...", path);
				return isHandlerChainContinued(event, path);
			}
		}

		// no path matched, allow the request to go through:
		return true;
	}

	private boolean isHandlerChainContinued(T event, String path) throws Exception {

		if (isEnabled(event, path)) { // isEnabled check

			if (LOG.isTraceEnabled()) {
				LOG.trace("Handler '{}' is enabled for the current event under path '{}'.  " + "Delegating to subclass implementation for 'onPreHandle' check.", new Object[] { getName(), path });
			}
			// The handler is enabled for this specific request, so delegate to
			// subclass implementations
			// so they can decide if the request should continue through the
			// chain or not:
			return onPreHandle(event);
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("Handler '{}' is disabled for the current event under path '{}'.  " + "The next element in the HandlerChain will be called immediately.", new Object[] { getName(), path });
		}
		// This handler is disabled for this specific request,
		// return 'true' immediately to indicate that the handler will not
		// process the request
		// and let the request/response to continue through the handler chain:
		return true;
	}

    /**
     * <p>On pre handle.</p>
     * @param event
     * @return the on pre handle
     */
	protected boolean onPreHandle(T event) throws Exception {
		return true;
	}
	
    /**
     * <p>Checks if enabled.</p>
     * @param event
     * @param path
     * @return the is enabled
     */
	protected boolean isEnabled(T event, String path) throws Exception {
		return isEnabled(event);
	}

    /**
     * <p>Returns the path matcher.</p>
     * @return the get path matcher
     */
	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

    /**
     * <p>Sets the path matcher.</p>
     * @param pathMatcher
     */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

    /**
     * <p>Returns the applied paths.</p>
     * @return the get applied paths
     */
	public List<String> getAppliedPaths() {
		return appliedPaths;
	}
	
}
