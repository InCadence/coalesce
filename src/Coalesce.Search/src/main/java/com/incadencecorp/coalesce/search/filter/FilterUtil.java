/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.search.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.geotools.xsd.Configuration;
import org.geotools.xsd.Encoder;
import org.geotools.xsd.Parser;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * This utility is used for encoding Filters into XML strings.
 * 
 * @author n78554
 */
public final class FilterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterUtil.class);

    private FilterUtil()
    {
        // Do Nothing
    }

    /**
     * Defines the different usable configurations
     * 
     * @author n78554
     */
    public enum EConfiguration
    {

        /**
         * Version 2.0
         */
        V2_0,
        /**
         * Version 1.1
         */
        V1_1,
        /**
         * Version 1.0
         */
        V1_0,
        /**
         * Uses a Filter Transformer instead.
         */
        CUSTOM

    }

    /**
     * Encodes a Filter as a XML document.
     * 
     * @param configuration
     * @param filter
     * @return The filter as a string.
     * @throws TransformerException
     */
    public static String toXml(EConfiguration configuration, final Filter filter) throws TransformerException
    {

        String xml = null;

        if (filter != null)
        {
            try
            {
                switch (configuration) {
                case CUSTOM:
                    xml = new CoalesceFilterTransformer().transform(filter);
                    break;
                case V1_0:
                    xml = encode(new org.geotools.filter.v1_0.OGCConfiguration(),
                                 org.geotools.filter.v1_0.OGC.Filter,
                                 filter);
                    break;
                case V1_1:
                    xml = encode(new org.geotools.filter.v1_1.OGCConfiguration(),
                                 org.geotools.filter.v1_1.OGC.Filter,
                                 filter);
                    break;
                case V2_0:
                    xml = encode(new org.geotools.filter.v2_0.FESConfiguration(),
                                 org.geotools.filter.v2_0.FES.Filter,
                                 filter);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Configuration");
                }
            }
            catch (IOException e)
            {
                throw new TransformerException(e);
            }
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Filter: {}", xml);
        }

        return xml;

    }

    /**
     * @param xml
     * @return a Filter decoded from the provided XML document.
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Filter fromXml(String xml) throws SAXException, IOException, ParserConfigurationException
    {

        Filter filter = null;

        if (xml != null)
        {

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Filter: {}", xml);
            }

            Configuration configuration;

            // Version 2.0?
            if (xml.contains("http://www.opengis.net/fes/2.0"))
            {
                configuration = new org.geotools.filter.v2_0.FESConfiguration();
            }
            else
            {
                configuration = new org.geotools.filter.v1_1.OGCConfiguration();
            }

            Parser ogcParser = new Parser(configuration);

            Object result = ogcParser.parse(new ByteArrayInputStream(xml.getBytes()));

            if (result instanceof Filter)
            {
                filter = (Filter) result;
            }
            else
            {
                LOGGER.warn("\t\tFailed to parse filter");
            }
        }

        return filter;

    }

    private static String encode(Configuration configuration, QName name, Filter filter) throws IOException
    {

        String xml;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {

            Encoder encoder = new Encoder(configuration);
            encoder.encode(filter, name, out);

            xml = out.toString();
        }

        return xml;

    }

}
