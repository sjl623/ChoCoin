package cn.scnu.team.LightNode;

import cn.scnu.team.API.Message;
import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.Account.Account;
import cn.scnu.team.SeedNode.SeedSocketClient;
import cn.scnu.team.FullNode.SocketClient;
import com.alibaba.fastjson.JSON;
import org.java_websocket.enums.ReadyState;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class LightNode {
    private static Map<String, Boolean> isConnect= new HashMap<>();
    private static Vector<SocketClient> nodeSocket = new Vector<>();
    private static SeedSocketClient seedSocketClient;

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

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, URISyntaxException {
        System.out.print("Enter account's file name:");
        Scanner scanner=new Scanner(System.in);
        String filename=scanner.next();
        Account account=new Account();
        account.loadInfo(filename);

        seedSocketClient = new SeedSocketClient(new URI("ws://localhost:5000"),false);
        seedSocketClient.connect();
        System.out.println("Linking to the seed node...");
        while (!seedSocketClient.getReadyState().equals(ReadyState.OPEN)) {
        }
        Timer nodeQueryTimer=new Timer();
        nodeQueryTimer.scheduleAtFixedRate(new updateNode(),1000,5000);
        while(true){
            String nowCom=scanner.next();
            System.out.println(nowCom);
        }
    }
}
