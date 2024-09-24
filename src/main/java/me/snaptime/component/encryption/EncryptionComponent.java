package me.snaptime.component.encryption;

import me.snaptime.snap.domain.Encryption;
import me.snaptime.user.domain.User;

import javax.crypto.SecretKey;

public interface EncryptionComponent {

    /*
    * getSecretKey
    * User Email을 인자로 받아
    * Encryption Entity의 field인 SecretKey에 접근하여 SecretKey를 가져오는 메소드
    * */
    SecretKey getSecretKey(String userEmail);

    /*
    * getEncryption
    * User Entity를 인자로 받아 User와 관계있는 Encryption를 가져오는 메소드
    * */
    Encryption getEncryption(User user);

    /*
    * setEncryption
    * User Entity를 인자로 받아 User와 새로운 Encryption을 관계 맺어주는 메소드
    * 반환 값으로는 User와 관계가 맺어진 새 Encryption Entity가 주어진다.
    * */
    Encryption setEncryption(User user);

    /*
    * encryptData
    * encryption entity와 byte[]를 인자로 받아 byte[]를 encryption entity의 field인 secretKey로
    * 암호화 한 후 암호화 한 데이터를 반환하는 메소드
    * */
    byte[] encryptData(Encryption encryption, byte[] fileBytes);

    /*
     * decryptData
     * encryption entity와 byte[]를 인자로 받아 byte[]를 encryption entity의 field인 secretKey로
     * 복호화 한 후 복호화 한 데이터를 반환하는 메소드
     * */
    byte[] decryptData(Encryption encryption, byte[] fileBytes);

    /*
     * deleteEncryption
     * encryption entity를 인자로 받아 인자와 일치하는 DB 레코드를 삭제하는 메소드
     * */
    void deleteEncryption(Encryption encryption);
}
