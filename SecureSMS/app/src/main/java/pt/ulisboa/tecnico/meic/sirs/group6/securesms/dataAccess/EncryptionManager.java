package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.BlockCipherPadding;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToDecryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class EncryptionManager {
    private static EncryptionManager ourInstance = new EncryptionManager();

    public static EncryptionManager getInstance() {
        return ourInstance;
    }

    private EncryptionManager() {
    }

    private byte[] passwordCipher(byte[] data, String password, byte[] salt, byte[] iv, boolean encrypt) throws
            NullPointerException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidCipherTextException {
        String algorithm = "PBKDF2WithHmacSHA1";

        // Define secret key
        PBEKeySpec pbeKeySpec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                1000,
                128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey secretKey = keyFactory.generateSecret(pbeKeySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

        // Define cipher parameters with key and IV
        KeyParameter keyParam = new KeyParameter(secretKeySpec.getEncoded());
        CipherParameters params = new ParametersWithIV(keyParam, iv);

        // Define AES cipher in CBC mode with PKCS7 padding
        BlockCipherPadding padding = new PKCS7Padding();
        CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new AESEngine());
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcBlockCipher, padding);
        cipher.reset();
        cipher.init(encrypt, params);

        // Update data
        byte[] newData = new byte[cipher.getOutputSize(data.length)];
        int dataLen = cipher.processBytes(data, 0, data.length, newData, 0);
        cipher.doFinal(newData, dataLen);

        return newData;
    }
    private byte[] sessionKeyCipher(byte[] data, SecretKey key, byte[] iv, int opmode) throws
            NullPointerException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            ShortBufferException,
            IllegalBlockSizeException,
            BadPaddingException {
        // Define IV parameters
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Define AES cipher in CBC mode with CTS
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Encrypt data
        cipher.init(opmode, key, ivSpec);
        byte[] newData= new byte[cipher.getOutputSize(data.length)];
        int dataLen = cipher.update(data, 0, data.length, newData, 0);
        cipher.doFinal(newData, dataLen);

        return newData;
    }

    public byte[] encryptWithPassword (String content, String password) throws FailedToEncryptSMSException {
        Charset charset = Charset.defaultCharset();
        int saltLen = 32, ivLen = 16;
        byte[] seed = password.getBytes(charset);
        SecureRandom random = new SecureRandom(seed);
        byte[] salt = new byte[saltLen],
                iv = new byte[ivLen],
                data = content.getBytes(charset);

        try {
            // Get salt and IV
            random.nextBytes(salt);
            random.nextBytes(iv);

            // Encrypt data
            byte[] encryptedData = passwordCipher(data ,password, salt, iv, true);

            // Add salt and IV
            byte[] result = new byte[saltLen + ivLen + encryptedData.length];
            System.arraycopy(salt, 0, result, 0, saltLen);
            System.arraycopy(iv, 0, result, saltLen, ivLen);
            System.arraycopy(encryptedData, 0, result, saltLen + ivLen, encryptedData.length);

            return result;
        } catch (
                NullPointerException |
                NoSuchAlgorithmException |
                InvalidKeySpecException |
                InvalidCipherTextException exception) {
            throw new FailedToEncryptSMSException(exception);
        }
    }
    public String decryptWithPassword (byte[] content, String password) throws FailedToDecryptSMSException {
        Charset charset = Charset.defaultCharset();
        int saltLen = 32, ivLen = 16;
        byte[] salt = new byte[saltLen],
                iv = new byte[ivLen],
                data = new byte[content.length - saltLen - ivLen];

        try {
            // Get salt and IV
            System.arraycopy(content, 0, salt, 0, saltLen);
            System.arraycopy(content, saltLen, iv, 0, ivLen);
            System.arraycopy(content, saltLen + ivLen, data, 0, data.length);

            // Decrypt data
            byte[] decryptedData = passwordCipher(data, password, salt, iv, false);

            // Remove padding
            return new String(decryptedData, charset);
        } catch (
                NullPointerException |
                        NoSuchAlgorithmException |
                        InvalidKeySpecException |
                        InvalidCipherTextException exception) {
            throw new FailedToDecryptSMSException(exception);
        }
    }
    public byte[] encryptWithSessionKey (String content, SecretKey key) throws FailedToEncryptSMSException {
        try {
            int ivLen = 16;
            SecureRandom random = new SecureRandom();

            byte[] iv = new byte[ivLen];
            byte[] data = content.getBytes();

            random.nextBytes(iv);

            // Encrypt data
            byte[] encryptedData = sessionKeyCipher(data, key, iv, Cipher.ENCRYPT_MODE);

            // Add IV
            byte[] result = new byte[ivLen + encryptedData.length];
            System.arraycopy(iv, 0, result, 0, ivLen);
            System.arraycopy(encryptedData, 0, result, ivLen, encryptedData.length);

            return result;
        } catch (
                NullPointerException |
                NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                InvalidAlgorithmParameterException |
                ShortBufferException |
                IllegalBlockSizeException |
                BadPaddingException exception) {
            throw new FailedToEncryptSMSException(exception);
        }
    }
    public String decryptWithSessionKey (byte[] content, SecretKey key) throws FailedToDecryptSMSException {
        try {
            Charset charset = Charset.defaultCharset();
            int ivLen = 16;

            byte[] iv = new byte[ivLen];
            byte[] data = new byte[content.length - ivLen];

            // Get salt and IV
            System.arraycopy(content, 0, iv, 0, ivLen);
            System.arraycopy(content, ivLen, data, 0, data.length);

            // Decrypt data
            byte[] decryptedData = sessionKeyCipher(data, key, iv, Cipher.DECRYPT_MODE);

            // Remove padding
            return new String(decryptedData, charset);
        } catch (
                NullPointerException |
                        NoSuchAlgorithmException |
                        NoSuchPaddingException |
                        InvalidKeyException |
                        InvalidAlgorithmParameterException |
                        ShortBufferException |
                        IllegalBlockSizeException |
                        BadPaddingException exception) {
            throw new FailedToDecryptSMSException(exception);
        }
    }
}
