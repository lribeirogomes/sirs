package pt.ulisboa.tecnico.meic.sirs.group6.securesms;

import android.os.Environment;
import android.util.Base64InputStream;
import android.util.Log;

import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.openssl.PEMDecryptorProvider;
import org.spongycastle.openssl.PEMEncryptedKeyPair;
import org.spongycastle.openssl.PEMKeyPair;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.spongycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.spongycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Iterator;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.KeyGenerator;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.exceptions.ImportKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.exceptions.InvalidCertificateException;

/**
 * Created by joao on 11/11/15.
 * This is a Singleton used to manage functionality related to the generation, storage, retrieval and processing of cryptographic keys
 * for the SecureSMS android application
 */
public class KeyManager {

    private final int EC_KEY_SIZE = 224;
    private final int RSA_KEY_SIZE = 2048;
    private final int AES_KEY_SIZE = 128;

    private static final String KEYSTORE_FILE = "/SecureSMS.ks";

    private static final String OWN = "Own";
    private static final String SIGNING_CERT = "_Signing_Certificate";
    private static final String ENCRYPTION_CERT = "_Encryption_Certificate";
    private static final String SIGNING_KEY = "_Signing_Key";
    private static final String ENCRYPTION_KEY = "_Encryption_Key";

    private static KeyStore ks = null;
    private static char[] keyStorePassword;

    private static KeyManager ourInstance = new KeyManager();

    public static KeyManager getInstance(char[] password)throws FailedToLoadKeyStoreException {
        init(password);
        return ourInstance;
    }

    private KeyManager() {

    }

