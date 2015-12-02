package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.os.Environment;

import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x500.RDN;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x500.style.IETFUtils;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.openssl.PEMDecryptorProvider;
import org.spongycastle.openssl.PEMEncryptedKeyPair;
import org.spongycastle.openssl.PEMKeyPair;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.spongycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGenerateKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.ImportKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.InvalidCertificateException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.KeyStoreIsLockedException;

/**
 * Created by joao on 11/11/15.
 * This is a Singleton used to manage functionality related to the generation, storage and retrieval of cryptographic keys
 * for the SecureSMS android application
 */
public class KeyManager {

    private final int AES_KEY_SIZE = 128;

    private static final String KEYSTORE_FILE = "/SecureSMS.ks";

    private static final String OWN = "Own";
    private static final String SIGNING_CERT = "_Signing_Certificate";
    private static final String ENCRYPTION_CERT = "_Encryption_Certificate";
    private static final String CACERT = "CA_Certificate";
    private static final String SIGNING_KEY = "_Signing_Key";
    private static final String ENCRYPTION_KEY = "_Encryption_Key";
    private static final String SECRET_KEY = "_Secret_Key";

    private static KeyStore _ks = null;
    private static char[] _keyStorePassword = null;

    private static KeyManager ourInstance = new KeyManager();

    public static KeyManager getInstance(String password)throws FailedToLoadKeyStoreException {
        init(password.toCharArray());
        return ourInstance;
    }

    public static KeyManager getInstance()throws KeyStoreIsLockedException{
        if(null == _ks)
            throw new KeyStoreIsLockedException();
        return ourInstance;
    }

    private KeyManager() {

    }

    private static void init(char[] password)throws FailedToLoadKeyStoreException{
        if(null == _ks){
            _keyStorePassword = password;
            try {
                _ks = KeyStore.getInstance("UBER", "SC");
                java.io.FileInputStream fis = null;
                try {
                    fis = new java.io.FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + KEYSTORE_FILE);
                    _ks.load(fis, _keyStorePassword);
                }catch(FileNotFoundException e){
                    _ks.load(null);
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException e){
                throw new FailedToLoadKeyStoreException("Failed to load the keystore");
            }
        }else if(!Arrays.equals(_keyStorePassword, password))
            throw new FailedToLoadKeyStoreException("Wrong password");
    }

