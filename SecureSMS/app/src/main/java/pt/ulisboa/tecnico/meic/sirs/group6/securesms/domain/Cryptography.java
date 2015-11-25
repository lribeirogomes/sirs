package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToHashException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToSignException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToVerifySignatureException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.InvalidSignatureException;

/**
 * Created by joao on 11/21/15.
 */
public class Cryptography {
    private static final int IV_SIZE = 16;

    public static byte[] symmetricCipher(byte[] plainText, SecretKey key)throws FailedToEncryptException{
        //Should we really use CBC? It will cost 16 extra characters
        try{
            SecureRandom random = new SecureRandom();
            byte iv[] = new byte[IV_SIZE];
            random.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            Cipher aesCipher = Cipher.getInstance("AES/CBC/withCTS", "BC");
            aesCipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            byte[] cipherText = aesCipher.doFinal(plainText);

            byte[] ivPlusCipherText = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, ivPlusCipherText, 0, iv.length);
            System.arraycopy(cipherText, 0, ivPlusCipherText, iv.length, cipherText.length);

            return ivPlusCipherText;
        } catch (InvalidKeyException
                | NoSuchProviderException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | IllegalBlockSizeException
                | InvalidAlgorithmParameterException
                | BadPaddingException exception){
            throw new FailedToEncryptException(exception);
        }
    }

    public static byte[] symmetricDecipher(byte[] cipheredData, SecretKey key)throws FailedToDecryptException{
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(cipheredData,0, iv, 0, IV_SIZE);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        byte[] cipheredMessage = new byte[cipheredData.length-IV_SIZE];
        System.arraycopy(cipheredData, IV_SIZE, cipheredMessage, 0, cipheredData.length-IV_SIZE);

        try{
            Cipher aesCipher = Cipher.getInstance("AES/CBC/withCTS", "BC");
            aesCipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            return aesCipher.doFinal(cipheredMessage);
        } catch (InvalidKeyException
                | NoSuchProviderException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | IllegalBlockSizeException
                | InvalidAlgorithmParameterException
                | BadPaddingException exception){
            throw new FailedToDecryptException(exception);
        }
    }

    public static byte[] asymmetricCipher(byte[] plainText, PublicKey key)throws FailedToEncryptException{
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        }catch(NoSuchAlgorithmException
                | NoSuchProviderException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException exception){
            throw new FailedToEncryptException(exception);
        }
    }

    public static byte[] asymmetricDecipher(byte[] cipheredData, PrivateKey key)throws FailedToDecryptException{
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(cipheredData);
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException exception){
            throw new FailedToDecryptException(exception);
        }
    }

    private static byte[] passwordCipher(byte[] data, String password, byte[] salt, byte[] iv, boolean encrypt) throws
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

    public static byte[] passwordCipher (byte[] plainText, String password) throws FailedToEncryptException {
        int saltLen = 32, ivLen = 16;
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLen],
                iv = new byte[ivLen],
                data = plainText;

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
        } catch (NullPointerException
                | NoSuchAlgorithmException
                | InvalidKeySpecException
                | InvalidCipherTextException exception){
            throw new FailedToEncryptException(exception);
        }
    }
    public static byte[] passwordDecipher(byte[] cipherData, String password) throws FailedToDecryptException {
        int saltLen = 32, ivLen = 16;
        byte[] salt = new byte[saltLen],
                iv = new byte[ivLen],
                data = new byte[cipherData.length - saltLen - ivLen];

        try {
            // Get salt and IV
            System.arraycopy(cipherData, 0, salt, 0, saltLen);
            System.arraycopy(cipherData, saltLen, iv, 0, ivLen);
            System.arraycopy(cipherData, saltLen + ivLen, data, 0, data.length);

            // Decrypt data
            byte[] decryptedData = passwordCipher(data, password, salt, iv, false);

            return decryptedData;
        } catch (NullPointerException
                | NoSuchAlgorithmException
                | InvalidKeySpecException
                | InvalidCipherTextException exception){
            throw new FailedToDecryptException(exception);
        }
    }

    public static byte[] sign(byte[] message, PrivateKey key)throws FailedToSignException{
        try{
            Signature dsa = Signature.getInstance("SHA224withECDSA");
            dsa.initSign(key);
            dsa.update(message);
            return dsa.sign();
        }catch (NoSuchAlgorithmException
                | InvalidKeyException
                | SignatureException exception){
            throw new FailedToSignException(exception);
        }
    }

    public static void verifySignature(byte[] message, byte[] signature, PublicKey key)throws FailedToVerifySignatureException, InvalidSignatureException{
        try{
            Signature dsa = Signature.getInstance("SHA224withECDSA");
            dsa.initVerify(key);
            dsa.update(message);
            if(!dsa.verify(signature))
                throw new InvalidSignatureException("This Signature is not valid");
        }catch (NoSuchAlgorithmException
                | InvalidKeyException
                | SignatureException exception){
            throw new FailedToVerifySignatureException(exception);
        }
    }

    public static byte[] hash(byte[] message) throws FailedToHashException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(message);
            byte[] messageDigest = md.digest();
            return messageDigest;
        } catch ( NoSuchAlgorithmException exception) {
            throw new FailedToHashException(exception);
        }
    }

    public static byte[] encode(String message) {
        return message.getBytes(Charset.defaultCharset());
    }
    public static String decode(byte[] message) {
        return new String(message, Charset.defaultCharset());
    }
}
