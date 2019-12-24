package cn.scnu.team.FullNode;

import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.API.Response;
import cn.scnu.team.Transaction.Detail;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static cn.scnu.team.Util.Other.timeStamp2Date;

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
        if(response.getType().equals("balance")){
            System.out.printf("The balance is %s.\n",response.getContent());
        }
        if(response.getType().equals("Detail")){
            System.out.println("Here are the details of the given account");
            List<Detail> details=JSON.parseArray(response.getContent(),Detail.class);
            for(Detail nowDetail:details){
                if(nowDetail.getType()==0){
                    System.out.printf("Got %f pts as reward at %s\n",nowDetail.getAmount(), timeStamp2Date(nowDetail.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                }
                if(nowDetail.getType()==1){
                    System.out.printf("Got %f pts from %s at %s\n",nowDetail.getAmount(),nowDetail.getFrom(),timeStamp2Date(nowDetail.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                }
                if(nowDetail.getType()==2){
                    System.out.printf("Sent %f pts to %s at %s\n",nowDetail.getAmount(),nowDetail.getTo(),timeStamp2Date(nowDetail.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        try {
            throw e;
        } catch (Exception ex) {
            System.out.println("Connect to a node failed.");
        }
    }
}
