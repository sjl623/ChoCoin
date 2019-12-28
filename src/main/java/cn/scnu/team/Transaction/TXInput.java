package cn.scnu.team.Transaction;

import static cn.scnu.team.Util.Hash.sha256;

/*
交易的输入
 */
public class TXInput {

    //交易Id的hash
    private String txId;

    //交易输出索引
    private int txOutputIndex;

    //签名
    private String signature;
    //公钥
    private  String publicKey;
    public  String getPublicKey(){
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public TXInput(String txId, int txOutputIndex, String signature, String publicKey){
        this.txId=txId;
        this.txOutputIndex=txOutputIndex;
        this.signature=signature;
        this.publicKey=publicKey;
    }

    public String getTxId() {
        return txId;
    }

    public int getTxOutputIndex() {
        return txOutputIndex;
    }

    //检查公钥hash是否用于交易输入
    public boolean usesKey(String publicKeyHash){
        String lockingHash= sha256(this.getPublicKey());
        return lockingHash.equals(publicKeyHash);
    }
}
