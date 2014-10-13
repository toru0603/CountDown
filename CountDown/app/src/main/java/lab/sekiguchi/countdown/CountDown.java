package lab.sekiguchi.countdown;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.view.Window;



public class CountDown extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Button startBtn, p10, p5, m5, m10;

    MyCountDownTimer cdt;

    TextView min;
    TextView timer;

    long time = 0;
    long orgTime = 0;
    long current = 0;
    boolean work = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_count_down);

        startBtn = (Button)findViewById(R.id.start);
        startBtn.setOnClickListener(this);


        p10 = (Button)findViewById(R.id.p10);
        p10.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                if(time < 990) {
                    time += 10;
                }
                if(!work) {
                    current = 1000 * time * 60;
                }
                draw();
            }
        });

        p5 = (Button)findViewById(R.id.p5);
        p5.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                if(time < 995) {
                    time += 5;
                }
                if(!work) {
                    current = 1000 * time * 60;
                }
                draw();
            }
        });

        m5 = (Button)findViewById(R.id.m5);
        m5.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                if(time > 0) {
                    time -= 5;
                }
                if(!work) {
                    current = 1000 * time * 60;
                }
                draw();
            }
        });

        m10 = (Button)findViewById(R.id.m10);
        m10.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                if(time > 5) {
                    time -= 10;
                }
                if(!work) {
                    current = 1000 * time * 60;
                }
                draw();
            }
        });


        timer = (TextView)findViewById(R.id.timer);
        min = (TextView)findViewById(R.id.time);

        // TTSのインスタンス生成
        tts = new TextToSpeech(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts != null) {
            tts.shutdown();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("TTS", "onClick");
        if(v == startBtn) {
            if(time != 0) {
                current = 1000 * time * 60;
                orgTime = current;
                this.speak("カウントダウンを開始します。残り"+ Long.toString(time) + "分です。");
                if (cdt != null) {
                    cdt.cancel();
                }
                work = true;

                cdt = new MyCountDownTimer(60 * 1000 * time + 1000, 1000);
                cdt.start();
            }
        }
    }


    private void speak(String text) {

        if(tts != null) {

            if(tts.isSpeaking()) {
                tts.stop();
            }
            work = true;
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void draw() {
        {
            min.setText(Long.toString(time));
        }
        {

            String buf = new String();

            long min = current / 1000 / 60;
            long sec = current / 1000 % 60;
            if(min < 10) {
                buf += "00" + Long.toString(min);
            } else if (min < 100) {
                buf += "0" + Long.toString(min);
            } else {
                buf += "" + Long.toString(min);
            }

            buf += ":";
            if(sec < 10) {
                buf += "0" + Long.toString(sec);
            } else {
                buf += "" + Long.toString(sec);
            }

            timer.setText(buf);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = Locale.JAPANESE;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                speak("Please download the TTS of Japanese.");
                Log.e("TTS", "Not support locale.");
            }
        } else {
            Log.e("TTS", "Init error.");
        }
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            current = 0;
            draw();
            work = false;
            speak("カウントダウンが終了しました。お疲れ様でした。");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            current = millisUntilFinished;
            Log.d("TTS", "onTick " + Long.toString(current));

            long min = current / 1000 / 60;
            long sec = current / 1000 % 60;
            long halfMin = orgTime / 2 / 1000 / 60;
            long halfSec = orgTime / 2 / 1000 % 60;

            if(min == halfMin && sec == halfSec) {
                if(sec == 0) {
                    speak("残り" + min + "分です。");
                } else {
                    speak("残り" + min + "分" + sec + "秒です。");
                }
            } else
            if(sec == 0 && min == 5) {
                speak("残り" + Long.toString(min) + "分です。");
            }

            if(orgTime + 1000 > current) {
                draw();
            }
        }
    }
}

