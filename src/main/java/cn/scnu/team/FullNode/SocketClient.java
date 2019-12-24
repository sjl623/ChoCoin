package cn.scnu.team.FullNode;

import cn.scnu.team.API.NodeInfo;
import cn.scnu.team.API.Response;
import cn.scnu.team.BlockChain.Block;
import cn.scnu.team.Transaction.Detail;
import cn.scnu.team.Transaction.TransDetail;
import cn.scnu.team.Util.Config;
import cn.scnu.team.Util.Hash;
import cn.scnu.team.Util.Merkle;
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
        Response response = JSON.parseObject(s, Response.class);
        if (response.getType().equals("balance")) {
            System.out.printf("The balance is %s.\n", response.getContent());
        }
        if (response.getType().equals("Detail")) {
            System.out.println("Here are the details of the given account");
            List<Detail> details = JSON.parseArray(response.getContent(), Detail.class);
            for (Detail nowDetail : details) {
                if (nowDetail.getType() == 0) {
                    System.out.printf("Got %f pts as reward at %s\n", nowDetail.getAmount(), timeStamp2Date(nowDetail.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                }
                if (nowDetail.getType() == 1) {
                    System.out.printf("Got %f pts from %s at %s\n", nowDetail.getAmount(), nowDetail.getFrom(), timeStamp2Date(nowDetail.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                }
                if (nowDetail.getType() == 2) {
                    System.out.printf("Sent %f pts to %s at %s\n", nowDetail.getAmount(), nowDetail.getTo(), timeStamp2Date(nowDetail.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        if (response.getType().equals("newBlock")) {
            synchronized (FullNode.globalLock) {
                Block newBlock = JSON.parseObject(response.getContent(), Block.class);
                if (FullNode.block.size() == 0 || FullNode.block.get(FullNode.block.size() - 1).getRootMerkleHash().equals(newBlock.getPreHash())) {
                    //check whether valid
                    int count = 0;
                    String BlockSha256 = Hash.sha256(response.getContent());
                    for (int i = 0; i < BlockSha256.length(); i++) {
                        if (BlockSha256.charAt(i) == '0') count++;
                        else break;
                    }
                    if (count >= Config.difficulty) {
                        List<TransDetail> newTrans = newBlock.getTransDetail();
                        Merkle merkle = new Merkle();
                        for (TransDetail nowTrans : newTrans) {
                            merkle.add(JSON.toJSONString(nowTrans));
                        }
                        merkle.build();
                        if (merkle.tree.get(merkle.tree.size() - 1).get(0).equals(newBlock.getRootMerkleHash())) {
                            FullNode.block.add(newBlock);
                            for (TransDetail nowNewTrans : newTrans) {
                                String transHash = Hash.sha256(JSON.toJSONString(nowNewTrans));
                                if (FullNode.toPackTrans.containsKey(transHash)) {
                                    FullNode.toPackTrans.remove(transHash);
                                }
                            }
                            System.out.println("Accept a block from other node.");
                        } else {
                            System.out.println("Hash check failed");
                        }
                    } else {
                        System.out.println("Difficulty check failed");
                    }
                } else {
                    System.out.println("PreHash check failed,pending to sync to the main chain.");
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
