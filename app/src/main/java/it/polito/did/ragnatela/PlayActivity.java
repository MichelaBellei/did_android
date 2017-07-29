package it.polito.did.ragnatela;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlayActivity extends Activity {

    Unbinder unbinder;

    private String host_url = "192.168.1.67";
    private int host_port = 8080;


   /* @BindView(R.id.set_display_pixels)
    Button set_display_pixels;

    @BindView(R.id.random_colors)
    Button randomColors;

    @BindView(R.id.move_backward_button)
    Button moveBackwardButton;

    @BindView(R.id.move_forward_button)
    Button moveForwardButton;

    @BindView(R.id.highlight_components_button)
    Button changeColorButton;

    @BindView(R.id.ramo1_button)
    Button ramo1Button;

    @BindViews({R.id.first_byte_ip, R.id.second_byte_ip, R.id.third_byte_ip, R.id.fourth_byte_ip})
    List<EditText> ip_address_bytes;

    @BindView(R.id.host_port)
    EditText hostPort; */

    private TextWatcher myIpTextWatcher;
    private JSONArray pixels_array;

    private JSONArray primo_t, secondo_t, terzo_t, mezzo_proiettile, mezzo_proiettile2, mezza_bomba, restanti, quarto_t, quinto_t, primo_c, secondo_c, terzo_c;

    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;
    private int l_primo_t = 51;
    private int l_secondo_t = 133;

    private int[][] ragnatela = new int[1072][4];// per ogni px abbiamo 4 colonne che identificano i valori di a, rgb
    private int[][] display_pos1 = new int[1024][4];
    private TextView tvSecond;
    private Handler handler;
    private Runnable runnable;
    int seconds = 62;
    private Button buttonRight, buttonLeft;
    private ImageView imageCannone;
    private int posizioneAttuale = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        unbinder = ButterKnife.bind(this);

         /*  set_display_pixels.setEnabled(false);
        randomColors.setEnabled(false);
        moveBackwardButton.setEnabled(false);
        moveForwardButton.setEnabled(false);
        changeColorButton.setEnabled(false);
        ramo1Button.setEnabled(false); */

        tvSecond = (TextView) findViewById(R.id.txtTimerSecond);
        countDownStart();

        imageCannone = (ImageView) findViewById(R.id.imageCannone);
        buttonLeft = (Button) findViewById(R.id.leftButton);
        buttonRight = (Button) findViewById(R.id.rightButton);

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posizioneAttuale++;
                if(posizioneAttuale==6){
                    posizioneAttuale=1;
                }
                setDisplayPixels();
                switch (posizioneAttuale) {
                    case 1:
                        imageCannone.setImageResource(R.drawable.cannone_up);
                        break;
                    case 2:
                        imageCannone.setImageResource(R.drawable.cannone_dx_up);
                        break;
                    case 3:
                        imageCannone.setImageResource(R.drawable.cannone_dx_down);
                        break;
                    case 4:
                        imageCannone.setImageResource(R.drawable.cannone_sx_down);
                        break;
                    case 5:
                        imageCannone.setImageResource(R.drawable.cannone_sx_up);
                        break;
                }


            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posizioneAttuale--;
                if(posizioneAttuale==0){
                    posizioneAttuale=5;
                }
                setDisplayPixels();
                switch (posizioneAttuale){
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
                                mezzo_proiettile2 = setProiettileRamo2(l_primo_t,j);
                                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile2, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        imageCannone.setImageResource(R.drawable.cannone_dx_down);

                        break;
                    case 4:
                        imageCannone.setImageResource(R.drawable.cannone_sx_down);
                        break;
                    case 5:
                        imageCannone.setImageResource(R.drawable.cannone_sx_up);
                        break;
                }


            }
        });

      /*  myIpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (checkCorrectIp()) {
                    /*moveBackwardButton.setEnabled(true);
                    moveForwardButton.setEnabled(true);
                    randomColors.setEnabled(true);
                    set_display_pixels.setEnabled(true);
                    changeColorButton.setEnabled(true);
                    ramo1Button.setEnabled(true);
                    Message msg = mNetworkHandler.obtainMessage();
                    msg.what = NetworkThread.SET_SERVER_DATA;
                    msg.obj = host_url;
                    msg.arg1 = host_port;
                    msg.sendToTarget();

                    handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        for (EditText ip_byte : ip_address_bytes) {
            ip_byte.addTextChangedListener(myIpTextWatcher);
        }

        hostPort.addTextChangedListener(myIpTextWatcher); */

        pixels_array = preparePixelsArray();

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(PlayActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();

        try {
            initalizePixels();
            spegniTutto();
            setDisplayPixels();
        } catch (JSONException e) {
            //Non dovrebbe avere problemi
            e.printStackTrace();
        }
    }

    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {

                        if(seconds>0) {
                            seconds = seconds - 1;
                            Typeface myTypeface = Typeface.createFromAsset(getAssets(),"fonts/wareagle.ttf");
                            // TextView waitingTimeView = (TextView) findViewById(R.id.txtTimerSecond);
                            tvSecond.setTypeface(myTypeface);
                            tvSecond.setText("" + String.format("%02d", seconds));
                            if (seconds <= 20) {
                                tvSecond.setTextColor(Color.RED);
                            }
                        }
                        else{
                            //chiamare activity classifica e game over
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }


    private void initalizePixels() throws JSONException {

        for (int i = 0; i < 1072; i++) {
            for (int j = 0; j < 4; j++) {
                ragnatela[i][j] = 0;// li spegniamo tutti
            }
        }

        for (int i = 0; i < 1024; i++) {
            for (int j = 0; j < 3; j++) {
                display_pos1[i][j] = 0; //spenti
            }
        }

        JSONObject tmp;
        primo_t = new JSONArray();
        for (int i = 0; i < l_primo_t; i++) {
            tmp = new JSONObject();
            tmp.put("a", 0);
            tmp.put("g", 0);
            tmp.put("b", 0);
            tmp.put("r", 0);
            primo_t.put(tmp);
        }

        secondo_t = new JSONArray();
        for (int i = 0; i < l_secondo_t; i++) {
            tmp = new JSONObject();
            tmp.put("a", 0);
            tmp.put("g", 0);
            tmp.put("b", 0);
            tmp.put("r", 0);
            secondo_t.put(tmp);
        }

        terzo_t = new JSONArray();
        for (int i = 0; i < 1072 - l_primo_t - l_secondo_t; i++) {
            tmp = new JSONObject();
            tmp.put("a", 0);
            tmp.put("g", 0);
            tmp.put("b", 0);
            tmp.put("r", 0);
            terzo_t.put(tmp);
        }
    }

    private void spegniTutto() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < primo_t.length(); i++)
                jsonArray.put(primo_t.get(i));


            for (int i = 0; i < secondo_t.length(); i++)
                jsonArray.put(secondo_t.get(i));


            for (int i = 0; i < terzo_t.length(); i++)
                jsonArray.put(terzo_t.get(i));

            handleNetworkRequest(NetworkThread.SET_PIXELS, jsonArray, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            for (int i = 0; i < 26; i++) {
                ((JSONObject) pixels_array.get(i)).put("r", 255);
                ((JSONObject) pixels_array.get(i)).put("g", 0);
                ((JSONObject) pixels_array.get(i)).put("b", 0);
                wait(1000);
                ((JSONObject) pixels_array.get(i)).put("r", 0);
                ((JSONObject) pixels_array.get(i)).put("g", 255);
                ((JSONObject) pixels_array.get(i)).put("b", 0);
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0 ,0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    void setDisplayPixels() {
        try {
            JSONArray pixels_array = new JSONArray();

            // BitmapFactory.Options options = new BitmapFactory.Options();
            //options.outHeight = 32;
            //options.outWidth = 32;
            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.cannone_blu_up);
            switch(posizioneAttuale){
                case 1:
                     tempBMP = BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.cannone_blu_up);
                    break;
                case 2:
                     tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.diag_dx_up);
                    break;
                case 3:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.diag_dx_down);
                    break;
                case 4:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.diag_sx_down);
                    break;
                case 5:
                    tempBMP = BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.diag_sx_up);
                    break;
            }

            tempBMP = Bitmap.createScaledBitmap(tempBMP,32,32, false);
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

    public void startHandlerThread() {
        mNetworkThread = new NetworkThread(mMainHandler);
        mNetworkThread.start();
        mNetworkHandler = mNetworkThread.getNetworkHandler();
    }

    /*private boolean checkCorrectIp() {
        StringBuilder sb = new StringBuilder();
        int port;

        if (hostPort.getText().length() == 0)
            return false;

        for (EditText editText : ip_address_bytes) {
            sb.append(editText.getText().toString());
            sb.append(".");
        }
        //cancello l'ultimo "."
        sb.deleteCharAt(sb.length() - 1);

        port = Integer.parseInt(hostPort.getText().toString());
        if (validIP(sb.toString()) && port >= 0 & port <= 65535) {
            host_url = sb.toString();
            host_port = port;
            return true;
        } else
            return false;
    } */

    //from http://stackoverflow.com/questions/4581877/validating-ipv4-string-in-java
   /* public static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mNetworkThread != null && mNetworkHandler != null) {
            mNetworkHandler.removeMessages(mNetworkThread.SET_PIXELS);
            mNetworkHandler.removeMessages(mNetworkThread.SET_DISPLAY_PIXELS);
            mNetworkHandler.removeMessages(mNetworkThread.SET_SERVER_DATA);
            mNetworkThread.quit();
            try {
                mNetworkThread.join(100);
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            } finally {
                mNetworkThread = null;
                mNetworkHandler = null;
            }
        }
    } */

    /*
    @OnClick(R.id.random_colors)
    void setRandomColors() {

        try {
            JSONArray pixels_array = preparePixelsArray();

            for (int i = 0; i < pixels_array.length(); i++) {
                ((JSONObject) pixels_array.get(i)).put("r", (int) (Math.random() * 255.0f));
                ((JSONObject) pixels_array.get(i)).put("g", (int) (Math.random() * 255.0f));
                ((JSONObject) pixels_array.get(i)).put("b", (int) (Math.random() * 255.0f));
            }
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    } */


    /*
    @OnClick(R.id.set_display_pixels)
    void setDisplayPixels() {
        try {
            JSONArray pixels_array = new JSONArray();

            // BitmapFactory.Options options = new BitmapFactory.Options();
            //options.outHeight = 32;
            //options.outWidth = 32;
            Bitmap tempBMP = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.cannone_blu_red_2);
            tempBMP = Bitmap.createScaledBitmap(tempBMP,32,32, false);
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

    } */

   /* @OnClick(R.id.highlight_components_button)
    void highLightComponents() {
        try {
            pixels_array = preparePixelsArray();
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

   /* @OnClick(R.id.move_forward_button)
    void movePixelsForward() {
        try {
            for (int j = 0; j < l_primo_t / 2; j++) {
                mezza_bomba = setBomba(j);
                handleNetworkRequest(NetworkThread.SET_PIXELS, mezza_bomba, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < pixels_array.length(); i++) {
                jsonArray.put(pixels_array.get((i + pixels_array.length() - 10) % pixels_array.length()));
            }
            pixels_array = jsonArray;
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */


     /* @OnClick(R.id.move_backward_button)
    void movePixelsBackward() {
        try {
            for (int j = 0; j < (l_primo_t / 2); j++) {
                mezzo_proiettile = setProiettile(j);
                handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < pixels_array.length(); i++) {
                jsonArray.put(pixels_array.get((i + 10) % pixels_array.length()));
            }
            pixels_array = jsonArray;
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */


    /* @OnClick(R.id.ramo1_button)
    void ramo1() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < primo_t.length(); i++)
                jsonArray.put(primo_t.get(i));


            for (int i = 0; i < secondo_t.length(); i++)
                jsonArray.put(secondo_t.get(i));


            for (int i = 0; i < terzo_t.length(); i++)
                jsonArray.put(terzo_t.get(i));

            handleNetworkRequest(NetworkThread.SET_PIXELS, jsonArray, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

       try {
            for (int i = 0; i < 26; i++) {
                ((JSONObject) pixels_array.get(i)).put("r", 255);
                ((JSONObject) pixels_array.get(i)).put("g", 0);
                ((JSONObject) pixels_array.get(i)).put("b", 0);
                wait(1000);
                ((JSONObject) pixels_array.get(i)).put("r", 0);
                ((JSONObject) pixels_array.get(i)).put("g", 255);
                ((JSONObject) pixels_array.get(i)).put("b", 0);
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0 ,0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
        Message msg = mNetworkHandler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    JSONArray setProiettileRamo1(int j) {//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile = new JSONArray();

        ragnatela[j][0] = 0;// coloriamo i primi 3 led verdi
        ragnatela[j][1] = 0;
        ragnatela[j][2] = 0;
        ragnatela[j][3] = 0;

        ragnatela[j + 1][0] = 100;// coloriamo i primi 3 led verdi
        ragnatela[j + 1][1] = 0;
        ragnatela[j + 1][2] = 255;
        ragnatela[j + 1][3] = 0;

        ragnatela[j + 2][0] = 255;// coloriamo i primi 3 led verdi
        ragnatela[j + 2][1] = 0;
        ragnatela[j + 2][2] = 255;
        ragnatela[j + 2][3] = 0;

        // anche gli ultimi si accendono

        ragnatela[l_primo_t - j][0] = 0;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t - j][1] = 0;
        ragnatela[l_primo_t - j][2] = 0;
        ragnatela[l_primo_t - j][3] = 0;

        ragnatela[l_primo_t - j - 1][0] = 100;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t - j - 1][1] = 0;
        ragnatela[l_primo_t - j - 1][2] = 255;
        ragnatela[l_primo_t - j - 1][3] = 0;

        ragnatela[l_primo_t - j - 2][0] = 255;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t - j - 2][1] = 0;
        ragnatela[l_primo_t - j - 2][2] = 255;
        ragnatela[l_primo_t - j - 2][3] = 0;

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

        ragnatela[j+count][0] = 0;// coloriamo i primi 3 led verdi
        ragnatela[j+count][1] = 0;
        ragnatela[j+count][2] = 0;
        ragnatela[j+count][3] = 0;

        ragnatela[j +count+ 1][0] = 100;// coloriamo i primi 3 led verdi
        ragnatela[j +count+ 1][1] = 0;
        ragnatela[j +count+ 1][2] = 255;
        ragnatela[j +count+ 1][3] = 0;

        ragnatela[j + count+2][0] = 255;// coloriamo i primi 3 led verdi
        ragnatela[j + count+2][1] = 0;
        ragnatela[j + count+2][2] = 255;
        ragnatela[j + count+2][3] = 0;

        // anche gli ultimi si accendono

        ragnatela[j+l_secondo_t - count+1][0] = 0;// coloriamo i primi 3 led verdi
        ragnatela[j+l_secondo_t - count+1][1] = 0;
        ragnatela[j+l_secondo_t - count+1][2] = 0;
        ragnatela[j+l_secondo_t - count+1][3] = 0;

        ragnatela[j+l_secondo_t - count ][0] = 100;// coloriamo i primi 3 led verdi
        ragnatela[j+l_secondo_t - count ][1] = 0;
        ragnatela[j+l_secondo_t - count ][2] = 255;
        ragnatela[j+l_secondo_t - count ][3] = 0;

        ragnatela[j+l_secondo_t - count - 1][0] = 255;// coloriamo i primi 3 led verdi
        ragnatela[j+l_secondo_t - count - 1][1] = 0;
        ragnatela[j+l_secondo_t - count - 1][2] = 255;
        ragnatela[j+l_secondo_t - count - 1][3] = 0;

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

    JSONArray setBomba(int j) {//muoviamo il proiettile in giù
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
            for (int i = 0; i < 1072; i++) {
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
                    tmp.put("g", 255);
                    tmp.put("b", 0);
                    tmp.put("r", 0);
                } else if (i < 613) {
                    tmp.put("r", 255);
                    tmp.put("g", 0);
                    tmp.put("b", 0);
                } else if (i < 791) {
                    tmp.put("b", 255);
                    tmp.put("g", 0);
                    tmp.put("r", 0);
                } else {
                    tmp.put("b", 255);
                    tmp.put("g", 0);
                    tmp.put("r", 255);
                }
                pixels_array.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return pixels_array;
    }

}
