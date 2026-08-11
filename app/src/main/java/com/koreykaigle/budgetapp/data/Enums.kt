package com.koreykaigle.budgetapp.data

/** How often a line item recurs. Nothing here is mandatory to pick — null means "not set". */
enum class Frequency(val label: String, val perYear: Double) {
    ONE_TIME("One-time", 0.0),
    WEEKLY("Weekly", 52.0),
    BIWEEKLY("Every 2 weeks", 26.0),
    SEMI_MONTHLY("Twice a month", 24.0),
    MONTHLY("Monthly", 12.0),
    QUARTERLY("Quarterly", 4.0),
    SEMI_ANNUALLY("Twice a year", 2.0),
    ANNUALLY("Annually", 1.0)
}

/** User-defined categories are tagged with one of these buckets so the app knows where to use them. */
enum class CategoryType(val label: String) {
    INCOME("Income"),
    EXPENSE("Expense"),
    DEDUCTION("Deduction")
}

/** Whether an account adds to or subtracts from net worth. */
enum class AccountKind(val label: String) {
    ASSET("Asset (savings, retirement, investment...)"),
    LIABILITY("Liability (debt, loan, credit card...)")
}

/** What kind of record a custom field is attached to. */
enum class OwnerType {
    INCOME, EXPENSE, DEDUCTION, ACCOUNT
}

enum class BudgetPeriod(val label: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    ANNUALLY("Annually")
}
