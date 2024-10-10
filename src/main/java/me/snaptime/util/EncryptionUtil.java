package me.snaptime.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class EncryptionUtil {
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static byte[] encryptData(byte[] dataToEncrypt, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // 16바이트의 초기화 벡터(IV)를 생성한다.
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // 암호화 모드로 초기화
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        // 데이터 암호화
        byte[] encryptedData = cipher.doFinal(dataToEncrypt);

        // IV와 암호화된 데이터를 하나로 결합하여 반환 (복호화 시 IV가 필요하기 때문에)
        byte[] result = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedData, 0, result, iv.length, encryptedData.length);

        return result;
    }

    public static byte[] decryptData(byte[] encryptedData, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // 암호화된 데이터의 앞부분에서 IV를 추출한다.
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // 나머지 부분이 실제 암호화된 데이터
        byte[] actualEncryptedData = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

        // 복호화 모드로 초기화
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        // 데이터 복호화
        return cipher.doFinal(actualEncryptedData);
    }

}
