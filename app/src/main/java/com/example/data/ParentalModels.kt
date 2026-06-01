package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val avatarEmoji: String, // Emoji representation instead of complex images
    val isDevicePaused: Boolean = false,
    val deviceModel: String,
    val batteryPercent: Int = 85,
    val isOnline: Boolean = true
)

@Entity(tableName = "screen_time_configs")
data class ScreenTimeConfig(
    @PrimaryKey val childId: Long,
    val dailyLimitMinutes: Int = 120,
    val usedMinutes: Int = 45,
    val bedtimeStartHour: Int = 21, // 9 PM
    val bedtimeStartMinute: Int = 0,
    val bedtimeEndHour: Int = 7, // 7 AM
    val bedtimeEndMinute: Int = 0
)

@Entity(tableName = "app_rules")
data class AppRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val appName: String,
    val packageName: String,
    val isBlocked: Boolean = false,
    val limitMinutes: Int = -1 // -1 means no limit
)

@Entity(tableName = "category_filters")
data class CategoryFilter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val categoryName: String,
    val isBlocked: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val type: String, // "WEB", "APP", "SEARCH", "SYSTEM"
    val detailText: String, // e.g. "Accessed roblox.com" or "Searched 'how to program in Kotlin'"
    val category: String, // e.g. "Gaming", "Education", "Social Media", "Violence"
    val isBlocked: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "child_locations")
data class ChildLocation(
    @PrimaryKey val childId: Long,
    val latitude: Double,
    val longitude: Double,
    val addressName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "coach_messages")
data class CoachMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user", "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "parent_users")
data class ParentUser(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val name: String = "Parent"
)

