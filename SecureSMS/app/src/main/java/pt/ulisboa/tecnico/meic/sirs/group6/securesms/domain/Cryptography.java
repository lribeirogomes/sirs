package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToSignException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToVerifySignatureException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.InvalidSignatureException;

/**
 * Created by joao on 11/21/15.
 */
public class Cryptography {
    private static final int IV_SIZE = 16;

    public static byte[] symmetricCipher(byte[] plainText, SecretKey key)throws FailedToEncryptException{//TODO: Should we really use CBC? It will cost 16 extra characters
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
        }catch (InvalidKeyException
                | NoSuchProviderException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | IllegalBlockSizeException
                | InvalidAlgorithmParameterException
                | BadPaddingException e){
            //throw new FailedToEncryptException("Failed to encrypt data");
            throw new FailedToEncryptException(e.getClass().toString() + e.getMessage());

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
        }catch (InvalidKeyException
                | NoSuchProviderException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | IllegalBlockSizeException
                | InvalidAlgorithmParameterException
                | BadPaddingException e){
            throw new FailedToDecryptException("Failed to decrypt data");
        }
    }
    public static byte[] asymmetricCipher(byte[] plainText, PublicKey key){
        return null;
    }
    public static byte[] asymmetricDecipher(byte[] cipheredData, PrivateKey key){
        return null;
    }
    public static byte[] passwordCipher(byte[] plainText, String password){
        return null;
    }
    public static byte[] passwordDecipher(byte[] cipheredData, String password){
        return null;
    }

    public static byte[] sign(byte[] message, PrivateKey key)throws FailedToSignException{
        try{
            Signature dsa = Signature.getInstance("SHA224withECDSA");
            dsa.initSign(key);
            dsa.update(message);
            return dsa.sign();
        }catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e){
            throw new FailedToSignException("Failed to produce a signature");
        }
    }
    public static void verifySignature(byte[] message, byte[] signature, PublicKey key)throws FailedToVerifySignatureException, InvalidSignatureException{
        try{
            Signature dsa = Signature.getInstance("SHA224withECDSA");
            dsa.initVerify(key);
            dsa.update(message);
            if(!dsa.verify(signature))
                throw new InvalidSignatureException("This Signature is not valid");
        }catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e){
            throw new FailedToVerifySignatureException("Error occurred when verifying the signature");
        }
    }
}
