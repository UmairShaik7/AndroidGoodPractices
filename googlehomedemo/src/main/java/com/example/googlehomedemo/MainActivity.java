package com.example.googlehomedemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {
    //Need to build this url but for this demo its ok
    private static final String URL_THERMOSTAT = "https://academic.cloud.thingworx.com/Thingworx/Things/SmartHomeThermostat_umair_shaik/Properties/Temperature";
    private static final String URL_TV_STATUS = "https://academic.cloud.thingworx.com/Thingworx/Things/SmartTV_umair_shaik/Properties/status";
    private static final String TAG = "TAG";
    private static final int REQ_CODE_SPEECH_INPUT = 1001;
    private static final Map<String, String> header;
    private static final int MY_DATA_CHECK_CODE = 1002;

    static {
        header = new HashMap<>();
        header.put("appKey", "bae175f3-4ef1-47d3-bc87-feb4c33fa061");
        header.put("Content-Type", "application/json");
    }

    private TextToSpeech myTTS;
    private TextView tempLabel;
    private TextView tempValue;
    private Button setTemp;
    private TextView txtSpeechInput;
    private TextView tvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempLabel = (TextView) findViewById(R.id.tv_temp_lable);
        tempValue = (TextView) findViewById(R.id.tv_temp_value);
        setTemp = (Button) findViewById(R.id.bt_settemp);
        txtSpeechInput = (TextView) findViewById(R.id.tv_speech_value);
        tvStatus = (TextView) findViewById(R.id.tv_tv_status);
        setTemp.setOnClickListener(this);
        getWidgetsData();
        checkTextToSpeech();
    }

    private void checkTextToSpeech() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }


    private void getWidgetsData() {
        getThermostatData();
        getTVStatus();
    }

    private void getTVStatus() {
        new AsyncTask<Object, Object, Document>() {
            @Override
            protected Document doInBackground(Object... params) {
                Document document = null;//can add as many as you like
                try {
                    document = Jsoup.connect(URL_TV_STATUS)
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
                        //tempLabel.setText(th.text());
                    }
                    if (!cols.text().equals("")) {
                        tvStatus.setText(cols.text().equals("true") ? "ON" : "OFF");
                    }
                }
            }
        }.execute();
    }

    private void getThermostatData() {
        new AsyncTask<Object, Object, Document>() {
            @Override
            protected Document doInBackground(Object... params) {
                Document document = null;//can add as many as you like
                try {
                    document = Jsoup.connect(URL_THERMOSTAT)
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
                        //tempLabel.setText(th.text());
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
                                    break;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            String[] sTVKeyword = {"TV", "Television"};

                            for (String items : sTVKeyword) {
                                if (a.toLowerCase().contains(items.toLowerCase())) {
                                    boolean status = extractNumberONorOFFStatus(a);
                                    setTVto(status);
                                    break;
                                }
                            }
                        }

                        break;
                    }
                }
                break;
            }
            case MY_DATA_CHECK_CODE:

                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //the user has the necessary data - create the TTS
                    myTTS = new TextToSpeech(this, this);
                } else {
                    //no data - install it now
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }

                break;
            default:
                break;

        }
    }

    private void setTVto(final boolean status) {
        SetTVStatusAPI client = ServiceGenerator
                .getClient()
                .create(SetTVStatusAPI.class);
        TVStatusPayload payload = new TVStatusPayload();
        payload.setStatus(status);
        Call<Void> call = client.setTVStatus(payload);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                okhttp3.Response raw = response.raw();
                if (response.isSuccessful()) {
                    Log.d("on Response", response.toString() + "\n" + raw.toString());
                    tvStatus.setText(status ? "ON" : "OFF");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("OnError called", "Error : " + t.toString());
            }
        });


    }

    private boolean extractNumberONorOFFStatus(String a) {
        String[] statusQuote = {"on", "off", "play"};
        if (a.toLowerCase().contains(statusQuote[0]) || a.toLowerCase().contains(statusQuote[2].toLowerCase())) {
            speakWords("Turing the tv on");
            return true;
        } else if (a.toLowerCase().contains(statusQuote[1].toLowerCase())) {
            speakWords("Turing off the tv");
            return false;
        } else {
            speakWords("I didn't quite get that");
            return Boolean.parseBoolean(tvStatus.getText().toString());
        }

    }

    private void setTemperatureValueTo(final Integer integer) {
        if (integer < 10 || integer > 50) {
            speakWords("Select a temperature between 10 to 50");
            return;
        }
        speakWords("setting temperature to" + integer);
        SetTemperatureAPI client = ServiceGenerator
                .getClient()
                .create(SetTemperatureAPI.class);
        TemperaturePayload temperaturePayload = new TemperaturePayload();
        temperaturePayload.setTemperature(integer + "");
        Call<Void> call = client.getTask(temperaturePayload);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                okhttp3.Response raw = response.raw();
                if (response.isSuccessful()) {
                    Log.d("on Response", response.toString() + "\n" + raw.toString());
                    tempValue.setText("" + integer);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("OnError called", "Error : " + t.toString());
            }
        });

    }

    private void speakWords(String s) {
        String utteranceId = this.hashCode() + "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
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

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
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
                loginWebView.loadUrl(URL_THERMOSTAT.BASE_URL);
            }*/
        }
    }
}
