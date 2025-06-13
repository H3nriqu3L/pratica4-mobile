package com.example.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SwitchMaterial lanternaSwitch;
    SwitchMaterial vibracaoSwitch;
    LanternaHelper lanternaHelper;
    MotorHelper motorHelper;

    private float currentLightValue = 0.0f;
    private float currentProximityValue = 0.0f;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private static final int REQUEST_CODE_CLASSIFICACAO = 1001;

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

        inicializarSensores();

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

    private void inicializarSensores() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (lightSensor == null) {
                Toast.makeText(this, "Sensor de luz não disponível", Toast.LENGTH_SHORT).show();
            }

            if (proximitySensor == null) {
                Toast.makeText(this, "Sensor de proximidade não disponível", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLightValue = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            currentProximityValue = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lanternaHelper != null) {
            lanternaHelper.desligar();
        }

        if (motorHelper != null) {
            motorHelper.pararVibracao();
        }
    }

    public void onButtonClick(View view) {
        Toast.makeText(this, "Enviando - Luz: " + currentLightValue + ", Proximidade: " + currentProximityValue, Toast.LENGTH_LONG).show();

        Intent it = new Intent("ACAO_DESEJADA");
        it.addCategory(Intent.CATEGORY_DEFAULT);
        it.putExtra("light_value", currentLightValue);
        it.putExtra("proximity_value", currentProximityValue);

        try {
            startActivityForResult(it, REQUEST_CODE_CLASSIFICACAO);
        } catch (Exception e) {
            Toast.makeText(this, "Aplicativo de classificação não encontrado!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CLASSIFICACAO && resultCode == RESULT_OK && data != null) {
            boolean ligarLanterna = data.getBooleanExtra("ligar_lanterna", false);
            boolean ligarVibracao = data.getBooleanExtra("ligar_vibracao", false);

            aplicarClassificacoes(ligarLanterna, ligarVibracao);
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