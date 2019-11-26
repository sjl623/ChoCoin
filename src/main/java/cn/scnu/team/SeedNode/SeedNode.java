package cn.scnu.team.SeedNode;

import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.API.Message;

import cn.scnu.team.API.Response;
import com.alibaba.fastjson.JSON;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SeedNode {


    private static Vector<NodeInfo> allNode;
    private static Map<String, Boolean> isLog= new HashMap<>();

    public static class SeedServer extends WebSocketServer {

        public SeedServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {

        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            Message message = JSON.parseObject(s, Message.class);
            if (message.methodName.equals("add")) {
                NodeInfo nodeInfo = JSON.parseObject(message.parameter,NodeInfo.class);
                String now=webSocket.getRemoteSocketAddress().getHostString()+":"+String.valueOf(nodeInfo.port);
                //System.out.println(now);
                Boolean isExisted=isLog.get(now);
                if(isExisted==null||!isExisted) {
                    allNode.add(new NodeInfo(webSocket.getRemoteSocketAddress().getHostString(), nodeInfo.port));
                    isLog.put(now, true);
                    System.out.printf("A node from %s:%d added\n", webSocket.getRemoteSocketAddress().getHostString(), nodeInfo.port);
                }
            }

            if(message.methodName.equals("query")){
                String result= JSON.toJSONString(allNode);
                Response response=new Response("nodeList",result);
                webSocket.send(JSON.toJSONString(response));
            }

        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            try {
                throw e;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onStart() {
            System.out.printf("Seed Server started in %s:%d\n", this.getAddress().getHostString(), this.getAddress().getPort());
            allNode= new Vector<>();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        SeedServer seedServer = new SeedServer(new InetSocketAddress("localhost", 5000));
        seedServer.run();
        //seedServer.stop();
    }
}
