package com.musestressband.musestressband;

import java.util.Date;

/**
 * Created by Shreyansh on 2015-06-07.
 */
public class Clench {
    private final Date timestamp;
    private Integer isClench;

    public Clench(Date timestamp, Integer isClench) {
        this.timestamp = new Date();
        this.isClench = isClench;
    }
}
