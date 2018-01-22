package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.EGraphNodeType;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphNode;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This controller exposes the underlying blueprints of a Karaf container.
 *
 * @author Derek Clemenzi
 */
public class BlueprintController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintController.class);

    // Default Directory
    private Path root = Paths.get("deploy");

    /**
     * Overrides the default directory to search for blueprints. By default it is the deploy directory.
     *
     * @param path directory to scan for xml documents
     */
    public void setDirectory(String path)
    {
        root = Paths.get(path);
    }

    /**
     * @return a list of xml documents within the configured directory.
     */
    public List<String> getBlueprints()
    {
        List<String> results = new ArrayList<>();

        File directory = new File(root.toString());

        File[] files = directory.listFiles(pathname -> pathname.getAbsolutePath().toLowerCase().endsWith("xml"));

        if (files != null)
        {
            for (File file : files)
            {
                if (file.isFile())
                {
                    results.add(file.getName());
                }
            }
        }

        return results;
    }

    /**
     * @param name filename to render.
     * @return nodes and linkages of the bean for the specified xml document.
     * @throws RemoteException on error
     */
    public GraphObj getBlueprint(String name) throws RemoteException
    {
        GraphObj result = new GraphObj();

        Document doc = loadBlueprint(name);

        // Create Beans / Linkages
        createNodes(result, doc);

        // Get Servers
        NodeList servers = doc.getDocumentElement().getElementsByTagNameNS("http://cxf.apache.org/blueprint/jaxrs",
                                                                           "server");

        // Iterate Through Servers
        for (int ii = 0; ii < servers.getLength(); ii++)
        {
            Element server = (Element) servers.item(ii);

            // Create Server Node
            GraphNode serverNode = new GraphNode();
            serverNode.setId(server.getAttribute("id"));
            serverNode.setLabel(server.getAttribute("address"));
            serverNode.setNodeType(EGraphNodeType.SERVER);

            // Add Server Node
            result.getNodes().add(serverNode);

            LOGGER.debug("Processing Server: ({})", serverNode.getId());

            Element servicesBeans = (Element) server.getElementsByTagNameNS("http://cxf.apache.org/blueprint/jaxrs",
                                                                            "serviceBeans").item(0);
            NodeList services = servicesBeans.getChildNodes();

            // Link Server to Services
            for (int jj = 0; jj < services.getLength(); jj++)
            {
                if (services.item(jj) instanceof Element)
                {
                    Element service = (Element) services.item(jj);

                    GraphLink link = new GraphLink();

                    switch (service.getLocalName())
                    {
                    case "ref":
                        link.setSource(serverNode.getId());
                        link.setTarget(service.getAttribute("component-id"));

                        result.getLinks().add(link);
                        break;
                    case "bean":
                        link.setSource(serverNode.getId());
                        link.setTarget(service.getAttribute("id"));

                        result.getLinks().add(link);
                        break;
                    }
                }
            }
        }

        return result;
    }

    private void createNodes(GraphObj results, Document doc)
    {
        // Get All Beans
        NodeList beans = doc.getDocumentElement().getElementsByTagName("bean");

        // Create Nodes
        for (int ii = 0; ii < beans.getLength(); ii++)
        {
            Element bean = (Element) beans.item(ii);

            GraphNode node = new GraphNode();
            node.setId(bean.getAttribute("id"));
            node.setClassname(bean.getAttribute("class"));
            node.setLabel(node.getClassname().substring(node.getClassname().lastIndexOf(".") + 1));

            // Is Bean a standalone bean?
            if (StringHelper.isNullOrEmpty(node.getId()))
            {
                // No; Generate an ID that can be used for linking
                node.setId(UUID.randomUUID().toString());
                bean.setAttribute("id", node.getId());
            }

            String simpleName = node.getLabel().toLowerCase();

            // Determine Node Type
            if (simpleName.contains("persister") || simpleName.contains("persistor"))
            {
                node.setNodeType(EGraphNodeType.PERSISTER);
            }
            else if (simpleName.contains("impl"))
            {
                node.setNodeType(EGraphNodeType.ENDPOINT);
            }
            else if (simpleName.contains("controller"))
            {
                node.setNodeType(EGraphNodeType.CONTROLLER);
            }
            else if (simpleName.equals("coalesceframework") || simpleName.equals("coalescesearchframework") )
            {
                node.setNodeType(EGraphNodeType.FRAMEWORK);
            }
            else if (simpleName.contains("entity"))
            {
                node.setNodeType(EGraphNodeType.ENTITY);
            }
            else if (simpleName.equals("serverconn"))
            {
                node.setNodeType(EGraphNodeType.SETTINGS);
            }
            else
            {
                node.setNodeType(EGraphNodeType.OTHER);
            }

            results.getNodes().add(node);
        }

        // Link Nodes
        for (int ii = 0; ii < beans.getLength(); ii++)
        {
            linkBeanRecursive(results, (Element) beans.item(ii), (Element) beans.item(ii));
        }
    }

    private void linkBeanRecursive(GraphObj results, Element bean, Element currentnode)
    {
        NodeList children = currentnode.getChildNodes();

        for (int ii = 0; ii < children.getLength(); ii++)
        {
            if (children.item(ii).getNodeType() == 1)
            {
                Element node = (Element) children.item(ii);

                if (node.getNodeName().equalsIgnoreCase("ref"))
                {
                    LOGGER.debug("(REF) " + bean.getAttribute("id") + " -> " + node.getAttribute("component-id"));

                    GraphLink link = new GraphLink();
                    link.setSource(bean.getAttribute("id"));
                    link.setTarget(node.getAttribute("component-id"));

                    results.getLinks().add(link);
                }
                else if (node.getNodeName().equalsIgnoreCase("bean"))
                {
                    LOGGER.debug("(BEAN) " + bean.getAttribute("id") + " -> " + node.getAttribute("id"));

                    GraphLink link = new GraphLink();
                    link.setSource(bean.getAttribute("id"));
                    link.setTarget(node.getAttribute("id"));

                    results.getLinks().add(link);

                    linkBeanRecursive(results, node, node);
                }
                else if (!StringHelper.isNullOrEmpty(node.getAttribute("ref")))
                {
                    LOGGER.debug("(REF Attr) " + bean.getAttribute("id") + " -> " + node.getAttribute("ref"));

                    GraphLink link = new GraphLink();
                    link.setSource(bean.getAttribute("id"));
                    link.setTarget(node.getAttribute("ref"));

                    results.getLinks().add(link);
                }
                else
                {
                    linkBeanRecursive(results, bean, node);
                }
            }
        }
    }

    private Document loadBlueprint(String name) throws RemoteException
    {
        Document result;

        Path filename = root.resolve(name);

        if (Files.exists(filename))
        {
            try (FileInputStream fis = new FileInputStream(new File(filename.toString())))
            {
                result = XmlHelper.loadXmlFrom(fis);
            }
            catch (SAXException | IOException e)
            {
                throw new RemoteException(String.format(CoalesceErrors.INVALID_INPUT_REASON, name, e.getMessage()));
            }
        }
        else
        {
            throw new RemoteException(String.format(CoalesceErrors.NOT_FOUND, "Blueprint", name));
        }

        return result;
    }

}
