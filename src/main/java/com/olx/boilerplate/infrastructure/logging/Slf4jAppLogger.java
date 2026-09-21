package com.olx.boilerplate.infrastructure.logging;

import com.olx.boilerplate.logging.AppLogger;
import org.slf4j.Logger;

final class Slf4jAppLogger implements AppLogger {

    private final Logger delegate;

    Slf4jAppLogger(Logger delegate) {
        this.delegate = delegate;
    }

    @Override
    public void debug(String message, Object... args) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(message, args);
        }
    }

    @Override
    public void info(String message, Object... args) {
        if (delegate.isInfoEnabled()) {
            delegate.info(message, args);
        }
    }

    @Override
    public void warn(String message, Object... args) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(message, args);
        }
    }

    @Override
    public void error(String message, Object... args) {
        if (delegate.isErrorEnabled()) {
            delegate.error(message, args);
        }
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        if (delegate.isErrorEnabled()) {
            if (args == null || args.length == 0) {
                delegate.error(message, throwable);
            } else {
                delegate.error(org.slf4j.helpers.MessageFormatter.arrayFormat(message, args).getMessage(), throwable);
            }
        }
    }
}
