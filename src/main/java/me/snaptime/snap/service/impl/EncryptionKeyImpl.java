package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.repository.EncryptionRepository;
import me.snaptime.snap.service.EncryptionService;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyImpl implements EncryptionService {
    private final UserRepository userRepository;
    private final EncryptionRepository encryptionRepository;

    @Override
    public SecretKey getSecretKey(String uId) {
        User foundUser = userRepository.findUserByName(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
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
