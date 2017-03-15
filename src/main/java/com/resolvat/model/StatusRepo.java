package com.resolvat.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class StatusRepo implements InitializingBean {

    /**
     * Class logger
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${check.count}")
    private int count;

    private final static AtomicReference<StatusRepo> INSTANCE = new AtomicReference<StatusRepo>();
    private boolean[] stats;

    public StatusRepo() {

        final StatusRepo previous = INSTANCE.getAndSet(this);
        if(previous != null)
            throw new IllegalStateException("ERROR!");
    }

    public static synchronized StatusRepo getInstance() {
        return INSTANCE.get();
    }

    public boolean isStatus() {
        logger.debug("StatusRepo isStatus is called");
        for (boolean x : stats) {
            if (!x) {
                logger.debug("StatusRepo returning false");
                return false;
            }
        }
        logger.debug("StatusRepo returning true");
        return true;
    }

    public void setStatus(boolean status, int index) {
        logger.debug("StatusRepo setStatus is called");
        logger.debug("Status: " + Boolean.toString(status) + ". Index: " + index);
        this.stats[index] = status;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("Filling status array with booleans");
        stats = new boolean[count];
        Arrays.fill(stats, Boolean.FALSE);
        }
    }
