/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author asani
 */
public class Crypto {
    
  LogInfo logInfo = new LogInfo();
    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_NAME = "AES";
    public static final String ALGORITHM = "AES/CBC/PKCS7Padding"; // PROD
//    public static final String ALGORITHM = "AES/CBC/PKCS5Padding";


    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public static byte[] setKey(String myKey) {
        MessageDigest sha = null;
        byte[] key = null;
        try {
            key = myKey.getBytes("UTF-8");
            key = Arrays.copyOf(key, 16);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static String encrypt(String content, String chiperKey) {
        byte[] result = null;
        try {
            byte[] KEY = setKey(chiperKey);
            byte[] IV = setKey(chiperKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(result);
    }

    public static String decrypt(String content, String chiperKey) {
        try {
            byte[] KEY = setKey(chiperKey);
            byte[] IV = setKey(chiperKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(Base64.decodeBase64(content)), CHARSET_NAME);
        } catch (Exception e) {
      System.out.println("s" + e.getMessage());
        }
        return StringUtils.EMPTY;
    }
    
}
