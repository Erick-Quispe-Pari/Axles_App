package com.example.axles_app;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Objetos View
    TextView aux_label;

    // Variables de Sensores
    SensorManager sensor_manager;
    Sensor acelerometer_sensor;
    Sensor magnetometer_sensor;

    // Valores de Sensores
    float[] magnetometer_values,acelerometer_values;
    String[] valoresImprimibiles;

    // Variables Auxiliares
    boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometer_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        valoresImprimibiles=new String[3];
        on=true;

        tarea.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensor_manager.unregisterListener(this);
        on=false;
        tarea.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensor_manager.registerListener(this,acelerometer_sensor,sensor_manager.SENSOR_DELAY_UI);
        sensor_manager.registerListener(this,magnetometer_sensor,sensor_manager.SENSOR_DELAY_UI);
        on=true;
        if(!(tarea.getStatus() ==(AsyncTask.Status.RUNNING))){
            tarea.execute();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            acelerometer_values=event.values;
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magnetometer_values=event.values;
        }
        if(acelerometer_values!=null&&magnetometer_values!=null){
            float[] R=new float[9],I=new float[9];
            boolean succesful = sensor_manager.getRotationMatrix(R,I,acelerometer_values,magnetometer_values);
            if(succesful){
                float[] orientation = new float [3];
                SensorManager.getOrientation(R,orientation);
                Toast.makeText(getApplicationContext(),"Asimut:"+orientation[0]+", Pitch:"+orientation[1]+", Roll:"+orientation[2],Toast.LENGTH_SHORT);
                valoresImprimibiles[0]=Math.round(orientation[0]*57.315f)+"°";
                valoresImprimibiles[1]=Math.round(orientation[1]*57.315f)+"°";
                valoresImprimibiles[2]=Math.round(orientation[2]*57.315f)+"°";
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void closeApp(View v){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    AsyncTask tarea = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            while(on){
                publishProgress();
                try {
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            TextView acimut = findViewById(R.id.acimut_value);
            TextView pitch = findViewById(R.id.pitch_value);
            TextView roll = findViewById(R.id.roll_value);

            acimut.setText(valoresImprimibiles[0]);
            pitch.setText(valoresImprimibiles[1]);
            roll.setText(valoresImprimibiles[2]);
        }
    };
}