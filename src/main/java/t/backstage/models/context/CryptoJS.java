package t.backstage.models.context;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/***
 * CryptoJS 加密解密类
 * @author zhangj
 * @date 2018年9月10日 下午8:48:12
 * @email zhangjin0908@hotmail.com
 */
public class CryptoJS {
	public static class AES {
		/***
		 * 对字符串进行AES解码
		 * @param body  要解码的内容
		 * @param key   要解码的key
		 * @return
		 * @throws NoSuchPaddingException 
		 * @throws NoSuchAlgorithmException 
		 * @throws InvalidAlgorithmParameterException 
		 * @throws InvalidKeyException 
		 * @throws BadPaddingException 
		 * @throws IllegalBlockSizeException 
		 */
		public static String decrypt(String cipherText ,String secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			byte[] cipherData = Base64.getDecoder().decode(cipherText);
			byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
			SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
			IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

			byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
			Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] decryptedData = aesCBC.doFinal(encrypted);
			return new String(decryptedData, StandardCharsets.UTF_8);
		}
		
		private static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {
		    int digestLength = md.getDigestLength();
		    int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
		    byte[] generatedData = new byte[requiredLength];
		    int generatedLength = 0;

		    try {
		        md.reset();
		        while (generatedLength < keyLength + ivLength) {
		            if (generatedLength > 0)
		                md.update(generatedData, generatedLength - digestLength, digestLength);
		            md.update(password);
		            if (salt != null)
		                md.update(salt, 0, 8);
		            md.digest(generatedData, generatedLength, digestLength);
		            for (int i = 1; i < iterations; i++) {
		                md.update(generatedData, generatedLength, digestLength);
		                md.digest(generatedData, generatedLength, digestLength);
		            }
		            generatedLength += digestLength;
		        }
		        byte[][] result = new byte[2][];
		        result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
		        if (ivLength > 0)
		            result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
		        return result;
		    } catch (DigestException e) {
		        throw new RuntimeException(e);
		    } finally {
		        Arrays.fill(generatedData, (byte)0);
		    }
		}
	}
}

