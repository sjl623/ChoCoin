package cn.scnu.team.Transaction;

public class  Transaction{
    public Transaction(String detailStr, String sign) {
        this.detailStr = detailStr;
        this.sign = sign;
    }

    public String getDetailStr() {
        return detailStr;
    }

    public void setDetailStr(String detailStr) {
        this.detailStr = detailStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    private String detailStr;
    private String sign;
}
