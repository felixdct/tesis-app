package esime.authentication.views;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

import esime.authentication.presenters.ValidateQRPresenter;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by toledo on 19/11/17.
 */

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView escanerView;
    private int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    String resultadoAnterior = "";
    int numTries;
    ValidateQRPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_qr);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }
        }

        this.numTries = 0;
        escanerView = new ZXingScannerView(this);
        setContentView(escanerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        escanerView.stopCamera();
    }
    public void onResume() {
        super.onResume();
        escanerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        escanerView.startCamera();          // Start camera on resume
    }


    @Override
    public void handleResult(Result result) {
        presenter = new ValidateQRPresenter(getApplicationContext(), result);
        String msg = "";
        String msgCancel = "";

        try {
            msg = presenter.isValidQR(URLEncoder.encode(result.getText(), "UTF-8"), this.numTries);


        escanerView.setResultHandler(this);
        if (msg.matches("Bienvenido .*.QR validado, por favor introduzca su huella dactilar")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msg)
                    .setTitle("Advertencia").setCancelable(false)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();

            escanerView.stopCameraPreview();

            /* Here we call the next activity to catch the fingerPrint */
            Intent fingerPrintIntent = new Intent(this, FingerPrintActivity.class);
            fingerPrintIntent.putExtra("user", presenter.getUserFromQR());
            fingerPrintIntent.putExtra("op", presenter.getOperationFromQR());
            fingerPrintIntent.putExtra("newpasswd", (presenter.getNewPasswd() == null)? "" : presenter.getNewPasswd());
            alert.dismiss();

            startActivity(fingerPrintIntent);

        } else {
            tryQR();
        }

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            this.numTries++;
        } catch (DecoderException e) {
            e.printStackTrace();
            tryQR();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            tryQR();
        } catch (IOException e) {
            e.printStackTrace();
            tryQR();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            tryQR();
        } catch (BadPaddingException e) {
            e.printStackTrace();
            tryQR();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            tryQR();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            tryQR();
        } catch (ExecutionException e) {
            e.printStackTrace();
            tryQR();
        } catch (InterruptedException e) {
            e.printStackTrace();
            tryQR();
        } catch(RuntimeException e) {
            e.printStackTrace();
            tryQR();
        }
    }

    private void tryQR() {
        this.numTries++;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (this.numTries >= 3) {
            builder.setMessage("Se excedio el numero de intentos")
                    .setTitle("Advertencia").setCancelable(false)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();

            escanerView.stopCameraPreview();

                /* Here we call the main activity: toDo */

            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);

        }else {
            builder.setMessage("QR no reconocido")
                    .setTitle("Advertencia").setCancelable(false)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();

            escanerView.resumeCameraPreview(this);
        }
    }

}

