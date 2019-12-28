package cn.scnu.team.Transaction;

import cn.scnu.team.BlockChain.Block;
//import com.sun.xml.internal.ws.util.StringUtils;
import cn.scnu.team.FullNode.FullNode;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static cn.scnu.team.Util.Hash.sha256;

/*
 交易
 */
public class UTXO_Transaction {
    private static final int SUBSIDY=10;//???

    //交易的hash
    private String txId;

    //交易的输入
    private TXInput[] inputs;

    //交易输出
    private  TXOutput[] outputs;
    //交易时间
    private String timestamp;


    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    /**
     * 计算交易信息的哈希值
     */
    //
    public UTXO_Transaction(String txId, TXInput[] inputs, TXOutput[] outputs, String timestamp){
        this.txId=txId;
        this.inputs=inputs;
        this.outputs=outputs;
        this.timestamp=timestamp;
    }

    public TXInput[] getInputs() {
        return inputs;
    }

    public TXOutput[] getOutputs() {
        return outputs;
    }

    //创建CoinBase交易
    public static UTXO_Transaction newCoinBaseTX(String to, String data){
        if(StringUtils.isBlank(data)){//奖励
            data=String.format("Reward to '%s'",to);
        }

        //创建交易输入
        TXInput txInput=new TXInput(new String(""),-1,null,data);
        //创建交易输出
        TXOutput txOutput=TXOutput.newTXOutput(SUBSIDY,to);

        UTXO_Transaction tx=new UTXO_Transaction(null,new TXInput[]{txInput},
                new TXOutput[]{txOutput},Long.toString(System.currentTimeMillis()));
        //设置交易id
        //tx.setTxId(sha256) txid设置为整个
        return tx;
    }
    //是否为coinbase交易
    public boolean isCoinBase(){//Coinbase 交易的输入id是空，只有一个输入，而且输出索引为-1
        return this.getInputs().length==1
                &&this.getInputs()[0].getTxId().length()==0
                &&this.getInputs()[0].getTxOutputIndex()==-1;
    }
    /**
     * 查找pubkey对应的所有UTXO
     *
     */
    public TXOutput[] findUTXOs(String pubKeyHash){
        TXOutput[] Utxos={};
        Map<String,TXOutput[]> utxospool=FullNode.utxos;
        if(utxospool.isEmpty()){
            return Utxos;
        }
        for(TXOutput[] value: utxospool.values()){
            for(TXOutput txOutput:value){
                if(txOutput.isLockedWithKey(pubKeyHash)){
                    Utxos=ArrayUtils.add(Utxos,txOutput);
                }
            }
        }
        return Utxos;
    }



    /**
     *
     * from 向to 转账 amount 个chocoin
     * from 发起交易钱包地址(发送者公钥
     * to 接收交易钱包地址（接收者公钥
     * amount 金额
     * blockchain 区块链
     */
    public static UTXO_Transaction newUTXOTransaction(String from, String to, int amount, Vector<Block> blocks)throws Exception{
        String pubKey=from;
        String pubKeyHash=sha256(pubKey);

        SpendableOutputResult result=new UTXOSet(blocks).findSpendableOutputs(pubKeyHash,amount);
        int accumulated=result.getAccumulated();
        Map<String ,int[]> unspentOuts=result.getUnspentOuts();

        if(accumulated<amount){
            //log.error("ERROR: Not enough funds ! accumulated=" + accumulated + ", amount=" + amount);
            throw new RuntimeException("ERROR:Not enough funds!");
        }

        //迭代器
        Iterator<Map.Entry<String,int[]>> iterator =unspentOuts.entrySet().iterator();

        TXInput[] txInputs={};
        while(iterator.hasNext()){
            Map.Entry<String ,int[]> entry=iterator.next();
            String txIdStr=entry.getKey();//((٩(//̀Д/́/)۶)未花费的output所在的交易的id
            int[] outIds=entry.getValue();//
            //byte[] txId = Hex.decodeHex(txIdStr);
            for(int outIndex:outIds){
                txInputs=ArrayUtils.add(txInputs,new TXInput(txIdStr,outIndex,null,pubKey));//utxo作为交易input
            }
        }
        TXOutput[] txOutput={};
        txOutput= ArrayUtils.add(txOutput,TXOutput.newTXOutput(amount,to));//接受者获得新的utxo
        if(accumulated>amount){
            txOutput=ArrayUtils.add(txOutput,TXOutput.newTXOutput((accumulated-amount),from));//找零
        }
        UTXO_Transaction newTx=new UTXO_Transaction(null,txInputs,txOutput,Long.toString(System.currentTimeMillis()));

        newTx.setTxId(sha256(JSON.toJSONString(newTx)));
        //进行交易签名

        return newTx;
    }

