package com.example.chords2.data.datastore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class Encryptor() {
    private val secretKey: SecretKey by lazy { getOrCreateSecretKey() }

    companion object {
        // Alias for the secret key stored in Android Keystore.
        private const val KEY_ALIAS = "MyAppCredentialKey"
        // Transformation algorithm for AES encryption/decryption.
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        // Initialization Vector (IV) size in bytes for GCM mode.
        private const val IV_SIZE = 12
        // Authentication Tag size in bits for GCM mode.
        private const val TAG_SIZE = 128
    }

    /**
     * Retrieves an existing SecretKey from Android Keystore or creates a new one if not present.
     * The key is used for encrypting and decrypting credentials.
     *
     * @return The [SecretKey] for cryptographic operations.
     * @throws Exception if Keystore or cryptographic operations fail.
     */
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256) // 256-bit AES key
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypts the given plain text string using AES/GCM.
     * The Initialization Vector (IV) is prepended to the ciphertext.
     *
     * @param plainText The string to encrypt.
     * @return A Base64 encoded string of the IV combined with the encrypted data.
     * @throws Exception if encryption fails.
     */
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv // Save the IV, GCM mode requires a unique IV for each encryption
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // Prepend IV to the ciphertext for use during decryption
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts the given Base64 encoded encrypted data using AES/GCM.
     * It expects the IV to be prepended to the ciphertext.
     *
     * @param encryptedData A Base64 encoded string containing the IV and ciphertext.
     * @return The original plain text string.
     * @throws Exception if decryption fails (e.g., wrong key, corrupted data).
     */
    fun decrypt(encryptedData: String): String {
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP)

        // Extract IV from the beginning of the combined array
        val iv = combined.copyOfRange(0, IV_SIZE)
        val cipherText = combined.copyOfRange(IV_SIZE, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedBytes = cipher.doFinal(cipherText)

        return String(decryptedBytes, Charsets.UTF_8)
    }
}