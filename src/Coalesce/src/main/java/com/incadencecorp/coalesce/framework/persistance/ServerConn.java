package com.incadencecorp.coalesce.framework.persistance;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

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

    public ServerConn() {
    }

    public ServerConn(String db, String user, String password, String serverName, int port) {
        this(db, user, password, serverName, port, new Properties(), false);
    }

    private ServerConn(String db, String user, String password, String serverName, int port, Properties properties, boolean integratedSecurity) {
        this._props = new Properties(properties);
        setDatabase(StringUtils.defaultString(db));
        setUser(StringUtils.defaultString(user));
        setPassword(StringUtils.defaultString(password));
        setServerName(StringUtils.defaultString(serverName));
        setPortNumber(port);
    }

    public String getDatabase() {
        return this._db.trim();
    }

    public String getPassword() {
        return this._password;
    }

    public int getPortNumber() {
        return _portNumber;
    }

    public String getServerNameWithPort() {
        return _portNumber == 0 ? _serverName : _serverName + ":" + _portNumber;
    }

    public String getServerName() {
        return _serverName;
    }

    public String getUser() {
        return this._user;
    }

    public boolean isIntegratedSecurity() {
        return _integratedSecurity;
    }

    public void setDatabase(String db)    {
        this._db = db.trim();
    }

    public void setIntegratedSecurity(boolean integratedSecurity) {
        this._integratedSecurity = integratedSecurity;
    }

    public void setPassword(String pass) {
        this._password = pass.trim();
        this._props.setProperty("password", this._password);
    }

    public void setPortNumber(int portNumber) {
        this._portNumber = portNumber;
    }

    public void setServerName(String serverName) {
        this._serverName = serverName;
    }

    public void setUser(String user)    {
        this._user = user.trim();
        this._props.setProperty("user", this._user);
    }

    public Properties getProperties() {
        return _props;
    }
    
    public static class Builder {
        private String db = "";
        private String user = "";
        private String password = "";
        private boolean integratedSecurity = false;
        private String serverName = "";
        private int port = 0;
        private Properties properties = new Properties();
        
        public Builder db(String db) {
            this.db = db;
            return this;
        }
        
        public Builder user(String user) {
            this.user = user;
            return this;
        }
        
        public Builder password (String password) {
            this.password = password;
            return this;
        }
        
        public Builder integratedSecurity(boolean integratedSecurity) {
            this.integratedSecurity = integratedSecurity;
            return this;
        }
        
        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }
        
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        
        public Builder properties(Properties properties) {
            this.properties = new Properties(properties);
            return this;
        }
        
        public Builder copyOf(ServerConn serverConnection) {
          db = serverConnection.getDatabase();
          user = serverConnection.getUser();
          password = serverConnection.getPassword();
          integratedSecurity = serverConnection.isIntegratedSecurity();
          serverName = serverConnection.getServerName();
          port = serverConnection.getPortNumber();
          properties = new Properties(serverConnection.getProperties());
          return this;    
        }
        
        public ServerConn build() {
            return new ServerConn(db,user,password,serverName,port,properties,integratedSecurity);
        }
        
    }
}
