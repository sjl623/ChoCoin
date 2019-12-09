package cn.scnu.team.FullNode;

import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.API.Response;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class SocketClient extends WebSocketClient {
    public SocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("connect to a node success.");
        FullNode.nodeSocket.add(this);
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
        Response response= JSON.parseObject(s,Response.class);

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        try {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
