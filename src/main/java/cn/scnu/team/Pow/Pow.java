package cn.scnu.team.Pow;

import cn.scnu.team.BlockChain.Block;
import cn.scnu.team.FullNode.FullNode;
import cn.scnu.team.Util.Config;
import cn.scnu.team.Util.Hash;
import cn.scnu.team.Util.Merkle;
import com.alibaba.fastjson.JSON;

import java.util.Random;
import java.util.Vector;

public class Pow {
    public static String pack(){

        if(FullNode.toPackTrans.size()==0) return null;
        Random r = new Random();
        Merkle merkle=new Merkle();
        Vector<String> nowAllTrans = new Vector<>();
        //System.out.println("build3");
        for(String nowTrans:FullNode.toPackTrans.values()){
            merkle.add(nowTrans);
            nowAllTrans.add(nowTrans);
        }
        //System.out.println("build2");
        merkle.build();
        String rootHash=merkle.tree.get(merkle.tree.size() - 1).get(0);
        //System.out.println("build");
        String preHash="";
        if(FullNode.block.size()==0) preHash= Hash.sha256("");
        else preHash= FullNode.block.get(FullNode.block.size() - 1).getRootMerkleHash();
        int nonce=r.nextInt();
        Block newBlock=new Block(preHash,rootHash,JSON.toJSONString(nowAllTrans),nonce,System.currentTimeMillis(),FullNode.account.encryption.getPublicKeyStr());
        String BlockSha256=Hash.sha256(JSON.toJSONString(newBlock));
        //System.out.println(BlockSha256);
        int count=0;
        for(int i=0;i<BlockSha256.length();i++){
            if(BlockSha256.charAt(i)=='0') count++;
            else break;
        }
        if(count>= Config.difficulty){//调整该值即调整挖矿难度
            System.out.println("A new block has been found!!!");
            System.out.println(JSON.toJSONString(newBlock));
            System.out.println(BlockSha256);
            return JSON.toJSONString(newBlock);
        }else{
            return null;
        }
    }
}
