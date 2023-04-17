package com.example.aiagenda.repository

import com.example.aiagenda.model.Course
import com.example.aiagenda.model.Timetable
import com.example.aiagenda.model.TimetableTime
import com.example.aiagenda.model.User
import com.example.aiagenda.util.FireStoreCollection
import com.example.aiagenda.util.UiStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class TimetableRepository(
    private val database: FirebaseFirestore
) {

    fun getCourses(user: User, result: (Timetable) -> Unit, uiState: (UiStatus) -> Unit) {
        val studyYear = user.study_year
        val docRef =
            database.collection(FireStoreCollection.TIMETABLE).document("year${studyYear}")
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val document = documentSnapshot.toObject<Timetable>()
                if (document != null) {
                    result.invoke(document)
                    uiState.invoke(UiStatus.SUCCESS)
                }
            }
            .addOnFailureListener {
                uiState.invoke(UiStatus.ERROR)
            }
    }

    fun getTimetableTime(
        result: (Map<String, TimetableTime>) -> Unit,
        uiState: (UiStatus) -> Unit
    ) {
        val docRef =
            database.collection(FireStoreCollection.TIMETABLE)
        docRef.get()
            .addOnSuccessListener { document ->
                val timeTimetable = mutableMapOf<String, TimetableTime>()
                for (item in document) {
                    val time = item.toObject<TimetableTime>()
                    timeTimetable[item.id] = time
                }
                result.invoke(timeTimetable)
                uiState.invoke(UiStatus.SUCCESS)
            }
            .addOnFailureListener {
                uiState.invoke(UiStatus.ERROR)
            }
    }

    fun getGroupCoursesByWeek(
        user: User,
        groupName: String,
        isOdd: String,
        result: (ArrayList<Course>) -> Unit,
        uiState: (UiStatus) -> Unit
    ) {
        val studyYear = user.study_year
        val docRef = database.collection(FireStoreCollection.TIMETABLE).document("year${studyYear}")
            .collection("group${groupName}${isOdd}")
        docRef
            .get()
            .addOnSuccessListener { document ->
                val groupCourses = arrayListOf<Course>()
                for (item in document) {
                    val time = item.toObject<Course>()
                    groupCourses.add(time)
                }
                result.invoke(groupCourses)
                uiState.invoke(UiStatus.SUCCESS)
            }
            .addOnFailureListener {
                uiState.invoke(UiStatus.ERROR)
            }
    }

    fun getGroupCourses(
        user: User,
        groupName: String,
        result: (ArrayList<Course>) -> Unit,
        uiState: (UiStatus) -> Unit
    ) {
        val studyYear = user.study_year
        val docRef = database.collection(FireStoreCollection.TIMETABLE).document("year${studyYear}")
            .collection("group${groupName}")
        docRef
            .get()
            .addOnSuccessListener { document ->
                val groupCourses = arrayListOf<Course>()
                for (item in document) {
                    val time = item.toObject<Course>()
                    groupCourses.add(time)
                }
                result.invoke(groupCourses)
                uiState.invoke(UiStatus.SUCCESS)
            }
            .addOnFailureListener {
                uiState.invoke(UiStatus.SUCCESS)
            }
    }

}