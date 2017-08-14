package it.polito.did.ragnatela;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {
    Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/wareagle.ttf");
    TextView classifica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        classifica = (TextView) findViewById(R.id.classifica);
        classifica.setTypeface(myTypeface);

        Button button_play_again = (Button) findViewById(R.id.play_again_button);
        button_play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity_play = new Intent(GameOverActivity.this, PlayActivity.class);
                startActivity(activity_play);
            }
        });

        Button button_exit = (Button) findViewById(R.id.exit_button);
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
