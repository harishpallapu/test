package com.resolvat.healthCheckers;

import com.resolvat.model.StatusRepo;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketException;

/**
 * Created by korteke on 11/03/17.
 */
@Service
public class SMTPHealthCheck extends HealthChecker {

    @Autowired
    private StatusRepo statusRepo;

    @Value("${smtp.address}")
    private String smtpAddress;

    @Value("${smtp.port}")
    private int smtpPort;

    @Value("${smtp.timeout}")
    private int smtpTimeout;

    @Value("${smtp.index}")
    private int index;

    @Value("${smtp.enabled}")
    private String enabled;

    public SMTPHealthCheck() {
    }

    /**
     * Default constructor
     */
    @Override
    public void runHealthCheck() {

        int replyCode;
        boolean isAlive = true;
        logger.debug("smtpAddress: " + smtpAddress);
        logger.debug("Smtp Index: " + index);

        SMTPClient smtpClient = new SMTPClient();
        smtpClient.setConnectTimeout(smtpTimeout);

        try {
            smtpClient.connect(smtpAddress, smtpPort);

        } catch (SocketException e) {
            isAlive = false;
            logger.error("SMTP Socket Exception!");
        } catch (IOException e1) {
            isAlive = false;
            logger.error("SMTP IO Exception!");
        } finally {
            try {
                smtpClient.disconnect();
            } catch (IOException e) {
                isAlive = false;
                logger.error("Cannot close SMTP connection");
            }
            statusRepo.setStatus(isAlive, index);
        }

        replyCode = smtpClient.getReplyCode();

        if (!SMTPReply.isPositiveCompletion(replyCode)) {
            try {
                smtpClient.disconnect();
            } catch (IOException e) {
                logger.error("SMTP IO Exception!");
            }
            logger.error("Can't Connect to the SMTP server - " + smtpAddress);
            statusRepo.setStatus(isAlive, index);

        } else {
            try {
                smtpClient.disconnect();
                logger.debug("Succesfully connected to SMTP server - " + smtpAddress);
                statusRepo.setStatus(isAlive, index);
            } catch (IOException e) {
                logger.error("SMTP IO Exception!");
                statusRepo.setStatus(isAlive, index);
            }

        }
    }
}
