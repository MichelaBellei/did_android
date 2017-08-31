package it.polito.did.ragnatela;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Instruction_Activity extends AppCompatActivity {

    int state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state=1;
        setContentView(R.layout.activity_instruction);
        final ImageView button_instruction = (ImageView) findViewById(R.id.button_instruction);
        button_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state++;
                switch (state){
                    case 2: button_instruction.setBackgroundResource(R.drawable.istruzioni_2); break;
                    case 3: button_instruction.setBackgroundResource(R.drawable.istruzioni_3); break;
                    case 4: button_instruction.setBackgroundResource(R.drawable.istruzioni_4); break;
                    case 5: button_instruction.setBackgroundResource(R.drawable.istruzioni_5); break;
                    case 6: button_instruction.setBackgroundResource(R.drawable.istruzioni_6); break;
                    case 7: button_instruction.setBackgroundResource(R.drawable.istruzioni_7); break;
                    case 8: button_instruction.setBackgroundResource(R.drawable.istruzioni_8);
                        break;
                    case 9:  Intent activity_menu = new Intent(Instruction_Activity.this, MenuActivity.class);
                        startActivity(activity_menu);
                        break;
                }

            }
        });
    }
}

