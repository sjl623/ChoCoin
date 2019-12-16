package cn.scnu.team.BlockChain;

public class Block {

    public Block(String preHash, String rootMerkleHash, String transDetail, int nonce, long timestamp, String founder) {
        this.preHash = preHash;
        this.rootMerkleHash = rootMerkleHash;
        this.transDetail = transDetail;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.founder = founder;
    }

    public String getTransDetail() {
        return transDetail;
    }

    public void setTransDetail(String transDetail) {
        this.transDetail = transDetail;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public String getRootMerkleHash() {
        return rootMerkleHash;
    }

    public void setRootMerkleHash(String rootMerkleHash) {
        this.rootMerkleHash = rootMerkleHash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    String preHash;
    String rootMerkleHash;
    String transDetail;
    int nonce;
    long timestamp;
    String founder;
}
