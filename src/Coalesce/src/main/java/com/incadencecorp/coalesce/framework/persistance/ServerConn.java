package com.incadencecorp.coalesce.framework.persistance;

import java.util.Properties;

public class ServerConn {

    private String _db = "";
    private String _user = "";
    private String _password = "";
    private boolean _integratedSecurity = false;
    private String _serverName = "";
    private int _portNumber = 0;
    private boolean _postGres = false;
    Properties _props = new Properties();

    public boolean isPostGres()
    {
        return _postGres;
    }

    public void setPostGres(boolean _postGres)
    {
        this._postGres = _postGres;
    }

    public String getDatabase()
    {
        return this._db.trim();
    }

    public String getPassword()
    {
        if (_postGres == false)
            return this._password.trim();
        else
        {
            return this._props.getProperty(_password);
        }
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
        if (_postGres == false)
            return this._user.trim();
        else
        {
            return this._props.getProperty(_user);
        }
    }

    public boolean isIntegratedSecurity()
    {
        return _integratedSecurity;
    }

    public void setDatabase(String db)
    {
        this._db = db.trim();
    }

    public void setIntegratedSecurity(boolean _integratedSecurity)
    {
        this._integratedSecurity = _integratedSecurity;
    }

    public void setPassword(String pass)
    {
        this._password = pass.trim();
        this._props.setProperty("password", this._password);
    }

    public void setPortNumber(int _portNumber)
    {
        this._portNumber = _portNumber;
    }

    public void setServerName(String _serverName)
    {
        this._serverName = _serverName;
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
