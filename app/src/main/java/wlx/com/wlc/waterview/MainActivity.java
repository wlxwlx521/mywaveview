package wlx.com.wlc.waterview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import car.com.wlc.waterview.R;
import wlx.com.wlc.waterview.view.Wave;
import wlx.com.wlc.waterview.view.WaveView;


public class MainActivity extends AppCompatActivity {

    private Wave waveView;
    private WaveView waveView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         waveView = (Wave) findViewById(R.id.wave_view);
        waveView1 = ((WaveView) findViewById(R.id.wave1));
        waveView.setCurrentText("hello", Color.parseColor("#3F51B5"),46);
        waveView.setWaveHeight(0.8f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        waveView.start();
        waveView1.start();
    }
}
