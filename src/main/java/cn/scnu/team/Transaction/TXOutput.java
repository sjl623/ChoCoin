package cn.scnu.team.Transaction;

import static cn.scnu.team.Util.Hash.sha256;

/**
    *交易输出
 */
public class TXOutput {
    //金额
    private int value;

    //公钥hash
    private String publicKeyHash;

    //创建交易输出
    public TXOutput(int value,String publicKeyHash){
        this.value=value;
        this.publicKeyHash=publicKeyHash;
    }
    public static  TXOutput newTXOutput(int value,String address){
        String publicKeyHash= sha256(address);
        return new TXOutput(value,publicKeyHash);
    }


    //检查交易输出是否能够使用指定的公钥
    public boolean isLockedWithKey(String publicKeyHash){
        return this.getPublicKeyHash().equals(publicKeyHash);
    }

    public int getValue() {
        return value;
    }


    public String getPublicKeyHash() {
        return sha256(this.publicKeyHash);
    }
}
