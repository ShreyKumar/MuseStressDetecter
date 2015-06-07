package com.musestressband.musestressband;

import java.io.Console;
import java.util.List;
import java.lang.ref.WeakReference;

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

    Button button;
    private ConnectionListener connectionListener = null;
    private Muse muse = null;
    private boolean dataTransmission = true;


    public MainActivity(){
        WeakReference<Activity> weakActivity =
                new WeakReference<Activity>(this);

        connectionListener = new ConnectionListener(weakActivity);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    class ConnectionListener extends MuseConnectionListener{

        final WeakReference<Activity> activityRef;

        ConnectionListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(MuseConnectionPacket museConnectionPacket) {


            TextView ConStat = (TextView) findViewById(R.id.ConStat);

            String ConnectionStatus = museConnectionPacket.getCurrentConnectionState().toString();

            ConStat.setText(ConnectionStatus);

        }
    }

    private void registerListeners() {
        muse.registerConnectionListener(connectionListener);
//        muse.registerDataListener(dataListener,
//                MuseDataPacketType.ACCELEROMETER);
//        muse.registerDataListener(dataListener,
//                MuseDataPacketType.EEG);
//        muse.registerDataListener(dataListener,
//                MuseDataPacketType.ALPHA_RELATIVE);
//        muse.registerDataListener(dataListener,
//                MuseDataPacketType.ARTIFACTS);
//        muse.registerDataListener(dataListener,
//                MuseDataPacketType.BATTERY);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(dataTransmission);
    }
}
