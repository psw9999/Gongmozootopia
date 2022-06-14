package com.psw9999.gongmozootopia.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.psw9999.gongmozootopia.DAO.FollowingDAO
import com.psw9999.gongmozootopia.data.FollowingResponse

@Database(entities = [FollowingResponse::class], version = 1, exportSchema = false)
abstract class StockDatabase : RoomDatabase() {

    abstract fun stockFollowingDAO() : FollowingDAO

    companion object{
        /* @Volatile = 접근가능한 변수의 값을 cache를 통해 사용하지 않고
        thread가 직접 main memory에 접근 하게하여 동기화. */

        @Volatile
        private var INSTANCE : StockDatabase? = null

        fun getDatabase(context: Context) : StockDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_following"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}