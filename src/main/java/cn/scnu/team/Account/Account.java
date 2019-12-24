package cn.scnu.team.Account;

import cn.hutool.core.io.file.FileReader;
import cn.scnu.team.BlockChain.Block;
import cn.scnu.team.FullNode.FullNode;
import cn.scnu.team.Transaction.Detail;
import cn.scnu.team.Transaction.TransDetail;
import cn.scnu.team.Util.Config;
import cn.scnu.team.Util.Encryption;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Account {
    public class AccountInfo {
        String publicKey;
        String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public AccountInfo(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

    }

    public AccountInfo info;
    public Encryption encryption;

    public Account() {
        encryption = new Encryption();
    }

    public Account(String publicKey, String privateKey) {
        encryption = new Encryption();
        info = new AccountInfo(publicKey, privateKey);
    }


    public void loadInfo(String path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {//加载账户文件
        //File file=new File(path);
        boolean isExists = true;
        try {
            FileReader fileReader = new FileReader(System.getProperties().getProperty("user.dir") + System.getProperties().getProperty("file.separator") + path);
        } catch (Exception e) {
            //System.out.println(e);
            //System.out.println("error");
            isExists = false;
        }

        if (isExists) {
            FileReader fileReader = new FileReader(System.getProperties().getProperty("user.dir") + System.getProperties().getProperty("file.separator") + path);
            info = JSON.parseObject(fileReader.readString(), AccountInfo.class);
            try {
                encryption.setPublicKey(info.publicKey);
                encryption.setPrivateKey(info.privateKey);
            } catch (Exception e) {
                System.out.println("Invalid account file.");
                System.exit(0);
            }
            System.out.println("Account initialization success");

        } else {
            FileWriter fileWriter = new FileWriter(System.getProperties().getProperty("user.dir") + System.getProperties().getProperty("file.separator") + path);
            encryption.randomPairKey();
            info = new AccountInfo(encryption.getPublicKeyStr(), encryption.getPrivateKeyStr());

            fileWriter.write(JSON.toJSONString(info));
            fileWriter.close();
            System.out.println("Account initialization success,new account created");
        }

    }

    public double queryBalance() {//查询余额
        double now = 0;
        synchronized (FullNode.globalLock) {
            System.out.println(JSON.toJSONString(FullNode.block));
            for (Block block : FullNode.block) {
                if (block.getFounder().equals(this.info.getPublicKey())) now += Config.award;
                System.out.println(JSON.toJSONString(block.getTransDetail()));
                List<TransDetail> nowTrans = block.getTransDetail();
                for (TransDetail nowTran : nowTrans) {
                    if (nowTran.getTo().equals(this.info.getPublicKey())) {
                        now += nowTran.getAmount();
                    }
                    if (nowTran.getFrom().equals(this.info.getPublicKey())) {
                        now -= nowTran.getAmount();
                    }
                }
            }
            for (String nowTran : FullNode.toPackTrans.values()) {
                TransDetail nowTranClass = (TransDetail) JSON.parseObject(nowTran, TransDetail.class);
                if (nowTranClass.getFrom().equals(this.info.getPublicKey())) now -= nowTranClass.getAmount();

            }
        }
        return now;
    }

    public String queryDetail() {//查询交易明细
        synchronized (FullNode.globalLock) {
            ArrayList<Detail> nowDetails = new ArrayList<Detail>();
            for (Block block : FullNode.block) {
                if (block.getFounder().equals(this.info.getPublicKey())) {
                    Detail detail = new Detail(0, "", this.info.getPublicKey(), Config.award, String.valueOf(block.getTimestamp()));
                    nowDetails.add(detail);
                }
                List<TransDetail> nowTrans = block.getTransDetail();
                for (TransDetail nowTran : nowTrans) {
                    if (nowTran.getTo().equals(this.info.getPublicKey())) {
                        Detail detail = new Detail(1, nowTran.getFrom(), nowTran.getTo(), nowTran.getAmount(), nowTran.getTimestamp());
                        nowDetails.add(detail);
                    }
                    if (nowTran.getFrom().equals(this.info.getPublicKey())) {
                        Detail detail = new Detail(2, nowTran.getFrom(), nowTran.getTo(), nowTran.getAmount(), nowTran.getTimestamp());
                        nowDetails.add(detail);
                    }
                }
            }
            return JSON.toJSONString(nowDetails);
        }

    }
}
