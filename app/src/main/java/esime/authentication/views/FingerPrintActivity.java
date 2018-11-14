package esime.authentication.views;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import esime.authentication.presenters.ValidateFingerprintPresenter;
import esime.authentication.views.R;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;

import esime.authentication.presenters.FingerPrintPresenter;
import esime.authentication.utils.FingerPrintEncryption;
import esime.authentication.utils.Utils;

/**
 * Created by DCMir on 20/11/17.
 */

public class FingerPrintActivity extends Activity {

    private Intent intentComeFromQR;
    private String user;
    private String op;
    private String newPasswd;

    /* FingerPrint Managers */
    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerprintManager;
    private FingerPrintPresenter fingerPrintPresenter = null;

    /* Setting FingerPrint */
    private Signature mSignature;
    private FingerprintManager.CryptoObject mCryptObject;
    private CancellationSignal mCancellationSignal;
    ValidateFingerprintPresenter mValidate;

    /* Views */
    private TextView userTxt;
    private TextView msgTxt;
    private ImageView mIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /* Get values from QR activity */
        intentComeFromQR = getIntent();
        user = intentComeFromQR.getStringExtra("user");
        op   = intentComeFromQR.getStringExtra("op");
        newPasswd = intentComeFromQR.getStringExtra("newpasswd");
        PrivateKey privateKey;

        /* Create our finger print presenter */
        try {
            fingerPrintPresenter = new FingerPrintPresenter(getApplicationContext(), user, op);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        if (checkFinger()) {
            /* If operation is equal to add user */
           // if (Utils.getOperation(op).equals(Utils.ADD_OP)) {
                /* Create public and private keys and send public key to the server */
            try {
                fingerPrintPresenter.deleteKeys();
                privateKey = (PrivateKey) fingerPrintPresenter.getPrivateKey();

                if (privateKey == null) {
                    fingerPrintPresenter.createKeys();
                    try {
                        fingerPrintPresenter.sendPublicKeyToServer();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    privateKey = (PrivateKey) fingerPrintPresenter.getPrivateKey();
                    //Log.i("Fingerprint :", new String(privateKey.getEncoded(),"utf-8"));
                }


            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //}
        }

        setContentView(R.layout.activity_fingerprint_auth);
        userTxt = (TextView) findViewById(R.id.Usuario);
        msgTxt  = (TextView) findViewById(R.id.Mensaje);
        mIcon   = (ImageView) findViewById(R.id.fingerImage);
        userTxt.setText(String.format(getApplicationContext().getResources().getString(R.string.txtViewFingerPrint_Usuario), user));

        settingFingerPrint();

        /* Setting FingerPrint */

        super.onCreate(savedInstanceState);
    }


    private boolean checkFinger() {
        // Keyguard Manager
        mKeyguardManager = (KeyguardManager)
                getSystemService(KEYGUARD_SERVICE);

        // Fingerprint Manager
        mFingerprintManager= (FingerprintManager)
                getSystemService(FINGERPRINT_SERVICE);
        // Keyguard Manager
        mKeyguardManager = (KeyguardManager)
                getSystemService(KEYGUARD_SERVICE);

        // Fingerprint Manager
        mFingerprintManager = (FingerprintManager)
                getSystemService(FINGERPRINT_SERVICE);
        try {
            // Check if the fingerprint sensor is present
            if (!mFingerprintManager.isHardwareDetected()) {
                // Update the UI with a message
                Toast.makeText(this,
                        "Autenticación por huella dactilar no soportada",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (!mFingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(this,
                        "Ve a 'Ajustes -> Seguridad -> Huella dactilar' y registrar al menos una huella digital",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (!mKeyguardManager.isKeyguardSecure()) {
                Toast.makeText(this,
                        "La pantalla de bloqueo seguro no se ha configurado.\n"
                                + "ve a 'Configuración -> Seguridad -> Huella digital, para configurar una huella digital",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch(SecurityException se) {
            se.printStackTrace();
        }
        return true;
    }

    private void settingFingerPrint()  {
        PrivateKey privateKey;
        try {
            mSignature = Signature.getInstance("SHA256withECDSA");
            privateKey = fingerPrintPresenter.getPrivateKey();
            mSignature.initSign(privateKey);
            mCryptObject = new FingerprintManager.CryptoObject(mSignature);
            mFingerprintManager= (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            mValidate = new ValidateFingerprintPresenter(this, mFingerprintManager, mCryptObject,
                                                         this.user, this.op, this.newPasswd);
            mValidate.startAuth();
            msgTxt.setText("Ingresa tu huella dactilar");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public void Aceptar(View view) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(FingerPrintActivity.this);
        alerta.setTitle("Regresar al main")
                .setMessage("Regresando al main...")
                .setCancelable(false)

                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FingerPrintActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).show();

    }
}
