package cn.scnu.team.Util;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Encryption {
    RSA rsa;

    public Encryption() {
        this.rsa = new RSA();
    }

    public void randomPairKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        String privateKey= Base64.encode(pair.getPrivate().getEncoded());
        String publicKey=Base64.encode(pair.getPublic().getEncoded());
        setPublicKey(publicKey);
        setPrivateKey(privateKey);
    }

    void setPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes;
        keyBytes = Base64.decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        rsa.setPublicKey(publicKey);
    }

    void setPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes;
        keyBytes=Base64.decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        rsa.setPrivateKey(privateKey);
    }

    public String encryptPub(String input){
        return Base64.encode(rsa.encrypt(input,KeyType.PublicKey));
    }

    public String encryptPrivate(String input){
        return Base64.encode(rsa.encrypt(input,KeyType.PrivateKey));
    }

    public String decryptPub(String input){
        return new String(rsa.decrypt(Base64.decode(input),KeyType.PublicKey));
    }

    public String decryptPrivate(String input){
        return new String(rsa.decrypt(Base64.decode(input),KeyType.PrivateKey));
    }



    String encryptRSA(String origin){ return "";}

}
