package com.example.sensor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import Helper.LanternaHelper;
import Helper.MotorHelper;

public class MainActivity extends AppCompatActivity {
    SwitchMaterial lanternaSwitch;
    SwitchMaterial vibracaoSwitch;
    LanternaHelper lanternaHelper;
    MotorHelper motorHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lanternaSwitch = findViewById(R.id.lanterna);
        vibracaoSwitch = findViewById(R.id.vibracao);

        lanternaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                Toast.makeText(this, "Lanterna ON", Toast.LENGTH_SHORT).show();
                lanternaHelper.ligar();
            } else {

                Toast.makeText(this, "Lanterna OFF", Toast.LENGTH_SHORT).show();
                lanternaHelper.desligar();
            }
        });

        // Listener para o switch da vibração
        vibracaoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                motorHelper.iniciarVibracao();
                Toast.makeText(this, "Vibração ON", Toast.LENGTH_SHORT).show();
            } else {
                motorHelper.pararVibracao();
                Toast.makeText(this, "Vibração OFF", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onButtonClick(View view){
        Toast.makeText(this, "Classificar leituras", Toast.LENGTH_SHORT).show();

        Intent it = new Intent("ACAO_DESEJADA");
        it.addCategory("");
    }


}