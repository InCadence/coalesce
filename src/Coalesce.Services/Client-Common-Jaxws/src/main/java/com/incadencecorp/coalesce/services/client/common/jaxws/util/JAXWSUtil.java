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

package com.incadencecorp.coalesce.services.client.common.jaxws.util;

import java.io.File;
import java.net.URL;
import java.security.Principal;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.ext.WSSecurityException.ErrorCode;
import org.apache.wss4j.common.principal.SAMLTokenPrincipal;
import org.apache.wss4j.common.principal.SAMLTokenPrincipalImpl;
import org.apache.wss4j.common.saml.SamlAssertionWrapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This utility class is a helper class for constructing clients and setting
 * security credentials.
 *
 * @author Derek Clemenzi
 */
public final class JAXWSUtil {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final Logger LOGGER = LoggerFactory.getLogger(JAXWSUtil.class);
    private static final String SAML_NS = "saml2";
    private static Bus bus;

    // TODO Pull these values from a property file.
    private static final String RENEWER_USERNAME = "";
    private static final String RENEWER_PASSWORD = "";
    private static final int EXPIRATION_THRESHOLD = 600;
    private static final String SPRING_CONFIG_PATH = "C:\\Users\\Derek/ Clemenzi\\Documents\\06-jcoalesce\\src\\Coalesce.Services\\CRUD\\client-jaxws\\src\\test\\resources\\wssec-client.xml"; // Paths.get("src",
                                                                                                                                                                                                // "test",
                                                                                                                                                                                                // "resources",
                                                                                                                                                                                                // "wssec-client.xml").toString();//"src\test\resources";
    private static final boolean SPRING_VALIDATION = true;

    /*--------------------------------------------------------------------------
    Default Constructor
    --------------------------------------------------------------------------*/

    private JAXWSUtil()
    {
        // Do Nothing
    }

    /*--------------------------------------------------------------------------
    Public Factory Methods
    --------------------------------------------------------------------------*/

    /**
     * Calls {@link #createClient(Class, URL, Bus)} passing <code>null</code>
     * for the bus implementation.
     *
     * @param clazz client implementation of the WSDL
     * @param url to the service WSDL
     * @return a JAXWS client targeting the WSDL.
     */
    public static <T> T createClient(Class<T> clazz, URL url)
    {
        return createClient(clazz, url, null);
    }

    /**
     * Creates a client using the specified bus that is used to as an argument
     * when calling other methods in this class.
     *
     * @param clazz client implementation of the WSDL
     * @param url to the service WSDL
     * @param bus if <code>null</code>; {@link #getBus()} is used to load a bus
     *            implementation.
     * @return a JAXWS client targeting the WSDL.
     */
    public static <T> T createClient(Class<T> clazz, URL url, Bus bus)
    {

        LOGGER.debug("Created {} Client (url = {})", clazz.getName(), url);

        if (bus == null)
        {
            bus = getBus();
        }

        // Create Factory
        final JaxWsProxyFactoryBean clientFactory;
        clientFactory = new JaxWsProxyFactoryBean();
        clientFactory.setBus(bus);
        clientFactory.setWsdlURL(url.toString());

        // Create Client
        return clientFactory.create(clazz);

    }

    /**
     * Creates a Security Token from a SAML assertion.
     *
     * @param assertion
     * @return {@link SecurityToken}
     * @throws WSSecurityException if there was an issue parsing the assertion.
     */
    public static SecurityToken createToken(Document assertion) throws WSSecurityException
    {

        Element assertionNode = getNode(assertion, SAML_NS + ":Assertion");
        Element conditionNode = getNode(assertion, SAML_NS + ":Conditions");

        if (assertionNode == null || conditionNode == null)
        {
            throw new IllegalArgumentException("Invalid Assertion");
        }

        String id = assertionNode.getAttribute("ID");

        Date created;
        Date expires;

        try
        {
            DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

            created = new Date(formatter.parseMillis(conditionNode.getAttribute("NotBefore")));
            expires = new Date(formatter.parseMillis(conditionNode.getAttribute("NotOnOrAfter")));

        }
        catch (IllegalArgumentException e)
        {
            throw new WSSecurityException(ErrorCode.INVALID_SECURITY_TOKEN, "Failed to find created / expire conditions");
        }

        return new SecurityToken(id, assertionNode, created, expires);

    }

