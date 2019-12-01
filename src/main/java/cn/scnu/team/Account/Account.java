package cn.scnu.team.Account;

import cn.hutool.core.io.file.FileReader;
import cn.scnu.team.Util.Encryption;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

    public Encryption encryption;

    public Account() {
        encryption = new Encryption();
    }

    public void loadInfo(String path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        //File file=new File(path);
        boolean isExists = true;
        try{
            FileReader fileReader=new FileReader(System.getProperties().getProperty("user.dir")+System.getProperties().getProperty("file.separator")+path);
        }catch (Exception e){
            //System.out.println(e);
            //System.out.println("error");
            isExists=false;
        }

        if (isExists) {
            FileReader fileReader = new FileReader(System.getProperties().getProperty("user.dir")+System.getProperties().getProperty("file.separator")+path);
            AccountInfo accountInfo = JSON.parseObject(fileReader.readString(), AccountInfo.class);
            try {
                encryption.setPublicKey(accountInfo.publicKey);
                encryption.setPrivateKey(accountInfo.privateKey);
            } catch (Exception e) {
                System.out.println("Invalid account file.");
                System.exit(0);
            }
            System.out.println("Account initialization success");

        } else {
            FileWriter fileWriter = new FileWriter(System.getProperties().getProperty("user.dir")+System.getProperties().getProperty("file.separator")+path);
            encryption.randomPairKey();
            AccountInfo accountInfo = new AccountInfo(encryption.getPublicKeyStr(), encryption.getPrivateKeyStr());

            fileWriter.write(JSON.toJSONString(accountInfo));
            fileWriter.close();
            System.out.println("Account initialization success,new account created");
        }

    }
}
