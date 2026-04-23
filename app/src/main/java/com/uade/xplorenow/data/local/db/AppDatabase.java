package com.uade.xplorenow.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.uade.xplorenow.data.local.db.dao.ReservationDao;
import com.uade.xplorenow.data.local.db.entity.ReservationEntity;

@Database(entities = {ReservationEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReservationDao reservationDao();
}
