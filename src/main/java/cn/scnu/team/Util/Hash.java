package cn.scnu.team.Util;


import cn.hutool.crypto.digest.DigestUtil;

public class Hash {
    public static String sha256(String origin){
        return DigestUtil.sha256Hex(origin);
    }
}
