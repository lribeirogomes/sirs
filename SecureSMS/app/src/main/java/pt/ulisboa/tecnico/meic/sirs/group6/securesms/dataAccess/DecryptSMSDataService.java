package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class DecryptSMSDataService {
    private SecretKeySpec _key;
    private byte[] _data;
    private String _result;

    public DecryptSMSDataService(SecretKeySpec key, byte[] data) {
        _key = key;
        _data = data;
        _result = null;
    }

    public String getResult () throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void Execute () throws FailedToEncryptSMSException {
        try {
            Charset charset = Charset.defaultCharset();
            int ivLen = 32;

            byte[] iv = new byte[ivLen];
            byte[] data = new byte[_data.length - ivLen];

            // Get salt and IV
            System.arraycopy(_data, 0, iv, 0, ivLen);
            System.arraycopy(_data, ivLen, data, 0, data.length);

            // Define IV parameters
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Define AES cipher in CBC mode with CTS
            Cipher cipher = Cipher.getInstance("AES/CBC/WithCTS");

            // Decrypt data
            cipher.init(Cipher.DECRYPT_MODE, _key, ivSpec);
            byte[] buf= new byte[cipher.getOutputSize(data.length)];
            int dataLen = cipher.update(data, 0, data.length, buf, 0);
            dataLen += cipher.doFinal(buf, dataLen);

            // Remove padding
            byte[] result = new byte[dataLen];
            System.arraycopy(buf, 0, result, 0, dataLen);
            _result = new String(result, charset);
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
