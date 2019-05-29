package com.jio.lbs.utils;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import static com.jio.lbs.utils.AppConstants.SALT_KEY;
import static com.jio.lbs.utils.AppConstants.SECRITE_KEY;

public class Encrypt {
	
	public static String encrypt(String strToEncrypt, String secret, String salt)
	{
	    try
	    {
	    	String secret_final = "";
	    	String salt_final = "";
	    	if(null != secret && !secret.isEmpty()) {
	    		secret_final = secret;
	    	}else {
	    		secret_final = SECRITE_KEY;
	    	}
	    	
	    	if(null != salt && !salt.isEmpty()) {
	    		salt_final = salt;
	    	}else {
	    		salt_final = SALT_KEY;
	    	}
	    	
	        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);
	         
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(secret_final.toCharArray(), salt_final.getBytes(), 65536, 256);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	         
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
	        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
	    }
	    catch (Exception e)
	    {
	        System.out.println("Error while encrypting: " + e.toString());
	    }
	    return null;
	}

}
