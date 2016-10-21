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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

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

import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
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
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;

/**
 * This utility class creates a XSD schema from a {@link CoalesceEntityTemplate}
 * . If the template contains {@link ECoalesceFieldDataTypes#ENUMERATION_TYPE}
 * then you must initialize {@link EnumerationProviderUtil}. .
 * 
 * @author Derek C.
 * @since Coalesce 0.0.16-SNAPSHOT
 */
public final class XSDGeneratorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XSDGeneratorUtil.class);

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final String UNBOUNDED = Integer.toString(5000);

    private static final String ATTRIBUTE_REF = "ref";
    private static final String ATTRIBUTE_BASE = "base";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_USE = "use";
    private static final String ATTRIBUTE_DEFAULT = "default";

    private static final String NS_TARGET = "tns";
    private static final String NS_XML = "xs";
    private static final String NS_XML_URI = "http://www.w3.org/2001/XMLSchema";

    private static final String COMPLEX_TYPE = "complexType";
    private static final String SIMPLE_TYPE = "simpleType";
    private static final String ELEMENT_TYPE = "element";

    private static final String CDS_STRING_TYPE = "cdsString";
    private static final String CDS_STRING_TYPE_NS = NS_TARGET + ":" + CDS_STRING_TYPE;

    private static final String LINKAGE_TYPE_NS = NS_TARGET + ":" + CoalesceLinkageSection.NAME;

    private static final String UUID_TYPE = "uuid";
    private static final String UUID_TYPE_NS = NS_TARGET + ":" + UUID_TYPE;

    private static final String STATUS_TYPE = ECoalesceObjectStatus.class.getSimpleName();
    private static final String STATUS_TYPE_NS = NS_TARGET + ":" + STATUS_TYPE;

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

        return doc;

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
        catch (IOException | SAXException e)
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
        doc.getFirstChild().appendChild(createUUIDType(doc));
        createComplexTypeLinkage(doc);
    }

    private static <E extends Enum<E>> Element createEnumeration(Document doc, Class<E> clazz)
    {
        Element element = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
        element.setPrefix(NS_XML);
        element.setAttribute(ATTRIBUTE_NAME, clazz.getSimpleName());

        Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
        restriction.setPrefix(NS_XML);
        restriction.setAttribute(ATTRIBUTE_BASE, NS_XML + ":string");
        element.appendChild(restriction);

        for (Iterator<E> it = EnumSet.allOf(clazz).iterator(); it.hasNext();)
        {
            Element enumeration = doc.createElementNS(NS_XML_URI, "enumeration");
            enumeration.setPrefix(NS_XML);
            enumeration.setAttribute(ATTRIBUTE_VALUE, it.next().toString());
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
        attribute.setAttribute(ATTRIBUTE_NAME, "key");
        attribute.setAttribute(ATTRIBUTE_TYPE, UUID_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_USE, "required");
        type.appendChild(attribute);

        // Date Created
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "datecreated");
        attribute.setAttribute(ATTRIBUTE_TYPE, NS_XML + ":dateTime");
        type.appendChild(attribute);

        // Last Modified
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "lastmodified");
        attribute.setAttribute(ATTRIBUTE_TYPE, NS_XML + ":dateTime");
        type.appendChild(attribute);

        // Status
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "status");
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
        restriction.setAttribute(ATTRIBUTE_BASE, NS_XML + ":string");
        type.appendChild(restriction);

        Element pattern = doc.createElementNS(NS_XML_URI, "maxLength");
        pattern.setPrefix(NS_XML);
        pattern.setAttribute(ATTRIBUTE_VALUE, Integer.toString(20));

        restriction.appendChild(pattern);

        return type;
    }

    private static Element createUUIDType(Document doc)
    {
        Element type = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
        type.setPrefix(NS_XML);
        type.setAttribute(ATTRIBUTE_NAME, UUID_TYPE);

        Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
        restriction.setPrefix(NS_XML);
        restriction.setAttribute(ATTRIBUTE_BASE, NS_XML + ":string");

        // Element length = doc.createElementNS(NS_XML_URI, "length");
        // length.setPrefix(NS_XML);
        // length.setAttribute(ATTRIBUTE_VALUE, Integer.toString(36));

        Element pattern = doc.createElementNS(NS_XML_URI, "pattern");
        pattern.setPrefix(NS_XML);
        pattern.setAttribute(ATTRIBUTE_VALUE,
                             "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[8-9a-bA-B][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

        // restriction.appendChild(length);
        restriction.appendChild(pattern);
        type.appendChild(restriction);

        return type;
    }

    private static void createComplexTypeLinkage(Document doc)
    {
        Element sequence = createBaseComplexType(doc, null, CoalesceLinkageSection.NAME, "sequence");

        Element linkageSequence = createBaseComplexType(doc, sequence, CoalesceLinkage.NAME, "all");

        Element linkage = (Element) sequence.getFirstChild();
        linkage.setAttribute("minOccurs", "0");
        linkage.setAttribute("maxOccurs", UNBOUNDED);

        createSimpleElement(doc, linkageSequence, "entity1key", UUID_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity1name", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity1source", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity1version", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "linktype", NS_TARGET + ":" + ELinkTypes.class.getSimpleName());
        createSimpleElement(doc, linkageSequence, "entity2key", UUID_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity2name", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity2source", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity2version", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "entity2objectversion", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "classificationmarking", CDS_STRING_TYPE_NS);
        createSimpleElement(doc, linkageSequence, "label", CDS_STRING_TYPE_NS);
    }

    private static void createSimpleElement(Document doc, Element node, String name, String type)
    {
        Element element;
        element = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        element.setPrefix(NS_XML);
        element.setAttribute(ATTRIBUTE_NAME, name);
        element.setAttribute(ATTRIBUTE_TYPE, type);

        node.appendChild(element);
    }

    private static void createComplexType(Principal principal, Document doc, CoalesceEntity entity)
    {
        Element sequence = createBaseComplexType(doc, entity.getName());

        // Reference Linkage Section
        Element linkage = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        linkage.setPrefix(NS_XML);
        linkage.setAttribute(ATTRIBUTE_REF, LINKAGE_TYPE_NS);
        linkage.setAttribute("minOccurs", "0");

        sequence.appendChild(linkage);

        for (CoalesceSection section : entity.getSectionsAsList())
        {
            createComplexType(principal, doc, section);

            Element reference = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
            reference.setPrefix(NS_XML);
            reference.setAttribute(ATTRIBUTE_REF, NS_TARGET + ":" + normalize(section.getName()));
            reference.setAttribute("minOccurs", "0");

            sequence.appendChild(reference);

        }

        Element attribute;

        // Source
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "source");
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_USE, "required");
        sequence.getParentNode().appendChild(attribute);

        // Version
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "version");
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        attribute.setAttribute(ATTRIBUTE_USE, "required");
        sequence.getParentNode().appendChild(attribute);

        // Entity ID
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "entityid");
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        sequence.getParentNode().appendChild(attribute);

        // Entity ID Type
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "entityidtype");
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        sequence.getParentNode().appendChild(attribute);

        // Title
        attribute = doc.createElementNS(NS_XML_URI, "attribute");
        attribute.setPrefix(NS_XML);
        attribute.setAttribute(ATTRIBUTE_NAME, "title");
        attribute.setAttribute(ATTRIBUTE_TYPE, CDS_STRING_TYPE_NS);
        sequence.getParentNode().appendChild(attribute);

        createBaseElements(doc);
    }

    private static void createComplexType(Principal principal, Document doc, CoalesceSection section)
    {
        Element sequence = createBaseComplexType(doc, section.getName());

        for (CoalesceSection subSection : section.getSectionsAsList())
        {
            createComplexType(principal, doc, subSection);
        }

        for (CoalesceRecordset recordset : section.getRecordsetsAsList())
        {

            createComplexType(principal, doc, recordset);

            Element reference = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
            reference.setPrefix(NS_XML);
            reference.setAttribute(ATTRIBUTE_REF, NS_TARGET + ":" + normalize(recordset.getName()));
            reference.setAttribute("minOccurs", "0");

            sequence.appendChild(reference);
        }
    }

    private static void createComplexType(Principal principal, Document doc, CoalesceRecordset recordset)
    {

        Element sequence = createBaseComplexType(doc, null, recordset.getName(), "sequence");

        String recordName = normalize(recordset.getName() + "Record");

        Element recordSequence = createBaseComplexType(doc, sequence, recordName, "all");
        // Element recordRef = doc.createElementNS(NS_XML_URI, ELEMENT_TYPE);
        // recordRef.setPrefix(NS_XML);
        // recordRef.setAttribute(ATTRIBUTE_REF, NS_TARGET + ":" + recordName);
        // sequence.appendChild(recordRef);

        for (CoalesceFieldDefinition definition : recordset.getFieldDefinitions())
        {
            recordSequence.appendChild(createComplexType(principal, doc, definition));
        }

        Element record = (Element) sequence.getFirstChild();

        record.setAttribute("minOccurs", Integer.toString(recordset.getMinRecords()));

        if (recordset.getMinRecords() != 1 || recordset.getMaxRecords() != 1)
        {
            record.setAttribute("minOccurs", Integer.toString(recordset.getMinRecords()));

            if (recordset.getMaxRecords() == 0)
            {
                record.setAttribute("maxOccurs", UNBOUNDED);
            }
            else
            {
                record.setAttribute("maxOccurs", Integer.toString(recordset.getMaxRecords()));
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
            values.setAttribute("maxOccurs", UNBOUNDED);
            values.setAttribute("minOccurs", "0");

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
            field.setAttribute("minOccurs", "0");
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

    private static String getXSDType(CoalesceFieldDefinition definition)
    {

        String result;

        switch (definition.getDataType()) {
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
            result = NS_TARGET + ":uuid";
            break;
        case LINE_STRING_TYPE:
        case CIRCLE_TYPE:
        case POLYGON_TYPE:
        case STRING_LIST_TYPE:
        case STRING_TYPE:
        case URI_TYPE:
        case FILE_TYPE:
        case GEOCOORDINATE_LIST_TYPE:
        case GEOCOORDINATE_TYPE:
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

        if (definition.getConstraints().size() > 0)
        {
            String name = definition.getName() + "Type";

            // Create a Pattern Facet
            Element statusEnumeration = doc.createElementNS(NS_XML_URI, SIMPLE_TYPE);
            statusEnumeration.setPrefix(NS_XML);
            statusEnumeration.setAttribute(ATTRIBUTE_NAME, name);
            doc.getFirstChild().appendChild(statusEnumeration);

            Element restriction = doc.createElementNS(NS_XML_URI, "restriction");
            restriction.setPrefix(NS_XML);
            restriction.setAttribute(ATTRIBUTE_BASE, getXSDType(definition));
            statusEnumeration.appendChild(restriction);

            node.setAttribute(ATTRIBUTE_TYPE, NS_TARGET + ":" + name);

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
                        Integer maxValue;

                        ConstraintEnumerationProviderImpl provider = EnumerationProviderUtil.getProvider(ConstraintEnumerationProviderImpl.class);
                        if (provider != null)
                        {
                            provider.add(constraint);
                        }

                        try
                        {
                            maxValue = EnumerationProviderUtil.getValues(principal, constraint.getValue()).size() - 1;
                        }
                        catch (IllegalArgumentException e)
                        {
                            LOGGER.warn("({}) ({}) ({}): ({})",
                                        constraint.getEntity().getName(),
                                        constraint.getEntity().getVersion(),
                                        definition.getName(),
                                        e.getMessage());
                            maxValue = 0;
                        }

                        createRestrictionMin(doc, restriction, true, "0");
                        createRestrictionMax(doc, restriction, true, Integer.toString(maxValue));

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
                    createRestrictionMandartory(doc, restriction);
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

    private static void createRestrictionMandartory(Document doc, Element restriction)
    {
        createRestrictionRegEx(doc, restriction, ".*\\S+.*");
    }

    private static void createRestrictionRegEx(Document doc, Element restriction, String regex)
    {

        Element pattern = doc.createElementNS(NS_XML_URI, "pattern");
        pattern.setAttribute(ATTRIBUTE_VALUE, regex);
        pattern.setPrefix(NS_XML);

        restriction.appendChild(pattern);

    }

}
