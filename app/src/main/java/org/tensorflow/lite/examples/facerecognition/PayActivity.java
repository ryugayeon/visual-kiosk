package org.tensorflow.lite.examples.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import java.util.Locale;

public class PayActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private Button pay_btn1;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        pay_btn1 = (Button) findViewById(R.id.pay_btn1);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                pay_btn1.setEnabled(true);
                String text = "카드를 넣어주세요";
                Locale locale = Locale.getDefault();
                tts.setLanguage(locale);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");

                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PayActivity.this, FinishActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 10000);
            }
        });

    }
}