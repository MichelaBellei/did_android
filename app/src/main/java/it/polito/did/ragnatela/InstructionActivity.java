package it.polito.did.ragnatela;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class InstructionActivity extends AppCompatActivity {
    TextView istruzioni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/wareagle.ttf");

        istruzioni = (TextView) findViewById(R.id.istruzioni);
        istruzioni.setTypeface(myTypeface);
        istruzioni.setMovementMethod(new ScrollingMovementMethod());
    }
}