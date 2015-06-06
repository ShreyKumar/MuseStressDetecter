package com.musestressband.musestressband;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.interaxon.libmuse.Muse;
import com.interaxon.libmuse.MuseManager;

import java.util.List;


public class Detect extends Activity {

    List<Muse> pairedDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        //create a new MuseManager
        pairedDevices = MuseManager.getPairedMuses();
        String var;
        if(pairedDevices.size() >= 1) {
            var = "yes";
        } else {
            var = "no";
        }

        Log.i("connected", var);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detect, menu);
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

    public boolean isConnected() {
        return pairedDevices.size() >= 1;
    }
}
