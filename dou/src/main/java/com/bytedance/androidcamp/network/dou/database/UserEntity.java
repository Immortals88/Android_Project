package com.bytedance.androidcamp.network.dou.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "star", indices = {@Index(value = {"name"}, unique = true)})
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int _id;

    @ColumnInfo(name = "name")
    private String name;

    public int get_id(){ return _id; }

    public void set_id(int _id) { this._id = _id; }

    public String getName() { return name; }

    public void setName(String content) { this.name = name; }

    public UserEntity(String name){
        this.name = name;
    }

}
