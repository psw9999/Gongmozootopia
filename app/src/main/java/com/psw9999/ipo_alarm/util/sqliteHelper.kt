package com.psw9999.ipo_alarm.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.psw9999.ipo_alarm.data.StockListResponse

class sqliteHelper(context: Context, name : String, version : Int) : SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val create = "create table stockDB (" +
                "ipoIndex integer primary key, " +
                "isFollowing integer, " +
                "isAlarm integer" +
                ")"
        db?.execSQL(create)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertSQLiteDB(stockData: StockListResponse) {
        val values = ContentValues()
        values.put("ipoIndex",stockData.ipoIndex)
        when (stockData.isFollowing) {
            null -> {
                values.put("isFollowing",0)
            }
            false -> {
                values.put("isFollowing",0)
            }
            else -> {
                values.put("isFollowing",1)
            }
        }
        when (stockData.isAlarm) {
            null -> {
                values.put("isAlarm",0)
            }
            false -> {
                values.put("isAlarm",0)
            }
            else -> {
                values.put("isAlarm",1)
            }
        }
        val wd = writableDatabase
        wd.insert("stockDB",null,values)
        wd.close()
    }

    fun selectSQLiteDB(stockDatas : ArrayList<StockListResponse>) : ArrayList<StockListResponse> {
        val select = "select * from stockDB"
        val rd = readableDatabase

        var cursor = rd.rawQuery(select,null)

        while(cursor.moveToNext()) {
            val ipoIndex = cursor.getLong(cursor.getColumnIndex("ipoIndex"))
            val isFollowing = cursor.getInt(cursor.getColumnIndex("isFollowing")) > 0
            val isAlarm = cursor.getInt(cursor.getColumnIndex("isAlarm")) > 0

            for(stockData in stockDatas) {
                if (stockData.ipoIndex == ipoIndex) {
                    stockData.isFollowing = isFollowing
                    stockData.isAlarm = isAlarm
                    break
                }
            }
        }
        cursor.close()
        rd.close()

        return stockDatas
    }

    fun updateSQLiteDB(stockData : StockListResponse) {
        val values = ContentValues()
        values.put("isFollowing",stockData.isFollowing)
        values.put("isAlarm",stockData.isAlarm)

        val wd = writableDatabase
        wd.update("stockDB",values,"ipoIndex = ${stockData.ipoIndex}",null)
        wd.close()
    }

    fun updateSQLiteDB(stockIndex : Long, isFollowing : Boolean, isAlarm : Boolean) {
        val values = ContentValues()
        values.put("isFollowing",isFollowing)
        values.put("isAlarm",isAlarm)

        val wd = writableDatabase
        wd.update("stockDB",values,"ipoIndex = $stockIndex",null)
        wd.close()
    }

}