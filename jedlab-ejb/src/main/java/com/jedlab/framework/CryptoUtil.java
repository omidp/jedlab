package com.jedlab.framework;

import java.io.UnsupportedEncodingException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.jboss.seam.util.Base64;

/**
 * @author Omid Pourhadi
 *
 */
public class CryptoUtil
{

    public static String decodeBase64(String input)
    {
        return new String(Base64.decode(input));
    }

    public static String encodeBase64(String input)
    {
        try
        {
            return new String(Base64.encodeBytes(input.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException e)
        {
        }
        return "";
    }

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;

    public CryptoUtil createSecurity()
    {
        try
        {
            myEncryptionKey = "WelcomeJedlabWelcomeJedlab";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public String encrypt(String unencryptedString)
    {
        if(key == null)
            createSecurity();
        String encryptedString = null;
        try
        {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(org.apache.commons.codec.binary.Base64.encodeBase64(encryptedText));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString)
    {
        if(key == null)
            createSecurity();
        String decryptedText = null;
        try
        {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return decryptedText;
    }
    

   

}
