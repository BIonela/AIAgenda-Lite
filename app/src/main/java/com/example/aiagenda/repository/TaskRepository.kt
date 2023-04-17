package com.example.aiagenda.repository

import android.util.Log
import com.example.aiagenda.model.Task
import com.example.aiagenda.model.TaskBody
import com.example.aiagenda.model.Timetable
import com.example.aiagenda.model.User
import com.example.aiagenda.util.FireStoreCollection
import com.example.aiagenda.util.UiStatus
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class TaskRepository(
    private val database: FirebaseFirestore
) {

    fun getTasks(user: User, result: (TaskBody) -> Unit, uiStatus: (UiStatus) -> Unit) {
        val docRef =
            database.collection("task").document(user.id)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->

                val item = documentSnapshot.toObject<TaskBody>()
                Log.e("repo", item.toString())
                if (item != null) {
                    result.invoke(item)
                    uiStatus.invoke(UiStatus.SUCCESS)
                }

            }
            .addOnFailureListener {
                uiStatus.invoke(UiStatus.ERROR)
            }
    }

    fun deleteTask(user: User, task: Task, uiState: (UiStatus) -> Unit) {
        val documentRef = database.collection("task").document(user.id)
        documentRef.update("tasks", FieldValue.arrayRemove(task))
            .addOnSuccessListener {
                uiState.invoke(UiStatus.SUCCESS)
            }
            .addOnFailureListener {
                uiState.invoke(UiStatus.ERROR)
            }
    }


}