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

package com.incadencecorp.coalesce.exim.xsd;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.CoalesceAttributes;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;

/**
 * This utility class creates a XSD schema from a {@link CoalesceEntityTemplate}
 * . If the template contains {@link ECoalesceFieldDataTypes#ENUMERATION_TYPE}
 * then you must initialize {@link EnumerationProviderUtil}. .
 * 
 * TODO Common elements should be imported from a common schema
 * 
 * @author Derek C.
 * @since Coalesce 0.0.16-SNAPSHOT
 */
public final class XSDGeneratorUtil {

    private static final int MAX_STRING_SIZE = XSDSettings.getMaxString();
    private static final int MAX_POINT = XSDSettings.getMaxPoints();

    /*--------------------------------------------------------------------------
    Regular Expressions
    --------------------------------------------------------------------------*/

    private static final String REGEX_UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final String REGEX_AXIS = "-?([0-9]*\\.)?[0-9]+";
    private static final String REGEX_COORD = "\\(?" + REGEX_AXIS + " " + REGEX_AXIS + " (" + REGEX_AXIS + ")?\\)?";
    private static final String REGEX_MULITPLE = " \\((" + REGEX_COORD + "(, )?){0," + MAX_POINT + "}\\)";
    private static final String REGEX_POINT = "POINT " + REGEX_COORD;
    private static final String REGEX_MULTIPOINT = "MULTIPOINT" + REGEX_MULITPLE;
    private static final String REGEX_POLYGON = "POLYGON" + REGEX_MULITPLE;
    private static final String REGEX_LINESTRING = "LINESTRING" + REGEX_MULITPLE;

    /*--------------------------------------------------------------------------
    Attribute Names
    --------------------------------------------------------------------------*/

    private static final String ATTRIBUTE_REF = "ref";
    private static final String ATTRIBUTE_BASE = "base";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_FIXED = "fixed";
    private static final String ATTRIBUTE_USE = "use";
    private static final String ATTRIBUTE_DEFAULT = "default";
    private static final String ATTRIBUTE_MIN_OCCURS = "minOccurs";
    private static final String ATTRIBUTE_MAX_OCCURS = "maxOccurs";

    /*--------------------------------------------------------------------------
    Name Spaces
    --------------------------------------------------------------------------*/

    private static final String NS_TARGET = "tns";
    private static final String NS_XML = "xs";
    private static final String NS_XML_URI = "http://www.w3.org/2001/XMLSchema";

    /*--------------------------------------------------------------------------
    Element Types
    --------------------------------------------------------------------------*/

    private static final String COMPLEX_TYPE = "complexType";
    private static final String SIMPLE_TYPE = "simpleType";
    private static final String ELEMENT_TYPE = "element";

    /*--------------------------------------------------------------------------
    Data Types
    --------------------------------------------------------------------------*/

    private static final String CDS_STRING_TYPE = "cdsString";
    private static final String CDS_STRING_TYPE_NS = NS_TARGET + ":" + CDS_STRING_TYPE;

    private static final String STRING_TYPE = "string";
    private static final String STRING_TYPE_NS = NS_XML + ":" + STRING_TYPE;

    private static final String LINKAGE_TYPE = CoalesceLinkageSection.NAME;
    private static final String LINKAGE_TYPE_NS = NS_TARGET + ":" + LINKAGE_TYPE;

    private static final String UUID_TYPE = ECoalesceFieldDataTypes.GUID_TYPE.getLabel();
    private static final String UUID_TYPE_NS = NS_TARGET + ":" + UUID_TYPE;

    private static final String POINT_TYPE = ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE.getLabel();
    private static final String POINT_TYPE_NS = NS_TARGET + ":" + POINT_TYPE;

    private static final String MULTIPOINT_TYPE = ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE.getLabel();
    private static final String MULTIPOINT_TYPE_NS = NS_TARGET + ":" + MULTIPOINT_TYPE;

