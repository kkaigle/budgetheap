package com.koreykaigle.budgetapp.data

import androidx.room.TypeConverter

/** Room stores enums as their name; null stays null so every field remains optional. */
class Converters {

    @TypeConverter
    fun fromFrequency(value: Frequency?): String? = value?.name

    @TypeConverter
    fun toFrequency(value: String?): Frequency? = value?.let { runCatching { Frequency.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromCategoryType(value: CategoryType?): String? = value?.name

    @TypeConverter
    fun toCategoryType(value: String?): CategoryType? = value?.let { runCatching { CategoryType.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromAccountKind(value: AccountKind?): String? = value?.name

    @TypeConverter
    fun toAccountKind(value: String?): AccountKind? = value?.let { runCatching { AccountKind.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromOwnerType(value: OwnerType?): String? = value?.name

    @TypeConverter
    fun toOwnerType(value: String?): OwnerType? = value?.let { runCatching { OwnerType.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod?): String? = value?.name

    @TypeConverter
    fun toBudgetPeriod(value: String?): BudgetPeriod? = value?.let { runCatching { BudgetPeriod.valueOf(it) }.getOrNull() }
}
