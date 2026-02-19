package org.bosf.pooch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.bosf.pooch.data.local.entities.ScanHistory

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history WHERE dogId = :dogId ORDER BY createdAt DESC")
    fun getScanHistoryForDog(dogId: String): Flow<List<ScanHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScans(scans: List<ScanHistory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistory)

    @Query("DELETE FROM scan_history WHERE dogId = :dogId")
    suspend fun clearHistoryForDog(dogId: String)
}