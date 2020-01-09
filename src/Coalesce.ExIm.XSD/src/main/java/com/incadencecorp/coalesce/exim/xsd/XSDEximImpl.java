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

import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.incadencecorp.coalesce.api.CoalesceAttributes;
import com.incadencecorp.coalesce.api.CoalesceExim;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.ArrayHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;

/**
 * This implementation creates an XSD from a Coalesce Template.
 * 
 * @author Derek C.
 * @since Coalesce 0.0.16-SNAPSHOT
 */
public class XSDEximImpl implements CoalesceExim<Document> {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final String NS = "cns";
    private static final List<String> ATTRIBUTES_TO_OMIT = Collections.singletonList(CoalesceEntity.ATTRIBUTE_CLASSNAME);

    private String namespace;

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public Document exportValues(CoalesceEntity entity, boolean includeEntityType) throws CoalesceException
    {
        try
        {
            Document doc;

            // Determine Namespace
            namespace = XSDGeneratorUtil.createNameSpace(entity.getClassName(), entity.getName());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            doc = factory.newDocumentBuilder().newDocument();
            doc.appendChild(createElement(doc, entity));

            return doc;
        }
        catch (ParserConfigurationException e)
        {
            throw new CoalesceException("Failed: ", e);
        }
    }

    @Override
    public void importValues(Document doc, CoalesceEntity entity) throws CoalesceException
    {
        XSDToCoalesceIterator iterator = new XSDToCoalesceIterator();
        iterator.createEntity(entity, doc);
    }

    @Override
    public CoalesceEntity importValues(Document doc, CoalesceEntityTemplate template) throws CoalesceException
    {
        CoalesceEntity entity = template.createNewEntity();

        XSDToCoalesceIterator iterator = new XSDToCoalesceIterator();
        iterator.createEntity(entity, doc);

        return entity;
    }

    /*--------------------------------------------------------------------------
    Import Methods
    --------------------------------------------------------------------------*/

    private static class XSDToCoalesceIterator extends CoalesceIterator<Document> {

        private Map<String, String> keysToReplace = new HashMap<>();

        public void createEntity(CoalesceEntity entity, Document doc) throws CoalesceException
        {
            processAllElements(entity, doc);

            for (Map.Entry<String, String> entry : keysToReplace.entrySet())
            {
                entity.getCoalesceObjectForKey(entry.getKey()).setKey(entry.getValue());
            }

        }

        @Override
        protected boolean visitCoalesceEntity(CoalesceEntity entity, Document doc)
        {
            copyAttributes(doc, entity);
            return true;
        }

        @Override
        protected boolean visitCoalesceSection(CoalesceSection section, Document doc)
        {
            copyAttributes(doc, section);
            return true;
        }

        @Override
        protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, Document doc)
        {
            Element element = copyAttributes(doc, section);

            if (element != null)
            {
                Element linkageElement = getFirstChildElement(element);

                while (linkageElement != null)
                {
                    CoalesceLinkage linkage = section.createLinkage();

                    copyAttributes(linkageElement, linkage);

                    Element otherAttributes = getFirstChildElement(linkageElement);

                    while (otherAttributes != null)
                    {
                        linkage.setAttribute(otherAttributes.getLocalName(), otherAttributes.getTextContent());
                        otherAttributes = getNextSiblingElement(otherAttributes);
                    }

                    linkageElement = getNextSiblingElement(linkageElement);
                }
            }
            return true;
        }

