package me.snaptime.component.encryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Encryption;
import me.snaptime.snap.repository.EncryptionRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.EncryptionUtil;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptionImpl implements EncryptionComponent {
    private final UserRepository userRepository;
    private final EncryptionRepository encryptionRepository;

    @Override
    public SecretKey getSecretKey(String userEmail) {
        User foundUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Encryption foundSecretKey = encryptionRepository.findByUser(foundUser);
        return foundSecretKey.getSecretKey();
    }

    @Override
    public Encryption getEncryption(User user) {
        return encryptionRepository.findByUser(user);
    }

    @Override
    public Encryption setEncryption(User user) {
        Encryption encryption = encryptionRepository.findByUser(user);
        if (encryption == null) {
            encryption = createSecretKey(user);
        }
        return encryption;
    }

    @Override
    public byte[] encryptData(Encryption encryption, byte[] fileBytes) {
        try {
            return EncryptionUtil.encryptData(fileBytes, encryption.getSecretKey());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
        }
    }

    @Override
    public byte[] decryptData(Encryption encryption, byte[] fileBytes) {
        try {
            return EncryptionUtil.decryptData(fileBytes, encryption.getSecretKey());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
        }
    }

    private Encryption createSecretKey(User user) {
        try {
            SecretKey secretKey = EncryptionUtil.generateAESKey();
            return encryptionRepository.save(
                Encryption.builder()
                        .secretKey(secretKey)
                        .user(user)
                        .build()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
    }

    @Override
    public void deleteEncryption(Encryption encryption) {
        encryptionRepository.delete(encryption);
    }
}