    /**
     * Creates a SecurityToken using the STS.
     * 
     * @param username if using Kerberos this is ignored and the keytab is used
     *            instead.
     * @param password
     * @return creates a token for the provided user.
     * @throws WSSecurityException
     */
    public static SecurityToken createToken(String username, String password) throws WSSecurityException
    {
        return createToken((Element) null, username, password);
    }

    /**
     * Defaults the user's name to {@link #RENEWER_USERNAME} and the password to
     * {@link #RENEWER_PASSWORD}.
     * 
     * @param token
     * @return {@link #renewToken(SecurityToken, String, String)}
     * @throws WSSecurityException
     */
    public static SecurityToken renewToken(SecurityToken token) throws WSSecurityException
    {
        return renewToken(token, JAXWSUtil.RENEWER_USERNAME, JAXWSUtil.RENEWER_PASSWORD);
    }

    /**
     * If token will expire in {@link #EXPIRATION_THRESHOLD} seconds this method
     * will call {@link #createOnBehalfOfToken(SecurityToken, String, String)}
     * to create a new token on behalf of the original; otherwise the original
     * is returned.
     * 
     * @param token
     * @param username must have permissions to create token on behalf of
     *            another user. If using Kerberos this is ignored and the keytab
     *            is used instead.
     * @param password
     * @return a non-expired token.
     * @throws WSSecurityException
     */
    public static SecurityToken renewToken(SecurityToken token, String username, String password) throws WSSecurityException
    {
        if (token.isExpired()
                || new DateTime(DateTimeZone.UTC).plusSeconds(EXPIRATION_THRESHOLD).isAfter(token.getExpires().getTime()))
        {
            token = createToken(token.getToken(), username, password);
        }

        return token;
    }

    /**
     * Defaults the user's name to {@link #RENEWER_USERNAME} and the password to
     * {@link #RENEWER_PASSWORD}.
     * 
     * @param principal
     * @return {@link #renewPrincipal(Principal, String, String)}
     * @throws WSSecurityException
     */
    public static Principal renewPrincipal(Principal principal) throws WSSecurityException
    {
        return renewPrincipal(principal, JAXWSUtil.RENEWER_USERNAME, JAXWSUtil.RENEWER_PASSWORD);
    }

    /**
     * If token will expire in {@link #EXPIRATION_THRESHOLD} seconds this method
     * will call {@link #renewToken(SecurityToken, String, String)} to create a
     * new token on behalf of the original; otherwise the original is returned.
     * 
     * @param principal
     * @param username must have permissions to create token on behalf of
     *            another user. If using Kerberos this is ignored and the keytab
     *            is used instead.
     * @param password
     * @return a non-expired token.
     * @throws WSSecurityException
     */
    public static Principal renewPrincipal(Principal principal, String username, String password) throws WSSecurityException
    {
        Principal result;

        if (principal instanceof SAMLTokenPrincipal)
        {
            SecurityToken token = JAXWSUtil.createToken(((SAMLTokenPrincipal) principal).getToken().getElement().getOwnerDocument());

            token = JAXWSUtil.renewToken(token, username, password);

            result = new SAMLTokenPrincipalImpl(new SamlAssertionWrapper(token.getToken()));

        }
        else
        {
            result = principal;
        }

        return result;
    }

    /**
     * Defaults the user's name to {@link #RENEWER_USERNAME} and the password to
     * {@link #RENEWER_PASSWORD}.
     * 
     * @param onBehalfOf
     * @return {@link #renewToken(SecurityToken, String, String)}
     * @throws WSSecurityException
     */
    public static SecurityToken createOnBehalfOfToken(SecurityToken onBehalfOf) throws WSSecurityException
    {
        return createOnBehalfOfToken(onBehalfOf, JAXWSUtil.RENEWER_USERNAME, JAXWSUtil.RENEWER_PASSWORD);
    }

