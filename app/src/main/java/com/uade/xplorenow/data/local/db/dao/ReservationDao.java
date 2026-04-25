package com.uade.xplorenow.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.uade.xplorenow.data.local.db.entity.ReservationEntity;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReservationEntity> reservations);

    @Query("SELECT * FROM reservations ORDER BY savedAt DESC")
    List<ReservationEntity> getAll();

    @Query("SELECT * FROM reservations WHERE id = :id LIMIT 1")
    ReservationEntity getById(String id);

    @Query("DELETE FROM reservations")
    void deleteAll();
}
