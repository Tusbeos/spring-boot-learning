package com.emedicalbooking.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Mã hoá / giải mã token đặt lịch bằng AES-256-GCM.
 * Token thô (UUID) được lưu trong DB, token mã hoá được gửi trong URL email.
 */
@Component
public class TokenEncryptionUtils {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_BIT_LENGTH = 128;

    private final byte[] keyBytes;

    public TokenEncryptionUtils(@Value("${app.booking.token-secret}") String secret) {
        // Pad/trim về đúng 32 bytes (AES-256)
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        this.keyBytes = Arrays.copyOf(raw, 32);
    }

    /**
     * Mã hoá token UUID → chuỗi Base64URL an toàn gửi qua email link.
     */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(keyBytes, "AES"),
                    new GCMParameterSpec(GCM_TAG_BIT_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Prepend IV vào đầu ciphertext rồi Base64URL encode
            byte[] combined = new byte[GCM_IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, GCM_IV_LENGTH);
            System.arraycopy(ciphertext, 0, combined, GCM_IV_LENGTH, ciphertext.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hoá token", e);
        }
    }

    /**
     * Giải mã chuỗi Base64URL từ email link → token UUID gốc.
     * Ném IllegalArgumentException nếu token không hợp lệ / bị giả mạo.
     */
    public String decrypt(String encryptedToken) {
        try {
            byte[] combined = Base64.getUrlDecoder().decode(encryptedToken);
            if (combined.length <= GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Token không hợp lệ");
            }
            byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyBytes, "AES"),
                    new GCMParameterSpec(GCM_TAG_BIT_LENGTH, iv));

            byte[] decrypted = cipher.doFinal(ciphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã bị giả mạo");
        }
    }
}
