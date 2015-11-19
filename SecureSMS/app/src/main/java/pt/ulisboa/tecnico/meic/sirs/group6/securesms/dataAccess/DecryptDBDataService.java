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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToDecryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class DecryptDBDataService {
    private String _password;
    private byte[] _data;
    private String _result;

    public DecryptDBDataService(String password, byte[] data) {
        _password = password;
        _data = data;
        _result = null;
    }

    public String getResult () throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void Execute () throws FailedToDecryptSMSException {
        try {
            Charset _charset = StandardCharsets.UTF_8;
            String algoritm = "PBKDF2WithHmacSHA1";
            int saltLen = 32, ivLen = 16;

            byte[] salt = new byte[saltLen],
                   iv = new byte[ivLen],
                   data = new byte[_data.length - saltLen - ivLen];
            char[] password = _password.toCharArray();

            // Get salt and IV
            System.arraycopy(_data, 0, salt, 0, saltLen);
            System.arraycopy(_data, saltLen, iv, 0, ivLen);
            System.arraycopy(_data, saltLen + ivLen, data, 0, data.length);

            PBEKeySpec pbeKeySpec = new PBEKeySpec(
                    password,
                    salt,
                    1000,
                    128);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algoritm);
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
            cipher.init(false, params);

            // Encrypt data
            byte[] decryptedData = new byte[cipher.getOutputSize(data.length)];
            int dataLen = cipher.processBytes(data, 0, data.length, decryptedData, 0);
            cipher.doFinal(decryptedData, dataLen);

            // Remove padding
            _result = new String(decryptedData, _charset);
        } catch (
                NullPointerException |
                NoSuchAlgorithmException |
                InvalidKeySpecException |
                InvalidCipherTextException exception) {
            throw new FailedToDecryptSMSException(exception);
        }
    }
}