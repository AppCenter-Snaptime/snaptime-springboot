package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.user.data.domain.User;

import javax.crypto.SecretKey;

public interface EncryptionService {

    SecretKey getSecretKey(String uId);
    Encryption getEncryption(User user);
    Encryption setEncryption(User user);
}
