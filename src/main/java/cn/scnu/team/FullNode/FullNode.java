package cn.scnu.team.FullNode;

import cn.scnu.team.API.Message;
import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.Account.Account;
import cn.scnu.team.BlockChain.Block;
import cn.scnu.team.SeedNode.SeedSocketClient;
import cn.scnu.team.Util.Hash;
import cn.scnu.team.Util.Merkle;
import com.alibaba.fastjson.JSON;
import org.java_websocket.enums.ReadyState;

import java.io.IOException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class FullNode {
    private static SocketServer socketServer;
    private static SeedSocketClient seedSocketClient;

    static Vector<SocketClient> nodeSocket = new Vector<>();
    private static Map<String, Boolean> isConnect= new HashMap<>();

    static Map<String,String> toPackTrans=new HashMap<>();
    static Vector<Block> block=new Vector<>();

    static Account account;

    static class Pack extends Thread{
        public void run(){
            while(true){
                System.out.println("hi");

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if(toPackTrans.size()==0) continue;;
                Random r = new Random();
                Merkle merkle=new Merkle();
                Vector<String> nowAllTrans = new Vector<>();
                System.out.println("build3");
                for(String nowTrans:toPackTrans.values()){
                    merkle.add(nowTrans);
                    nowAllTrans.add(nowTrans);
                }
                System.out.println("build2");
                merkle.build();
                String rootHash=merkle.tree.get(merkle.tree.size() - 1).get(0);
                System.out.println("build");
                String preHash="";
                if(block.size()==0) preHash= Hash.sha256("");
                else preHash= block.get(block.size() - 1).getPreHash();
                int nonce=r.nextInt();
                Block newBlock=new Block(preHash,rootHash,"",nonce,System.currentTimeMillis(),account.encryption.getPublicKeyStr());
                String BlockSha256=Hash.sha256(JSON.toJSONString(newBlock));
                //System.out.println(BlockSha256);
                int count=0;
                for(int i=0;i<BlockSha256.length();i++){
                    if(BlockSha256.charAt(i)=='0') count++;
                    else break;
                }
                if(count>=2){//调整该值即调整挖矿难度
                    System.out.println("Found!!!");
                    System.out.println(JSON.toJSONString(newBlock));
                    System.out.println(BlockSha256);
                }
            }
        }
    }


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
            nodeSocket.add(socketClient);
        }
    }


    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.print("Enter account's file name:");
        Scanner scanner=new Scanner(System.in);
        String filename=scanner.next();
        account=new Account();
        account.loadInfo(filename);

        Thread server = new Server();
        server.start();
        seedSocketClient = new SeedSocketClient(new URI("ws://localhost:5000"),true);
        seedSocketClient.connect();

        NodeInfo nodeInfo=new NodeInfo(socketServer.getAddress().getHostName(),socketServer.getAddress().getPort());
        //System.out.println(JSON.toJSONString(nodeInfo));
        System.out.println("Linking to the seed node...");
        while (!seedSocketClient.getReadyState().equals(ReadyState.OPEN)) {
        }
        Message message=new Message("add",JSON.toJSONString(nodeInfo));
        seedSocketClient.send(JSON.toJSONString(message));

        Timer nodeQueryTimer=new Timer();
        nodeQueryTimer.scheduleAtFixedRate(new updateNode(),1000,5000);

        Thread pack =new Pack();
        pack.start();

    }
}
