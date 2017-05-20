package com.example.googlehomedemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Need to build this url but for this demo its ok
    private static final String URLConstant = "https://academic.cloud.thingworx.com/Thingworx/Things/SmartHomeThermostat_umair_shaik/Properties/Temperature";
    private static final String TAG = "TAG";
    private static final int REQ_CODE_SPEECH_INPUT = 1001;
    private static final Map<String, String> header;

    static {
        header = new HashMap<>();
        header.put("appKey", "bae175f3-4ef1-47d3-bc87-feb4c33fa061");
        header.put("Content-Type", "application/json");
    }

    private TextView tempLabel;
    private TextView tempValue;
    private Button setTemp;
    private TextView txtSpeechInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempLabel = (TextView) findViewById(R.id.tv_temp_lable);
        tempValue = (TextView) findViewById(R.id.tv_temp_value);
        setTemp = (Button) findViewById(R.id.bt_settemp);
        txtSpeechInput = (TextView) findViewById(R.id.tv_speech_value);
        setTemp.setOnClickListener(this);
        getThermostatData();
    }


    private void getThermostatData() {
        /*Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("appKey", "bae175f3-4ef1-47d3-bc87-feb4c33fa061");
        extraHeaders.put("Content-Type", "application/json");
        loginWebView.setVisibility(View.VISIBLE);
        loginWebView.loadUrl(URLConstant, extraHeaders);
        final WebSettings webSettings = loginWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSaveFormData(false);
        webSettings.setAppCacheEnabled(false);
        loginWebView.setWebViewClient(new LoginWebViewClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        loginWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");*/


        new AsyncTask<Object, Object, Document>() {
            @Override
            protected Document doInBackground(Object... params) {
                Document document = null;//can add as many as you like
                try {
                    document = Jsoup.connect(URLConstant)
                            .headers(header).get();
                    Log.d(TAG, document.toString());

                    return document;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Document document) {
                Element table = document.select("table").get(0);
                Element tbody = table.select("tbody").get(0);
                Elements rows = tbody.select("tr");
                for (int i = 0; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements th = row.select("th");
                    Elements cols = row.select("td");
                    Log.d("TAG, TH", th.toString());
                    Log.d("TAG, Td", cols.toString());
                    Log.d("TAG, Th element", th.text());
                    Log.d("TAG, Td element", cols.text());
                    if (!th.text().equals("")) {
                        tempLabel.setText(th.text());
                    }
                    if (!cols.text().equals("")) {
                        tempValue.setText(cols.text());
                    }
                }
            }
        }.execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_settemp:
                promptSpeechInput();
                break;
            default:
                break;

        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    for (String a : result) {
                       /* //String s = a.replaceAll("[^0-9]", "");
                        boolean s=a.matches(".*\\d+.*");

                        txtSpeechInput.append(s);*/
                        String search = "temperature";
                        if (a.toLowerCase().contains(search.toLowerCase())) {
                            Log.d(TAG, "I found the keyword");
                            final String temValue = extractNumber(a);
                            if (!temValue.equals("")) {
                                try {
                                    setTemperatureValueTo(Integer.valueOf(temValue));
                                    txtSpeechInput.setText(temValue);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                            Log.d(TAG, "not found");

                        }

                        break;
                    }
                }
                break;
            }

        }
    }

    private void setTemperatureValueTo(final Integer integer) {
        if (integer < 10) {
            return;
        } else if (integer > 50) {
            return;
        }
        RestaurantAPI client = ServiceGenerator
                .getClient()
                .create(RestaurantAPI.class);
        Payload payload = new Payload();
        payload.setTemperature(integer + "");
        Call<String> call = client.getTask(payload);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                okhttp3.Response raw = response.raw();
                if (response.isSuccessful()) {
                    Log.d("on Response", response.toString() + "\n" + raw.toString());
                    tempValue.setText("" + integer);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private String getJsonData(Integer integer) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Temperature", integer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject.toString());
        return jsonObject.toString();
    }

    private String extractNumber(final String str) {

        if (str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
                found = true;
                if (sb.length() > 2) {
                    break;
                }
            } else if (found) {
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }

        return sb.toString();
    }


    private class LoginWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, url);
        }
    }

    private class MyJavaScriptInterface {
        public MyJavaScriptInterface() {
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(final String html) {
            //boolean hasSSOTag = !TextUtils.isEmpty(html) && html.contains(SSO_START_TAG);
           /* if (hasSSOTag) {
                int startIndex = html.indexOf(SSO_START_TAG);
                int endIndex = html.indexOf(SSO_END_TAG);
                String tempToken = html.substring(startIndex + SSO_START_TAG.length(), endIndex);
                AccessTokenModel model = gson.fromJson(tempToken, AccessTokenModel.class);
                presenter.storeAccessToken(model);
                finish();
            } else {
                loginWebView.loadUrl(URLConstant.BASE_URL);
            }*/
        }
    }
}
