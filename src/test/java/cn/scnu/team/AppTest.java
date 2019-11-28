package cn.scnu.team;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cn.scnu.team.Util.Encryption;
import cn.scnu.team.Util.Hash;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
    

}
