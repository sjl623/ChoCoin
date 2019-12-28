package cn.scnu.team.Transaction;

import cn.scnu.team.BlockChain.Block;
import cn.scnu.team.FullNode.FullNode;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Vector;

/**
 * 未被花费的交易输出池
 */
public class UTXOSet {
    //private Blockchain blockchain;
    private Vector<Block> blockchain;
    /**
     *寻找能够花费的交易
     */

    public UTXOSet(Vector<Block> block){
        this.blockchain=block;
    }
    public SpendableOutputResult findSpendableOutputs(String pubKeyHash,int amount){
        Map<String,int[]> unspentOuts= Maps.newHashMap();//utxo对应的txid和output的索引
        int accumulated=0;

        for (Map.Entry<String, TXOutput[]> entry:FullNode.utxos.entrySet()) {
            String txId = entry.getKey();

            TXOutput[] txOutputs = entry.getValue();
            for (int outId = 0; outId < txOutputs.length; outId++) {
                TXOutput txOutput = txOutputs[outId];
                if (txOutput.isLockedWithKey(pubKeyHash) && accumulated < amount) {
                    accumulated += txOutput.getValue();

                    int[] outIds = unspentOuts.get(txId);
                    if (outIds == null) {
                        outIds = new int[]{outId};
                    } else {
                        outIds = ArrayUtils.add(outIds, outId);
                    }
                    unspentOuts.put(txId, outIds);
                    if (accumulated >= amount) break;
                }

            }
        }
        return new SpendableOutputResult(accumulated,unspentOuts);
    }
    /**
     * 查找钱包地址对应的所有utxo
     * @param pubKeyHash 钱包公钥hash
     */
    /*public TXOutput[] findUTXOs(String pubKeyHash){
        TXOutput[] utxos={};
        if(FullNode.block.isEmpty()){
            return utxos;
        }
    }*/
    /**
     * 更新UTXO池
     * @param  tipBlock 最新的区块
     *
     */
    public void update(Block tipBlock){
        if(tipBlock==null){
            //新区块为空提示
        }
        for(UTXO_Transaction transaction_:tipBlock.getTransactions()){
            //根据交易输入排查剩余未被使用的交易输出
            if(!transaction_.isCoinBase()){
                for(TXInput txInput:transaction_.getInputs()){
                    //余下未被使用的交易输出
                    TXOutput[] remainderUTXOs={};
                    String txId=txInput.getTxId();
                    TXOutput[] txOutputs
                            =FullNode.utxos.get(txId);//根据txid到utxos交易池中找
                    if (txOutputs==null){
                        continue;
                    }

                    for(int outIndex=0;outIndex<txOutputs.length;outIndex++){
                        if(outIndex!=txInput.getTxOutputIndex()){
                            remainderUTXOs=ArrayUtils.add(remainderUTXOs,txOutputs[outIndex]);
                        }
                    }

                    //没有剩余则删除，否则更新
                    if(remainderUTXOs.length==0){
                        FullNode.utxos.remove(txId);
                    }else{
                        FullNode.utxos.put(txId,remainderUTXOs);
                    }
                }
            }
            //新的交易输出加入utxos
            TXOutput[] txOutputs=transaction_.getOutputs();
            String txId= transaction_.getTxId();
            FullNode.utxos.put(txId,txOutputs);
        }
    }
}
