package esime.authentication.models;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.HttpGet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URL;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by DCMir on 19/11/17.
 */

public class WSConsumer extends AsyncTask <URL, Integer, Integer>{
    public WSConsumer() {
        super();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer aInteger) {
        super.onPostExecute(aInteger);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Integer aInteger) {
        super.onCancelled(aInteger);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(URL... urls) {
        HttpGet httpGet = new HttpGet(String.valueOf(urls[0]));
        HttpClient mClient = new DefaultHttpClient();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String resp = "";
        String response = "";
        Integer sol = 0;

        try {
            resp = mClient.execute(httpGet, responseHandler);
            Log.i("WS :", resp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonResp = new JSONObject(resp);
            response = jsonResp.getString("errCode");
            Log.i("WS :", response);
            sol = Integer.parseInt(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return sol;
    }
}
