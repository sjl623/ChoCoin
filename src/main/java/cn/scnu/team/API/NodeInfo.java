package cn.scnu.team.API;

public class NodeInfo {
    public NodeInfo(String address, int port) {
        this.address=address;
        this.port = port;
    }

    public String address;
    public int port;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}