package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val id: String,
    val name: String
)

enum class SplitType { EQUAL, EXACT, PERCENTAGE, SHARES }

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val amount: Double,
    val currency: String,
    val date: Long,
    val paidBy: String,
    val participantsJson: String,
    val splitType: String,
    val splitDataJson: String,
    val isFavorite: Boolean
)
