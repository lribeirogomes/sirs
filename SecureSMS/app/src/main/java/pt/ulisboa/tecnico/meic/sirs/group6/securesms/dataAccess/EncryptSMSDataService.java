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
import java.nio.charset.StandardCharsets;
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

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class EncryptSMSDataService implements EncryptionService {
    private SecretKeySpec _key;
    private String _data;
    private byte[] _result;
    private Charset _charset = StandardCharsets.UTF_8;

    public EncryptSMSDataService(SecretKeySpec key, String data) {
        _key = key;
        _data = data;
        _result = null;
    }

    public byte[] getResult () throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void Execute () throws FailedToEncryptSMSException {
        try {
            int ivLen = 32;
            SecureRandom random = new SecureRandom();

            byte[] iv = new byte[ivLen];
            byte[] data = _data.getBytes();

            random.nextBytes(iv);

            // Define IV parameters
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Define AES cipher in CBC mode with CTS
            Cipher cipher = Cipher.getInstance("AES/CBC/WithCTS");

            // Encrypt data
            cipher.init(Cipher.ENCRYPT_MODE, _key, ivSpec);
            byte[] encryptedData= new byte[cipher.getOutputSize(data.length)];
            int dataLen = cipher.update(data, 0, data.length, encryptedData, 0);
            cipher.doFinal(encryptedData, dataLen);

            // Add IV
            byte[] result = new byte[ivLen + encryptedData.length];
            System.arraycopy(iv, 0, result, 0, ivLen);
            System.arraycopy(encryptedData, 0, result, ivLen, result.length);

            _result = result;
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
}
