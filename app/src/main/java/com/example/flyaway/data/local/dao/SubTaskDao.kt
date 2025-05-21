package com.example.flyaway.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flyaway.data.local.entity.SubTaskEntity

@Dao
interface SubTaskDao {
    @Query("SELECT * FROM subtasks WHERE parentTaskId = :taskId")
    suspend fun getSubTasksForTask(taskId: Int): List<SubTaskEntity>

    @Insert
    suspend fun addSubTask(subTask: SubTaskEntity): Long

    @Query("DELETE FROM subtasks WHERE id = :subTaskId")
    suspend fun deleteSubTask(subTaskId: Int)

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)
}
