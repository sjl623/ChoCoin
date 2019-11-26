package cn.scnu.team.FullNode;

import cn.scnu.team.API.Message;
import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.Util.Hash;
import com.alibaba.fastjson.JSON;
import org.java_websocket.enums.ReadyState;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class FullNode {
    private static SocketServer socketServer;
    private static SeedSocketClient seedSocketClient;
    public static Vector<SocketClient> nodeSocket = new Vector<>();
    private static Map<String, Boolean> isConnect= new HashMap<>();
    static class Server extends Thread {
        public void run() {
            String host = "0.0.0.0";
            int port = 0;
            try {
                ServerSocket s = new ServerSocket(0);
                port=s.getLocalPort();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            socketServer = new SocketServer(new InetSocketAddress(host, port));
            socketServer.run();
        }
    }

    private static class updateNode extends TimerTask {
        @Override
        public void run(){
            if(nodeSocket.size()<=200){//get new node while connection less 200
                seedSocketClient.send(JSON.toJSONString(new Message("query","")));
            }
        }
    }

    public static void addNode(NodeInfo newNodeInfo) throws URISyntaxException {
        if(!isConnect.containsKey(newNodeInfo.address+":"+newNodeInfo.port)){
            SocketClient socketClient=new SocketClient(new URI("ws://"+newNodeInfo.address+":"+newNodeInfo.port));
            socketClient.connect();
            isConnect.put(newNodeInfo.address+":"+newNodeInfo.port,true);
        }
    }


    public static void main(String[] args) throws URISyntaxException, InterruptedException, MalformedURLException {
        Thread server = new Server();
        server.start();
        seedSocketClient = new SeedSocketClient(new URI("ws://localhost:5000"));
        seedSocketClient.connect();

        NodeInfo nodeInfo=new NodeInfo(socketServer.getAddress().getHostName(),socketServer.getAddress().getPort());
        //System.out.println(JSON.toJSONString(nodeInfo));
        while (!seedSocketClient.getReadyState().equals(ReadyState.OPEN)) {
        }
        Message message=new Message("add",JSON.toJSONString(nodeInfo));
        seedSocketClient.send(JSON.toJSONString(message));

        Timer nodeQueryTimer=new Timer();
        nodeQueryTimer.scheduleAtFixedRate(new updateNode(),1000,5000);

    }
}