        @Override
        protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Document doc)
        {
            Element element = copyAttributes(doc, recordset);

            if (element != null)
            {
                Element recordElement = getFirstChildElement(element);

                while (recordElement != null)
                {
                    CoalesceRecord record;
                    List<CoalesceRecord> records = recordset.getAllRecords();
                    if (recordset.getMaxRecords() == 1 && records.size() >= 1)
                    {
                        record = records.get(0);
                    }
                    else
                    {
                        record = ArrayHelper.getItem(records, recordElement.getAttribute(CoalesceObject.ATTRIBUTE_KEY));
                    }

                    // Exists?
                    if (record == null)
                    {
                        // No; Create
                        record = recordset.addNew();
                        record.setKey(recordElement.getAttribute(CoalesceObject.ATTRIBUTE_KEY));
                    }

                    copyAttributes(recordElement, record);

                    Element fieldElement = getFirstChildElement(recordElement);

                    while (fieldElement != null)
                    {
                        CoalesceField<?> field = record.getFieldByName(fieldElement.getLocalName());

                        if (field != null)
                        {
                            if (field.getDataType().isListType())
                            {
                                Element value = getFirstChildElement(fieldElement);

                                List<String> values = new ArrayList<>();

                                while (value != null)
                                {
                                    values.add(value.getTextContent());
                                    value = getNextSiblingElement(value);
                                }

                                field.setAttribute(CoalesceField.ATTRIBUTE_VALUE, fromArray(values));

                            }
                            else if (field.getDataType() == ECoalesceFieldDataTypes.CIRCLE_TYPE)
                            {
                                Element value = getFirstChildElement(fieldElement);

                                do
                                {
                                    switch (value.getLocalName()) {

                                    case "center":
                                        field.setAttribute(CoalesceField.ATTRIBUTE_VALUE, value.getTextContent());
                                        break;
                                    case CoalesceCircleField.ATTRIBUTE_RADIUS:
                                        field.setAttribute(CoalesceCircleField.ATTRIBUTE_RADIUS, value.getTextContent());
                                        break;
                                    default:
                                        // Do Nothing
                                    }
                                    value = getNextSiblingElement(value);
                                }
                                while (value != null);
                            }
                            else
                            {
                                field.setAttribute(CoalesceField.ATTRIBUTE_VALUE, fieldElement.getTextContent());
                            }
                        }

                        fieldElement = getNextSiblingElement(fieldElement);
                    }

                    recordElement = getNextSiblingElement(recordElement);
                }

            }

            return false;
        }

        private Element getFirstChildElement(Element element)
        {
            Node node = element.getFirstChild();

            while (node != null && !(node instanceof Element))
            {
                node = node.getNextSibling();
            }

            return (Element) node;
        }

        private Element getNextSiblingElement(Element element)
        {
            Node node = element.getNextSibling();

            while (node != null && !(node instanceof Element))
            {
                node = node.getNextSibling();
            }

            return (Element) node;
        }

        private Element copyAttributes(Document doc, CoalesceObject coalesceObject)
        {
            Element element = getElement(doc, coalesceObject.getNamePath().split("[/]"));
            copyAttributes(element, coalesceObject);
            return element;
        }

        private void copyAttributes(Element element, CoalesceObject coalesceObject)
        {
            if (element != null)
            {
                NamedNodeMap attrs = element.getAttributes();

                for (int ii = 0; ii < attrs.getLength(); ii++)
                {
                    Node attr = attrs.item(ii);

                    if (!attr.getNodeName().startsWith("xmlns"))
                    {
                        if (!attr.getLocalName().equalsIgnoreCase(CoalesceObject.ATTRIBUTE_KEY)
                                || StringHelper.isNullOrEmpty(coalesceObject.getKey()))
                        {
                            coalesceObject.setAttribute(attr.getLocalName(), attr.getNodeValue());
                        }
                        else
                        {
                            keysToReplace.put(coalesceObject.getKey(), attr.getNodeValue());
                        }
                    }
                }
            }
        }

        private String fromArray(List<String> values)
        {
            String result = null;

            if (values != null && values.size() > 0)
            {
                String[] escaped = new String[values.size()];

                for (int ii = 0; ii < values.size(); ii++)
                {
                    escaped[ii] = StringEscapeUtils.escapeCsv(values.get(ii));
                }

                result = StringUtils.join(escaped, ",");
            }

            return result;
        }

        /**
         * 
         * @param names
         * @return the element at the given xpath.
         */
        private Element getElement(Document doc, String... names)
        {
            Element result = (Element) doc.getFirstChild();

            // Iterate Through Each Depth
            for (int ii = 0; ii < names.length; ii++)
            {
                // Locate Element
                while (result != null && (result.getLocalName() == null
                        || !result.getLocalName().equalsIgnoreCase(XSDGeneratorUtil.normalize(names[ii]))))
                {
                    result = getNextSiblingElement(result);
                }

                // Found?
                if (result == null)
                {
                    // No; Exit
                    break;
                }
                else if (ii < names.length - 1)
                {
                    // Yes; and we are not at the last depth.
                    result = getFirstChildElement(result);
                }
            }

            return result;
        }
    }

    /*--------------------------------------------------------------------------
    Export Methods
    --------------------------------------------------------------------------*/

    private Element createElement(Document doc, CoalesceEntity entity)
    {
        Element element = createBaseElement(doc, entity);
        element.setAttributeNS(namespace, CoalesceEntity.ATTRIBUTE_NAME, entity.getName());
        element.setAttributeNS(namespace, CoalesceEntity.ATTRIBUTE_SOURCE, entity.getSource());
        element.setAttributeNS(namespace, CoalesceEntity.ATTRIBUTE_VERSION, entity.getVersion());
        element.setAttributeNS(namespace, CoalesceEntity.ATTRIBUTE_TITLE, XSDGeneratorUtil.getCDSValue(entity.getTitle()));
        element.setAttributeNS(namespace,
                               CoalesceEntity.ATTRIBUTE_ENTITYID,
                               XSDGeneratorUtil.getCDSValue(entity.getEntityId()));
        element.setAttributeNS(namespace,
                               CoalesceEntity.ATTRIBUTE_ENTITYIDTYPE,
                               XSDGeneratorUtil.getCDSValue(entity.getEntityIdType()));

        for (Map.Entry<QName, String> entry : entity.getOtherAttributes().entrySet())
        {
            if (!ATTRIBUTES_TO_OMIT.contains(entry.getKey().getLocalPart()))
            {
                element.setAttributeNS(namespace,
                                       entry.getKey().getLocalPart(),
                                       XSDGeneratorUtil.getCDSValue(entry.getValue()));
            }
        }

        element.appendChild(createElement(doc, entity.getLinkageSection()));

        for (CoalesceSection section : entity.getSectionsAsList())
        {
            element.appendChild(createElement(doc, section));
        }

        return element;
    }

    private Element createElement(Document doc, CoalesceLinkageSection linkageSection)
    {

        Element element = createBaseElement(doc, linkageSection);

        for (CoalesceLinkage linkage : linkageSection.getLinkages().values())
        {
            element.appendChild(createElement(doc, linkage));
        }

        return element;

    }

    private Element createElement(Document doc, CoalesceLinkage linkage)
    {
        Element link = createBaseElement(doc, linkage);

        link.appendChild(createElement(doc, linkage, false, CoalesceLinkage.ATTRIBUTE_ENTITY1KEY));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_ENTITY1NAME));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_ENTITY1SOURCE));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_ENTITY1VERSION));
        link.appendChild(createElement(doc, CoalesceLinkage.ATTRIBUTE_LINKTYPE, linkage.getLinkType().toString()));
        link.appendChild(createElement(doc, linkage, false, CoalesceLinkage.ATTRIBUTE_ENTITY2KEY));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_ENTITY2NAME));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_ENTITY2SOURCE));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_ENTITY2VERSION));
        link.appendChild(createElement(doc, linkage, true, CoalesceLinkage.ATTRIBUTE_LABEL));
        // If Markings exceed the CDS limits it should fail validation
        link.appendChild(createElement(doc, linkage, false, CoalesceAttributes.ATTRIBUTE_MARKING));
        link.appendChild(createElement(doc, linkage, false, CoalesceLinkage.ATTRIBUTE_ENTITY2OBJECTVERSION));

        return link;
    }

    private Element createElement(Document doc, CoalesceObject object, boolean isCDS, String attr)
    {
        String value = object.getAttribute(attr);

        if (isCDS)
        {
            value = XSDGeneratorUtil.getCDSValue(value);
        }

        return createElement(doc, attr, value);
    }

    private Element createElement(Document doc, String name, String value)
    {
        Element element = doc.createElementNS(namespace, name);
        element.setPrefix(NS);
        element.setTextContent(value);

        return element;
    }

    private Element createElement(Document doc, CoalesceSection section)
    {
        Element element = createBaseElement(doc, section);

        for (CoalesceSection childSection : section.getSectionsAsList())
        {
            element.appendChild(createElement(doc, childSection));
        }

        for (CoalesceRecordset recordset : section.getRecordsetsAsList())
        {
            element.appendChild(createElement(doc, recordset));
        }

        return element;
    }

    private Element createElement(Document doc, CoalesceRecordset recordset)
    {
        Element element = createBaseElement(doc, recordset);

        for (CoalesceRecord record : recordset.getRecords())
        {
            element.appendChild(createElement(doc, record));
        }

        return element;
    }

    private Element createElement(Document doc, CoalesceRecord record)
    {
        Element element = createBaseElement(doc, record);

        for (CoalesceField<?> field : record.getFields())
        {
            if (!StringHelper.isNullOrEmpty(field.getBaseValue()))
            {
                Element fieldElement = doc.createElementNS(namespace, field.getName().replace(" ", ""));
                fieldElement.setPrefix(NS);

                if (field.getDataType().isListType())
                {
                    for (String value : field.getBaseValues())
                    {
                        Element valueElement = doc.createElementNS(namespace, "values");
                        valueElement.setPrefix(NS);
                        valueElement.setTextContent(XSDGeneratorUtil.getCDSValue(field.getDataType(), value));

                        fieldElement.appendChild(valueElement);
                    }
                }
                else if (field.getDataType() == ECoalesceFieldDataTypes.CIRCLE_TYPE)
                {
                    Element center = doc.createElementNS(namespace, "center");
                    center.setPrefix(NS);
                    center.setTextContent(field.getBaseValue());

                    Element radius = doc.createElementNS(namespace, CoalesceCircleField.ATTRIBUTE_RADIUS);
                    radius.setPrefix(NS);
                    radius.setTextContent(field.getAttribute(CoalesceCircleField.ATTRIBUTE_RADIUS));

                    fieldElement.appendChild(center);
                    fieldElement.appendChild(radius);
                }
                else
                {
                    fieldElement.setTextContent(XSDGeneratorUtil.getCDSValue(field.getDataType(), field.getBaseValue()));
                }

                element.appendChild(fieldElement);
            }
        }

        return element;
    }

    private Element createBaseElement(Document doc, CoalesceObject object)
    {
        Element element = doc.createElementNS(namespace, object.getName().replace(" ", ""));
        element.setPrefix(NS);
        element.setAttributeNS(namespace, CoalesceObject.ATTRIBUTE_KEY, object.getKey());
        element.setAttributeNS(namespace,
                               CoalesceObject.ATTRIBUTE_DATECREATED,
                               JodaDateTimeHelper.toXmlDateTimeUTC(object.getDateCreated()));
        element.setAttributeNS(namespace,
                               CoalesceObject.ATTRIBUTE_LASTMODIFIED,
                               JodaDateTimeHelper.toXmlDateTimeUTC(object.getLastModified()));

        if (object.getStatus() != ECoalesceObjectStatus.ACTIVE)
        {
            element.setAttributeNS(namespace, CoalesceObject.ATTRIBUTE_STATUS, object.getStatus().toString().toUpperCase());
        }

        return element;
    }

}
