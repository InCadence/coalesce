package com.database.persister;

import java.util.Properties;

public class ServerConn {

    private String _url;
    private String _db;
    private String _user;
    private String _userPassword;
    private boolean _integratedSecurity;
    private String _serverName;
    private int _portNumber;
    private boolean _postGres=false;


    
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
        if(_postGres==false)
            return this._userPassword.trim();
        else
            return "password="+this._userPassword.trim();
    }

    public int getPortNumber()
    {
        return _portNumber;
    }

    public String getServerName()
    {
        return _serverName;
    }

    public String getURL()
    {
        return this._url.trim();
    }

    public String getUser()
    {
        if(_postGres==false)
            return this._user.trim();
        else
            return "user="+this._user.trim();
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
        this._userPassword = pass.trim();
    }

    public void setPortNumber(int _portNumber)
    {
        this._portNumber = _portNumber;
    }

    public void setServerName(String _serverName)
    {
        this._serverName = _serverName;
    }

    public void setURL(String url)
    {
        this._url = url.trim();
    }

    public void setUser(String user)
    {
        this._user = user.trim();
    }
}