    private static final String CIRCLE_TYPE = ECoalesceFieldDataTypes.CIRCLE_TYPE.getLabel();
    private static final String CIRCLE_TYPE_NS = NS_TARGET + ":" + CIRCLE_TYPE;

    private static final String POLYGON_TYPE = ECoalesceFieldDataTypes.POLYGON_TYPE.getLabel();
    private static final String POLYGON_TYPE_NS = NS_TARGET + ":" + POLYGON_TYPE;

    private static final String LINESTRING_TYPE = ECoalesceFieldDataTypes.LINE_STRING_TYPE.getLabel();
    private static final String LINESTRING_TYPE_NS = NS_TARGET + ":" + LINESTRING_TYPE;

    private static final String STATUS_TYPE = ECoalesceObjectStatus.class.getSimpleName();
    private static final String STATUS_TYPE_NS = NS_TARGET + ":" + STATUS_TYPE;

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final String UNBOUNDED = Integer.toString(5000);

    private static final List<String> ATTRIBUTES_TO_OMIT = Collections.singletonList(CoalesceEntity.ATTRIBUTE_CLASSNAME);

    private static final Logger LOGGER = LoggerFactory.getLogger(XSDGeneratorUtil.class);

    /*--------------------------------------------------------------------------
    Default Constructor
    --------------------------------------------------------------------------*/

    private XSDGeneratorUtil()
    {
        // Do Nothing
    }

    /*--------------------------------------------------------------------------
    Public Utility Methods
    --------------------------------------------------------------------------*/

    /**
     * @param principal
     * @param template
     * @return an XSD representation of the template which can be used by this
     *         implementation to convert between XSD and Coalesce versions of
     *         the object.
     * @throws ParserConfigurationException
     */
    public static Document createXsd(Principal principal, CoalesceEntityTemplate template)
            throws ParserConfigurationException
    {
        CoalesceEntity entity = template.createNewEntity();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document doc = factory.newDocumentBuilder().newDocument();

        Element element = doc.createElementNS(NS_XML_URI, "schema");
        element.setPrefix(NS_XML);
        doc.appendChild(element);

        String nameSpace = createNameSpace(template.getClassName(), template.getName());

        element.setAttribute("targetNamespace", nameSpace);
        element.setAttribute("xmlns:" + NS_TARGET, nameSpace);
        element.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
        element.setAttribute("elementFormDefault", "qualified");
        element.setAttribute("attributeFormDefault", "qualified");
        element.setAttribute("jxb:version", "2.1");
        element.setAttribute("version", template.getVersion());

        createComplexType(principal, doc, entity);

        try
        {
            // TODO Something with how the Document is created prevents it from
            // being used as a DOMSource. Serializing it and Deserializing it
            // appears to resolve the issue at a performance hit. It would be
            // nice to remove this step.
            return XmlHelper.loadXmlFrom(XmlHelper.formatXml(doc));
        }
        catch (SAXException | IOException e)
        {
            throw new ParserConfigurationException(e.getMessage());
        }

    }

