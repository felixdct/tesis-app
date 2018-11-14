package esime.authentication.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;

/**
 * Created by DCMir on 20/11/17.
 */

public class FingerPrintEncryption {

    public static final String KEY_NAME = "T351sF3l1x1994119505062805251129112710@!";
    private KeyPairGenerator mKeyPairGenerator;
    private KeyStore mKeyStore;

    public FingerPrintEncryption() throws NoSuchProviderException, NoSuchAlgorithmException,
            KeyStoreException
    {
        /* Get instance of pair generator, using android key store to save the key */
        mKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        mKeyStore = KeyStore.getInstance("AndroidKeyStore");
    }

    public void createKeyPair() throws InvalidAlgorithmParameterException
    {
        KeyPair mKeyPair;

        /* Set generator key */
        mKeyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                        KEY_NAME,
                        KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                        .setUserAuthenticationRequired(true)
                        .build());

            /* Generate and get public and private keys */
        mKeyPair = mKeyPairGenerator.generateKeyPair();
        PublicKey  publicKey  = mKeyPair.getPublic();
        PrivateKey privateKey = (PrivateKey) mKeyPair.getPrivate();

        Log.i("FingerPrint public: ", String.valueOf(publicKey.getEncoded()));
        Log.i("FingerPrint private:", String.valueOf(privateKey.getEncoded()));
    }

    public PublicKey getPublicKey() throws CertificateException, NoSuchAlgorithmException, IOException,
            KeyStoreException
    {
        mKeyStore.load(null);
        PublicKey publicKey = mKeyStore.getCertificate(KEY_NAME).getPublicKey();
        return publicKey;
    }

    public PrivateKey getPrivateKey() throws CertificateException, NoSuchAlgorithmException,
            IOException, UnrecoverableKeyException, KeyStoreException {
        mKeyStore.load(null);

        PrivateKey privateKey = (PrivateKey) mKeyStore.getKey(KEY_NAME, null);
        return privateKey;
    }

    public void deleteKeys() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        mKeyStore.load(null);
        mKeyStore.deleteEntry(KEY_NAME);
    }
}
