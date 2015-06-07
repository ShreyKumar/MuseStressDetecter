package com.musestressband.musestressband;

/**
 * Created by Fred on 2015-06-07.
 */
public class WaveMagnitude {
    public double avg;
    public long timestamp;

    public WaveMagnitude(double avg, long timestamp){
        this.avg = avg;
        this.timestamp = timestamp;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAvg() {

        return avg;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
