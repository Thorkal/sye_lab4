/*
 -----------------------------------------------------------------------------------
 Laboratoire : 04
 Fichier     : CompassActivity.java
 Auteur(s)   : Pierrick Muller, Guillaume Zaretti, Tommy Gerardi
 Date        : 03.01.2020
 -----------------------------------------------------------------------------------
*/
package ch.heigvd.iict.sym_labo4;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import ch.heigvd.iict.sym_labo4.gl.OpenGLRenderer;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    //opengl
    private OpenGLRenderer  opglr           = null;
    private GLSurfaceView   m3DView         = null;

    //sensor managers
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;

    private float[] newMatrix = new float[4*4];
    private float[] oldMatrix = new float[4*4];

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we need fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // we initiate the view
        setContentView(R.layout.activity_compass);

        //we create the renderer
        this.opglr = new OpenGLRenderer(getApplicationContext());

        // link to GUI
        this.m3DView = findViewById(R.id.compass_opengl);

        //init opengl surface view
        this.m3DView.setRenderer(this.opglr);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER :
                gravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD :
                geomagnetic = event.values;
                break;
        }
        SensorManager.getRotationMatrix(newMatrix, null, gravity, geomagnetic);
        oldMatrix = this.opglr.swapRotMatrix(newMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // On ne s'occupe pas de la precision
    }

    /* TODO
        your activity need to register to accelerometer and magnetometer sensors' updates
        then you may want to call
        this.opglr.swapRotMatrix()
        with the 4x4 rotation matrix, everytime a new matrix is computed
        more information on rotation matrix can be found on-line:
        https://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[],%20float[],%20float[],%20float[])
    */
}
