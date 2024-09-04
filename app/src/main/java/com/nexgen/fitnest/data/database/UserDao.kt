package com.nexgen.fitnest.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun loginUser(phoneNumber: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun isPhoneNumberTaken(phoneNumber: String): Int

    @Query("SELECT phoneNumber FROM users LIMIT 1") // Add this query to fetch the phone number
    suspend fun getUserPhoneNumber(): String?

    @Query("DELETE FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun deleteUser(phoneNumber: String)
}


