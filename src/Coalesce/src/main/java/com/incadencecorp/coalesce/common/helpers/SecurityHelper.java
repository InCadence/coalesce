/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.common.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHelper.class);

    private static KeyManager[] getKeyManagers(Path keystore, String password) throws IOException, GeneralSecurityException
    {
        final String alg = KeyManagerFactory.getDefaultAlgorithm();
        final KeyManagerFactory kmfact = KeyManagerFactory.getInstance(alg);

        LOGGER.debug("Default Algorithm: {}", alg);

        kmfact.init(getKeystore(keystore, password), password.toCharArray());

        return kmfact.getKeyManagers();
    }

    private static TrustManager[] getTrustManagers(Path keystore, String password) throws IOException,
            GeneralSecurityException
    {
        final String alg = TrustManagerFactory.getDefaultAlgorithm();
        final TrustManagerFactory tmfact = TrustManagerFactory.getInstance(alg);

        LOGGER.debug("Default Algorithm: {}", alg);

        tmfact.init(getKeystore(keystore, password));

        return tmfact.getTrustManagers();
    }

    public static KeyStore getKeystore(Path keystore, String password) throws IOException, GeneralSecurityException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Keystore: {}", keystore.toUri().toString());
        }

        final KeyStore ks = KeyStore.getInstance("jks");
        try (FileInputStream fis = new FileInputStream(keystore.toUri().toString()))
        {
            ks.load(fis, password.toCharArray());
        }

        return ks;
    }
    
    // public static CloseableHttpClient getSSLHttpClient() {
    //
    // try {
    // trustManagers = getTrustManagers(GSASettings.getKeystorePwd());
    // keyManagers = getKeyManagers(GSASettings.getKeystorePwd());
    // } catch (IOException | GeneralSecurityException e) {
    // LOGGER.error("Error while loading the trust manager and key managers",
    // e);
    // }
    //
    // SSLContext context =
    // SSLContext.getInstance(GSASettings.getSSLProtocol());
    // context.init(keyManagers, trustManagers, null);
    //
    // SSLConnectionSocketFactory sslsf = new
    // SSLConnectionSocketFactory(context);
    // httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
    //
    // }
    //
    // public static getKerberosHttpClient() {
    //
    // }

}
