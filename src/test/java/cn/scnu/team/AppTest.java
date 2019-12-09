package cn.scnu.team;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cn.scnu.team.Account.Account;
import cn.scnu.team.Util.Encryption;
import cn.scnu.team.Util.Hash;

import cn.scnu.team.Util.Merkle;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Random;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testHashSha256(){
        assertEquals(Hash.sha256("test"),"9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
    }

    @Test
    public void testRSA() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Encryption encryption=new Encryption();
        encryption.randomPairKey();
        String testStr="hello world";

        String encPri=encryption.encryptPrivate(testStr);
        String decPub=encryption.decryptPub(encPri);
        assertEquals(testStr,decPub);

        String encPub=encryption.encryptPub(testStr);
        String decPri=encryption.decryptPrivate(encPub);
        assertEquals(testStr,decPri);
    }
    @Test
    public void testMerkle(){
        Merkle merkle=new Merkle();
        Random random=new Random();
        for(int i=0;i<10;i++){
            merkle.add(String.valueOf(random.nextInt()));
        }
        merkle.build();
        merkle.output();
        String nowHash=null;
        for(int i=0;i<merkle.tree.size()-1;i+=1){
            List<String> nowLayer=merkle.tree.get(i);
            List<String> nextLayer=merkle.tree.get(i+1);
            for(int j=0;j<nowLayer.size();j+=2){
                if(j==nowLayer.size()-1){
                    nowHash=Hash.sha256(nowLayer.get(j)+nowLayer.get(j));
                }else{
                    nowHash=Hash.sha256(nowLayer.get(j)+nowLayer.get(j+1));
                }
                assertEquals(nowHash,nextLayer.get(j/2));
            }
        }
    }

    @Test
    public void testFileRead(){
        Account account=new Account();
        //account.loadInfo("aaa");
    }

}
