package com.example.pratica4_segundoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private float lightValue = 0.0f;
    private float proximityValue = 0.0f;

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

        Intent intent = getIntent();
        if (intent != null) {
            lightValue = intent.getFloatExtra("light_value", 0.0f);
            proximityValue = intent.getFloatExtra("proximity_value", 0.0f);

            Toast.makeText(this, "Valores recebidos - Luz: " + lightValue + ", Proximidade: " + proximityValue, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Nenhum valor recebido. Usando valores padrão (0.0)", Toast.LENGTH_LONG).show();
        }
    }

    public void devolverClassificacoes(View view) {
        Toast.makeText(this, "Classificando - Proximidade: " + proximityValue + " (>3 = vibrar)", Toast.LENGTH_LONG).show();

        boolean ligarLanterna = lightValue < 20.0f;
        boolean ligarVibracao = proximityValue > 3.0f;

        Intent resultIntent = new Intent();
        resultIntent.putExtra("ligar_lanterna", ligarLanterna);
        resultIntent.putExtra("ligar_vibracao", ligarVibracao);
        resultIntent.putExtra("light_value", lightValue);
        resultIntent.putExtra("proximity_value", proximityValue);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}