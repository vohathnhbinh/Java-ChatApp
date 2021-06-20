package myClasses;

import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;

public class Server implements Serializable {
    private String server_name;
    private int port;
    private boolean isOnline = false;

    public Server(String server_name, int port) {
        this.server_name = server_name;
        this.port = port;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return port == server.port && Objects.equals(server_name, server.server_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server_name, port);
    }
}
