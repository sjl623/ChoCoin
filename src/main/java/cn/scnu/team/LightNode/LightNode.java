package cn.scnu.team.LightNode;

import cn.scnu.team.API.Message;
import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.Account.Account;
import cn.scnu.team.SeedNode.SeedSocketClient;
import cn.scnu.team.FullNode.SocketClient;
import cn.scnu.team.Transaction.TransDetail;
import cn.scnu.team.Transaction.Transaction;
import cn.scnu.team.Util.Config;
import cn.scnu.team.Util.Hash;
import com.alibaba.fastjson.JSON;
import io.airlift.airline.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class LightNode {
    private static Map<String, Boolean> isConnect = new HashMap<>();
    private static Vector<SocketClient> nodeSocket = new Vector<>();
    private static SeedSocketClient seedSocketClient;

    static Scanner scanner;
    static Account accountInfo;

    private static class updateNode extends TimerTask {
        @Override
        public void run() {
            if (nodeSocket.size() <= 200) {//get new node while connection less 200
                seedSocketClient.send(JSON.toJSONString(new Message("query", "")));
            }
        }
    }

    public static void addNode(NodeInfo newNodeInfo) throws URISyntaxException {
        if (!isConnect.containsKey(newNodeInfo.address + ":" + newNodeInfo.port)) {
            SocketClient socketClient = new SocketClient(new URI("ws://" + newNodeInfo.address + ":" + newNodeInfo.port));
            socketClient.connect();
            isConnect.put(newNodeInfo.address + ":" + newNodeInfo.port, true);
            nodeSocket.add(socketClient);
        }
    }

    @Command(name = "transfer", description = "Transfer some coin to other account.")
    public static class Transfer implements Runnable {
        @Option(name = {"-a"}, description = "The goal account address", required = true)
        String account;
        @Option(name = {"-m"}, description = "The amount", required = true)
        Double amount;

        @Override
        public void run() {
            TransDetail transDetail = new TransDetail(accountInfo.info.getPublicKey(), account, amount, String.valueOf(System.currentTimeMillis()));
            String transDetailStr = JSON.toJSONString(transDetail);
            String detailHash= Hash.sha256(transDetailStr);
            Transaction transaction = new Transaction(transDetailStr, accountInfo.encryption.encryptPrivate(detailHash));
            String transactionStr = JSON.toJSONString(transaction);
            Message message=new Message("transaction",transactionStr);
            String messageStr=JSON.toJSONString(message);
            for (SocketClient nowSocket:nodeSocket) {
                if(nowSocket.isOpen())  nowSocket.send(messageStr);
            }
            System.out.println(transactionStr);
        }
    }

    @Command(name="balance",description = "Query the balance of an account,default to now account")
    public static class Balance implements Runnable{
        @Option(name={"-a"},description = "The goal account address")
        String account="";

        @Override
        public void run() {
            if(account.equals("")){
                account=accountInfo.info.getPublicKey();
            }
            Message message=new Message("balance",account);
            String messageStr=JSON.toJSONString(message);
            for(SocketClient nowSocket:nodeSocket){
                if(nowSocket.isOpen()) {nowSocket.send(messageStr);break;}
            }
            System.out.println(messageStr);
        }
    }

    @Command(name="detail",description = "Query the transactions of an account")
    public static class Detail implements Runnable{
        @Option(name={"-a"},description = "The goal account address")
        String account="";


        @Override
        public void run() {
            if(account.equals("")){
                account=accountInfo.info.getPublicKey();
            }
            Message message=new Message("detail",account);
            String messageStr= JSON.toJSONString(message);
            for(SocketClient nowSocket:nodeSocket){
                if(nowSocket.isOpen()) {nowSocket.send(messageStr);break;}
            }
            System.out.println(messageStr);
        }
    }

    @Command(name="address",description = "display your wallet address")
    public static class Address implements Runnable{

        @Override
        public void run() {
            System.out.printf("%s\n",accountInfo.encryption.getPublicKeyStr());
        }
    }

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, URISyntaxException {
        System.out.print("Enter account's file name:");
        scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        accountInfo = new Account();
        accountInfo.loadInfo(filename);

        seedSocketClient = new SeedSocketClient(new URI(Config.nodeAdd), false);
        seedSocketClient.connect();
        System.out.println("Linking to the seed node...");
        while (!seedSocketClient.getReadyState().equals(ReadyState.OPEN)) {
        }
        Timer nodeQueryTimer = new Timer();
        nodeQueryTimer.scheduleAtFixedRate(new updateNode(), 1000, 5000);


        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("ChoCoin")
                .withDescription("ChoCoin Light node")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class, Transfer.class,Balance.class,Detail.class,Address.class);//注册命令行对象


        Cli<Runnable> commandParser = builder.build();
        System.out.println("Welcome to use ChoCoin,type \"help\" to get help");
        while (true) {
            String nowCom = scanner.nextLine();
            String[] nowArg = nowCom.split(" ");
            try {
                commandParser.parse(nowArg).run();
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                System.out.println("Invalid command,type \"help\" for usage instructions.");
            }
        }

    }


}
