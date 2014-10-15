package com.incadencecorp.coalesce.framework.persistance;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

/**
 * Defines default behavior for Data Connectors that extends this abstract class.
 */
public abstract class CoalesceDataConnectorBase implements AutoCloseable {

    /*-----------------------------------------------------------------------------'
    Private Member Variables
    -----------------------------------------------------------------------------*/

    protected Connection _conn = null;
    protected ServerConn _settings;

    /*-----------------------------------------------------------------------------'
    Abstract Functions
    -----------------------------------------------------------------------------*/

    public abstract void openConnection() throws SQLException;

    protected abstract String getProcedurePrefix();

    /*-----------------------------------------------------------------------------'
    Public Functions
    -----------------------------------------------------------------------------*/

    /**
     * Returns the results from the executed SQL Command.
     * 
     * @param sql the statement to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     * @throws CoalescePersistorException
     */
    public final ResultSet executeQuery(final String sql, final CoalesceParameter... parameters) throws SQLException
    {

        openDataConnection();

        CallableStatement stmt = this._conn.prepareCall(sql);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].getValue().trim(), parameters[ii].getType());
        }

        return stmt.executeQuery();

    }

    /**
     * Returns the results from the executed SQL Command, that contains LIKE wildcards.
     * 
     * @param sql the statement to be executed against the database.
     * @param likeParams the number of like parameters in the SQL statement
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     * @throws CoalescePersistorException
     */
    public final ResultSet executeLikeQuery(final String sql, final int likeParams, final CoalesceParameter... parameters)
            throws SQLException
    {

        openDataConnection();

        CallableStatement stmt = this._conn.prepareCall(sql);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii + 1 <= likeParams)
            {
                stmt.setObject(ii + 1, "%" + parameters[ii].getValue().trim() + "%", parameters[ii].getType()); // Like
            }
            else
            {
                stmt.setObject(ii + 1, parameters[ii].getValue().trim(), parameters[ii].getType()); // Normal
            }
        }

        return stmt.executeQuery();

    }

    /**
     * Executes a SQL statement on a database.
     * 
     * @param sql the statement to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return true = success
     * @throws SQLException
     */
    public final boolean executeCmd(final String sql, final CoalesceParameter... parameters) throws SQLException
    {
        openDataConnection();

        CallableStatement stmt = this._conn.prepareCall(sql);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].getValue().trim(), parameters[ii].getType());
        }

        stmt.executeUpdate();

        return true;
    }

    /**
     * Executes a SQL statement on a database.
     * 
     * @param sql the statement to be executed against the database.
     * @return true = success
     * @throws SQLException
     */
    public final boolean executeCmd(final String sql) throws SQLException
    {
        openDataConnection();

        CallableStatement stmt = this._conn.prepareCall(sql);

        stmt.executeUpdate();

        return true;
    }

    /**
     * Executes a stored procedure(or function) on a database.
     * 
     * @param procedureName the name of the stored procedure to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return true = success
     * @throws SQLException
     */
    public final boolean executeProcedure(final String procedureName, final CoalesceParameter... parameters)
            throws SQLException
    {

        // Compile SQL Command
        StringBuilder sb = new StringBuilder("{" + getProcedurePrefix() + procedureName + " (");

        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii != 0)
            {
                sb.append(",");
            }
            
            sb.append("?");
        }

        sb.append(")}");

        // TODO: Implement Retry

        openDataConnection();

        CallableStatement stmt = this._conn.prepareCall(sb.toString());

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            String value = parameters[ii].getValue();
            if (value != null)
            {
                value = value.trim();
            }

            stmt.setObject(ii + 1, value, parameters[ii].getType());
        }

        stmt.executeUpdate();

        return true;

    }

    /**
     * Returns meta data for Coalesce Entity Template as XML.
     * 
     * @param sql statement specifying the elements to be returned.
     * @return XML of meta data for template.
     * @throws SQLException
     * @throws ParserConfigurationException
     */
    public final String getTemplateMetaData(final String sql) throws SQLException, ParserConfigurationException
    {

        // Open Connection if not already created
        openDataConnection();

        CallableStatement stmt = this._conn.prepareCall(sql);
        ResultSet rs = stmt.executeQuery();
        DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
        Document domDocument = docBuild.newDocument();
        Element rootEle = domDocument.createElement("coalescetemplate");
        domDocument.appendChild(rootEle);
        String xmlTemplate = "";
        while (rs.next())
        {

            Element mdElement = domDocument.createElement("metadata");

            Element tempKey = domDocument.createElement("templatekey");
            Text tempKeyText = domDocument.createTextNode((String) rs.getObject(1));
            tempKey.appendChild(tempKeyText);
            mdElement.appendChild(tempKey);

            Element tempNAME = domDocument.createElement("name");
            Text tempNAMEText = domDocument.createTextNode((String) rs.getObject(2));
            tempNAME.appendChild(tempNAMEText);
            mdElement.appendChild(tempNAME);

            Element tempSource = domDocument.createElement("source");
            Text tempSourceText = domDocument.createTextNode((String) rs.getObject(3));
            tempSource.appendChild(tempSourceText);
            mdElement.appendChild(tempSource);

            Element tempVersion = domDocument.createElement("version");
            Text tempVersionText = domDocument.createTextNode((String) rs.getObject(4));
            tempVersion.appendChild(tempVersionText);
            mdElement.appendChild(tempVersion);

            Element tempCreated = domDocument.createElement("datecreated");
            Text tempCreatedText = null;
            if (rs.getObject(5) != null)
                tempCreatedText = domDocument.createTextNode(rs.getString(5));
            else
                tempCreatedText = domDocument.createTextNode("");
            tempCreated.appendChild(tempCreatedText);
            mdElement.appendChild(tempCreated);

            Element tempModified = domDocument.createElement("lastmodified");
            Text tempModifiedText = null;
            if (rs.getObject(6) != null)
                tempModifiedText = domDocument.createTextNode(rs.getString(6));
            else
                tempModifiedText = domDocument.createTextNode("");
            tempModified.appendChild(tempModifiedText);
            mdElement.appendChild(tempModified);
            rootEle.appendChild(mdElement);
            xmlTemplate = XmlHelper.formatXml(domDocument);
        }

        return xmlTemplate;

    }

    @Override
    public final void close() throws Exception
    {
        if (this._conn != null)
        {
            if (!this._conn.getAutoCommit())
            {
                this._conn.commit();
            }

            this._conn.close();
        }
    }

    /*-----------------------------------------------------------------------------'
    Protected Functions
    -----------------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------------'
    Private Functions
    -----------------------------------------------------------------------------*/

    private void openDataConnection() throws SQLException
    {
        if (this._conn == null)
        {
            this.openConnection();
        }
    }

}
