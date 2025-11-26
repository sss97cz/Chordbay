# Chordbay

Chordbay is an open‑source songbook mobile app built with Kotlin and Jetpack Compose for musicians who want a simple, flexible way to organize, view, and play songs with chords and lyrics. The main focus is to provide a free, offline‑first personal songbook.

> **Tech stack:** Kotlin · Jetpack Compose · Room · Android  
> **Key features:** Offline songbook · Chord transposition · Playlists · Sharing & sync

---

## ✨ Features

### 🎵 Song management

- Create, read, update, and delete (CRUD) songs
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
- Designed for readability

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
- Follows modern Android app architecture best practices

---

## 🚀 Getting started (development)

> This section assumes you are familiar with Android development and Android Studio.

### Prerequisites

- Android Studio (Giraffe or newer recommended)
- JDK 17 (or version used in this project)
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

1. Connect a physical Android device or start an emulator
2. Press **Run ▶** in Android Studio
3. The app should install and launch automatically

---

## 🧩 Project structure

> This is a high‑level overview. Package names may differ slightly in the actual project.




For exact structure, browse the source in this repository.

---

## 💡 Roadmap ideas

Some possible future improvements:

- 

If you have suggestions or want to help with any of these, contributions are welcome.

---

## 🤝 Contributing

Contributions, bug reports, and feature requests are welcome.

1. Fork the repo
2. Create a feature branch:  
   `git checkout -b feature/my-feature`
3. Commit your changes:  
   `git commit -m "Add my feature"`
4. Push the branch:  
   `git push origin feature/my-feature`
5. Open a Pull Request on GitHub

---

## 📝 License

This project is open source. See the [LICENSE](./LICENSE) file for details.

---

## 📬 Contact

If you’re using Chordbay or want to collaborate:

- GitHub: [Chordbay repository](https://github.com/sss97cz/Chordbay)
- Issues: Use the [Issues](https://github.com/sss97cz/Chordbay/issues) section for bugs and feature requests

---

Made for musicians who just want a fast, offline‑first songbook that gets out of the way and lets them play.
