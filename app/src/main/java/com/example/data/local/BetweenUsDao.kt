package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MentalSpaceCheckInEntity
import com.example.data.model.MicroActionEntity
import com.example.data.model.ReflectionEntity
import com.example.data.model.ResponsibilityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BetweenUsDao {
    // --- Reflections ---
    @Query("SELECT * FROM reflections ORDER BY timestamp DESC")
    fun getAllReflections(): Flow<List<ReflectionEntity>>

    @Query("SELECT * FROM reflections WHERE id = :id LIMIT 1")
    suspend fun getReflectionById(id: Long): ReflectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: ReflectionEntity): Long

    @Delete
    suspend fun deleteReflection(reflection: ReflectionEntity)

    @Query("DELETE FROM reflections WHERE id = :id")
    suspend fun deleteReflectionById(id: Long)

    @Query("DELETE FROM reflections")
    suspend fun deleteAllReflections()

    // --- Responsibilities ---
    @Query("SELECT * FROM responsibilities ORDER BY pressureLevel DESC")
    fun getAllResponsibilities(): Flow<List<ResponsibilityEntity>>

    @Query("SELECT * FROM responsibilities WHERE ownerRole = :role")
    fun getResponsibilitiesByRole(role: String): Flow<List<ResponsibilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponsibility(item: ResponsibilityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponsibilities(items: List<ResponsibilityEntity>)

    @Delete
    suspend fun deleteResponsibility(item: ResponsibilityEntity)

    // --- Micro Actions ---
    @Query("SELECT * FROM micro_actions ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllMicroActions(): Flow<List<MicroActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicroAction(action: MicroActionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicroActions(actions: List<MicroActionEntity>)

    @Update
    suspend fun updateMicroAction(action: MicroActionEntity)

    @Query("UPDATE micro_actions SET isCompleted = :completed, completedAt = :completedAt WHERE id = :id")
    suspend fun toggleMicroAction(id: Long, completed: Boolean, completedAt: Long?)

    @Delete
    suspend fun deleteMicroAction(action: MicroActionEntity)

    // --- Check-Ins ---
    @Query("SELECT * FROM mental_space_checkins ORDER BY timestamp DESC LIMIT 30")
    fun getRecentCheckIns(): Flow<List<MentalSpaceCheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: MentalSpaceCheckInEntity): Long
}
