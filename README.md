# Lumera - College Attendance Tracker & Bunk Calculator

<div align="center">

<img src="helper/Lumera.png" alt="Lumera Logo" width="120" height="120" />

**Track classes. Plan bunks. Attend smartly.**

A free, open-source attendance tracker for Android. No account, no ads, all data stays on your device.

</div>

---

## Features

- **Attendance tracking** - one-tap marking per class, with support for multi-hour classes counted as multiple units
- **Safe bunk calculator** - see exactly how many classes you can skip and still stay above your target (e.g. 75%)
- **Recovery mode** - fell below target? Get the exact number of classes to attend in a row to climb back
- **What-if simulator** - preview the impact of skipping before you decide
- **Weekly timetable** - recurring classes with rooms, teachers, extra classes, and rescheduling (past records stay intact)
- **Timetable scanner** - import from a photo; works fully offline on-device, optional AI enhancement with your own free Gemini API key
- **Class reminders** - notifications before class with one-tap mark present/absent, plus nudges for unmarked classes
- **Home-screen widget** - your attendance %, safe bunks, and next class at a glance
- **Planner** - assignments with due-soon reminders and exam countdowns with attendance-eligibility warnings
- **Backups** - automatic daily local backups, JSON/CSV export and import, Android cloud backup support

---

## Installation

1. Go to the [Releases](https://github.com/alzimerahmed/Lectura-attendance-app/releases) page.
2. Download the latest APK.
3. Install it (requires Android 7.0+).

### Build from source

Requires Android Studio and JDK 17.

```bash
git clone https://github.com/alzimerahmed/Lectura-attendance-app.git
cd Lectura-attendance-app
./gradlew assembleDebug
```

---

## How the math works

- **Attendance %** = present units / conducted units x 100 (cancelled classes don't count)
- **Safe bunks** = the most classes you can miss before dropping below your target
- **Recovery** = how many classes in a row you must attend to reach your target again

---

## Tech stack

Kotlin - Jetpack Compose (Material 3) - Room - DataStore - WorkManager + AlarmManager - Hilt - ML Kit (on-device OCR)

---

## Privacy

No accounts, no analytics, no tracking. Everything stays on your device. Optional AI timetable scanning uses your own Gemini API key and sends images directly to Google - never to us.

---

## License

Licensed under the [GNU GPL v3](LICENSE).

(c) alzimer ahmed - [github.com/alzimerahmed](https://github.com/alzimerahmed)