    /**
     * 创建用于签名的交易数据副本，交易输入的signature和pubkey设置为null
     */
    public UTXO_Transaction trimmedCopy(){
        TXInput[] tmpTXInputs=new TXInput[this.getInputs().length];
        for(int i=0;i<this.getInputs().length;i++){
            TXInput txInput=this.getInputs()[i];
            tmpTXInputs[i]=new TXInput(txInput.getTxId(),txInput.getTxOutputIndex(),null,null);

        }
        TXOutput[] tmpTXOutputs=new TXOutput[this.getOutputs().length];
        for(int i=0;i<this.getOutputs().length;i++){
            TXOutput txOutput=this.getOutputs()[i];
            tmpTXOutputs[i]=new TXOutput(txOutput.getValue(),txOutput.getPublicKeyHash());
        }
        return new UTXO_Transaction(this.getTxId(),tmpTXInputs,tmpTXOutputs,this.timestamp);
    }
    /**
     * 签名
     */
    public void sign(BCECPrivateKey privateKey,Map<String, UTXO_Transaction> prevTxMap) throws Exception{
        //coinbase交易不需要签名
        if(this.isCoinBase()){
            return;
        }
        //再一次验证一下交易信息中的交易输入是否正确，能否找到对应的交易数据
        for(TXInput txInput:this.getInputs()){
            if(prevTxMap.get(txInput.getTxId())==null){
                throw new RuntimeException("ERROE: Pre tx is nor correct");
            }
        }
        //创建用于签名的交易信息的副本
        UTXO_Transaction txCopy=this.trimmedCopy();

        Security.addProvider(new BouncyCastleProvider());
        Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",BouncyCastleProvider.PROVIDER_NAME);
        ecdsaSign.initSign(privateKey);

        for(int i=0;i<txCopy.getInputs().length;i++){
            TXInput txInputCopy=txCopy.getInputs()[i];
            //获取交易输入Txid对应的交易数据
            UTXO_Transaction prevTX=prevTxMap.get(txInputCopy.getTxId());
            //获取交易输入所对应的上一笔交易中的交易输出
            TXOutput prevTxOutput=prevTX.getOutputs()[txInputCopy.getTxOutputIndex()];
            txInputCopy.setPublicKey(prevTxOutput.getPublicKeyHash());
            txInputCopy.setSignature(null);

            //对整个交易信息仅进行签名，即对交易id进行签名
            ecdsaSign.update(txCopy.getTxId().getBytes());
            String signature=ecdsaSign.sign().toString();

            // 将整个交易数据的签名赋值给交易输入，因为交易输入需要包含整个交易信息的签名
            // 注意是将得到的签名赋值给原交易信息中的交易输入
            this.getInputs()[i].setSignature(signature);
        }

    }
    /**
     * 验证交易信息
     *
     * @param prevTxMap 前面多笔交易集合
     * @return
     */
    public boolean verify(Map<String, UTXO_Transaction> prevTxMap) throws Exception {
        // coinbase 交易信息不需要签名，也就无需验证
        if (this.isCoinBase()) {
            return true;
        }

        // 再次验证一下交易信息中的交易输入是否正确，也就是能否查找对应的交易数据
        for (TXInput txInput : this.getInputs()) {
            if (prevTxMap.get(txInput.getTxId()) == null) {
                throw new RuntimeException("ERROR: Previous transaction is not correct");
            }
        }

        // 创建用于签名验证的交易信息的副本
        UTXO_Transaction txCopy = this.trimmedCopy();

        Security.addProvider(new BouncyCastleProvider());
        ECParameterSpec ecParameters = ECNamedCurveTable.getParameterSpec("secp256k1");
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);

        for (int i = 0; i < this.getInputs().length; i++) {
            TXInput txInput = this.getInputs()[i];
            // 获取交易输入TxID对应的交易数据
            UTXO_Transaction prevTx = prevTxMap.get(txInput.getTxId());
            // 获取交易输入所对应的上一笔交易中的交易输出
            TXOutput prevTxOutput = prevTx.getOutputs()[txInput.getTxOutputIndex()];

            TXInput txInputCopy = txCopy.getInputs()[i];
            txInputCopy.setSignature(null);
            txInputCopy.setPublicKey(prevTxOutput.getPublicKeyHash());
            // 得到要签名的数据，即交易ID
            txCopy.setTxId(JSON.toJSONString(txCopy));
            txInputCopy.setPublicKey(null);

            // 使用椭圆曲线 x,y 点去生成公钥Key
            BigInteger x = new BigInteger(1, Arrays.copyOfRange(txInput.getPublicKey().getBytes(), 1, 33));
            BigInteger y = new BigInteger(1, Arrays.copyOfRange(txInput.getPublicKey().getBytes(), 33, 65));
            ECPoint ecPoint = ecParameters.getCurve().createPoint(x, y);

            ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, ecParameters);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(txCopy.getTxId().getBytes());
            if (!ecdsaVerify.verify(txInput.getSignature().getBytes())) {
                return false;
            }
        }
        return true;
    }
}
