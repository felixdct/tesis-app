package esime.authentication.presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;

import esime.authentication.models.WSConsumer;
import esime.authentication.utils.FingerPrintEncryption;
import esime.authentication.utils.SysError;
import esime.authentication.utils.Utils;
import esime.authentication.views.R;

/**
 * Created by DCMir on 20/11/17.
 */

public class FingerPrintPresenter {
    private FingerPrintEncryption fingerPrintEncryption = null;
    private WSConsumer wsConsumer;
    private Context context;
    private String user;
    private String op;

    private static final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";

    public FingerPrintPresenter(Context context, String user, String op) throws NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException {
        this.fingerPrintEncryption = new FingerPrintEncryption();
        this.wsConsumer = new WSConsumer();
        this.context = context;
        this.user = user;
        this.op   = op;
    }

    public void createKeys() {
        /* Create public and private keys */
        try {
            fingerPrintEncryption.createKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void deleteKeys() {
        try {
            fingerPrintEncryption.deleteKeys();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StringFormatInvalid")
    public String sendPublicKeyToServer() throws ExecutionException, InterruptedException {
        PublicKey publicKey;
        String    publicKeyPEM;
        String urlStr;
        String opStr;
        Integer resp;
        String msg = "";

        try {
            publicKey = fingerPrintEncryption.getPublicKey();
            if (publicKey != null ){
                publicKeyPEM = PUBLICKEY_PREFIX + "\n" + Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT).replaceAll("(.{64})", "$1\n")  + PUBLICKEY_POSTFIX;

                opStr = Utils.getOperation(this.op);
                Log.i("Fingerprint: ", "sending publicKeyPEM : "+ publicKeyPEM);
                urlStr = String.format(context.getResources().getString(R.string.SendPublicKey), this.user, opStr, URLEncoder.encode(publicKeyPEM));
                Log.i("Fingerprint: ", "sending request " + urlStr);
                resp = this.wsConsumer.execute(new URL(urlStr)).get();

                Log.i("WS: ", String.valueOf(resp));

                if (resp == 0) {
                    msg = String.format(SysError._SUCCESS_QR_MSG, user);
                }else {
                    msg = SysError.getMessage(resp);
                }

            }else {
                msg = "public key not found";
            }
            /* toDo : send public key to the server */
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return msg;
    }

    public PrivateKey getPrivateKey() throws UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException
    {
        return  fingerPrintEncryption.getPrivateKey();
    }
}
