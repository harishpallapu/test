package com.resolvat.controller;

import com.resolvat.healthCheckers.HTTPHealthCheck;
import com.resolvat.healthCheckers.LDAPHealthCheck;
import com.resolvat.healthCheckers.SMTPHealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by korteke on 11/03/17.
 */
@Component
public class ScheduledChecks {

    /**
     * Class logger
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LDAPHealthCheck ldapHealthCheck;

    @Autowired
    private SMTPHealthCheck smtpHealthCheck;

    @Autowired
    private HTTPHealthCheck httpHealthCheck;

    @Value("${ldap.enabled}")
    private String ldapEnabled;

    @Value("${smtp.enabled}")
    private String smtpEnabled;

    @Value("${http.enabled}")
    private String httpEnabled;

    @Scheduled(fixedDelayString = "${general.interval}")
    public void runChecks() {

        if (Boolean.parseBoolean(ldapEnabled))
            ldapHealthCheck.runHealthCheck();

        if (Boolean.parseBoolean(smtpEnabled))
            smtpHealthCheck.runHealthCheck();

        if (Boolean.parseBoolean(httpEnabled))
            httpHealthCheck.runHealthCheck();

    }


}
