package com.incadencecorp.coalesce.framework.persistance;

import java.util.Properties;

/**
 * Stores credentials and other settings for database connections.
 * 
 * @see CoalesceDataConnectorBase
 */
public class ServerConn {

    private String _db = "";
    private String _user = "";
    private String _password = "";
    private boolean _integratedSecurity = false;
    private String _serverName = "";
    private int _portNumber = 0;
    private Properties _props = new Properties();

    public String getDatabase()
    {
        return this._db.trim();
    }

    public String getPassword()
    {
        return this._password;
    }

    public int getPortNumber()
    {
        return _portNumber;
    }

    public String getServerNameWithPort()
    {
        if (_portNumber == 0)
        {
            return _serverName;
        }
        else
        {
            return _serverName + ":" + _portNumber;
        }
    }

    public String getServerName()
    {
        return _serverName;
    }

    public String getUser()
    {

        return this._user;
    }

    public boolean isIntegratedSecurity()
    {
        return _integratedSecurity;
    }

    public void setDatabase(String db)
    {
        this._db = db.trim();
    }

    public void setIntegratedSecurity(boolean integratedSecurity)
    {
        this._integratedSecurity = integratedSecurity;
    }

    public void setPassword(String pass)
    {
        this._password = pass.trim();
        this._props.setProperty("password", this._password);
    }

    public void setPortNumber(int portNumber)
    {
        this._portNumber = portNumber;
    }

    public void setServerName(String serverName)
    {
        this._serverName = serverName;
    }

    public void setUser(String user)
    {
        this._user = user.trim();
        this._props.setProperty("user", this._user);
    }

    public Properties getProperties()
    {
        return _props;
    }
}
