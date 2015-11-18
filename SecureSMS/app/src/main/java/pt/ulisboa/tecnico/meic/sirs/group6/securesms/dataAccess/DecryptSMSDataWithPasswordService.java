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
public class DecryptSMSDataWithPasswordService {
    private String _password;
    private byte[] _data;
    private String _result;
    private Charset _charset = StandardCharsets.UTF_8;

    public DecryptSMSDataWithPasswordService(String password, byte[] data) {
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
            int saltLen = 32, ivLen = 32;

            byte[] salt = new byte[saltLen],
                   iv = new byte[ivLen],
                   data = new byte[_data.length - saltLen - ivLen];

            // Get salt and IV
            System.arraycopy(_data, 0, salt, 0, saltLen);
            System.arraycopy(_data, saltLen, iv, 0, ivLen);
            System.arraycopy(_data, saltLen + ivLen, data, 0, data.length);

            char[] passwordChar = _password.toCharArray();

            // Define encryption key
            PBEKeySpec pbeKeySpec = new PBEKeySpec(
                    passwordChar,
                    salt,
                    100,
                    256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
            SecretKey secretKey = keyFactory.generateSecret(pbeKeySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            byte[] key = secretKeySpec.getEncoded();

            // Define cipher parameters with key and IV
            KeyParameter keyParam = new KeyParameter(key);
            CipherParameters params = new ParametersWithIV(keyParam, iv);

            // Define AES cipher in CBC mode with PKCS7 padding
            BlockCipherPadding padding = new PKCS7Padding();
            CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new AESEngine());
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcBlockCipher, padding);
            cipher.reset();
            cipher.init(true, params);

            // Decrypt data
            byte[] decryptedData = new byte[cipher.getOutputSize(data.length)];
            int dataLen = cipher.processBytes(data, 0, data.length, decryptedData, 0);
            dataLen += cipher.doFinal(decryptedData, dataLen);

            // Remove padding
            byte[] result = new byte[dataLen];
            System.arraycopy(decryptedData, 0, result, 0, dataLen);
            _result = new String(result, _charset);
        } catch (
                NullPointerException |
                NoSuchAlgorithmException |
                InvalidKeySpecException |
                InvalidCipherTextException exception) {
            throw new FailedToDecryptSMSException(exception);
        }
    }
}