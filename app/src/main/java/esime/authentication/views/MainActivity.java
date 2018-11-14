package esime.authentication.views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import esime.authentication.views.R;

public class MainActivity extends AppCompatActivity {
    int verificarConex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verificarConexion();
        if(verificarConex == 1){ //Si hay conexion a internet, habilita los botones
            setContentView(R.layout.activity_main);
        }
        else {
            Button iniciar= (Button) findViewById(R.id.iniciar);
            iniciar.setEnabled(false);//Deshabilida Iniciar sesión
        }
    }
    public void start(View view){
        Intent intent = new Intent(MainActivity.this, QRActivity.class);
        startActivity(intent);
    }
    public void verificarConexion(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Conectado vía WiFi");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                verificarConex=1;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Conectado por Datos Móviles");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                verificarConex=1;
            }
        } else {
            //Toast.makeText(getApplicationContext(), getString(R.string.internet_failed), Toast.LENGTH_LONG).show();
            // not connected to the internet

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sin conexion a internet");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            verificarConex=0;
        }
    }
}
