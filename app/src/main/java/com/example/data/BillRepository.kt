package com.example.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Serializable
data class DomainBill(
    val id: String,
    val title: String,
    val category: String,
    val amount: Double,
    val currency: String,
    val date: Long,
    val paidBy: String,
    val participants: List<Participant>,
    val splitType: SplitType,
    val splitData: Map<String, Double>, // Participant ID to amount/percent/share
    val isFavorite: Boolean
)

fun DomainBill.toEntity(): BillEntity {
    return BillEntity(
        id = id,
        title = title,
        category = category,
        amount = amount,
        currency = currency,
        date = date,
        paidBy = paidBy,
        participantsJson = Json.encodeToString(participants),
        splitType = splitType.name,
        splitDataJson = Json.encodeToString(splitData),
        isFavorite = isFavorite
    )
}

fun BillEntity.toDomain(): DomainBill {
    return DomainBill(
        id = id,
        title = title,
        category = category,
        amount = amount,
        currency = currency,
        date = date,
        paidBy = paidBy,
        participants = Json.decodeFromString(participantsJson),
        splitType = SplitType.valueOf(splitType),
        splitData = Json.decodeFromString(splitDataJson),
        isFavorite = isFavorite
    )
}

class BillRepository(private val dao: BillDao) {
    val allBills: Flow<List<DomainBill>> = dao.getAllBills().map { it.map { entity -> entity.toDomain() } }
    val favoriteBills: Flow<List<DomainBill>> = dao.getFavoriteBills().map { it.map { entity -> entity.toDomain() } }

    suspend fun getBillById(id: String): DomainBill? {
        return dao.getBillById(id)?.toDomain()
    }

    suspend fun insertBill(bill: DomainBill) {
        dao.insertBill(bill.toEntity())
    }

    suspend fun deleteBill(id: String) {
        dao.deleteBillById(id)
    }
    
    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }
}
