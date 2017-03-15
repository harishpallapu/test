package com.resolvat.healthCheckers;

import com.resolvat.model.StatusRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by korteke on 11/03/17.
 */
public abstract class HealthChecker implements InitializingBean {

    @Autowired
    protected StatusRepo statusRepo;

    protected String enabled;

    protected int index;

    protected String getEnabled() {
        return enabled;
    }

    public int getIndex() {
        return index;
    }

    /**
     * Class logger
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract void runHealthCheck();

    public void afterPropertiesSet() throws Exception {
        logger.debug("Check if disabled: " +  this.getClass().getName());
        logger.debug("Boolean: " + getEnabled());
        if (!Boolean.parseBoolean(getEnabled())) {
            logger.debug("Disabling check: " + this.getClass().getName());
            statusRepo.setStatus(true, getIndex());
        }
    }

}
