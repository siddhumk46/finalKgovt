package com.kgovt.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;

import com.kgovt.models.ApplicationDetailes;

public class AppUtilities {
	
	/**
	 * isNotNullAndNotEmpty is used to check given number is not null or not empty
	 * @param value
	 * @return true if string is not null and not empty
	 */
	public static boolean isNotNullAndNotEmpty(String value) {
		if(null == value) {
			return false;
		}
		value = value.trim();
		return value.length() >= 1 && !"null".equalsIgnoreCase(value);
	}
	
	public static String generateReceptNo(ApplicationDetailes applicationDetailes) {
		String prefix = String.valueOf(applicationDetailes.getApplicantNumber());
		String sufix = String.valueOf(applicationDetailes.getMobile());
		String seperator = "-";
		String receiptNo = prefix + seperator + sufix;
		return receiptNo;
	}
	
	 public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
	        // application/pdf
	        // application/xml
	        // image/gif, ...
	        String mineType = servletContext.getMimeType(fileName);
	        try {
	            MediaType mediaType = MediaType.parseMediaType(mineType);
	            return mediaType;
	        } catch (Exception e) {
	            return MediaType.APPLICATION_OCTET_STREAM;
	        }
	    }
	 
	 public static String generateCommonLangPassword() {
		    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		    String numbers = RandomStringUtils.randomNumeric(2);
		    String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
		    String totalChars = RandomStringUtils.randomAlphanumeric(2);
		    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
		      .concat(numbers)
		      .concat(specialChar)
		      .concat(totalChars);
		    List<Character> pwdChars = combinedChars.chars()
		      .mapToObj(c -> (char) c)
		      .collect(Collectors.toList());
		    Collections.shuffle(pwdChars);
		    String password = pwdChars.stream()
		      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
		      .toString();
		    return password;
		}
	 
	 private static SecretKeySpec secretKey;
	    private static byte[] key;
	 
	    public static void setKey(String myKey) 
	    {
	        MessageDigest sha = null;
	        try {
	            key = myKey.getBytes("UTF-8");
	            sha = MessageDigest.getInstance("SHA-1");
	            key = sha.digest(key);
	            key = Arrays.copyOf(key, 16); 
	            secretKey = new SecretKeySpec(key, "AES");
	        } 
	        catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } 
	        catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    public static String encrypt(String strToEncrypt, String secret) 
	    {
	        try
	        {
	            setKey(secret);
	            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
	        } 
	        catch (Exception e) 
	        {
	            System.out.println("Error while encrypting: " + e.toString());
	        }
	        return null;
	    }
	 
	    public static String decrypt(String strToDecrypt, String secret) 
	    {
	        try
	        {
	            setKey(secret);
	            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	            cipher.init(Cipher.DECRYPT_MODE, secretKey);
	            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
	        } 
	        catch (Exception e) 
	        {
	            System.out.println("Error while decrypting: " + e.toString());
	        }
	        return null;
	    }
}
