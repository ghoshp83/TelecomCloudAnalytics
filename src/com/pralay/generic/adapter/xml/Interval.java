package com.pralay.generic.adapter.xml;


/**
 * Created by edmyvar on 10/20/2015.
 */
public enum Interval {
    IVL_1_MIN(0, 60 * 1000L), IVL_5_MIN(1, 5 * 60 * 1000L), IVL_15_MIN(2, 15 * 60 * 1000L), IVL_30_MIN(3,
            30 * 60 * 1000L), IVL_1_HOUR(4, 1 * 60 * 60 * 1000L), IVL_1_DAY(5, 24 * 60 * 60 * 1000L);

    private long milliSeconds;

    Interval(int id, long milliSeconds) {
        this.id = (short) id;
        this.milliSeconds = milliSeconds;
    }

    public long getMilliSeconds() {
        return milliSeconds;
    }

    private short id;
}