    /**
     * Validates the XML against a schema and throws an exception on an error.
     * 
     * @param xsd
     * @param xml
     * @throws IllegalArgumentException if the XML is invalid
     */
    public static void validateXMLSchema(Source xsd, Source xml) throws IllegalArgumentException
    {
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(xml);
        }
        catch (SAXException | IOException e)
        {
            throw new IllegalArgumentException("Invalid Entity XML", e);
        }
    }

    /**
     * Validates the XML against a schema and throws an exception on an error.
     * 
     * @param xsdPath
     * @param xml
     * @throws IllegalArgumentException if the XML is invalid
     */
    public static void validateXMLSchema(String xsdPath, Source xml) throws IllegalArgumentException
    {
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(xml);
        }
        catch (SAXException | IOException e)
        {
            throw new IllegalArgumentException("Invalid Entity XML", e);
        }
    }

    /**
     * @param className
     * @param name
     * @return a namespace for the given classname.
     */
    public static String createNameSpace(String className, String name)
    {
        StringBuilder sb = new StringBuilder();

        // ClassName Specified?
        if (className != null)
        {
            // Yes; Valid?
            String[] parts = className.split("[.]");

            if (parts.length > 1)
            {
                sb.append("http://");

                // Reverse Package Name
                for (int ii = parts.length - 2; ii >= 0; ii--)
                {

                    if (ii != parts.length - 2)
                    {
                        sb.append(".");
                    }

                    sb.append(parts[ii]);
                }

                sb.append("/" + parts[parts.length - 1]);
            }

        }

        // Namespace Created?
        if (sb.length() == 0)
        {
            // No; Use Default
            sb.append("http://schema.coalesce.incadencecorp.com/" + normalize(name));
        }

        return sb.toString();
    }

    /**
     * @param name
     * @return a normalized name that can be used to XSDs.
     */
    public static String normalize(String name)
    {
        return name.replace(" ", "");
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private static void createBaseElements(Document doc)
    {
        doc.getFirstChild().appendChild(createEnumeration(doc, ECoalesceObjectStatus.class));
        doc.getFirstChild().appendChild(createEnumeration(doc, ELinkTypes.class));
        doc.getFirstChild().appendChild(createBaseType(doc));
        doc.getFirstChild().appendChild(createBaseStringType(doc));

        addDataType(doc, UUID_TYPE, REGEX_UUID, 36);
        addDataType(doc, POINT_TYPE, REGEX_POINT);
        addDataType(doc, MULTIPOINT_TYPE, REGEX_MULTIPOINT);
        addDataType(doc, POLYGON_TYPE, REGEX_POLYGON);
        addDataType(doc, LINESTRING_TYPE, REGEX_LINESTRING);
        addCircleType(doc);

        createComplexTypeLinkage(doc);
    }

    private static <E extends Enum<E>> Element createEnumeration(Document doc, Class<E> clazz)
    {
        Element element = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
        element.setPrefix(NS_XML);
        element.setAttribute(ATTRIBUTE_NAME, clazz.getSimpleName());

        Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
        restriction.setPrefix(NS_XML);
        restriction.setAttribute(ATTRIBUTE_BASE, STRING_TYPE_NS);
        element.appendChild(restriction);

        for (E e : EnumSet.allOf(clazz))
        {
            Element enumeration = doc.createElementNS(NS_XML_URI, "enumeration");
            enumeration.setPrefix(NS_XML);
            enumeration.setAttribute(ATTRIBUTE_VALUE, e.toString());
            restriction.appendChild(enumeration);
        }

        return element;
    }

    private static Element createBaseType(Document doc)
    {
        Element type = doc.createElementNS(NS_XML_URI, COMPLEX_TYPE);
        type.setPrefix(NS_XML);
        type.setAttribute(ATTRIBUTE_NAME, "baseType");

        Element attribute;

        // Entity Key
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_KEY);
        attribute.setAttribute(ATTRIBUTE_TYPE, UUID_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_USE, "required");
        type.appendChild(attribute);

        // Date Created
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_DATECREATED);
        attribute.setAttribute(ATTRIBUTE_TYPE, NS_XML + ":dateTime");
        type.appendChild(attribute);

        // Last Modified
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_LASTMODIFIED);
        attribute.setAttribute(ATTRIBUTE_TYPE, NS_XML + ":dateTime");
        type.appendChild(attribute);

        // Status
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_STATUS);
        attribute.setAttribute(ATTRIBUTE_TYPE, STATUS_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_DEFAULT, ECoalesceObjectStatus.ACTIVE.toString());
        type.appendChild(attribute);

        return type;
    }

    private static Element createBaseStringType(Document doc)
    {
        // Create String Type
        Element type = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
        type.setPrefix(NS_XML);
        type.setAttribute(ATTRIBUTE_NAME, CDS_STRING_TYPE);
        doc.getFirstChild().appendChild(type);

        Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
        restriction.setPrefix(NS_XML);
        restriction.setAttribute(ATTRIBUTE_BASE, STRING_TYPE_NS);
        type.appendChild(restriction);

        Element pattern = doc.createElementNS(NS_XML_URI, "maxLength");
        pattern.setPrefix(NS_XML);
        pattern.setAttribute(ATTRIBUTE_VALUE, Integer.toString(MAX_STRING_SIZE));

        restriction.appendChild(pattern);

        return type;
    }

    private static void addDataType(Document doc, String name, String regex)
    {
        addDataType(doc, name, regex, 0);
    }

    private static void addDataType(Document doc, String name, String regex, int maxlength)
    {
        Element type = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
        type.setPrefix(NS_XML);
        type.setAttribute(ATTRIBUTE_NAME, name);

        Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
        restriction.setPrefix(NS_XML);
        restriction.setAttribute(ATTRIBUTE_BASE, STRING_TYPE_NS);

        createRestrictionRegEx(doc, restriction, regex);

        if (maxlength != 0)
        {
            Element pattern = doc.createElementNS(NS_XML_URI, "maxLength");
            pattern.setPrefix(NS_XML);
            pattern.setAttribute(ATTRIBUTE_VALUE, Integer.toString(maxlength));

            restriction.appendChild(pattern);
        }

        type.appendChild(restriction);

        doc.getFirstChild().appendChild(type);
    }

    private static void addCircleType(Document doc)
    {
        Element type = doc.createElementNS(NS_XML_URI, COMPLEX_TYPE);
        type.setPrefix(NS_XML);
        type.setAttribute(ATTRIBUTE_NAME, CIRCLE_TYPE);

        Element sequence = doc.createElementNS(NS_XML_URI, "sequence");
        sequence.setPrefix(NS_XML);

        Element center = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        center.setPrefix(NS_XML);
        center.setAttribute(ATTRIBUTE_NAME, "center");
        center.setAttribute(ATTRIBUTE_TYPE, getXSDType(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE));

        Element radius = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        radius.setPrefix(NS_XML);
        radius.setAttribute(ATTRIBUTE_NAME, CoalesceCircleField.ATTRIBUTE_RADIUS);
        radius.setAttribute(ATTRIBUTE_TYPE, getXSDType(ECoalesceFieldDataTypes.DOUBLE_TYPE));

        sequence.appendChild(center);
        sequence.appendChild(radius);

        type.appendChild(sequence);
        doc.getFirstChild().appendChild(type);
    }

    private static void createComplexTypeLinkage(Document doc)
    {
        Element sequence = createBaseComplexType(doc, null, CoalesceLinkageSection.NAME, "sequence");

        Element linkageSequence = createBaseComplexType(doc, sequence, CoalesceLinkage.NAME, "all");

        Element linkage = (Element) sequence.getFirstChild();
        linkage.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
        linkage.setAttribute(ATTRIBUTE_MAX_OCCURS, UNBOUNDED);

        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY1KEY, UUID_TYPE_NS);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY1NAME, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY1SOURCE, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY1VERSION, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc,
                            linkageSequence,
                            CoalesceLinkage.ATTRIBUTE_LINKTYPE,
                            NS_TARGET + ":" + ELinkTypes.class.getSimpleName());
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY2KEY, UUID_TYPE_NS);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY2NAME, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY2SOURCE, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY2VERSION, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_ENTITY2OBJECTVERSION, NS_XML + ":int");
        createSimpleElement(doc, linkageSequence, CoalesceAttributes.ATTRIBUTE_MARKING, CDS_STRING_TYPE_NS, true);
        createSimpleElement(doc, linkageSequence, CoalesceLinkage.ATTRIBUTE_LABEL, CDS_STRING_TYPE_NS, true);
    }

    private static void createSimpleElement(Document doc, Element node, String name, String type)
    {
        createSimpleElement(doc, node, name, type, false);
    }

    private static void createSimpleElement(Document doc, Element node, String name, String type, boolean isOptional)
    {
        Element element;
        element = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        element.setPrefix(NS_XML);
        element.setAttribute(ATTRIBUTE_NAME, name);
        element.setAttribute(ATTRIBUTE_TYPE, type);

        if (isOptional)
        {
            element.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
        }

        node.appendChild(element);
    }

    private static void createComplexType(Principal principal, Document doc, CoalesceEntity entity)
    {
        Element sequence = createBaseComplexType(doc, entity.getName());

        // Reference Linkage Section
        Element linkage = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        linkage.setPrefix(NS_XML);
        linkage.setAttribute(ATTRIBUTE_REF, LINKAGE_TYPE_NS);
        linkage.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
        linkage.setAttribute(ATTRIBUTE_MAX_OCCURS, "1");

        sequence.appendChild(linkage);

        for (CoalesceSection section : entity.getSectionsAsList())
        {
            createComplexType(principal, doc, section);

            Element reference = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
            reference.setPrefix(NS_XML);
            reference.setAttribute(ATTRIBUTE_REF, NS_TARGET + ":" + normalize(section.getName()));
            reference.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
            reference.setAttribute(ATTRIBUTE_MAX_OCCURS, "1");

            sequence.appendChild(reference);

        }

        Element attribute;

        // Name
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_NAME);
        attribute.setAttribute(ATTRIBUTE_TYPE, STRING_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_FIXED, entity.getName());
        sequence.getParentNode().appendChild(attribute);

        // Source
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_SOURCE);
        attribute.setAttribute(ATTRIBUTE_TYPE, STRING_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_FIXED, entity.getSource());
        sequence.getParentNode().appendChild(attribute);

        // Version
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_VERSION);
        attribute.setAttribute(ATTRIBUTE_TYPE, STRING_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_FIXED, entity.getVersion());
        sequence.getParentNode().appendChild(attribute);

        // Entity ID
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_ENTITYID);
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        sequence.getParentNode().appendChild(attribute);

        // Entity ID Type
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_ENTITYIDTYPE);
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        sequence.getParentNode().appendChild(attribute);

        // Title
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, CoalesceEntity.ATTRIBUTE_TITLE);
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        sequence.getParentNode().appendChild(attribute);

        for (QName qname : entity.getOtherAttributes().keySet())
        {
            if (!ATTRIBUTES_TO_OMIT.contains(qname.getLocalPart()))
            {
                // Type
                attribute = doc.createElementNS(NS_XML_URI, "attribute");
                attribute.setPrefix(NS_XML);
                attribute.setAttribute(ATTRIBUTE_NAME, qname.getLocalPart());
                attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
                sequence.getParentNode().appendChild(attribute);
            }
        }

        createBaseElements(doc);
    }

    private static void createComplexType(Principal principal, Document doc, CoalesceSection section)
    {
        Element sequence = createBaseComplexType(doc, section.getName());

        for (CoalesceSection subSection : section.getSectionsAsList())
        {
            createComplexType(principal, doc, subSection);

            Element reference = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
            reference.setPrefix(NS_XML);
            reference.setAttribute(ATTRIBUTE_REF, NS_TARGET + ":" + normalize(subSection.getName()));
            reference.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
            reference.setAttribute(ATTRIBUTE_MAX_OCCURS, "1");

            sequence.appendChild(reference);
        }

        for (CoalesceRecordset recordset : section.getRecordsetsAsList())
        {

            createComplexType(principal, doc, recordset);

            Element reference = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
            reference.setPrefix(NS_XML);
            reference.setAttribute(ATTRIBUTE_REF, NS_TARGET + ":" + normalize(recordset.getName()));
            reference.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
            reference.setAttribute(ATTRIBUTE_MAX_OCCURS, "1");

            sequence.appendChild(reference);
        }
    }

    private static void createComplexType(Principal principal, Document doc, CoalesceRecordset recordset)
    {

        Element sequence = createBaseComplexType(doc, null, recordset.getName(), "sequence");

        String recordName = normalize(recordset.getName() + "Record");

        Element recordSequence = createBaseComplexType(doc, sequence, recordName, "all");

        for (CoalesceFieldDefinition definition : recordset.getFieldDefinitions())
        {
            recordSequence.appendChild(createComplexType(principal, doc, definition));
        }

        Element record = (Element) sequence.getFirstChild();

        record.setAttribute(ATTRIBUTE_MIN_OCCURS, Integer.toString(recordset.getMinRecords()));

        if (recordset.getMinRecords() != 1 || recordset.getMaxRecords() != 1)
        {
            record.setAttribute(ATTRIBUTE_MIN_OCCURS, Integer.toString(recordset.getMinRecords()));

            if (recordset.getMaxRecords() == 0)
            {
                record.setAttribute(ATTRIBUTE_MAX_OCCURS, UNBOUNDED);
            }
            else
            {
                record.setAttribute(ATTRIBUTE_MAX_OCCURS, Integer.toString(recordset.getMaxRecords()));
            }
        }
    }

    private static Element createComplexType(Principal principal, Document doc, CoalesceFieldDefinition definition)
    {
        boolean isMandatory;

        Element field = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        field.setPrefix(NS_XML);
        field.setAttribute(ATTRIBUTE_NAME, definition.getName());
        field.setAttribute("block", "substitution");

        if (definition.getDataType().isListType())
        {
            Element type = doc.createElementNS(NS_XML_URI, COMPLEX_TYPE);
            type.setPrefix(NS_XML);

            Element sequence = doc.createElementNS(NS_XML_URI, "sequence");
            sequence.setPrefix(NS_XML);

            Element values = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
            values.setPrefix(NS_XML);
            values.setAttribute(ATTRIBUTE_NAME, "values");
            values.setAttribute(ATTRIBUTE_TYPE, getXSDType(definition));
            values.setAttribute(ATTRIBUTE_MAX_OCCURS, UNBOUNDED);
            values.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");

            field.appendChild(type);
            type.appendChild(sequence);
            sequence.appendChild(values);

            isMandatory = createRestrictions(principal, doc, definition, values);
        }
        else
        {
            field.setAttribute(ATTRIBUTE_TYPE, getXSDType(definition));
            isMandatory = createRestrictions(principal, doc, definition, field);
        }

        if (!isMandatory)
        {
            field.setAttribute(ATTRIBUTE_MIN_OCCURS, "0");
        }

        return field;
    }

    private static Element createBaseComplexType(Document doc, String name)
    {
        return createBaseComplexType(doc, null, name, "all");
    }

    private static Element createBaseComplexType(Document doc, Element parent, String name, String indicator)
    {

        // Create Element
        Element element = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        element.setPrefix(NS_XML);
        element.setAttribute(ATTRIBUTE_NAME, normalize(name));
        element.setAttribute("block", "substitution");

        Element complexType = doc.createElementNS(NS_XML_URI, COMPLEX_TYPE);
        complexType.setPrefix(NS_XML);
        element.appendChild(complexType);

        Element contentNode = doc.createElementNS(NS_XML_URI, "complexContent");
        contentNode.setPrefix(NS_XML);
        complexType.appendChild(contentNode);

        // Create Extension
        Element extension = doc.createElementNS(NS_XML_URI, "extension");
        extension.setPrefix(NS_XML);
        extension.setAttribute(ATTRIBUTE_BASE, NS_TARGET + ":baseType");
        contentNode.appendChild(extension);

        Element sequence = doc.createElementNS(NS_XML_URI, indicator);
        sequence.setPrefix(NS_XML);
        extension.appendChild(sequence);

        if (parent == null)
        {
            // Add to Schema
            doc.getFirstChild().appendChild(element);
        }
        else
        {
            parent.appendChild(element);
        }

        return sequence;

    }

    /**
     * @param value
     * @return a normalized value for cross domaining treating it as a
     *         {@link ECoalesceFieldDataTypes#STRING_TYPE}.
     */
    public static String getCDSValue(String value)
    {
        return getCDSValue(ECoalesceFieldDataTypes.STRING_TYPE, value);
    }

    /**
     * @param type
     * @param value
     * @return a normalized value for cross domaining.
     */
    public static String getCDSValue(ECoalesceFieldDataTypes type, String value)
    {
        String result;

        switch (type) {
        case BOOLEAN_LIST_TYPE:
        case BOOLEAN_TYPE:
        case DATE_TIME_TYPE:
        case DOUBLE_LIST_TYPE:
        case DOUBLE_TYPE:
        case FLOAT_LIST_TYPE:
        case FLOAT_TYPE:
        case INTEGER_LIST_TYPE:
        case INTEGER_TYPE:
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
        case LONG_LIST_TYPE:
        case LONG_TYPE:
        case BINARY_TYPE:
        case GUID_LIST_TYPE:
        case GUID_TYPE:
        case LINE_STRING_TYPE:
        case CIRCLE_TYPE:
        case POLYGON_TYPE:
        case GEOCOORDINATE_LIST_TYPE:
        case GEOCOORDINATE_TYPE:
            // Do Nothing
            result = value;
            break;
        case STRING_LIST_TYPE:
        case STRING_TYPE:
        case URI_TYPE:
        case FILE_TYPE:
        default:
            if (value != null && value.length() > MAX_STRING_SIZE)
            {
                result = value.substring(0, MAX_STRING_SIZE);
                LOGGER.warn(String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "max length", value));
            }
            else
            {
                result = value;
            }
            break;

        }

        return result;
    }

    private static String getXSDType(CoalesceFieldDefinition definition)
    {
        return getXSDType(definition.getDataType());
    }

    private static String getXSDType(ECoalesceFieldDataTypes type)
    {

        String result;

        switch (type) {
        case BOOLEAN_LIST_TYPE:
        case BOOLEAN_TYPE:
            result = NS_XML + ":boolean";
            break;
        case DATE_TIME_TYPE:
            result = NS_XML + ":dateTime";
            break;
        case DOUBLE_LIST_TYPE:
        case DOUBLE_TYPE:
            result = NS_XML + ":double";
            break;
        case FLOAT_LIST_TYPE:
        case FLOAT_TYPE:
            result = NS_XML + ":float";
            break;
        case INTEGER_LIST_TYPE:
        case INTEGER_TYPE:
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
            result = NS_XML + ":int";
            break;
        case LONG_LIST_TYPE:
        case LONG_TYPE:
            result = NS_XML + ":long";
            break;
        case BINARY_TYPE:
            result = NS_XML + ":base64Binary";
            break;
        case GUID_LIST_TYPE:
        case GUID_TYPE:
            result = UUID_TYPE_NS;
            break;
        case LINE_STRING_TYPE:
            result = LINESTRING_TYPE_NS;
            break;
        case CIRCLE_TYPE:
            result = CIRCLE_TYPE_NS;
            break;
        case POLYGON_TYPE:
            result = POLYGON_TYPE_NS;
            break;
        case GEOCOORDINATE_LIST_TYPE:
            result = MULTIPOINT_TYPE_NS;
            break;
        case GEOCOORDINATE_TYPE:
            result = POINT_TYPE_NS;
            break;
        case STRING_LIST_TYPE:
        case STRING_TYPE:
        case URI_TYPE:
        case FILE_TYPE:
        default:
            result = CDS_STRING_TYPE_NS;
            break;

        }

        return result;
    }

    private static boolean createRestrictions(Principal principal,
                                              Document doc,
                                              CoalesceFieldDefinition definition,
                                              Element node)
    {
        boolean isMandatory = false;

        String name = definition.getName() + "Type";

        // Create a Pattern Facet
        Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
        restriction.setPrefix(NS_XML);
        restriction.setAttribute(ATTRIBUTE_BASE, getXSDType(definition));

        for (CoalesceConstraint constraint : definition.getConstraints())
        {

            switch (constraint.getConstraintType()) {
            case CUSTOM:
                // Ignore
                break;
            case ENUMERATION:

                switch (definition.getDataType()) {
                case ENUMERATION_LIST_TYPE:
                case ENUMERATION_TYPE:
                    createRestrictionMin(doc, restriction, true, "0");
                    break;
                case STRING_TYPE:
                    List<String> values = CoalesceConstraint.regExToValues(constraint.getValue());
                    createRestrictionEnumeration(doc, restriction, values.toArray(new String[values.size()]));
                    break;
                default:
                    // Do Nothing
                }

                break;
            case MANDATORY:
                createRestrictionMandartory(doc, restriction, Boolean.parseBoolean(constraint.getAttribute("allowEmpty")));
                isMandatory = true;
                break;
            case MAX:
                createRestrictionMax(doc,
                                     restriction,
                                     Boolean.parseBoolean(constraint.getAttribute("inclusive")),
                                     constraint.getValue());
                break;
            case MIN:
                createRestrictionMin(doc,
                                     restriction,
                                     Boolean.parseBoolean(constraint.getAttribute("inclusive")),
                                     constraint.getValue());
                break;
            case REGEX:
                createRestrictionRegEx(doc, restriction, constraint.getValue());
                break;
            default:
                break;
            }
        }

        if (restriction.hasChildNodes())
        {
            Element statusEnumeration;
            statusEnumeration = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
            statusEnumeration.setPrefix(NS_XML);
            statusEnumeration.setAttribute(ATTRIBUTE_NAME, name);
            statusEnumeration.appendChild(restriction);

            doc.getFirstChild().appendChild(statusEnumeration);

            node.setAttribute(ATTRIBUTE_TYPE, NS_TARGET + ":" + name);
        }

        return isMandatory;
    }

    private static void createRestrictionEnumeration(Document doc, Element restriction, String... values)
    {
        for (String value : values)
        {
            Element enumeration = doc.createElementNS(NS_XML_URI, "enumeration");
            enumeration.setPrefix(NS_XML);
            enumeration.setAttribute(ATTRIBUTE_VALUE, value);
            restriction.appendChild(enumeration);
        }
    }

    private static void createRestrictionMin(Document doc, Element restriction, boolean isInclusive, String value)
    {
        Element pattern;

        if (isInclusive)
        {
            pattern = doc.createElementNS(NS_XML_URI, "minInclusive");
            pattern.setPrefix(NS_XML);
        }
        else
        {
            pattern = doc.createElementNS(NS_XML_URI, "minExclusive");
            pattern.setPrefix(NS_XML);
        }

        pattern.setAttribute(ATTRIBUTE_VALUE, value);
        restriction.appendChild(pattern);
    }

    private static void createRestrictionMax(Document doc, Element restriction, boolean isInclusive, String value)
    {
        Element pattern;

        if (isInclusive)
        {
            pattern = doc.createElementNS(NS_XML_URI, "maxInclusive");
            pattern.setPrefix(NS_XML);
        }
        else
        {
            pattern = doc.createElementNS(NS_XML_URI, "maxExclusive");
            pattern.setPrefix(NS_XML);
        }

        pattern.setAttribute(ATTRIBUTE_VALUE, value);
        restriction.appendChild(pattern);
    }

    private static void createRestrictionMandartory(Document doc, Element restriction, boolean allowEmpty)
    {
        // ".*\\S+.*"
        createRestrictionRegEx(doc, restriction, "[ -~]*[!-~]+[ -~]*");
    }

    private static void createRestrictionRegEx(Document doc, Element restriction, String regex)
    {
        Element pattern = doc.createElementNS(NS_XML_URI, "pattern");
        pattern.setAttribute(ATTRIBUTE_VALUE, regex);
        pattern.setPrefix(NS_XML);

        restriction.appendChild(pattern);
    }

}
