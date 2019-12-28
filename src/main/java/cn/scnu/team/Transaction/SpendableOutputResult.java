package cn.scnu.team.Transaction;

import java.util.Map;

/**
 * 查询UTXO
 */
public class SpendableOutputResult {
    //交易的金额
    private int accumulated;
    private Map<String,int[]> unspentOuts;
    public int getAccumulated(){
        return this.accumulated;
    }
    public Map<String ,int[]> getUnspentOuts(){
        return unspentOuts;
    }
    //未花费的交易
    SpendableOutputResult(int accumulated,Map<String,int[]> unspentOuts){
        this.accumulated=accumulated;
        this.unspentOuts=unspentOuts;
    }


}
