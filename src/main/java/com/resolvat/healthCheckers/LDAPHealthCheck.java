package com.resolvat.healthCheckers;

import com.resolvat.model.StatusRepo;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocketFactory;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

/**
 * Created by korteke on 11/03/17.
 */
@Service
public class LDAPHealthCheck extends HealthChecker {

    @Autowired
    private StatusRepo statusRepo;

    @NotNull
    @Value("${ldap.address}")
    private String ldapAddress;

    @NotNull
    @Value("${ldap.port}")
    private int ldapPort;

    @NotNull
    @Value("${ldap.secure}")
    private String ldapSecure;

    @NotNull
    @Value("${ldap.timeout}")
    private int ldapTimeout;

    @NotNull
    @Value("${ldap.index}")
    private int index;

    @Value("${ldap.enabled}")
    private String enabled;

    @Value("${ldap.binding}")
    private String binding;

    @Value("${ldap.bindUser}")
    private String bindUser;

    @Value("${ldap.bindPassword}")
    private String bindPassword;

    /**
     * Default constructor
     */
    public LDAPHealthCheck() {

    }

    @Override
    public void runHealthCheck() {
        LDAPConnection ldc = null;

        try {
            ldc = createLdapConnection(Boolean.valueOf(ldapSecure));
        } catch (GeneralSecurityException e) {
            logger.error("Can't create LDAP client!");
        }

        InetAddress address = null;
        logger.debug("Ldap Address: " + ldapAddress);
        logger.debug("Ldap Index: " + index);

        if (Boolean.parseBoolean(binding))
            logger.debug("Ldap User: " + bindUser);

        try {
            address = InetAddress.getByName(ldapAddress);
        } catch (UnknownHostException e) {
            logger.error("Can't resolve ldap server address - " + ldapAddress);
        }

        if (address != null) {
            try {
                ldc.connect(address, ldapPort, ldapTimeout);

                if (Boolean.parseBoolean(binding)) {
                    logger.debug("Trying to bind to the - " + ldapAddress + " With user: " + bindUser);
                    ldc.bind(new SimpleBindRequest(bindUser, bindPassword));
                }

                logger.debug("Successfully connected to the LDAP server - " + ldapAddress + " port: " + ldapPort + " secure: " + ldapSecure);
            } catch (LDAPException e) {
                logger.error("Can't connected to the LDAP server. Check LDAP credentials - " + ldapAddress);
            }

            statusRepo.setStatus(ldc.isConnected(), index);
            ldc.close();

        } else {
            statusRepo.setStatus(false, index);
        }
    }

    private LDAPConnection createLdapConnection(boolean secure) throws GeneralSecurityException {

        LDAPConnection ldc = new LDAPConnection();
        logger.debug("Creating LDAP client. Secure: {}", secure);

        if (secure) {
            SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
            SSLSocketFactory sslSocketFactory = null;
            sslSocketFactory = sslUtil.createSSLSocketFactory();
            ldc = new LDAPConnection(sslSocketFactory);
        }

        return ldc;
    }
}
