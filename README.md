# 📸 StoryTime

**StoryTime** is a modern Android app that captures photos and allows users to guide a bedtime story using a prompt input. It’s designed with children and storytelling in mind — providing a clean and soft UI using Jetpack Compose and Material 3.

> This is an in-progress project for one of my Mobile Applications class. More features are being added every week!

---

## ✨ Current Features

- 📷 **Take photos** using [CameraX]
- 🧠 **Add a text prompt** (modern input style)
- 🖼 **Attach & preview thumbnail** of captured photo
- ❌ **Remove attachment** before submitting
- 💬 **Placeholder for story output** section
- 🌗 Fully themed for **light and dark mode**

---

## 🚧 Coming Soon

- 🔗 Connect to OpenAI API to generate stories
- 🧠 Save and view stories with Firebase
- 🖼 Pick photo from local gallery
- 🔊 Text-to-speech playback for stories

---

## 🛠️ Tech Stack

- **Jetpack Compose** (Material 3)
- **CameraX** (LifecycleCameraController)
- **Kotlin**
- **ViewModel + StateFlow**
- Compose Navigation (planned)

---

## 🏗️ Project Structure
ui/
├── create/            # Camera and CreateStory UI
├── theme/             # Material3 theming
viewmodel/
├── CreateStoryViewModel.kt

---

## 📸 Screenshots

_(Add screenshots after full UI pass is ready)_

---

## 📦 Getting Started

### Requirements

- Android Studio Giraffe or later
- Kotlin 1.9+
- Compile SDK 34+
- Minimum SDK 29
