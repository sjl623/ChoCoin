package cn.scnu.team.FullNode;

import cn.scnu.team.API.Message;
import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.Account.Account;
import cn.scnu.team.BlockChain.Block;
import cn.scnu.team.LightNode.mainPage;
import cn.scnu.team.Pow.Pow;
import cn.scnu.team.SeedNode.SeedSocketClient;
import cn.scnu.team.Transaction.TXOutput;
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

    public static Map<String,String> toPackTrans=new HashMap<>();
    public static Vector<Block> block=new Vector<>();
    public static Map<String, TXOutput[]> utxos=new HashMap<>();

   /* public static TXOutput[] getOutputsByPubKeyInUtxos(String key){
        TXOutput[] txOutputs={};
        for(TXOutput txOutput:utxos){
            if(txOutput.getPublicKeyHash()==key){
                ArrayUtils.add(txOutputs,txOutput);
            }
        }
        return txOutputs;
    }*/
    public static Account account;
    public static final Object globalLock=new Object();

    static class Pack extends Thread{
        public void run(){
            while(true){//尝试打包交易
                //System.out.println("hi");
                try {
                    //System.out.println(JSON.toJSONString(block));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (globalLock){
                    Block res=Pow.pack();
                    if(res!=null){
                        Block nowBlock=res;
                        block.add(nowBlock);
                        for (SocketClient nowSocket:nodeSocket) {
                            if(nowSocket.isOpen()){
                                System.out.println("Send to a node");
                                Message message=new Message("newBlock",JSON.toJSONString(res));
                                nowSocket.send(JSON.toJSONString(message));
                            }
                        }
                        toPackTrans.clear();
                        System.out.println(JSON.toJSONString(block));
                    }
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

    private static class updateNode extends TimerTask {//定时任务，更新相邻节点
        @Override
        public void run(){
            if(nodeSocket.size()<=200){//get new node while connection less 200
                seedSocketClient.send(JSON.toJSONString(new Message("query","")));
            }
        }
    }

    private static class syncBlock extends TimerTask {//定时任务，同步区块
        @Override
        public void run(){
            for (SocketClient nowSocket:nodeSocket) {
                if(nowSocket.isOpen()){
                    System.out.printf("Try to sync %d block...\n",block.size());
                    Message message=new Message("getBlock",String.valueOf(block.size()));
                    nowSocket.send(JSON.toJSONString(message));
                }
            }
        }
    }

    public static void addNode(NodeInfo newNodeInfo) throws URISyntaxException {
        synchronized (FullNode.globalLock){
            if(!isConnect.containsKey(newNodeInfo.address+":"+newNodeInfo.port)){
                try{
                    SocketClient socketClient=new SocketClient(new URI("ws://"+newNodeInfo.address+":"+newNodeInfo.port));
                    socketClient.connect();
                }catch (Exception e){
                    return;//connection failed;
                }

                isConnect.put(newNodeInfo.address+":"+newNodeInfo.port,true);
                //nodeSocket.add(socketClient);
            }
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

        Timer syncBlockTimer=new Timer();
        syncBlockTimer.scheduleAtFixedRate(new syncBlock(),1000,5000);

        Thread pack =new Pack();
        pack.start();

    }
}
