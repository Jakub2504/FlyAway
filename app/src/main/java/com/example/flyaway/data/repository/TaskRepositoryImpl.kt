package com.example.flyaway.data.repository

import com.example.flyaway.data.local.dao.SubTaskDao
import com.example.flyaway.data.local.dao.TaskDao
import com.example.remotepersistence.data.local.mapper.toEntity
import com.example.remotepersistence.data.local.mapper.toDomain
import com.example.flyaway.domain.model.SubTask
import com.example.flyaway.domain.model.Task
import com.example.flyaway.domain.repository.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) : TaskRepository {

    // Listas mutables para almacenar datos en memoria
    //private val tasks = mutableListOf<Task>()
    //private val subTasks = mutableListOf<SubTask>()

    override suspend fun getTasks(): List<Task> {
        val taskEntities = taskDao.getTasks()
        // Podríamos obtener todas las subtasks de golpe para optimizar
        // y luego agruparlas, pero para el ejemplo haremos una consulta por tarea.
        return taskEntities.map { taskEntity ->
            val subs = subTaskDao.getSubTasksForTask(taskEntity.id).map { it.toDomain() }
            taskEntity.toDomain(subs)
        }
    }

    override suspend  fun addTask(task: Task) {
        // Insertar la tarea en la DB
        taskDao.addTask(task.toEntity())
        // SubTasks se gestionan por separado si fuera necesario
    }

    override suspend  fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
        // Por la ForeignKey con onDelete = CASCADE,
        // las subtareas también se borran automáticamente
    }

    override suspend  fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend  fun getSubTasksForTask(taskId: Int): List<SubTask> {
        return subTaskDao.getSubTasksForTask(taskId).map { it.toDomain() }
    }

    override suspend fun addSubTask(subTask: SubTask) {
        subTaskDao.addSubTask(subTask.toEntity())
    }

    override suspend fun deleteSubTask(subTaskId: Int) {
        subTaskDao.deleteSubTask(subTaskId)
    }

    override suspend fun updateSubTask(subTask: SubTask) {
        subTaskDao.updateSubTask(subTask.toEntity())
    }
}