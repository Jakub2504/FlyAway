package com.example.flyaway.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.SubTask
import com.example.flyaway.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Se espera que "taskId" sea pasado en la navegación
    val taskId: Int = savedStateHandle["taskId"] ?: 0

    private val _subTasks = mutableStateListOf<SubTask>()
    val subTasks: List<SubTask> get() = _subTasks

    init {
        loadSubTasks()
    }

    private fun loadSubTasks() {
        _subTasks.clear()
        viewModelScope.launch {
            _subTasks.addAll(repository.getSubTasksForTask(taskId))
        }
    }

    fun addSubTask(subTask: SubTask) {
        viewModelScope.launch {
            repository.addSubTask(subTask)
            loadSubTasks()
        }
    }

    fun deleteSubTask(subTaskId: Int) {
        viewModelScope.launch {
            repository.deleteSubTask(subTaskId)
            loadSubTasks()
        }
    }

    fun updateSubTask(updatedSubTask: SubTask) {
        viewModelScope.launch {
            repository.updateSubTask(updatedSubTask)
            loadSubTasks()
        }
    }
}