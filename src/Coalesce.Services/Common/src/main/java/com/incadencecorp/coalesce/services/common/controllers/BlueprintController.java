package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.CoalesceThreadFactoryImpl;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Edge;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Vertex;
import com.incadencecorp.coalesce.services.api.mappers.CoalesceMapper;
import com.incadencecorp.coalesce.services.common.api.IBlueprintController;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.EGraphNodeType;
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
import java.util.*;

/**
 * This controller exposes the underlying blueprints of a Karaf container.
 *
 * @author Derek Clemenzi
 */
public class BlueprintController implements IBlueprintController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintController.class);
    private static final List<String> IGNORE_LIST = Arrays.asList(CoalesceThreadFactoryImpl.class.getSimpleName(),
                                                                  CoalesceMapper.class.getSimpleName());

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

    @Override
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

    @Override
    public Graph getBlueprint(String name) throws RemoteException
    {
        Graph result = new Graph();

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
            Vertex serverNode = new Vertex();
            serverNode.setId(server.getAttribute("id"));
            serverNode.put("label", server.getAttribute("address"));
            serverNode.setType(EGraphNodeType.SERVER.toString());

            // Add Server Node
            result.getVertices().add(serverNode);

            LOGGER.debug("Processing Server: ({})", serverNode.getId());

            result.getEdges().addAll(linkServices(serverNode, server));
            result.getEdges().addAll(linkProviders(serverNode, server));
        }

        return result;
    }

    private Collection<Edge> linkServices(Vertex parent, Element server)
    {
        Element beans = (Element) server.getElementsByTagNameNS("http://cxf.apache.org/blueprint/jaxrs",
                                                                "serviceBeans").item(0);

        return link(parent, beans.getChildNodes());
    }

    private Collection<Edge> linkProviders(Vertex parent, Element server)
    {
        Element beans = (Element) server.getElementsByTagNameNS("http://cxf.apache.org/blueprint/jaxrs",
                                                                "providers").item(0);

        return link(parent, beans.getChildNodes());
    }

    private Collection<Edge> link(Vertex parent, NodeList children)
    {
        Collection<Edge> results = new ArrayList<>();

        // Link Server to Services
        for (int jj = 0; jj < children.getLength(); jj++)
        {
            if (children.item(jj) instanceof Element)
            {
                Element service = (Element) children.item(jj);

                Edge link = new Edge();

                switch (service.getLocalName())
                {
                case "ref":
                    link.setOutV(parent.getId());
                    link.setInV(service.getAttribute("component-id"));

                    results.add(link);
                    break;
                case "bean":
                    link.setOutV(parent.getId());
                    link.setInV(service.getAttribute("id"));

                    results.add(link);
                    break;
                }
            }
        }

        return results;
    }

    private void createNodes(Graph results, Document doc)
    {
        // Get All Beans
        NodeList beans = doc.getDocumentElement().getElementsByTagName("bean");

        // Create Nodes
        for (int ii = 0; ii < beans.getLength(); ii++)
        {
            Element bean = (Element) beans.item(ii);

            String classname = bean.getAttribute("class");
            String label = classname.substring(classname.lastIndexOf(".") + 1);

            Vertex node = new Vertex();
            node.setId(bean.getAttribute("id"));
            node.put("classname", classname);
            node.put("label", label);

            // Is Bean a standalone bean?
            if (StringHelper.isNullOrEmpty(node.getId()))
            {
                // No; Generate an ID that can be used for linking
                node.setId(UUID.randomUUID().toString());
                bean.setAttribute("id", node.getId());
            }

            if (!IGNORE_LIST.contains(label))
            {
                String simpleName = label.toLowerCase();

                // Determine Node Type
                if (simpleName.contains("persister") || simpleName.contains("persistor"))
                {
                    node.setType(EGraphNodeType.PERSISTER.toString());
                }
                else if (simpleName.contains("impl"))
                {
                    node.setType(EGraphNodeType.ENDPOINT.toString());
                }
                else if (simpleName.contains("controller"))
                {
                    if (simpleName.contains("jaxrs"))
                    {
                        node.setType(EGraphNodeType.CONTROLLER_ENDPOINT.toString());
                    }
                    else
                    {
                        node.setType(EGraphNodeType.CONTROLLER.toString());
                    }
                }
                else if (simpleName.equals("coalesceframework") || simpleName.equals("coalescesearchframework"))
                {
                    node.setType(EGraphNodeType.FRAMEWORK.toString());
                }
                else if (simpleName.contains("entity"))
                {
                    node.setType(EGraphNodeType.ENTITY.toString());
                }
                else if (simpleName.equals("serverconn"))
                {
                    node.setType(EGraphNodeType.SETTINGS.toString());
                }
                else if (simpleName.contains("client"))
                {
                    node.setType(EGraphNodeType.CLIENT.toString());
                }
                else
                {
                    node.setType(EGraphNodeType.OTHER.toString());
                }
            }
            else
            {
                node.setType(EGraphNodeType.OTHER.toString());
            }

            results.getVertices().add(node);
        }

        // Link Nodes
        for (int ii = 0; ii < beans.getLength(); ii++)
        {
            linkBeanRecursive(results, (Element) beans.item(ii), (Element) beans.item(ii));
        }
    }

    private void linkBeanRecursive(Graph results, Element bean, Element currentnode)
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

                    Edge link = new Edge();
                    link.setOutV(bean.getAttribute("id"));
                    link.setInV(node.getAttribute("component-id"));

                    results.getEdges().add(link);
                }
                else if (node.getNodeName().equalsIgnoreCase("bean"))
                {
                    LOGGER.debug("(BEAN) " + bean.getAttribute("id") + " -> " + node.getAttribute("id"));

                    Edge link = new Edge();
                    link.setOutV(bean.getAttribute("id"));
                    link.setInV(node.getAttribute("id"));

                    results.getEdges().add(link);

                    linkBeanRecursive(results, node, node);
                }
                else if (!StringHelper.isNullOrEmpty(node.getAttribute("ref")))
                {
                    LOGGER.debug("(REF Attr) " + bean.getAttribute("id") + " -> " + node.getAttribute("ref"));

                    Edge link = new Edge();
                    link.setOutV(bean.getAttribute("id"));
                    link.setInV(node.getAttribute("ref"));

                    results.getEdges().add(link);
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
