package org.tensorflow.lite.examples.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class SelectIceHotActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "192.168.0.14";//ip주소 업데이트 계속 해줘야함
    private static String TAG = "phptest";

    private TextToSpeech tts;
    private Button button_ice;
    private Button button_hot;

    //일정 시간 터치 없을시 자동 처음 화면 돌아가기 위한 코드
    private int count = TimerCount.COUNT;
    private CountDownTimer countDownTimer;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ice_hot);

        //데이터받기
        Intent intent = getIntent();
        String menu = intent.getStringExtra("menu");
        String price = intent.getStringExtra("price");

        button_ice = (Button) findViewById(R.id.button_ice);
        button_hot = (Button) findViewById(R.id.button_hot);

        countDownTimer();
        countDownTimer.start();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                button_ice.setEnabled(true);
                button_hot.setEnabled(true);
                String text = "아이스 핫을 선택해 주세요";
                Locale locale = Locale.getDefault();
                tts.setLanguage(locale);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");

                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SelectIceHotActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 10000);// 10초 정도 딜레이를 준 후 반응 없으면 메인화면으로 돌아감
            }
        });
        button_ice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);

                String text = "아이스";
                Locale locale = Locale.getDefault();
                tts.setLanguage(locale);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
            }
        });

        button_ice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                handler.removeMessages(0);
                Intent intent = new Intent(SelectIceHotActivity.this, SelectModeActivity.class);
                intent.putExtra("temp", "아이스");
                intent.putExtra("menu", menu);
                intent.putExtra("price", price);
                startActivity(intent);
                finish();
                return true;  //true 설정
            }
        });

        button_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);

                String text = "핫";
                Locale locale = Locale.getDefault();
                tts.setLanguage(locale);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
            }
        });

        button_hot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                handler.removeMessages(0);

                Intent intent = new Intent(SelectIceHotActivity.this, SelectModeActivity.class);
                intent.putExtra("temp", "핫");
                intent.putExtra("menu", menu);
                intent.putExtra("price", price);
                startActivity(intent);
                finish();
                return true;  //true 설정
            }
        });
    }

    public void countDownTimer(){
        countDownTimer = new CountDownTimer(TimerCount.MILLISINFUTURE, TimerCount.COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                count --;
            }
            public void onFinish() {
                Intent intent = new Intent(SelectIceHotActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        };
    }

    @Override
    protected void onPause() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        //일정 시간 터치 없을시 자동 처음 화면 돌아가기 위한 코드
        try{
            countDownTimer.cancel();
        } catch (Exception e) {}
        countDownTimer=null;

        super.onPause();
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


}