package com.example.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private float currentLightValue = 0.0f;
    private float currentProximityValue = 0.0f;

    // BroadcastReceiver para receber classificações
    private BroadcastReceiver classificacoesReceiver;

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

        lanternaHelper = new LanternaHelper(this);
        motorHelper = new MotorHelper(this);

        lanternaSwitch = findViewById(R.id.lanterna);
        vibracaoSwitch = findViewById(R.id.vibracao);

        // Configurar BroadcastReceiver
        configurarBroadcastReceiver();

        lanternaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Lanterna ON", Toast.LENGTH_SHORT).show();
                lanternaHelper.ligar();
            } else {
                Toast.makeText(this, "Lanterna OFF", Toast.LENGTH_SHORT).show();
                lanternaHelper.desligar();
            }
        });

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

    private void configurarBroadcastReceiver() {
        classificacoesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("CLASSIFICACOES_RESULTADO".equals(intent.getAction())) {
                    boolean ligarLanterna = intent.getBooleanExtra("ligar_lanterna", false);
                    boolean ligarVibracao = intent.getBooleanExtra("ligar_vibracao", false);


                    aplicarClassificacoes(ligarLanterna, ligarVibracao);
                }
            }
        };

        // Registrar o receiver
        IntentFilter filter = new IntentFilter("CLASSIFICACOES_RESULTADO");
        registerReceiver(classificacoesReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (classificacoesReceiver != null) {
            unregisterReceiver(classificacoesReceiver);
        }

        // SEMPRE desligar lanterna e vibração quando o app for fechado
        if (lanternaHelper != null) {
            lanternaHelper.desligar();
        }

        if (motorHelper != null) {
            motorHelper.pararVibracao();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (lanternaHelper != null) {
            lanternaHelper.desligar();
        }

        if (motorHelper != null) {
            motorHelper.pararVibracao();
        }

        if (lanternaSwitch != null) {
            lanternaSwitch.setChecked(false);
        }

        if (vibracaoSwitch != null) {
            vibracaoSwitch.setChecked(false);
        }
    }

    public void onButtonClick(View view) {
        Toast.makeText(this, "Enviando leituras para classificação...", Toast.LENGTH_SHORT).show();

        Intent it = new Intent("ACAO_DESEJADA");
        it.addCategory(Intent.CATEGORY_DEFAULT);

        it.putExtra("light_value", currentLightValue);
        it.putExtra("proximity_value", currentProximityValue);

        try {
            startActivity(it);
        } catch (Exception e) {
            Toast.makeText(this, "Aplicativo de classificação não encontrado!", Toast.LENGTH_LONG).show();
        }
    }

    private void aplicarClassificacoes(boolean ligarLanterna, boolean ligarVibracao) {
        if (ligarLanterna) {
            lanternaHelper.ligar();
            lanternaSwitch.setChecked(true);
        } else {
            lanternaHelper.desligar();
            lanternaSwitch.setChecked(false);
        }

        if (ligarVibracao) {
            motorHelper.iniciarVibracao();
            vibracaoSwitch.setChecked(true);
        } else {
            motorHelper.pararVibracao();
            vibracaoSwitch.setChecked(false);
        }

        String resultado = String.format("Classificação aplicada - Lanterna: %s, Vibração: %s",
                ligarLanterna ? "ON" : "OFF",
                ligarVibracao ? "ON" : "OFF");
        Toast.makeText(this, resultado, Toast.LENGTH_LONG).show();
    }

}