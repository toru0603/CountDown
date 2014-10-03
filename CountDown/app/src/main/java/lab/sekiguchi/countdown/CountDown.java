package lab.sekiguchi.countdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import java.util.Locale;
import android.view.View;
import android.widget.Button;
import android.util.Log;


public class CountDown extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private float pitch = 1.0f;
    private float rate = 1.0f;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        startBtn = (Button)findViewById(R.id.start);
        startBtn.setOnClickListener(this);

        // TTSのインスタンス生成
        tts = new TextToSpeech(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts != null) {
            // リソースを解放
            tts.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.count_down, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d("TTS", "onClick");
        if(v == startBtn) {
            if(tts != null) {
                if(tts.setPitch(pitch) == TextToSpeech.ERROR) {
                    Log.e("TTS", "Ptich(" + pitch + ") set error.");
                }
                if(tts.setSpeechRate(rate) == TextToSpeech.ERROR) {
                    Log.e("TTS", "Speech rate(" + rate + ") set error.");
                }

                if(tts.isSpeaking()) {
                    // 読上げ中ならストップ
                    tts.stop();
                }

                // テキスト読上げ
                tts.speak("こんにちわ、世界！", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = Locale.JAPANESE;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                Log.e("TTS", "Not support locale.");
            }
        } else {
            Log.e("TTS", "Init error.");
        }
    }
}