    /**
     * Creates a SecurityToken using the STS on the behalf of another user.
     * 
     * @param onBehalfOf SAML assertion of the user to create the token for.
     *            This assertion can be expired.
     * @param username must have permissions to create token on behalf of
     *            another user. If using Kerberos this is ignored and the keytab
     *            is used instead.
     * @param password
     * @return creates a token on behalf of another token through the STS.
     * @throws WSSecurityException
     */
    public static SecurityToken createOnBehalfOfToken(SecurityToken onBehalfOf, String username, String password)
            throws WSSecurityException
    {
        return createToken(onBehalfOf.getToken(), username, password);
    }

    /**
     * Defaults the user's name to {@link #RENEWER_USERNAME} and
     * the password to {@link #RENEWER_PASSWORD}.
     * 
     * @param onBehalfOf
     * @return {@link #renewToken(SecurityToken, String, String)}
     * @throws WSSecurityException
     * @throws ParserConfigurationException
     */
    public static SecurityToken createOnBehalfOfToken(String onBehalfOf)
            throws WSSecurityException, ParserConfigurationException
    {
        return createOnBehalfOfToken(onBehalfOf, JAXWSUtil.RENEWER_USERNAME, JAXWSUtil.RENEWER_PASSWORD);
    }

    /**
     * Creates a SecurityToken using the STS on the behalf of another user.
     * 
     * @param onBehalfOf of the user to create the token for.
     * @param username must have permissions to create token on behalf of
     *            another user. If using Kerberos this is ignored and the keytab
     *            is used instead.
     * @param password
     * @return creates a token on behalf of the specified user's id.
     * @throws WSSecurityException
     * @throws ParserConfigurationException
     */
    public static SecurityToken createOnBehalfOfToken(String onBehalfOf, String username, String password)
            throws WSSecurityException, ParserConfigurationException
    {
        // Create OnBehalfOf Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().newDocument();

        Element usernameTokenElement = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                                                           "UsernameToken");
        Element usernameElement = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                                                      "Username");

        usernameElement.setPrefix("wsse");
        usernameElement.setTextContent(onBehalfOf);

        usernameTokenElement.setPrefix("wsse");
        usernameTokenElement.appendChild(usernameElement);

        return createToken(usernameTokenElement, username, password);
    }

    private static SecurityToken createToken(Element onBehalfOf, String username, String password) throws WSSecurityException
    {
        // Create STS
        STSClient sts = new STSClient(getBus());

        sts.getProperties().put(SecurityConstants.USERNAME, username);
        sts.getProperties().put(SecurityConstants.PASSWORD, password);

        if (onBehalfOf != null)
        {
            sts.setOnBehalfOf(onBehalfOf);
        }

        // Request Token
        try
        {
            return sts.requestSecurityToken();
        }
        catch (Exception e)
        {
            throw new WSSecurityException(ErrorCode.FAILED_AUTHENTICATION, e);
        }
    }

    /**
     * Sets the credentials to use for authenticating with the service.
     *
     * @param client client created from {@link #createClient(Class, URL)}.
     * @param userId if using Kerberos this is ignored and the keytab is used
     *            instead.
     * @param password
     * @deprecated Not thread safe
     */
    public static void setCredentials(Object client, String userId, String password)
    {
        LOGGER.debug("Set Credentials (userId = {}, password = ****)", userId);

        // Retrieve CXF Client
        final Client proxyClient = ClientProxy.getClient(client);
        final Endpoint ep = proxyClient.getEndpoint();
        final STSClient sts = (STSClient) ep.get(SecurityConstants.STS_CLIENT);

        if (sts != null)
        {

            final Map<String, Object> stsProps = sts.getProperties();
            final Map<String, Object> clientProps = proxyClient.getRequestContext();

            // Clear Existing Credentials
            clearCredentials(sts, proxyClient, ep);

            // Set Username
            clientProps.put(SecurityConstants.USERNAME, userId);
            stsProps.put(SecurityConstants.USERNAME, userId);

            if (password != null)
            {
                // Set Password
                clientProps.put(SecurityConstants.PASSWORD, password);
                stsProps.put(SecurityConstants.PASSWORD, password);
            }
        }
    }

    /**
     * Sets the credentials to use for authenticating with the service.
     *
     * @param client client created from {@link #createClient(Class, URL)}.
     * @param principal
     * @return the user ID associated with the token.
     * @throws WSSecurityException
     */
    public static String setCredentials(Object client, Principal principal) throws WSSecurityException
    {
        String userId;

        if (principal != null && principal instanceof SAMLTokenPrincipal)
        {

            // Get Token
            SecurityToken token = JAXWSUtil.createToken(((SAMLTokenPrincipal) principal).getToken().getElement().getOwnerDocument());

            userId = setCredentials(client, token);

        }
        else
        {
            throw new WSSecurityException(ErrorCode.UNSUPPORTED_SECURITY_TOKEN, "Expected a SAMLTokenPrincipal");
        }

        return userId;
    }

    /**
     * Sets the credentials to use for authenticating with the service.
     *
     * @param client client created from {@link #createClient(Class, URL)}.
     * @param token
     * @return the user ID associated with the token.
     * @throws WSSecurityException
     */
    public static String setCredentials(Object client, SecurityToken token) throws WSSecurityException
    {
        // Determine Token's User ID
        String userId = getUserId(token);

        LOGGER.debug("Set Credentials (userId = {}, Token ID = {})", userId, token.getId());

        // Retrieve CXF Client
        final Client proxyClient = ClientProxy.getClient(client);
        final Endpoint ep = proxyClient.getEndpoint();
        final STSClient sts = (STSClient) ep.get(SecurityConstants.STS_CLIENT);

        // Clear Existing Credentials
        clearCredentials(sts, proxyClient, ep);

        // Set the Endpoint's Security Token
        ep.put(SecurityConstants.TOKEN, token);
        ep.put(SecurityConstants.TOKEN_ID, token.getId());

        return userId;
    }

    /**
     * Clears the user's credentials.
     *
     * @param client client created from {@link #createClient(Class, URL)}.
     */
    public static void clearCredentials(Object client)
    {
        LOGGER.debug("Clearing Credentials");

        // Retrieve CXF Client
        final Client proxyClient = ClientProxy.getClient(client);
        final Endpoint ep = proxyClient.getEndpoint();
        final STSClient sts = (STSClient) ep.get(SecurityConstants.STS_CLIENT);

        clearCredentials(sts, proxyClient, ep);
    }

    /**
     * Attempts to use dependency injection to load the {@link Bus} from an OSGi
     * container. If running outside of Karaf or a Bus is not provided by the
     * container it will use the XXXXX file to create the
     * Bus from a Spring configuration file.
     *
     * @return <code>null</code> if there is no bus configured otherwise the bus
     *         to be used by the clients.
     */
    public static Bus getBus()
    {
        // Already Initialized?
        if (bus == null)
        {
            // No; Create from OSGI
            try
            {
                bus = getBusFromOSGI();
            }
            catch (InvalidSyntaxException e)
            {
                LOGGER.error("Failed to load Bus from container", e);
            }

            // Created?
            if (bus == null)
            {
                // No; Create from Configuration
                bus = getBusFromConfiguration();
            }

            // Created?
            if (bus == null)
            {
                // Throw Error
                throw new IllegalStateException("Bus NOT Initialized");
            }
        }

        return bus;
    }

    /**
     * Creates a principle from a provided token.
     *
     * @param token
     * @return a SAML Token Principal that can be used to authenticate.
     * @throws WSSecurityException
     */
    public static SAMLTokenPrincipal createPrincipal(SecurityToken token) throws WSSecurityException
    {
        return new SAMLTokenPrincipalImpl(new SamlAssertionWrapper(token.getToken()));
    }

    /**
     * Creates a principle from the provided credentials using an STS and the
     * renewer credentials.
     * 
     * @return a SAML Token Principal that can be used to authenticate.
     * @throws WSSecurityException
     */
    public static SAMLTokenPrincipal createPrincipal() throws WSSecurityException
    {
        return createPrincipal(createToken(JAXWSUtil.RENEWER_USERNAME, JAXWSUtil.RENEWER_PASSWORD));
    }

    /**
     * Creates a principle from the provided credentials using an STS.
     * 
     * @param username
     * @param password
     * @return a SAML Token Principal that can be used to authenticate.
     * @throws WSSecurityException
     */
    public static SAMLTokenPrincipal createPrincipal(String username, String password) throws WSSecurityException
    {
        return createPrincipal(createToken(username, password));
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private static void clearCredentials(STSClient sts, Client client, Endpoint ep)
    {
        // Clear Client
        if (client != null)
        {
            final Map<String, Object> clientProps = client.getRequestContext();
            clientProps.remove(SecurityConstants.USERNAME);
            // Removing the property for some reason does not actually clear the
            // password.
            clientProps.put(SecurityConstants.PASSWORD, "");
        }

        // Clear STS
        if (sts != null)
        {
            final Map<String, Object> stsProps = sts.getProperties();
            stsProps.remove(SecurityConstants.USERNAME);
            stsProps.put(SecurityConstants.PASSWORD, "");
        }

        // Clear the Endpoint's Security Token
        if (ep != null)
        {
            ep.remove(SecurityConstants.TOKEN);
            ep.remove(SecurityConstants.TOKEN_ID);
        }
    }

    /**
     * @return a bus using dependency injection in an OSGi environment.
     * @throws InvalidSyntaxException
     */
    private static Bus getBusFromOSGI() throws InvalidSyntaxException
    {
        Bus bus = null;

        // Retrieve Bundle Information
        Bundle bundle = FrameworkUtil.getBundle(JAXWSUtil.class);

        // Is OSGI Environment?
        if (bundle != null)
        {

            // Yes; Get Context
            BundleContext bc = bundle.getBundleContext();

            ServiceReference[] refs = null;

            // Get Service References
            refs = bc.getServiceReferences(Bus.class.getName(), null);

            if (refs != null && refs.length > 0)
            {

                switch (refs.length) {
                case 0:
                    LOGGER.warn("No Spring Bus Found");
                    break;
                default:
                case 1:

                    LOGGER.warn("Busses Found:");

                    String configName = getConfigName();

                    for (ServiceReference ref : refs)
                    {

                        Bus serviceBus = (Bus) bc.getService(ref);
                        String busId = getSpringBusID(serviceBus);

                        if (busId.equalsIgnoreCase(configName))
                        {
                            bus = (Bus) bc.getService(ref);
                            LOGGER.info("USING {} from {}", serviceBus.getId(), ref.getBundle().getBundleId());
                        }
                        else
                        {
                            LOGGER.info("{} from {}", serviceBus.getId(), ref.getBundle().getBundleId());
                        }

                    }
                }
            }
        }

        return bus;
    }

    private static String getConfigName()
    {
        String filename = SPRING_CONFIG_PATH;

        int idx = filename.lastIndexOf(File.separatorChar);

        if (idx != -1)
        {
            filename = filename.substring(idx + 1);
        }

        return filename;
    }

    private static String getSpringBusID(Bus bus)
    {

        String id = bus.getId();

        int idx = id.lastIndexOf("-");

        if (idx != -1)
        {
            id = id.substring(0, idx);
        }

        return id;
    }

    private static Bus getBusFromConfiguration()
    {

        Bus bus = null;

        // Read Full Path from Configuration File
        String path = SPRING_CONFIG_PATH;

        // Validation On?
        if (SPRING_VALIDATION)
        {
            // Yes; Create Bus
            final SpringBusFactory bf = new SpringBusFactory();

            try
            {
                bus = bf.createBus(path);
            }
            catch (Exception e)
            {
                bus = bf.createBus();
            }
        }
        else
        {

            // No; Disable Validation
            GenericXmlApplicationContext context = new GenericXmlApplicationContext();
            context.setValidating(false);
            context.load(path);

            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
            reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
            reader.loadBeanDefinitions(path);
            context.refresh();

            // Create Bus
            bus = new SpringBusFactory(context).createBus();
        }

        return bus;
    }

    private static Element getNode(Document node, String name) throws WSSecurityException
    {
        NodeList list = node.getElementsByTagName(name);
        Node result = null;

        switch (list.getLength()) {
        case 1:
            result = list.item(0);
            break;
        case 0:
            throw new WSSecurityException(ErrorCode.INVALID_SECURITY_TOKEN, "Failed to find node: " + name);
        default:
            throw new WSSecurityException(ErrorCode.INVALID_SECURITY_TOKEN, "Multiple nodes found: " + name);

        }
        return (Element) result;
    }

    private static String getUserId(SecurityToken token) throws WSSecurityException
    {
        Node result = getNode(token.getToken().getOwnerDocument(), SAML_NS + ":NameID");
        return result.getTextContent();
    }

}
