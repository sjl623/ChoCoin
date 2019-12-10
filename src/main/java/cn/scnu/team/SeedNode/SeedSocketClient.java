package cn.scnu.team.SeedNode;

import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.API.Response;
import cn.scnu.team.FullNode.FullNode;
import cn.scnu.team.LightNode.LightNode;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class SeedSocketClient extends WebSocketClient {
    private boolean isFullNode;
    public SeedSocketClient(URI serverUri,boolean isFullNode) {
        super(serverUri);
        this.isFullNode=isFullNode;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connect seed server success");
    }

    @Override
    public void onMessage(String s) {
        //System.out.println(s);
        Response response=JSON.parseObject(s,Response.class);
        if(response.getType().equals("nodeList")){
            List<NodeInfo> nodeInfo= JSON.parseArray(response.getContent(),NodeInfo.class);
            for(int i=0;i<nodeInfo.size();i++){
                try {
                    if(isFullNode) FullNode.addNode(nodeInfo.get(i));
                    else LightNode.addNode(nodeInfo.get(i));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        System.out.println("Connect seed server failed");
        try {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
