package org.tensorflow.lite.examples.facerecognition.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.facerecognition.R;
import org.tensorflow.lite.examples.facerecognition.TimerCount;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BasketActivity extends AppCompatActivity {

    private static String TAG = "phpquerytest";
    private static final String TAG_JSON = "cwnu";

    ArrayList<HashMap<String, String>> mArrayList;

    private static final String TAG_WHERE = "r_where";
    private static final String TAG_TEMP = "m_temp";
    private static final String TAG_NAME = "m_name";
    private static final String TAG_COUNT = "p_count";
    private static final String TAG_TOTAL = "total_price";

    String mJsonString;

    private TextToSpeech tts;
    private Button basket_btn;

    private int count = TimerCount.COUNT;
    private CountDownTimer countDownTimer;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        basket_btn = (Button) findViewById(R.id.basket_btn);

        countDownTimer();
        countDownTimer.start();

        mArrayList = new ArrayList<>();

        //Select.Basket 쿼리 실행
        SelectBasket task = new SelectBasket();
        task.execute( TimerCount.starttime);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                basket_btn.setEnabled(true);
                String text = "장바구니 화면입니다." + mArrayList.get(0).get(TAG_WHERE);
                Locale locale = Locale.getDefault();
                tts.setLanguage(locale);
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, "id1");

                for (int i = 0; i < mArrayList.size(); i++) {
                    String text2 = mArrayList.get(i).get(TAG_TEMP) + mArrayList.get(i).get(TAG_NAME)
                            + Integer.valueOf(mArrayList.get(i).get(TAG_COUNT)) + "개" + mArrayList.get(i).get(TAG_TOTAL) + "원";
                    tts.speak(text2, TextToSpeech.QUEUE_ADD, null, null);

                    if(i == mArrayList.size()-1){
                        String text3 = "확인하였으면 화면을 길게 눌러주세요 ";
                        tts.speak(text3, TextToSpeech.QUEUE_ADD, null, null);
                    }
                }
            }
        });


        basket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100); // 0.1초간 진동

                Locale locale = Locale.KOREA;
                tts.setLanguage(locale);
                for (int i = 0; i < mArrayList.size(); i++) {
                    String text2 = mArrayList.get(i).get(TAG_TEMP) + mArrayList.get(i).get(TAG_NAME)
                            + Integer.valueOf(mArrayList.get(i).get(TAG_COUNT)) + "개" + mArrayList.get(i).get(TAG_TOTAL) + "원입니다." +
                            "확인하였으면 화면을 길게 눌러주세요.";
                    tts.speak(text2, TextToSpeech.QUEUE_ADD, null, null);
                }

            }
        });

        basket_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(BasketActivity.this, SelectModeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
    }

    //일정 시간 터치 없을시 자동 처음 화면 돌아가기 위한 코드
    public void countDownTimer(){

        countDownTimer = new CountDownTimer(TimerCount.MILLISINFUTURE, TimerCount.COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                count --;
            }
            public void onFinish() {
                Intent intent = new Intent(BasketActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        //일정 시간 터치 없을시 자동 처음 화면 돌아가기 위한 코드
        try{
            countDownTimer.cancel();
        } catch (Exception e) {}
        countDownTimer=null;

        super.onDestroy();
    }

    private class SelectBasket extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BasketActivity.this,
                    "Please Wait", null, true, true);
        }


        //여기서 텍스트 뷰 내용 붙이기 구현
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "response - " + result);

            if (result == null){

//                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String r_time = params[0];

            String serverURL = "http://"+TimerCount.IP+"/select_basket.php";
            String postParameters = "r_time=" + r_time ;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "SelectBasket: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String r_where = item.getString(TAG_WHERE);
                String m_temp = item.getString(TAG_TEMP);
                String m_name = item.getString(TAG_NAME);
                String p_count = item.getString(TAG_COUNT);
                String total_price = item.getString(TAG_TOTAL);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_WHERE, r_where);
                hashMap.put(TAG_TEMP, m_temp);
                hashMap.put(TAG_NAME, m_name);
                hashMap.put(TAG_COUNT, p_count);
                hashMap.put(TAG_TOTAL, total_price);

                mArrayList.add(hashMap);
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}