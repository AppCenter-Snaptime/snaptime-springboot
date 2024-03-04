package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.repository.EncryptionRepository;
import me.snaptime.snap.service.EncryptionService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionKeyImpl implements EncryptionService {
    private final UserRepository userRepository;
    private final EncryptionRepository encryptionRepository;

    @Override
    public String getEncryptionKey(String uid) {
        User foundUser = userRepository.findUserByName(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Encryption foundEncryption = encryptionRepository.findByUser(foundUser);
        return Base64.getEncoder().encodeToString(foundEncryption.getEncryptionKey().getEncoded());
    }
}
