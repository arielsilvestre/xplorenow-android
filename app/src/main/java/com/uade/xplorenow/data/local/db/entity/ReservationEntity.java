package com.uade.xplorenow.data.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reservations")
public class ReservationEntity {

    @PrimaryKey
    @NonNull
    public String id = "";

    public String activityId;
    public String activityName;
    public String date;
    public int people;
    public String status;
    public long savedAt;
}
