package com.example.aiagenda.repository

import androidx.core.net.toUri
import com.example.aiagenda.model.Task
import com.example.aiagenda.model.User
import com.example.aiagenda.util.UiStatus
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class CreateTaskRepository(
    private val database: FirebaseFirestore,
    private val storageFirebase: StorageReference
) {

    fun addTask(user: User, task: Task, photoUri: String, uiState: (UiStatus) -> Unit) {

        val documentRef = database.collection("task").document(user.id)

        if (photoUri != "") {
            val imageRef =
                storageFirebase.child("images/tasks/" + user.id + task.id)
            val uploadTask = imageRef.putFile(photoUri.toUri())
            uploadTask.addOnSuccessListener {
                val downloadUrl = imageRef.downloadUrl
                downloadUrl.addOnSuccessListener {
                    task.photoUrl = it.toString()
                    documentRef.update("tasks", FieldValue.arrayUnion(task))
                        .addOnSuccessListener {
                            uiState.invoke(UiStatus.SUCCESS)
                        }
                        .addOnFailureListener {
                            uiState.invoke(UiStatus.ERROR)
                        }
                }
                downloadUrl.addOnFailureListener {
                    uiState.invoke(UiStatus.ERROR)
                }
            }
        } else {
            documentRef.update("tasks", FieldValue.arrayUnion(task))
                .addOnSuccessListener {
                    uiState.invoke(UiStatus.SUCCESS)
                }
                .addOnFailureListener {
                    uiState.invoke(UiStatus.ERROR)
                }
        }
    }

}