    private void saveKeyStore()throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + KEYSTORE_FILE);
            _ks.store(fos, _keyStorePassword);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public PrivateKey getMySigningPrivateKey()throws FailedToRetrieveKeyException{
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_keyStorePassword);
        try {
            KeyStore.PrivateKeyEntry privKeyEntry = (KeyStore.PrivateKeyEntry) _ks.getEntry(OWN + SIGNING_KEY, protParam);
            if (null == privKeyEntry)
                throw new FailedToRetrieveKeyException("Private signing key not yet imported.");
            return privKeyEntry.getPrivateKey();

        }catch(NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e){
            throw new FailedToRetrieveKeyException("Key storage error.");
        }
    }

    public PrivateKey getMyEncryptionPrivateKey()throws FailedToRetrieveKeyException{
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_keyStorePassword);
        try {
            KeyStore.PrivateKeyEntry privKeyEntry = (KeyStore.PrivateKeyEntry) _ks.getEntry(OWN + ENCRYPTION_KEY, protParam);
            if (null == privKeyEntry)
                throw new FailedToRetrieveKeyException("Private encryption key not yet imported.");
            return privKeyEntry.getPrivateKey();

        }catch(NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e){
            throw new FailedToRetrieveKeyException("Key storage error.");
        }
    }

    public PublicKey getContactSigningPublicKey(String phonenumber)throws FailedToRetrieveKeyException{
        try{
            Certificate signCert = _ks.getCertificate(phonenumber + SIGNING_CERT);
            if(null == signCert)
                throw new FailedToRetrieveKeyException("Don't have a certificate for this phonenumber");
            return signCert.getPublicKey();
        }catch (KeyStoreException e){
            throw new FailedToRetrieveKeyException("KeyStore failed");
        }
    }

    public PublicKey getContactEncryptionPublicKey(String phonenumber)throws FailedToRetrieveKeyException{
        try{
            Certificate signCert = _ks.getCertificate(phonenumber + ENCRYPTION_CERT);
            if(null == signCert)
                throw new FailedToRetrieveKeyException("Don't have a certificate for this phonenumber");
            return signCert.getPublicKey();
        }catch (KeyStoreException e){
            throw new FailedToRetrieveKeyException("KeyStore failed");
        }
    }

    public SecretKey generateNewSessionKey(String sessionId) throws FailedToGenerateKeyException, FailedToStoreException{
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(AES_KEY_SIZE, new SecureRandom());
            SecretKey sessionKey = keyGen.generateKey();

            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_keyStorePassword);
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(sessionKey);
            _ks.setEntry(sessionId + SECRET_KEY, secretKeyEntry, protParam);
            saveKeyStore();

            return sessionKey;
        }catch(KeyStoreException | IOException | CertificateException e){
            throw new FailedToStoreException("Failed to store the session key");
        }catch (NoSuchAlgorithmException e){
            throw new FailedToGenerateKeyException("AES cipher is not available");
        }
    }

    public SecretKey importSessionKey(byte[] encodedKey, String sessionId)throws FailedToStoreException{
        try{
            SecretKey sessionKey = new SecretKeySpec(encodedKey, "AES");

            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_keyStorePassword);
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(sessionKey);
            _ks.setEntry(sessionId + SECRET_KEY, secretKeyEntry, protParam);
            saveKeyStore();

            return sessionKey;
        }catch(IllegalArgumentException
                | KeyStoreException
                | NoSuchAlgorithmException
                | CertificateException
                |IOException e){
            throw new FailedToStoreException("Failed to import the session key");
        }
    }

    public SecretKey getSessionKey(String sessionId)throws FailedToRetrieveKeyException{
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_keyStorePassword);
        try {
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) _ks.getEntry(sessionId + SECRET_KEY, protParam);
            if (null == secretKeyEntry)
                throw new FailedToRetrieveKeyException("No session key with that id.");
            return secretKeyEntry.getSecretKey();

        }catch(NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e){
            throw new FailedToRetrieveKeyException("Key storage error.");
        }
    }

    public void removeSessionKey(String sessionId)throws FailedToRemoveKeyException {
        try{
            _ks.deleteEntry(sessionId + SECRET_KEY);
            saveKeyStore();
        }catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e){
            throw new FailedToRemoveKeyException("Failed to remove session key");
        }
    }

    public void importCACertificate(String filename)throws FailedToStoreException, InvalidCertificateException{
        //TODO: For now we are limited to only one CA certificate
        try{
            X509Certificate cacert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(new FileInputStream(filename));
            cacert.checkValidity();
            _ks.setCertificateEntry(CACERT, cacert);
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

    public void importUserCertificates(String filename, boolean own, boolean validate) throws FailedToStoreException, InvalidCertificateException, FailedToRetrieveKeyException, CertPathValidatorException{
        try{
            Collection col_crt = CertificateFactory.getInstance("X509").generateCertificates(new FileInputStream(filename));
            Iterator<X509Certificate> iter = col_crt.iterator();
            while(iter.hasNext()){
                X509Certificate cert = iter.next();
                cert.checkValidity();
                //We might want to import a self-signed certificate that would otherwise fail validity checks
                if(validate)
                    checkCertificateValidity(cert);
                String name;

                if(own)
                     name = OWN;
                else {
                    //Get the CN of the subject
                    X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
                    RDN cnRdn = x500name.getRDNs(BCStyle.CN)[0];
                    name = IETFUtils.valueToString(cnRdn.getFirst().getValue());
                    name = name.replaceAll("[^\\d.]", ""); //Strip everything that is not a digit
                    name = "+" + name;
                }

                if(cert.getPublicKey().getAlgorithm().equals("EC")) {
                    _ks.setCertificateEntry(name + SIGNING_CERT, cert);
                }
                if(cert.getPublicKey().getAlgorithm().equals("RSA")) {
                    _ks.setCertificateEntry(name + ENCRYPTION_CERT, cert);
                }
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

    public void importPrivateKey(String keyFilename, String password)throws  ImportKeyException, FailedToStoreException{ //TODO:Check key length
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_keyStorePassword);
        try {
            PrivateKey privateKey = importEncryptedPEMPrivateKey(keyFilename, password);
            Certificate cert;
            if(privateKey.getAlgorithm().equals("EC"))
                cert = _ks.getCertificate(OWN + SIGNING_CERT);
            else if(privateKey.getAlgorithm().equals("RSA"))
                cert = _ks.getCertificate(OWN + ENCRYPTION_CERT);
            else
                throw new ImportKeyException("Private key uses an unsupported algorithm");

            Certificate[] certificate_chain = {cert};
            KeyStore.PrivateKeyEntry privKeyEntry = new KeyStore.PrivateKeyEntry(privateKey, certificate_chain);

            if(privateKey.getAlgorithm().equals("EC")){
                //((ECKey)privateKey).getParams().getOrder().bitLength()
                _ks.setEntry(OWN + SIGNING_KEY, privKeyEntry, protParam);
            }else if(privateKey.getAlgorithm().equals("RSA"))
                _ks.setEntry(OWN + ENCRYPTION_KEY, privKeyEntry, protParam);

        }catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new ImportKeyException("Failed to import the private key");
        }catch(KeyStoreException e){
            throw new ImportKeyException("Missing the certificate for the private key");
        }

        try {
            saveKeyStore();
        }catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e){
            throw new FailedToStoreException("Failed to store the private key");
        }

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

    private void checkCertificateValidity(X509Certificate certificate)throws FailedToRetrieveKeyException, InvalidCertificateException, CertPathValidatorException{
        /* Certificate validity checking only validates certificates that were signed directly by the CA Certificate.
           (no chains and remember that we only support having only ONE CA certificate)
        */
        try{
            //Get the CA certificate and set up a TrustAnchor
            X509Certificate caCert = (X509Certificate) _ks.getCertificate(CACERT);
            if(null == caCert)
                throw new FailedToRetrieveKeyException("Missing the CA Certificate");
            TrustAnchor trustAnchor = new TrustAnchor(caCert, null);
            Set<TrustAnchor> trustAnchorSet = new HashSet<TrustAnchor>();    //This can only be a HashSet (ArraySet is only available in API23 and the other sets require Comparable)
            trustAnchorSet.add(trustAnchor);

            //Setup the the certificate validator
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            PKIXParameters params = new PKIXParameters(trustAnchorSet);
            CertPathValidator cpv = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
            params.setRevocationEnabled(false);

            List<X509Certificate> certList = new ArrayList<X509Certificate>();

            //Validate the signing certificate
            certList.add(certificate);
            CertPath cp = cf.generateCertPath(certList);
            cpv.validate(cp, params);

        }catch (KeyStoreException e){
            throw new FailedToRetrieveKeyException("KeyStore failed");
        }catch(CertificateException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e){
            throw new InvalidCertificateException("Cannot check validity of certificate");
        }
    }
}
