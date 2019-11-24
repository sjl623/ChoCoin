package cn.scnu.team.FullNode;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class FullNode {
    static class Server extends Thread{
        public void run(){
            String host="localhost";
            int port=9000;

            SocketServer socketServer=new SocketServer(new InetSocketAddress(host,port));
            socketServer.run();
        }
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        Thread server=new Server();
        server.start();
        //Thread.sleep(1000);
        SocketClient socketClient=new SocketClient(new URI("ws://localhost:9000"));
        socketClient.connect();
    }
}
