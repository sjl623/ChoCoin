package cn.scnu.team.FullNode;

import cn.hutool.crypto.asymmetric.RSA;
import cn.scnu.team.API.Message;
import cn.scnu.team.Transaction.TransDetail;
import cn.scnu.team.Transaction.Transaction;
import cn.scnu.team.Util.Encryption;
import cn.scnu.team.Util.Hash;
import com.alibaba.fastjson.JSON;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.rmi.CORBA.Util;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SocketServer extends WebSocketServer {
    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        //broadcast( "" );
        System.out.println("new connection from " + webSocket.getRemoteSocketAddress().getHostString() + ":" + webSocket.getRemoteSocketAddress().getPort());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println(s);
        Message message= JSON.parseObject(s,Message.class);
        if(message.getMethodName().equals("transaction")){
            Transaction transaction=JSON.parseObject(message.getParameter(),Transaction.class);
            TransDetail transDetail=JSON.parseObject(transaction.getDetailStr(),TransDetail.class);
            Encryption encryption=new Encryption();
            try {
                encryption.setPublicKey(transDetail.getFrom());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
            String origin=encryption.decryptPub(transaction.getSign());
            String goal=Hash.sha256(transaction.getDetailStr());
            if(!(origin.equals(goal))){
                System.out.printf("Signature check failed.%s %s",origin,goal);
            }
            else{
                System.out.println("A transaction has been stored,pending for pack...");
                if(!FullNode.toPackTrans.containsKey(goal)){
                    FullNode.toPackTrans.put(goal,transaction.getDetailStr());
                }
            }

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
        System.out.printf("server started successfully in %s:%d\n", this.getAddress().getHostName(), this.getAddress().getPort());
    }

}
