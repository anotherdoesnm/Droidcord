package com.atipls.chat.model;

public class Snowflake {
    public static final long EPOCH = 1420070400000L;

    public long id;

    public Snowflake(long snowflake) {
        id = snowflake;
    }
}
