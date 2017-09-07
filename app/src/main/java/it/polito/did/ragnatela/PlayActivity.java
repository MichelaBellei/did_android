package it.polito.did.ragnatela;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlayActivity extends Activity {

    Unbinder unbinder;
    private JSONArray pixels_array;

    private Handler mNetworkHandler, mMainHandler, bombaMainHandler, bombaNetworkHandler;

    private NetworkThread mNetworkThread = null;
    private NetworkThread bombaNetworkThread = null;

    private TextView tvSecond, score,suggestion;
    private Handler handler;
    private Runnable runnable;
    int seconds = 60;
    private CarroArmato carro = new CarroArmato();
    private Button buttonRight, buttonLeft;
    private Button imageCannone;
    private int posizioneAttuale = 1;
    private boolean game_over = false;
    private ArrayList<Proiettile> proiettileList = new ArrayList<Proiettile>();
    private ArrayList<Bomba> bombaList = new ArrayList<Bomba>();
    private Timer timer, timerBomba;
    private TimerTask timerTask, timerTaskBomba;
    private Suggestion s=new Suggestion();
    String sug=s.getUccidili();
    Anelli anello = new Anelli(1);
    private int ringCount=522, step=1, stepBomba=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        unbinder = ButterKnife.bind(this);

        pixels_array = preparePixelsArray();

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(PlayActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();

        tvSecond = (TextView) findViewById(R.id.txtTimerSecond);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/wareagle.ttf");
        tvSecond.setTypeface(myTypeface);
        score = (TextView) findViewById(R.id.score);
        score.setTypeface(myTypeface);
        suggestion = (TextView) findViewById(R.id.suggestion);
        suggestion.setTypeface(myTypeface);
        imageCannone = (Button) findViewById(R.id.imageCannone);
        buttonLeft = (Button) findViewById(R.id.leftButton);
        buttonRight = (Button) findViewById(R.id.rightButton);
        anello.setStep(anello.getAnello());

        startHandlerBombaThread();

        setDisplayThree();

        //thread che aggiorna le posizioni dei proiettili e delle bombe
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ad ogni click vedo la posizione e agg proiettile nel ramo
                posizioneAttuale++;
                if (posizioneAttuale == 6) {
                    posizioneAttuale = 1;
                }
                setDisplayPixels();
                switch (posizioneAttuale) {
                    case 1:
                        imageCannone.setBackgroundResource(R.drawable.cannone_up);
                        break;
                    case 2:
                        imageCannone.setBackgroundResource(R.drawable.cannone_dx_up);
                        break;
                    case 3:
                        imageCannone.setBackgroundResource(R.drawable.cannone_dx_down);
                        break;
                    case 4:
                        imageCannone.setBackgroundResource(R.drawable.cannone_sx_down);
                        break;
                    case 5:
                        imageCannone.setBackgroundResource(R.drawable.cannone_sx_up);
                        break;
                }
            }
        });

        imageCannone.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Proiettile p = new Proiettile(posizioneAttuale);
                proiettileList.add(p);
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posizioneAttuale--;
                if (posizioneAttuale == 0) {
                    posizioneAttuale = 5;
                }
                setDisplayPixels();
                switch (posizioneAttuale) {
                    case 1:
                        imageCannone.setBackgroundResource(R.drawable.cannone_up);
                        break;
                    case 2:
                        imageCannone.setBackgroundResource(R.drawable.cannone_dx_up);
                        break;
                    case 3:
                        imageCannone.setBackgroundResource(R.drawable.cannone_dx_down);
                        break;
                    case 4:
                        imageCannone.setBackgroundResource(R.drawable.cannone_sx_down);
                        break;
                    case 5:
                        imageCannone.setBackgroundResource(R.drawable.cannone_sx_up);
                        break;
                }
            }
        });
    }

    void showBomba() {

        JSONArray pixels_array = preparePixelsArray();
        //aggiorno le varie bombe
        try {
            for(int j=522; j< ringCount; j++) {
                ((JSONObject) pixels_array.get(j)).put("r", 0);
                ((JSONObject) pixels_array.get(j)).put("g", 255);
                ((JSONObject) pixels_array.get(j)).put("b", 0);
            }

            for (Bomba p : bombaList) {
                int[] colors = new int[3];
                colors[0] = 255;    // R
                colors[1] = 255;  // G
                colors[2] = 0;    // B

                ((JSONObject) pixels_array.get(p.getPos1())).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos1())).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos1())).put("b", colors[2]);
                ((JSONObject) pixels_array.get(p.getPos2())).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos2())).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos2())).put("b", colors[2]);
                ((JSONObject) pixels_array.get(p.getPos1() + 1)).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos1() + 1)).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos1() + 1)).put("b", colors[2]);
                ((JSONObject) pixels_array.get(p.getPos2() - 1)).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos2() - 1)).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos2() - 1)).put("b", colors[2]);
            }
            handleNetworkRequest(bombaNetworkHandler, NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        } catch (Exception e) {
            // Exception
        }
    }
    ;
    void showProiettile() {

        JSONArray pixels_array = preparePixelsArray();
        try {
            for (Proiettile p : proiettileList) {
                int[] colors = new int[3];
                colors[0] = 128;    // R
                colors[1] = 195;  // G
                colors[2] = 235;    // B

                ((JSONObject) pixels_array.get(p.getPos1())).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos1())).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos1())).put("b", colors[2]);
                ((JSONObject) pixels_array.get(p.getPos2())).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos2())).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos2())).put("b", colors[2]);
                ((JSONObject) pixels_array.get(p.getPos1() + 1)).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos1() + 1)).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos1() + 1)).put("b", colors[2]);
                ((JSONObject) pixels_array.get(p.getPos2() - 1)).put("r", colors[0]);
                ((JSONObject) pixels_array.get(p.getPos2() - 1)).put("g", colors[1]);
                ((JSONObject) pixels_array.get(p.getPos2() - 1)).put("b", colors[2]);
            }
            handleNetworkRequest(bombaNetworkHandler, NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        } catch (Exception e) {
            // Exception
        }
    }
    ;
    public void deathControl(int i) {
        if (!bombaList.get(i).isAlive()) {
            bombaList.remove(i);
            carro.hit();
            sug=s.getCarroColpito();
        }
    }

    //controllo se una bomba e un proiettile si incontrano
    public void hitControl(int i) {
        for (int j = 0; j < proiettileList.size(); j++) {
            if (bombaList.get(i).getTirante() == proiettileList.get(j).getTirante()) {
                if ((bombaList.get(i).getPos1() == proiettileList.get(j).getPos2()) || (bombaList.get(i).getPos1() == proiettileList.get(j).getPos2()-1) || (bombaList.get(i).getPos1() == proiettileList.get(j).getPos2()-2)){
                    bombaList.remove(i);
                    proiettileList.remove(j);
                    carro.upScore();
                    sug=s.getBombaColpita();
                    ring();
                }
            }
        }
    }

    public void ring() {
        try {

            if (ringCount < anello.getStop()) {//se non ha finito l'anello
                if (step == 6) {
                    step = 1;
                }
                switch (step){
                    case 1:
                        ringCount= ringCount+ anello.getStep1();
                        step++;
                        break;
                    case 2:
                        ringCount= ringCount+ anello.getStep2();
                        step++;
                        break;
                    case 3:
                        ringCount= ringCount+ anello.getStep3();
                        step++;
                        break;
                    case 4:
                        ringCount= ringCount+ anello.getStep4();
                        step++;
                        break;
                    case 5:
                        ringCount= ringCount+ anello.getStep5();
                        step++;
                        break;
                }
                if (ringCount == anello.getStop()) {
                    ringCount++;
                    // incrementiamo lo step bombe e cambiamo anello
                    anello.setAnello(anello.getAnello()+1);
                    anello.setStep(anello.getAnello());
                    stepBomba++;
                }
            }
            if(anello.getAnello()==4){
                seconds=-1; // forzato per aprire activity win
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    //timer per aggiornare posizioni di bombe e proiettili
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, 300);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                for (int i = 0; i < bombaList.size(); i++) {
                    deathControl(i);
                    hitControl(i);
                    bombaList.get(i).update();
                }
                showBomba();

                for (int i = 0; i < proiettileList.size(); i++) {
                    proiettileList.get(i).update();
                }
                showProiettile();
            }
        };
    }

    //timer per creazione bombe
    public void startTimerBomba() {
        timerBomba = new Timer();
        initializeTimerTaskBomba();
        timerBomba.schedule(timerTaskBomba, 0, 3000);
    }

    public void initializeTimerTaskBomba() {
        timerTaskBomba = new TimerTask() {
            public void run() {
                Bomba b = new Bomba(stepBomba);
                bombaList.add(b);
            }
        };
    }

    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    int punteggio=carro.getScore();
                    if (seconds > 0) {
                        seconds = seconds - 1;
                        tvSecond.setText(String.format("%02d", seconds));
                        score.setText(String.format("%2d",punteggio) + "!");
                        suggestion.setText(String.format("%s",sug));
                        if (seconds <= 20) {
                            sug=s.getTempo();
                            tvSecond.setTextColor(Color.RED);
                        }
                    } else if(seconds==0) {
                        game_over = true;
                        timer.cancel();
                        timerBomba.cancel();
                        setDisplayPixels();
                        Intent activity_gameover = new Intent(PlayActivity.this, GameOverActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("score", punteggio); //Your id
                        activity_gameover.putExtras(b); //Put your id to your next Intent
                        startActivity(activity_gameover);
                        handler.removeCallbacks(runnable);
                        //finish();
                    } else if(seconds==-1){
                        Intent activity_win = new Intent(PlayActivity.this, WinActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("score", punteggio); //Your id
                        activity_win.putExtras(b);//Put your id to your next Intent
                        startActivity(activity_win);
                        timer.cancel();
                        timerBomba.cancel();
                        timerTask.cancel();
                        timerTaskBomba.cancel();
                        handler.removeCallbacks(runnable);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    void setDisplayPixels() {
        try {
            JSONArray pixels_array = new JSONArray();

            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.cannone_blu_up);
            switch (posizioneAttuale) {
                case 1:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.cannone_blu_up);
                    break;
                case 2:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.cannone_dx_alto);
                    break;
                case 3:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.cannone_dx_basso);
                    break;
                case 4:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.cannone_sx_basso);
                    break;
                case 5:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.cannone_sx_alto);
                    break;
            }

            if (game_over == true) {
                tempBMP = BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.gameover);
            }
            tempBMP = Bitmap.createScaledBitmap(tempBMP, 32, 32, false);
            int[] pixels = new int[tempBMP.getHeight() * tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int pixel = pixels[i];
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                JSONObject tmp = new JSONObject();
                tmp.put("r", redValue);
                tmp.put("g", greenValue);
                tmp.put("b", blueValue);
                tmp.put("a", 0);
                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0, 0);
        } catch (
                JSONException e)

        {
            // There should be no Exception
        }

    }

    void setDisplayThree() {
        try {
            JSONArray pixels_array = new JSONArray();

            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.three);

            tempBMP = Bitmap.createScaledBitmap(tempBMP, 32, 32, false);
            int[] pixels = new int[tempBMP.getHeight() * tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int pixel = pixels[i];
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);


                JSONObject tmp = new JSONObject();
                tmp.put("r", redValue);
                tmp.put("g", greenValue);
                tmp.put("b", blueValue);
                tmp.put("a", 0);
                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDisplayTwo();
                    imageCannone.setBackgroundResource(R.drawable.tel2);
                }
            }, 1000);

        } catch (
                JSONException e)

        {
            // There should be no Exception
        }

    }

    void setDisplayTwo() {
        try {
            JSONArray pixels_array = new JSONArray();

            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.two);

            tempBMP = Bitmap.createScaledBitmap(tempBMP, 32, 32, false);
            int[] pixels = new int[tempBMP.getHeight() * tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int pixel = pixels[i];
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);


                JSONObject tmp = new JSONObject();
                tmp.put("r", redValue);
                tmp.put("g", greenValue);
                tmp.put("b", blueValue);
                tmp.put("a", 0);
                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDisplayOne();
                    imageCannone.setBackgroundResource(R.drawable.tel1);
                }
            }, 1000);
        } catch (
                JSONException e)

        {
            // There should be no Exception
        }

    }

    void setDisplayOne() {
        try {
            JSONArray pixels_array = new JSONArray();

            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.one);

            tempBMP = Bitmap.createScaledBitmap(tempBMP, 32, 32, false);
            int[] pixels = new int[tempBMP.getHeight() * tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int pixel = pixels[i];
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);


                JSONObject tmp = new JSONObject();
                tmp.put("r", redValue);
                tmp.put("g", greenValue);
                tmp.put("b", blueValue);
                tmp.put("a", 0);
                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDisplayGo();
                    imageCannone.setBackgroundResource(R.drawable.go_tel);
                }
            }, 1000);
        } catch (
                JSONException e)

        {
            // There should be no Exception
        }

    }

    void setDisplayGo() {
        try {
            JSONArray pixels_array = new JSONArray();

            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.go);

            tempBMP = Bitmap.createScaledBitmap(tempBMP, 32, 32, false);
            int[] pixels = new int[tempBMP.getHeight() * tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int pixel = pixels[i];
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);


                JSONObject tmp = new JSONObject();
                tmp.put("r", redValue);
                tmp.put("g", greenValue);
                tmp.put("b", blueValue);
                tmp.put("a", 0);
                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDisplayPixels();
                    imageCannone.setBackgroundResource(R.drawable.cannone_up);
                    countDownStart();
                    startTimer();
                    startTimerBomba();

                }
            }, 1000);

        } catch (
                JSONException e) {
            // There should be no Exception
        }

    }

    public void startHandlerThread() {
        mNetworkThread = new NetworkThread(mMainHandler);
        mNetworkThread.start();
        mNetworkHandler = mNetworkThread.getNetworkHandler();
    }

    public void startHandlerBombaThread() {
        bombaNetworkThread = new NetworkThread(bombaMainHandler);
        bombaNetworkThread.start();
        bombaNetworkHandler = bombaNetworkThread.getNetworkHandler();
    }

    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
        Message msg = mNetworkHandler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    private void handleNetworkRequest(Handler handler, int what, Object payload, int arg1, int arg2) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    //spenti tutti i led
    JSONArray preparePixelsArray() {
        JSONArray pixels_array = new JSONArray();
        JSONObject tmp;
        try {
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", 0);
                if (i < 522) {
                    tmp.put("g", 0);
                    tmp.put("b", 0);
                    tmp.put("r", 0);
                } else if (i < 613) {
                    tmp.put("r", 0);
                    tmp.put("g", 0);
                    tmp.put("b", 0);
                } else if (i < 791) {
                    tmp.put("b", 0);
                    tmp.put("g", 0);
                    tmp.put("r", 0);
                } else {
                    tmp.put("b", 0);
                    tmp.put("g", 0);
                    tmp.put("r", 0);
                }
                pixels_array.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return pixels_array;
    }

}
