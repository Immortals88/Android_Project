package com.bytedance.androidcamp.network.dou.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserEntityDao {

    @Query("select * from star")
    List<UserEntity> getUserList();

    @Query("select * from star where name Like :user_name")
    boolean isInStarList(String user_name);

    @Query("select * from star where name Like :user_name")
    UserEntity entityInList(String user_name);

    @Insert
    void add(UserEntity userEntity);

    @Delete
    void delete(UserEntity userEntity);

}
