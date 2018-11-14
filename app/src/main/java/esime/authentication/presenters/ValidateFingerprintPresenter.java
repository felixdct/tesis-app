package esime.authentication.presenters;

/**
 * Created by toledo on 20/11/17.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Signature;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import esime.authentication.models.WSConsumer;
import esime.authentication.utils.SysError;
import esime.authentication.utils.Utils;
import esime.authentication.views.FingerPrintActivity;
import esime.authentication.views.MainActivity;
import esime.authentication.views.R;

import static java.security.AccessController.getContext;

public class ValidateFingerprintPresenter extends FingerprintManager.AuthenticationCallback {


    private Context context;
    private ImageView mIcon;
    private FingerprintManager manager;
    private FingerprintManager.CryptoObject cryptoObject;
    private String user;
    private String op;
    private String newPasswd;
    private Integer numTry;
    int c=0;

    // Constructor
    public ValidateFingerprintPresenter(Context mContext, FingerprintManager manager,
                                        FingerprintManager.CryptoObject cryptoObject,
                                        String user, String op, String newPasswd) {
        context = mContext;
        this.manager = manager;
        this.cryptoObject = cryptoObject;
        this.user = user;
        this.op   = op;
        this.newPasswd = newPasswd;
        this.numTry = 0;
        mIcon = (ImageView) ((Activity)context).findViewById(R.id.fingerImage);
    }


    public void startAuth() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        mIcon.setImageResource(R.drawable.ic_fp_40px);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.numTry++;
        this.update("Error de autenticación. Intento " + String.valueOf(numTry), false);
        sendFingerPrintFailToServer();
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        if(numTry==3){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent returnMain= new Intent(context,MainActivity.class);
                    context.startActivity(returnMain);
                }
            }, 1900);
        }
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Se encontró un error recuperable.\n" + helpString, false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.numTry++;
        this.update("Falló la autenticación. Intento " + String.valueOf(numTry), false);
        sendFingerPrintFailToServer();
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        if(numTry==3){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent returnMain= new Intent(context,MainActivity.class);
                    context.startActivity(returnMain);
                }
            }, 1900);
        }
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Autenticación exitosa", true);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        verifyFingerPrintOnServer();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent returnMain= new Intent(context,MainActivity.class);
                context.startActivity(returnMain);
            }
        }, 1900);

    }

    public void update(String e, Boolean success){
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.Mensaje);
        textView.setText(e);
        if(success){
            textView.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        }
    }

    private void verifyFingerPrintOnServer() {
        Signature signature = cryptoObject.getSignature();
        String urlStr;
        String opStr;
        byte byteArray[];
        byte sigBytes[];
        String dataStr = "";
        String signatureStr = "";
        String msg;
        Integer resp;
        String opVerb = "";
        String obj = "";
        WSConsumer wsConsumer = new WSConsumer();

        try {
            byteArray = toByteArray(user, this.op);
            signature.update(byteArray);
            sigBytes = signature.sign();

            dataStr = Base64.encodeToString(byteArray, Base64.DEFAULT);
            signatureStr = "-----BEGIN SIGNATURE-----" + "\n" +
                    Base64.encodeToString(sigBytes, Base64.DEFAULT).replaceAll("(.{64})", "$1\n") +
                    "-----END SIGNATURE-----";;
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        /* Send byteArray(data) and sigBytes(signature) to verify in server */
        opStr = Utils.getOperation(this.op);
        Log.i("Fingerprint: ","sending data Base64 code "+ dataStr);
        Log.i("Fingerprint: ", "sending signature Base64 code" + signatureStr);
        Log.i("Fingerprint: ", "seding passwd "+this.newPasswd);
        urlStr = String.format(context.getResources().getString(R.string.VerifyFingerPrintData),
                    this.user, opStr, this.newPasswd, URLEncoder.encode(dataStr), URLEncoder.encode(signatureStr), "success", String.valueOf(numTry));
        Log.i("Fingerprint: ", "sending request "+urlStr);

        try {
            resp = wsConsumer.execute(new URL(urlStr)).get();
            if (resp == 0) {
                switch (this.op) {
                    case "1":
                        opVerb = "logueado";
                        obj    = "el usuario";
                        break;
                    case "2":
                        opVerb = "agregado";
                        obj    = "el usuario";
                        break;
                    case "3":
                        opVerb = "eliminado";
                        obj    = "el usuario";
                        break;
                    case "4":
                        opVerb = "cambiado";
                        obj    = "la contraseña";
                        break;
                    default:
                        opVerb = "";
                        obj    = "";
                        break;
                }
                msg = String.format(SysError._SUCCESS_OP_MSG, opVerb, obj);
            }else {
                msg = SysError.getMessage(resp);
            }

            /* Update txt message to inform to user */
            this.update(msg, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private byte[] toByteArray(String user, String op) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF(this.user);
            dataOutputStream.writeUTF(this.op);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException ignore) {
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException ignore) {
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void sendFingerPrintFailToServer() {
        String urlStr;
        String opStr = opStr = Utils.getOperation(this.op);
        Integer resp;
        String msg = "";
        WSConsumer wsConsumer = new WSConsumer();

        urlStr = String.format(context.getResources().getString(R.string.VerifyFingerPrintData),
                this.user, opStr,"", "", "", "fingerprintnotmatch", String.valueOf(numTry));
        Log.i("Fingerprint: ", "sending request "+urlStr);
        try {
            resp = wsConsumer.execute(new URL(urlStr)).get();
            Log.i("Fingerprint: ", "numTries :"+numTry);
            if (resp == SysError._CREDENTIAL_FINGERPRINT_NOT_VALIDATED ||
                    resp == SysError._CREDENTIAL_FINGERPRINT_ERROR_VERIFYING){
                if (numTry >= SysError._MAXIMUM_NUM_TRIES) {
                    msg = "Se ha cancelado la operación " + opStr + ". Intento " + String.valueOf(numTry);
                } else {
                    msg = "Falló la autenticación. Intento " + String.valueOf(numTry);
                }
            }else {
                msg = SysError.getMessage(resp);
            }

            /* Update txt message to inform to user */
            this.update(msg, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Intent returnMain = new Intent(this,MainActivity.class);
        //context.startActivity(returnMain);
    }

}
