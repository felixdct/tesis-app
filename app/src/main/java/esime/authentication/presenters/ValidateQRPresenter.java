package esime.authentication.presenters;

import android.content.Context;
import android.util.Log;

import esime.authentication.views.R;
import com.google.zxing.Result;

import org.apache.commons.codec.DecoderException;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import esime.authentication.utils.RSA;
import esime.authentication.utils.SysError;
import esime.authentication.models.WSConsumer;
import esime.authentication.utils.Utils;

/**
 * Created by DCMir on 19/11/17.
 */

public class ValidateQRPresenter {
    private Context context;
    private Result  result;
    private String user;
    private String op;
    private String newPasswd;
    private Integer numTks;

    public ValidateQRPresenter(Context context, Result result)
    {
            this.context = context;
            this.result  = result;
            user = null;
            op   = null;
            newPasswd = null;
    }

    public String isValidQR(final String qrEncryptedBytes, int  numTry) throws InvalidKeySpecException,
            DecoderException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException, InvalidKeyException, ExecutionException,
            InterruptedException
    {
        String urlStr;
        String msg;
        String opStr;
        WSConsumer wsConsumer = new WSConsumer();

        decryptQRAndGetUserAndOperation();


        opStr = Utils.getOperation(getOperationFromQR());

        urlStr = String.format(context.getResources().getString(R.string.QRValidateURI), getUserFromQR(), opStr,
                qrEncryptedBytes, Integer.toString(numTry));
        Log.i("WS: ", urlStr);
        Integer resp     = wsConsumer.execute(new URL(urlStr)).get();
        Log.i("WS: ", String.valueOf(resp));

        if (resp == 0) {
            msg = String.format(SysError._SUCCESS_QR_MSG, user);
        }else {
            msg = SysError.getMessage(resp);
        }

        return msg;
    }

    public String cancelOp() throws BadPaddingException, InvalidKeySpecException,
            NoSuchAlgorithmException, IllegalBlockSizeException, DecoderException,
            NoSuchPaddingException, InvalidKeyException, IOException, ExecutionException,
            InterruptedException
    {
        String opStr = Utils.getOperation(getOperationFromQR());
        String urlStr = String.format(context.getResources().getString(R.string.GenCancelOp), getUserFromQR(), opStr, "cancel");
        WSConsumer wsConsumer = new WSConsumer();

        Log.i("WS: ", urlStr);
        Integer resp = wsConsumer.execute(new URL(urlStr)).get();
        Log.i("WS: ", String.valueOf(resp));
        String msg;

        if (resp == 0) {
            msg = String.format(SysError._SUCCESS_CANCEL_OP_MSG, opStr);
        } else {
            msg = SysError.getMessage(resp);
        }
        return msg;
    }

    public String getUserFromQR() throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, IOException, DecoderException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException
    {
        if (this.user == null) {
            decryptQRAndGetUserAndOperation();
        }
        return this.user;
    }

    public String getOperationFromQR() throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, IOException, DecoderException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException {
        if (this.op == null) {
            decryptQRAndGetUserAndOperation();
        }
        return this.op;
    }

    public String getNewPasswd() throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, IOException, DecoderException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException {
        if (this.newPasswd == null && this.numTks == 4){
            decryptQRAndGetUserAndOperation();
        }
        return this.newPasswd;
    }

    private String decryptQRAndGetUserAndOperation() throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException, DecoderException,InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException
    {
        RSA rsa = new RSA(this.context);
        String resultRSA = rsa.RSADecrypt(this.result.getText());
        Log.i("WS: ", resultRSA);

        String strTkns[] = resultRSA.split(" ");
        this.numTks = strTkns.length;


        Log.i("Fingerprint :", String.valueOf(this.numTks));
        this.user = strTkns[0];
        this.op   = strTkns[1];
        if (this.numTks == 4) {
            this.newPasswd = strTkns[2];
        }else {
            this.newPasswd = "";
        }

        return resultRSA;
    }

}
