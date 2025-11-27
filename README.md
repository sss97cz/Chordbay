# Chordbay

Chordbay is an open‑source songbook mobile app built with Kotlin and Jetpack Compose for musicians who want a simple, flexible way to organize, view, and play songs with chords and lyrics. The main focus is to provide a free, offline‑first personal songbook.

> **Tech stack:** Kotlin · Jetpack Compose · Room · Android  
> **Key features:** Offline songbook · Chord transposition · Playlists · Sharing & sync

---
## Screenshots
<img width="300" height="622" alt="image" src="https://github.com/user-attachments/assets/21cbff75-517a-413e-8335-a281ab9dfbda" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="300" height="622" alt="image" src="https://github.com/user-attachments/assets/d3b95a76-8e68-4c8e-9132-011818125679" />




## ✨ Features
### 🎵 Song management

- Local song library
- Song editor
- Store chords and lyrics together in a clean format
- Organize songs by:
  - Title
  - Artist
- Fast song browsing and search

### 📚 Offline‑first songbook

- All songs are stored locally using **Room** (SQLite)
- Works completely offline by default
- Your library is always available – no network required

### 👤 User accounts & sharing

- Optional user accounts for sync and backup
- Spring Boot backend written in Kotlin
- Mark songs as:
  - **Private** – only visible to you, used for personal sync across your devices
  - **Public** – share songs with other Chordbay users

### 📂 Import & export

- Import songs from plain **.txt** files
- Export songs to text for:
  - Backups
  - Editing on desktop

### 🎛 Chord tools

- **Chord transposition**:
  - Quickly transpose a song to any key
  - Preserve chord structure while adjusting lyrics layout
- Highlighting chords in songs
- Support for English (Bb/B) and German (B,H) chord format

### 🎧 Playlists & sets

- Build **playlists** / setlists from your songs
- Useful for:
  - Live gigs
  - Rehearsal sets
  - Practice sessions

### 🧱 Built with modern Android

- **Kotlin** throughout
- **Jetpack Compose** for UI:
  - Declarative UI
  - Theming and dark mode friendly
- **Room** for persistence and structured querying
- **Koin** for managing dependencies
- **MVVM** for better code management
- Follows modern Android app architecture best practices

---

## 🚀 Getting started (development)

> This section assumes you are familiar with Android development and Android Studio.

### Prerequisites

- Android Studio
- Android SDK + required build tools

### Clone the repository

```bash
git clone https://github.com/sss97cz/Chordbay.git
cd Chordbay
```

### Open in Android Studio

1. Open **Android Studio**
2. Select **“Open an existing project”**
3. Choose the `Chordbay` folder
4. Let Gradle sync finish

### Run the app
1. Connect a physical Android device or start an emulator (Make sure you have enabled developer mode https://developer.android.com/studio/debug/dev-options)
2. Press **Run ▶** in Android Studio
3. The app should install and launch automatically

---

## 🧩 Project structure

> This is a high‑level overview. Package names may differ slightly in the actual project.




For exact structure, browse the source in this repository.

---

## 💡 Roadmap ideas

Some possible future improvements:
- Publishing the app on Google Play (in process, if you want to help with testing you can contact me at dev@chordbay.eu)
- Move the project to KMP (Kotlin Multiplatform) for desktop and browser.
- Improve the chord detection algorithm.

If you have suggestions or want to help with any of these, contributions are welcome.

---

## 🤝 Contributing

Contributions, bug reports, and feature requests are welcome.

---

## 📝 License

This project is open source. See the [LICENSE](./LICENSE) file for details.

---

