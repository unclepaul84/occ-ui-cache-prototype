package com.unclepaul.uicacheprototype.utils;

public class OperationTimer implements  AutoCloseable{

    private final String _title;
    private final long _start;

    public OperationTimer(String title)
    {

        _title = title;
        _start = System.currentTimeMillis();
    }

    @Override
    public void close() throws Exception {

        var finish =  System.currentTimeMillis();

        System.out.println(_title + " :" + Long.toString(finish - _start) + "ms");
    }
}
