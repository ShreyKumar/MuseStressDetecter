package com.musestressband.musestressband;

import java.util.Date;

/**
 * Created by Shreyansh on 2015-06-07.
 */
public class Clench {
    private long timestamp;
    private Integer isClench;

    public Clench(long timestamp, Integer isClench) {
        this.timestamp = timestamp;
        this.isClench = isClench;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Integer getIsClench() {
        return isClench;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public void setIsClench(Integer isClench) {
        this.isClench = isClench;
    }
}
