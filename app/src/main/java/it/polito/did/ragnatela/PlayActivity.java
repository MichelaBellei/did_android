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

    private JSONArray mezzo_proiettile, mezzo_proiettile2, mezzo_proiettile3, mezzo_proiettile4, mezzo_proiettile5;

    private Handler mNetworkHandler, mMainHandler, bombaMainHandler, bombaNetworkHandler;

    private NetworkThread mNetworkThread = null;
    private NetworkThread bombaNetworkThread = null;

    private int l_primo_t = 51;
    private int l_secondo_t = 133;
    private int l_terzo_t = 133;
    private int l_quarto_t = 105;
    private int l_quinto_t = 98;

    private int[][] ragnatela = new int[1072][4];// per ogni px abbiamo 4 colonne che identificano i valori di a, rgb

    private TextView tvSecond;
    private Handler handler ;
    private Handler handlerRagnatela;
    private Runnable runnable;
    int seconds = 60;
    private CarroArmato carro = new CarroArmato();
    private Button buttonRight, buttonLeft;
    private ImageView imageCannone;
    private int posizioneAttuale = 1;
    private boolean game_over = false;
    private ArrayList<Proiettile> proiettileList = new ArrayList<Proiettile>();
    private ArrayList<Bomba> bombaList = new ArrayList<Bomba>();
    private Timer timer, timerBomba;
    private TimerTask timerTask, timerTaskBomba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        unbinder = ButterKnife.bind(this);

        pixels_array=preparePixelsArray();

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
        imageCannone = (ImageView) findViewById(R.id.imageCannone);
        buttonLeft = (Button) findViewById(R.id.leftButton);
        buttonRight = (Button) findViewById(R.id.rightButton);

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
                        imageCannone.setImageResource(R.drawable.cannone_up);
                        Proiettile p = new Proiettile(posizioneAttuale);
                        proiettileList.add(p);
                        break;
                    case 2:
                        imageCannone.setImageResource(R.drawable.cannone_dx_up);
                        Proiettile p2 = new Proiettile(posizioneAttuale);
                        proiettileList.add(p2);
                        break;
                    case 3:
                        imageCannone.setImageResource(R.drawable.cannone_dx_down);
                        Proiettile p3 = new Proiettile(posizioneAttuale);
                        proiettileList.add(p3);
                        break;
                    case 4:
                        imageCannone.setImageResource(R.drawable.cannone_sx_down);
                        Proiettile p4 = new Proiettile(posizioneAttuale);
                        proiettileList.add(p4);
                        break;
                    case 5:
                        imageCannone.setImageResource(R.drawable.cannone_sx_up);
                        Proiettile p5 = new Proiettile(posizioneAttuale);
                        proiettileList.add(p5);
                        break;
                }

                    //aggiornaRagnatela.run();
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
                        imageCannone.setImageResource(R.drawable.cannone_up);
                        try {
                            for (int j = 0; j < (l_primo_t / 2); j++) {
                                mezzo_proiettile = setProiettileRamo1(j);
                                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        imageCannone.setImageResource(R.drawable.cannone_dx_up);
                        try {
                            for (int j = 0; j < (l_secondo_t / 2); j++) {
                                mezzo_proiettile2 = setProiettileRamo2(l_primo_t, j);
                                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile2, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        imageCannone.setImageResource(R.drawable.cannone_dx_down);
                        try {
                            for (int j = 0; j < (l_terzo_t / 2); j++) {
                                mezzo_proiettile3 = setProiettileRamo3(l_primo_t + l_secondo_t, j);
                                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile3, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        imageCannone.setImageResource(R.drawable.cannone_sx_down);
                        try {
                            for (int j = 0; j < (l_quarto_t / 2); j++) {
                                mezzo_proiettile4 = setProiettileRamo4(l_primo_t + l_secondo_t + l_terzo_t, j);
                                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile4, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        imageCannone.setImageResource(R.drawable.cannone_sx_up);
                        try {
                            for (int j = 0; j < (l_quinto_t / 2); j++) {
                                mezzo_proiettile5 = setProiettileRamo5(l_primo_t + l_secondo_t + l_terzo_t + l_quarto_t, j);
                                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile5, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });


    }

     void showBomba() {

         JSONArray pixels_array = preparePixelsArray();
        //aggiorno i vari proiettili/bombe

            try {
                for (Bomba p : bombaList) {
                    int[] colors = new int[3];
                    colors[0] = 128;    // R
                    colors[1] = 195;  // G
                    colors[2] = 238;    // B

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
            }
            catch (Exception e) {
                // Exception
            }
    };

    public void deathControl(int i){
        if(!bombaList.get(i).isAlive()){
            bombaList.remove(i);
            carro.hit();
        }
    }


    public void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,0,500);
    }

    public void initializeTimerTask(){
        timerTask = new TimerTask(){
            public void run(){
                for (int i=0; i<bombaList.size();i++){
                    deathControl(i);
                    bombaList.get(i).update();
                }
                showBomba();
            }
        };
    }

    public void startTimerBomba(){
        timerBomba = new Timer();
        initializeTimerTaskBomba();
        timerBomba.schedule(timerTaskBomba,0,5000);
    }

    public void initializeTimerTaskBomba(){
        timerTaskBomba = new TimerTask(){
            public void run(){
                Bomba b = new Bomba();
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

                    if (seconds > 0) {
                        seconds = seconds - 1;
                        tvSecond.setText("" + String.format("%02d", seconds));
                        if (seconds <= 20) {
                            tvSecond.setTextColor(Color.RED);
                        }

                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int j = 0; j < l_primo_t / 2; j++) {
                                            mezza_bomba = setBombaRamo1(j);
                                            handleNetworkRequest(NetworkThread.SET_PIXELS, mezza_bomba, 0, 0);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 5000);   bombe a ripetizione primo tirante*/
                    } else {
                        game_over = true;
                        timer.cancel();
                        timerBomba.cancel();
                        setDisplayPixels();
                        Intent activity_gameover = new Intent(PlayActivity.this, GameOverActivity.class);
                        startActivity(activity_gameover);
                        handler.removeCallbacks(runnable);
                        //chiamata activity classifica e game over
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    // tutti spenti
    /*JSONArray initalizePixels() throws JSONException{
        pixels_array = new JSONArray();
        for (int i = 0; i < 1072; i++) {
            for (int j = 0; j < 4; j++) {
                ragnatela[i][j] = 0;
            }
        }
        try {

            for (int i = 0; i < 1072; i++) {
                pixels_array.put(ragnatela[i]);
            }
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
        return pixels_array;
    }*/



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
                    imageCannone.setImageResource(R.drawable.tel2);
                }
            }, 2000);

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
                    imageCannone.setImageResource(R.drawable.tel1);
                }
            }, 2000);
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
                    imageCannone.setImageResource(R.drawable.go_tel);
                }
            }, 2000);
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
                    imageCannone.setImageResource(R.drawable.cannone_up);
                    countDownStart();
                    startTimer();
                    startTimerBomba();

                }
            }, 2000);

        } catch (
                JSONException e)

        {
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
    private void handleNetworkRequest(Handler handler,int what, Object payload, int arg1, int arg2) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    JSONArray setProiettileRamo1(int j) {//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile = new JSONArray();

        ragnatela[j][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j][1] = 0;
        ragnatela[j][2] = 0;
        ragnatela[j][3] = 0;

        ragnatela[j + 1][0] = 100;
        ragnatela[j + 1][1] = 128;
        ragnatela[j + 1][2] = 195;
        ragnatela[j + 1][3] = 238;

        ragnatela[j + 2][0] = 255;
        ragnatela[j + 2][1] = 128;
        ragnatela[j + 2][2] = 195;
        ragnatela[j + 2][3] = 238;

        // anche gli ultimi si accendono

        ragnatela[l_primo_t - j][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[l_primo_t - j][1] = 0;
        ragnatela[l_primo_t - j][2] = 0;
        ragnatela[l_primo_t - j][3] = 0;

        ragnatela[l_primo_t - j - 1][0] = 100;
        ragnatela[l_primo_t - j - 1][1] = 128;
        ragnatela[l_primo_t - j - 1][2] = 195;
        ragnatela[l_primo_t - j - 1][3] = 238;

        ragnatela[l_primo_t - j - 2][0] = 255;
        ragnatela[l_primo_t - j - 2][1] = 128;
        ragnatela[l_primo_t - j - 2][2] = 195;
        ragnatela[l_primo_t - j - 2][3] = 238;

        try {
            for (int i = 0; i < 52; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezzo_proiettile.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile;
    }

    JSONArray setProiettileRamo2(int j, int count) {//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile2 = new JSONArray();

        ragnatela[j + count][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + count][1] = 0;
        ragnatela[j + count][2] = 0;
        ragnatela[j + count][3] = 0;

        ragnatela[j + count + 1][0] = 100;
        ragnatela[j + count + 1][1] = 128;
        ragnatela[j + count + 1][2] = 195;
        ragnatela[j + count + 1][3] = 238;

        ragnatela[j + count + 2][0] = 255;
        ragnatela[j + count + 2][1] = 128;
        ragnatela[j + count + 2][2] = 195;
        ragnatela[j + count + 2][3] = 238;

        // anche gli ultimi si accendono

        ragnatela[j + l_secondo_t - count + 1][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + l_secondo_t - count + 1][1] = 0;
        ragnatela[j + l_secondo_t - count + 1][2] = 0;
        ragnatela[j + l_secondo_t - count + 1][3] = 0;

        ragnatela[j + l_secondo_t - count][0] = 100;
        ragnatela[j + l_secondo_t - count][1] = 128;
        ragnatela[j + l_secondo_t - count][2] = 195;
        ragnatela[j + l_secondo_t - count][3] = 238;

        ragnatela[j + l_secondo_t - count - 1][0] = 255;
        ragnatela[j + l_secondo_t - count - 1][1] = 128;
        ragnatela[j + l_secondo_t - count - 1][2] = 195;
        ragnatela[j + l_secondo_t - count - 1][3] = 238;

        try {
            for (int i = 0; i < 186; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezzo_proiettile2.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile2;
    }

    JSONArray setProiettileRamo3(int j, int count) {//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile3 = new JSONArray();

        ragnatela[j + count][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + count][1] = 0;
        ragnatela[j + count][2] = 0;
        ragnatela[j + count][3] = 0;

        ragnatela[j + count + 1][0] = 100;
        ragnatela[j + count + 1][1] = 128;
        ragnatela[j + count + 1][2] = 195;
        ragnatela[j + count + 1][3] = 238;

        ragnatela[j + count + 2][0] = 255;
        ragnatela[j + count + 2][1] = 128;
        ragnatela[j + count + 2][2] = 195;
        ragnatela[j + count + 2][3] = 238;

        // anche gli ultimi si accendono

        ragnatela[j + l_terzo_t - count + 1][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + l_terzo_t - count + 1][1] = 0;
        ragnatela[j + l_terzo_t - count + 1][2] = 0;
        ragnatela[j + l_terzo_t - count + 1][3] = 0;

        ragnatela[j + l_terzo_t - count][0] = 100;
        ragnatela[j + l_terzo_t - count][1] = 128;
        ragnatela[j + l_terzo_t - count][2] = 195;
        ragnatela[j + l_terzo_t - count][3] = 238;

        ragnatela[j + l_terzo_t - count - 1][0] = 255;
        ragnatela[j + l_terzo_t - count - 1][1] = 128;
        ragnatela[j + l_terzo_t - count - 1][2] = 195;
        ragnatela[j + l_terzo_t - count - 1][3] = 238;

        try {
            for (int i = 0; i < 318; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezzo_proiettile3.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile3;
    }

    JSONArray setProiettileRamo4(int j, int count) {//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile4 = new JSONArray();

        ragnatela[j + count][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + count][1] = 0;
        ragnatela[j + count][2] = 0;
        ragnatela[j + count][3] = 0;

        ragnatela[j + count + 1][0] = 100;
        ragnatela[j + count + 1][1] = 128;
        ragnatela[j + count + 1][2] = 195;
        ragnatela[j + count + 1][3] = 238;

        ragnatela[j + count + 2][0] = 255;
        ragnatela[j + count + 2][1] = 128;
        ragnatela[j + count + 2][2] = 195;
        ragnatela[j + count + 2][3] = 238;

        // anche gli ultimi si accendono

        ragnatela[j + l_quarto_t - count + 1][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + l_quarto_t - count + 1][1] = 0;
        ragnatela[j + l_quarto_t - count + 1][2] = 0;
        ragnatela[j + l_quarto_t - count + 1][3] = 0;

        ragnatela[j + l_quarto_t - count][0] = 100;
        ragnatela[j + l_quarto_t - count][1] = 128;
        ragnatela[j + l_quarto_t - count][2] = 195;
        ragnatela[j + l_quarto_t - count][3] = 238;

        ragnatela[j + l_quarto_t - count - 1][0] = 255;
        ragnatela[j + l_quarto_t - count - 1][1] = 128;
        ragnatela[j + l_quarto_t - count - 1][2] = 195;
        ragnatela[j + l_quarto_t - count - 1][3] = 238;

        try {
            for (int i = 0; i < 424; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezzo_proiettile4.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile4;
    }

    JSONArray setProiettileRamo5(int j, int count) {//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile5 = new JSONArray();

        ragnatela[j + count][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + count][1] = 0;
        ragnatela[j + count][2] = 0;
        ragnatela[j + count][3] = 0;

        ragnatela[j + count + 1][0] = 100;
        ragnatela[j + count + 1][1] = 128;
        ragnatela[j + count + 1][2] = 195;
        ragnatela[j + count + 1][3] = 238;

        ragnatela[j + count + 2][0] = 255;
        ragnatela[j + count + 2][1] = 128;
        ragnatela[j + count + 2][2] = 195;
        ragnatela[j + count + 2][3] = 238;

        // anche gli ultimi si accendono

        ragnatela[j + l_quinto_t - count + 1][0] = 0;// coloriamo i primi 3 led blu
        ragnatela[j + l_quinto_t - count + 1][1] = 0;
        ragnatela[j + l_quinto_t - count + 1][2] = 0;
        ragnatela[j + l_quinto_t - count + 1][3] = 0;

        ragnatela[j + l_quinto_t - count][0] = 100;
        ragnatela[j + l_quinto_t - count][1] = 128;
        ragnatela[j + l_quinto_t - count][2] = 195;
        ragnatela[j + l_quinto_t - count][3] = 238;

        ragnatela[j + l_quinto_t - count - 1][0] = 255;
        ragnatela[j + l_quinto_t - count - 1][1] = 128;
        ragnatela[j + l_quinto_t - count - 1][2] = 195;
        ragnatela[j + l_quinto_t - count - 1][3] = 238;

        try {
            for (int i = 0; i < 522; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezzo_proiettile5.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile5;
    }

    JSONArray setBombaRamo1(int j) {//muoviamo la bomba in giù
        JSONObject tmp;
        JSONArray mezza_bomba = new JSONArray();

        ragnatela[l_primo_t / 2 - j][0] = 0;// coloriamo i primi 3 led rossi
        ragnatela[l_primo_t / 2 - j][1] = 0;
        ragnatela[l_primo_t / 2 - j][2] = 0;
        ragnatela[l_primo_t / 2 - j][3] = 0;

        ragnatela[l_primo_t / 2 - j - 1][0] = 100;// coloriamo i primi 3 led rossi
        ragnatela[l_primo_t / 2 - j - 1][1] = 255;
        ragnatela[l_primo_t / 2 - j - 1][2] = 0;
        ragnatela[l_primo_t / 2 - j - 1][3] = 0;

        ragnatela[l_primo_t / 2 - j - 2][0] = 255;// coloriamo i primi 3 led rossi
        ragnatela[l_primo_t / 2 - j - 2][1] = 255;
        ragnatela[l_primo_t / 2 - j - 2][2] = 0;
        ragnatela[l_primo_t / 2 - j - 2][3] = 0;

        // anche gli ultimi si accendono

        ragnatela[l_primo_t / 2 + j][0] = 0;// coloriamo i primi 3 led rossi
        ragnatela[l_primo_t / 2 + j][1] = 0;
        ragnatela[l_primo_t / 2 + j][2] = 0;
        ragnatela[l_primo_t / 2 + j][3] = 0;

        ragnatela[l_primo_t / 2 + j + 1][0] = 100;// coloriamo i primi 3 led rossi
        ragnatela[l_primo_t / 2 + j + 1][1] = 255;
        ragnatela[l_primo_t / 2 + j + 1][2] = 0;
        ragnatela[l_primo_t / 2 + j + 1][3] = 0;

        ragnatela[l_primo_t / 2 + j + 2][0] = 255;// coloriamo i primi 3 led rossi
        ragnatela[l_primo_t / 2 + j + 2][1] = 255;
        ragnatela[l_primo_t / 2 + j + 2][2] = 0;
        ragnatela[l_primo_t / 2 + j + 2][3] = 0;

        try {
            for (int i = 0; i < 52; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezza_bomba.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezza_bomba;
    }

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

//to do:
// - proiettili laser
// - show score
// - frasi all hit e laser
// - bombe diverse velocità--> quando la rossa, più veloce, ti colpisce ti blocca per 2 sec
// - istruzioni
// - classifica
// -colorare i cerchi, quando ne completi uno aumenta lo step delle bombe
