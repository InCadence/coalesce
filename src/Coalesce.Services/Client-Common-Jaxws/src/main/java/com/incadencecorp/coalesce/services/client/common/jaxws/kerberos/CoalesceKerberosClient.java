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

package com.incadencecorp.coalesce.services.client.common.jaxws.kerberos;

import java.util.Map;

import org.apache.cxf.ws.security.kerberos.KerberosClient;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;

import com.incadencecorp.coalesce.framework.PropertyLoader;

/**
 * This extension allows additional parameters to be set for Kerberos. The order
 * in which you set the loader and properties determines which one will override
 * the other.
 * 
 * @author Derek Clemenzi
 */
public class CoalesceKerberosClient extends KerberosClient {

    /**
     * (String) Location of the krb5.conf file to use.
     */
    public static final String PROPERTY_KRB5_CONF_PATH = "omega.krb5.conf.path";

    /**
     * (String) Location of the login.conf file to use.
     */
    public static final String PROPERTY_KRB5_LOGIN_PATH = "omega.krb5.login.path";

    /**
     * (String) The login context within the login.conf file to use.
     */
    public static final String PROPERTY_KRB5_LOGIN_CONTEXT = "omega.krb5.login.context";

    /**
     * (Boolean) Whether to print debug messages.
     */
    public static final String PROPERTY_KRB5_IS_DEBUG = "omega.krb5.isdebug";

    /**
     * (String) Username to use to retrieve keys from the keytab. If not
     * specified it will use the account (SSO) running the process.
     */
    public static final String PROPERTY_KRB5_CLIENT_PRINCIPAL = "omega.krb5.principal.client";

    /**
     * (String) Service principal of the service you are connecting to. (ex:
     * HTTP/xmlp001.nmic.navy-lmdev.mil)
     */
    public static final String PROPERTY_KRB5_SERVICE_PRINCIPAL = "omega.krb5.principal.service";

    /**
     * (Boolean) Determines whether to append the host to the end of the
     * principals. Default is true.
     */
    public static final String PROPERTY_IS_USERNAME_FORM = "omega.krb5.isusernameform";

    private boolean isServiceNameUsernameForm = true;

    /**
     * Sets the clients properties.
     * 
     * @param properties
     */
    public final void setProperties(Map<String, String> properties)
    {
        if (properties.containsKey(PROPERTY_IS_USERNAME_FORM))
        {
            isServiceNameUsernameForm = Boolean.parseBoolean(properties.get(PROPERTY_IS_USERNAME_FORM));
        }

        if (properties.containsKey(PROPERTY_KRB5_IS_DEBUG))
        {
            System.setProperty("sun.security.krb5.debug", properties.get(PROPERTY_KRB5_IS_DEBUG));
        }

        if (properties.containsKey(PROPERTY_KRB5_CLIENT_PRINCIPAL))
        {
            System.setProperty("sun.security.krb5.principal", properties.get(PROPERTY_KRB5_CLIENT_PRINCIPAL));
        }

        if (properties.containsKey(PROPERTY_KRB5_LOGIN_PATH))
        {
            System.setProperty("java.security.auth.login.config", properties.get(PROPERTY_KRB5_LOGIN_PATH));
        }

        if (properties.containsKey(PROPERTY_KRB5_CONF_PATH))
        {
            System.setProperty("java.security.krb5.conf", properties.get(PROPERTY_KRB5_CONF_PATH));
        }

        if (properties.containsKey(PROPERTY_KRB5_SERVICE_PRINCIPAL))
        {
            setServiceName(properties.get(PROPERTY_KRB5_SERVICE_PRINCIPAL));
        }

        if (properties.containsKey(PROPERTY_KRB5_LOGIN_CONTEXT))
        {
            setContextName(properties.get(PROPERTY_KRB5_LOGIN_CONTEXT));
        }
    }

    /**
     * Sets a property loader for loading properties.
     * 
     * @param loader
     */
    public final void setPropertyLoader(PropertyLoader loader)
    {

        String value;

        value = loader.getProperty(PROPERTY_IS_USERNAME_FORM);

        if (value != null)
        {
            isServiceNameUsernameForm = Boolean.parseBoolean(value);
        }

        value = loader.getProperty(PROPERTY_KRB5_IS_DEBUG);

        if (value != null)
        {
            System.setProperty("sun.security.krb5.debug", value);
        }

        value = loader.getProperty(PROPERTY_KRB5_CLIENT_PRINCIPAL);

        if (value != null)
        {
            System.setProperty("sun.security.krb5.principal", value);
        }

        value = loader.getProperty(PROPERTY_KRB5_LOGIN_PATH);

        if (value != null)
        {
            System.setProperty("java.security.auth.login.config", value);
        }

        value = loader.getProperty(PROPERTY_KRB5_CONF_PATH);

        if (value != null)
        {
            System.setProperty("java.security.krb5.conf", value);
        }

        value = loader.getProperty(PROPERTY_KRB5_SERVICE_PRINCIPAL);

        if (value != null)
        {
            setServiceName(value);
        }

        value = loader.getProperty(PROPERTY_KRB5_LOGIN_CONTEXT);

        if (value != null)
        {
            setContextName(value);
        }

    }

    @Override
    public SecurityToken requestSecurityToken() throws Exception
    {

//        KerberosSecurity bst = new KerberosSecurity(DOMUtils.createDocument());
//        bst.retrieveServiceTicket(getContextName(), getCallbackHandler(), getServiceName(), isServiceNameUsernameForm);
//        bst.addWSUNamespace();
//        bst.setID(WSSConfig.getNewInstance().getIdAllocator().createSecureId("BST-", bst));
//
//        SecurityToken token = new SecurityToken(bst.getID());
//        token.setToken(bst.getElement());
//        token.setWsuId(bst.getID());
//        token.setData(bst.getToken());
//        SecretKey secretKey = bst.getSecretKey();
//
//        if (secretKey != null)
//        {
//            token.setKey(secretKey);
//            token.setSecret(secretKey.getEncoded());
//        }
//
//        String sha1 = Base64.encode(WSSecurityUtil.generateDigest(bst.getToken()));
//
//        token.setSHA1(sha1);
//        token.setTokenType(bst.getValueType());
//
//        return token;
        return null;
    }

}
