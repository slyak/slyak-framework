package com.slyak.core.ssh2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * .
 *
 * @author stormning 2018/5/24
 * @since 1.3.0
 */
public class StdEventLogger extends CustomStdLogger {
    private final ApplicationEventPublisher eventPublisher;

    public StdEventLogger(ApplicationEventPublisher eventPublisher) {
        this(null, eventPublisher);
    }

    public StdEventLogger(Logger logger, ApplicationEventPublisher eventPublisher) {
        super(logger == null ? LoggerFactory.getLogger(StdEventLogger.class) : logger);
        this.eventPublisher = eventPublisher;
    }

    private int number = 0;

    @Override
    public void info(String info) {
        super.info(info);
        eventPublisher.publishEvent(decorate(new StdEvent(StdEventType.INFO, info, ++number)));
    }

    @Override
    public void processOut(String out) {
        super.processOut(out);
        eventPublisher.publishEvent(decorate(new StdEvent(StdEventType.OUT, out, ++number)));
    }

    @Override
    public void processError(String error) {
        super.processError(error);
        eventPublisher.publishEvent(decorate(new StdEvent(StdEventType.ERROR, error, ++number)));
    }

    protected StdEvent decorate(StdEvent stdEvent) {
        return stdEvent;
    }
}
