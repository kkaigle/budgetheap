# Budget — a fully customizable Android budgeting app

Kotlin + Jetpack Compose + Room. Everything is local-only (no accounts, no
backend, no data leaves the device).

## What's in it

Five tabs, bottom navigation:

- **Expenses** — add any expense with just a name and amount. Category,
  frequency, notes, and any number of custom fields (vendor, due day,
  payment method, anything) are optional.
- **Income** — same idea, for income sources.
- **Accounts** — two sub-tabs:
  - *Accounts*: savings, retirement, investment, or debt accounts. Track a
    balance, an optional recurring contribution, and an optional
    interest/growth rate.
  - *Deductions*: recurring paycheck deductions (taxes withheld, pre-tax
    retirement, insurance premiums) tracked separately so gross → net income
    math is accurate.
- **Budget** — a slider per category you've created, comparing your planned
  monthly total against the target you set.
- **Health** — the reporting dashboard: net worth, monthly cash flow
  (gross income → deductions → net income → expenses), savings rate,
  spending/income breakdowns by category, and budget adherence.

Nothing is preloaded. There are no default categories or required fields
beyond a name and an amount on any entry — you build the structure that
fits how you actually manage money.

`ExpenseEntry` and `DeductionEntry` also carry a `linkedAccountId` field,
unused for now — it's there so a future "add a precanned way to save"
feature can turn an expense line item into a savings contribution without a
schema change.

## Getting an installable APK

This project was written in an environment without the Android SDK, so it
couldn't be compiled here. Two ways to get an APK onto your phone:

### Option A — GitHub Actions (no Android Studio needed)

1. Push this folder to a new GitHub repo (public or private).
2. The included workflow (`.github/workflows/build-apk.yml`) runs
   automatically on push and builds a debug APK.
3. Open the **Actions** tab on your repo → the latest run → download the
   `budget-app-debug-apk` artifact → unzip it → you'll have `app-debug.apk`.
4. Transfer that APK to your Android phone (email, Drive, USB) and open it
   to install. You'll need to allow "install from unknown sources" the
   first time.

### Option B — Android Studio

1. Install [Android Studio](https://developer.android.com/studio) (free).
2. Open this folder as a project. Studio will offer to regenerate the
   Gradle wrapper automatically (the binary wrapper jar isn't checked into
   this repo on purpose) — accept it, or just let Studio sync with its
   bundled Gradle.
3. Click Run to install straight onto a connected phone or emulator, or
   Build → Build Bundle(s)/APK(s) → Build APK(s) to get a file you can
   share.

Either path produces a debug-signed APK, installable on any device with
"unknown sources" allowed. It is not signed for the Play Store — that's a
separate step (a release keystore) you'd only need if you plan to publish it.

## Project structure

```
app/src/main/java/com/koreykaigle/budgetapp/
  data/            Room entities, DAOs, database, repository
  ui/
    theme/         Material3 theme
    navigation/     Bottom nav + NavHost
    common/         Shared editor UI (category picker, custom fields, line-item form)
    expenses/       Expenses tab
    income/         Income tab
    accounts/       Accounts + Deductions tab
    budget/         Budget sliders tab
    health/         Financial health dashboard
  util/            Currency formatting, frequency-to-monthly math
```

## Data model notes

- Every income/expense/deduction row only requires `name` + `amount`.
  Category, frequency, date, and notes are all nullable.
- Categories are created inline as you type them — nothing is predefined.
- `CustomField` is a free-form key/value table attached to any entry, so you
  can track anything the fixed columns don't cover.
- Frequencies (weekly, biweekly, monthly, etc.) are normalized to a monthly
  equivalent everywhere totals are computed, so items with different
  cadences can be compared and summed consistently.
