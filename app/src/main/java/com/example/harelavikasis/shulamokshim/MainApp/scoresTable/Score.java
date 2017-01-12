package com.example.harelavikasis.shulamokshim.MainApp.scoresTable;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harelavikasis on 09/12/2016.
 */

public class Score implements Comparable{
    private int timeRecord;
    private Date date;
    private String name;
    private LatLng location;

    public Score(int timeRecord, Date date, String name,LatLng location) {
        this.timeRecord = timeRecord;
        this.date = date;
        this.name = name;
        this.location = location;
    }

    public Score(Score score){
        this.name = score.getName();
        this.date = score.getDate();
        this.timeRecord = score.getTimeRecord();
        this.location = score.getLocation();
    }



    public int getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(int timeRecord) {
        this.timeRecord = timeRecord;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }


    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        return dateFormat.format(date)  + " Name: " + name + " time: " + timeRecord;
    }

    @Override
    public int compareTo(Object o) {
        if (this.timeRecord > ((Score)o).getTimeRecord()) return 1;
        else if (this.timeRecord < ((Score)o).getTimeRecord()) return -1;
        return 0;
    }
}
