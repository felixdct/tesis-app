package esime.authentication.utils;

import android.content.Context;
import android.util.Base64;

import org.apache.commons.codec.DecoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by DCMir on 19/11/17.
 */

public class RSA {
    private InputStream keyStream;
    PrivateKey privateKey;

    public RSA(Context context) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, DecoderException {
        byte[] keyBytes;
        keyStream = context.getResources().openRawResource(
                context.getResources().getIdentifier("privatekey4", "raw",
                context.getPackageName()));
        keyBytes = new byte[(int)keyStream.available()];

        keyStream.read(keyBytes);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.privateKey = kf.generatePrivate(spec);

    }

    public String RSADecrypt(final String encryptedBytes) throws NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        Cipher cipher;
        byte []decryptedBytes;
        String decrypted;

        byte []data = Base64.decode(encryptedBytes, Base64.DEFAULT);

        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedBytes = cipher.doFinal(data);
        decrypted = new String(decryptedBytes,"utf-8");

        return decrypted;

    }
}