    private static void init(char[] password)throws FailedToLoadKeyStoreException{
        if(null == ks){
            keyStorePassword = password;
            try {
                ks = getKeyStore();
            }catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException e){
                throw new FailedToLoadKeyStoreException(e.getMessage());
            }
        }
    }

    private static KeyStore getKeyStore()throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException{
        KeyStore ks = KeyStore.getInstance("UBER", "SC");
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + KEYSTORE_FILE);
            ks.load(fis, keyStorePassword);
        }catch(FileNotFoundException e){
            ks.load(null);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return ks;
    }

    private void saveKeyStore()throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + KEYSTORE_FILE);
            ks.store(fos, keyStorePassword);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public void importCertificates(String filename) throws FailedToStoreException, InvalidCertificateException{
        try{
            Collection col_crt = CertificateFactory.getInstance("X509").generateCertificates(new FileInputStream(filename));
            Iterator<X509Certificate> iter = col_crt.iterator();
            while(iter.hasNext()){
                X509Certificate cert = iter.next();
                cert.checkValidity();
                //TODO: use CertificateFactory.generateCertPath(cert) to verify the authenticity of the certificate with a CertPathValidator
                if(cert.getPublicKey().getAlgorithm().equals("EC"))
                    ks.setCertificateEntry(OWN + SIGNING_CERT, cert);
                if(cert.getPublicKey().getAlgorithm().equals("RSA"))
                    ks.setCertificateEntry(OWN + ENCRYPTION_CERT, cert);
            }
            saveKeyStore();
        }catch(CertificateExpiredException e){
            throw new InvalidCertificateException("Certificate has expired.");
        }catch (CertificateNotYetValidException e){
            throw new InvalidCertificateException("Certificate is not yet valid.");
        }catch(CertificateException e){
            throw new InvalidCertificateException("Not a valid X509 Certificate.");
        }catch(FileNotFoundException e){
            throw new InvalidCertificateException("File not found");
        }catch(KeyStoreException | IOException | NoSuchAlgorithmException e){
            throw new FailedToStoreException("Failed to store Certificates");
        }
    }

    private PrivateKey importDERPrivateKey(String filename, String algorithm)throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        FileInputStream fis = new FileInputStream(filename);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        byte[] key = new byte[bais.available()];
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        bais.read(key, 0, bais.available());
        bais.close();

        PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec(key);
        return kf.generatePrivate(keysp);
    }

    private PrivateKey importEncryptedPEMPrivateKey(String filename, String password)throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        FileReader fileReader = new FileReader(filename);
        PEMParser keyReader = new PEMParser(fileReader);

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PEMDecryptorProvider decryptionProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());

        Object keyPair = keyReader.readObject();
        PrivateKeyInfo keyInfo;

        if (keyPair instanceof PEMEncryptedKeyPair) {
            PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) keyPair).decryptKeyPair(decryptionProv);
            keyInfo = decryptedKeyPair.getPrivateKeyInfo();
        } else {
            keyInfo = ((PEMKeyPair) keyPair).getPrivateKeyInfo();
        }

        fileReader.close();
        keyReader.close();

        PrivateKey privateKey = converter.getPrivateKey(keyInfo);

        /*OK so now we have the private key, and for RSA that's enough we can just return it
          Unfortunately for ECDSA we have to perform a little Hack to get it working...
          What happens is that openssl generates the private key saying its algorithm is "ECDSA"
          but when it generates the corresponding certificate it says that the public key algorithm is "EC"
          That causes a problem, because the keystore.PrivateKeyEntry constructor compares the two strings
          and says they are not the same algorithm so we have to regenerate the PrivateKey with algorithm set to "EC"
         */
        if(privateKey.getAlgorithm().equals("ECDSA")){
            byte[] encodedPrivKey = privateKey.getEncoded();
            PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec(encodedPrivKey);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(keysp);
        }else
            return privateKey;


    }

    public void importPrivateKeys(String signingKeyFilename, String encryptionKeyFilename, String password)throws ImportKeyException, FailedToStoreException{
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword);

        //Import the EC private key
        try {
            PrivateKey signingPrivateKey = importEncryptedPEMPrivateKey(signingKeyFilename, password);
            Certificate signingCert = ks.getCertificate(OWN + SIGNING_CERT);
            Certificate[] certificate_chain = {signingCert};//WARNING! HACK!! This should be the cert chain!
            KeyStore.PrivateKeyEntry privKeyEntry = new KeyStore.PrivateKeyEntry(signingPrivateKey, certificate_chain);
            ks.setEntry(OWN + SIGNING_KEY, privKeyEntry, protParam);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new ImportKeyException("Failed to import the signing private key");
        }catch(KeyStoreException e){
            throw new ImportKeyException("Missing the signing certificate for the private key");
        }

        //Import the RSA private key
        try {
            PrivateKey encryptionPrivateKey = importEncryptedPEMPrivateKey(encryptionKeyFilename, password);
            Certificate encryptionCert = ks.getCertificate(OWN + ENCRYPTION_CERT);
            Certificate[] certificate_chain = {encryptionCert};//WARNING! HACK!! This should be the cert chain!
            KeyStore.PrivateKeyEntry privKeyEntry = new KeyStore.PrivateKeyEntry(encryptionPrivateKey, certificate_chain);
            ks.setEntry(OWN + ENCRYPTION_KEY, privKeyEntry, protParam);
        }catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new ImportKeyException("Failed to import the encryption private key");
        }catch(KeyStoreException e){
            throw new ImportKeyException("Missing the encryption certificate for the private key");
        }

        //Store both keys
        try {
            saveKeyStore();
        }catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e){
            throw new FailedToStoreException("Failed to store the private keys");
        }
    }

    public PrivateKey getMySigningPrivateKey()throws FailedToRetrieveKeyException{
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword);
        try {
            KeyStore.PrivateKeyEntry privKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(OWN + SIGNING_KEY, protParam);
            if (null == privKeyEntry)
                throw new FailedToRetrieveKeyException("Private signing key not yet imported.");
            if (!(privKeyEntry instanceof KeyStore.PrivateKeyEntry))
                throw new FailedToRetrieveKeyException("Private key is invalid.");
            return privKeyEntry.getPrivateKey();

        }catch(NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e){
            throw new FailedToRetrieveKeyException("Key storage error.");
        }
    }

    public PrivateKey getMyEncryptionPrivateKey()throws FailedToRetrieveKeyException{
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword);
        try {
            KeyStore.PrivateKeyEntry privKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(OWN + ENCRYPTION_KEY, protParam);
            if (null == privKeyEntry)
                throw new FailedToRetrieveKeyException("Private encryption key not yet imported.");
            if (!(privKeyEntry instanceof KeyStore.PrivateKeyEntry))
                throw new FailedToRetrieveKeyException("Private key is invalid.");
            return privKeyEntry.getPrivateKey();

        }catch(NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e){
            throw new FailedToRetrieveKeyException("Key storage error.");
        }
    }

    public Key generateNewAESKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    //THIS IS A TESTING METHOD, TO BE REMOVED
    public String testECSignature(){
        try{

            String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

            Certificate ECSignCert = ks.getCertificate(OWN + SIGNING_CERT);
            if(null == ECSignCert){
                    importCertificates(sdcard + "/certificates.pem");
                    ECSignCert = ks.getCertificate(OWN + SIGNING_CERT);
            }
            PublicKey pubKey = ECSignCert.getPublicKey(); //Get the public key from the signing certificate


            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword);
            KeyStore.PrivateKeyEntry privKeyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(OWN+SIGNING_KEY, protParam);
            if(null == privKeyEntry || !(privKeyEntry instanceof KeyStore.PrivateKeyEntry)) {
                importPrivateKeys(sdcard + "/privateECkey.pem", sdcard + "/privateRSAkey.pem", "password12345");
                privKeyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(OWN+SIGNING_KEY, protParam);
            }
            PrivateKey privKey = privKeyEntry.getPrivateKey();

            //Sign some text
            Signature dsa = Signature.getInstance("SHA224withECDSA");
            dsa.initSign(privKey);
            String str = "This is a string to sign";

            byte[] strByte = str.getBytes("UTF-8");
            dsa.update(strByte);
            byte[] realSig = dsa.sign();

            //Verify the signature
            dsa.initVerify(pubKey);
            dsa.update(strByte);
            if (dsa.verify(realSig))
                return "Signature verified successfully! Signature length: " + realSig.length + " bytes";
            else
                return "Something went wrong!";
        }catch (Exception e){
            return e.getClass().toString() + e.getMessage();
        }
   }

}
