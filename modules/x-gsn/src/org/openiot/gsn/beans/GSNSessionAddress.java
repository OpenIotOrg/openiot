package org.openiot.gsn.beans;

public class GSNSessionAddress {
    protected String host;
    protected String path;
    protected int port;
    protected boolean needspassword;
    protected String username;
    protected String password;

    public GSNSessionAddress() {
        host="";
        path="";
        port=0;
        needspassword=false;
        username="";
        password="";
    }

    public GSNSessionAddress(String host, String path, int port, boolean needspassword, String username, String password) {
        this.host = host;
        this.path = path;
        this.port = port;
        this.needspassword = needspassword;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean needsPassword() {
        return needspassword;
    }

    public void setNeedsPassword(boolean needspassword) {
        this.needspassword = needspassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURL() {
        return "http://" + host+ ":"+ port + path;
    }

    @Override
    public String toString() {
        if (this.needspassword)
            return getURL()+"@"+username+":"+password;
        else
            return getURL();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GSNSessionAddress that = (GSNSessionAddress) o;

        if (port != that.port) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }
}
