package com.musestressband.musestressband;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.ref.WeakReference;
import java.util.Queue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.interaxon.libmuse.MuseManager;
import com.interaxon.libmuse.Muse;
import com.interaxon.libmuse.Accelerometer;
import com.interaxon.libmuse.AnnotationData;
import com.interaxon.libmuse.ConnectionState;
import com.interaxon.libmuse.Eeg;
import com.interaxon.libmuse.LibMuseVersion;
import com.interaxon.libmuse.MessageType;
import com.interaxon.libmuse.MuseArtifactPacket;
import com.interaxon.libmuse.MuseConfiguration;
import com.interaxon.libmuse.MuseConnectionListener;
import com.interaxon.libmuse.MuseConnectionPacket;
import com.interaxon.libmuse.MuseDataListener;
import com.interaxon.libmuse.MuseDataPacket;
import com.interaxon.libmuse.MuseDataPacketType;
import com.interaxon.libmuse.MuseFileFactory;
import com.interaxon.libmuse.MuseFileReader;
import com.interaxon.libmuse.MuseFileWriter;
import com.interaxon.libmuse.MusePreset;
import com.interaxon.libmuse.MuseVersion;



public class MainActivity extends Activity {

    class ConnectionListener extends MuseConnectionListener{

        final WeakReference<Activity> activityRef;

        ConnectionListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }

        /**
         *
         * @param museConnectionPacket
         */
        @Override
        public void receiveMuseConnectionPacket(MuseConnectionPacket museConnectionPacket) {

            final String ConnectionStatus = museConnectionPacket.getCurrentConnectionState().toString();
            TextView ConStat = (TextView) findViewById(R.id.ConStat);
            Activity activity = activityRef.get();
            // UI thread is used here only because we need to update
            // TextView values. You don't have to use another thread, unless
            // you want to run disconnect() or connect() from connection packet
            // handler. In this case creating another thread is required.
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView ConStat = (TextView) findViewById(R.id.ConStat);

                        ConStat.setText(ConnectionStatus);
                    }
                });
            }



        }
    }

    class DataListener extends MuseDataListener{

        final WeakReference<Activity> activityRef;
        private MuseFileWriter fileWriter;

        DataListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }


        @Override
        public void receiveMuseDataPacket(MuseDataPacket museDataPacket) {
            switch(museDataPacket.getPacketType()){
                case ALPHA_ABSOLUTE:{
                    ArrayList<Double> values = museDataPacket.getValues();
                    Double avg = new Double(0);
                    for(int i = 0; i < values.size(); i++)
                        avg += values.get(i);
                    avg = avg/values.size();
                    long time = System.currentTimeMillis();
                    WaveMagnitude waveObj = new WaveMagnitude(avg, time);
                    alpha.add(0, waveObj);

                    while(waveObj.getTimestamp()-alpha.get(alpha.size()-1).getTimestamp() > minute)
                        alpha.remove(alpha.size() - 1);


                    Activity activity = activityRef.get();
                    if(activity != null){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView alphaArray = (TextView) findViewById(R.id.AlphaArray);
                                alphaArray.setText(alpha.size()+"");
                            }
                        });
                    }
                }
                case BETA_ABSOLUTE:{
                    ArrayList<Double> values = museDataPacket.getValues();
                    Double avg = new Double(0);
                    for(int i = 0; i < values.size(); i++)
                        avg += values.get(i);
                    avg = avg/values.size();
                    long time = System.currentTimeMillis();
                    WaveMagnitude waveObj = new WaveMagnitude(avg, time);
                    beta.add(0,waveObj);

                    while(waveObj.getTimestamp()-beta.get(beta.size()-1).getTimestamp() > minute)
                        beta.remove(beta.size() - 1);

                }
            }
        }

        @Override
        public void receiveMuseArtifactPacket(MuseArtifactPacket museArtifactPacket) {
            final boolean clench = museArtifactPacket.getJawClench();
            Activity activity = activityRef.get();
            final ArrayList<Clench> clenches = new ArrayList<Clench>();

            if (clench) {
                clenches.add(new Clench(new Date(), 1));
            } else {
                clenches.add(new Clench(new Date(), 0));
            }

            /*
            if(activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            */
        }



    }

    private final long minute = 60000;
    private Button button;
    private ConnectionListener connectionListener = null;
    private DataListener dataListener = null;
    private Muse muse = null;
    private boolean dataTransmission = true;
    private ArrayList<WaveMagnitude> alpha = null;
    private ArrayList<WaveMagnitude> beta = null;

    public MainActivity(){
        WeakReference<Activity> weakActivity =
                new WeakReference<Activity>(this);

        connectionListener = new ConnectionListener(weakActivity);
        dataListener = new DataListener(weakActivity);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alpha = new ArrayList<WaveMagnitude>();
        beta = new ArrayList<WaveMagnitude>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void detect(View view) {

        button = (Button) view;
        TextView ConStat = (TextView) findViewById(R.id.ConStat);

        MuseManager.refreshPairedMuses();
        List<Muse> muses = MuseManager.getPairedMuses();

        if(muses.size() > 0) {
            muse = muses.get(0);
            registerListeners();

            try {
                muse.runAsynchronously();
            } catch (Exception e) {
                Log.e("Muse Headband", e.toString());
            }
//            button.setEnabled(false);
//            button.setVisibility(View.INVISIBLE);

            ConStat.setText(muse.getConnectionState().toString());
//            Intent intent = new Intent(this, Scan.class);
//            startActivity(intent);
        }
        else{

            button.setText("you suck");
        }



    }



    private void registerListeners() {
        muse.registerConnectionListener(connectionListener);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.ACCELEROMETER);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.EEG);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.ALPHA_ABSOLUTE);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.ARTIFACTS);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.BATTERY);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(dataTransmission);
    }


}
