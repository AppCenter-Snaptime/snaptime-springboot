package me.snaptime.snap.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static byte[] encryptData(byte[] dataToEncrypt, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        /*
          Cipher 클래스는 JCA의 일부로 암호화 및 복호화 연산을 위한 기능을 제공한다.
          Cipher 인스턴스는 getInstance(String algorithm) 메소드를 호출해 생성할 수 있다.
          이 코드에서는 AES를 매개변수로 제공해 AES 암호화 알고리즘을 사용하는 Cipher 객체를 생성하고 있다.
         */
        Cipher cipher = Cipher.getInstance("AES");
        // cipher 인스턴스를 암호화 모드로 초기화한다.
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // doFinal 메소드를 호출해 암호화 연산을 완료한다.
        return cipher.doFinal(dataToEncrypt);
    }

    public static byte[] decryptData(byte[] encryptedData, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

}
