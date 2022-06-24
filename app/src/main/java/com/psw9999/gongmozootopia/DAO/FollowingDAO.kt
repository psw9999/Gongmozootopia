package com.psw9999.gongmozootopia.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.psw9999.gongmozootopia.data.FollowingResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowingDAO {
    // 팔로잉 공모주 추가
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addStock(followingResponse : FollowingResponse)

    // 모든 팔로잉 데이터 가져오기
    @Query("SELECT ipoIndex FROM stock_following")
    fun getFollowingList() : List<Long>

    // 모든 팔로잉 데이터 가져오기
    @Query("SELECT ipoIndex FROM stock_following")
    fun getFollowingListFlow() : Flow<List<Long>>

    // 특정 팔로잉 데이터 가져오기
    @Query("SELECT is_following FROM stock_following WHERE ipoIndex = :ipoIndex")
    fun getFollowing(ipoIndex : Long) : Boolean

    // 특정 팔로잉 데이터 가져오기
    @Query("SELECT is_following FROM stock_following WHERE ipoIndex = :ipoIndex")
    fun getFollowingFlow(ipoIndex : Long) : Flow<Boolean>

    // 특정 팔로잉 데이터 업데이트
    @Query("UPDATE stock_following SET is_following = :isFollowing WHERE ipoIndex = :ipoIndex")
    fun updateStockFollowing(ipoIndex : Long, isFollowing : Boolean)

    // 인덱스 번호를 통해 팔로잉을 해제한다.
    @Query("DELETE FROM stock_following WHERE ipoIndex = :ipoIndex")
    fun deleteStock(ipoIndex : Long)
}