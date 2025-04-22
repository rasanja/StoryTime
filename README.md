# StoryTime

**StoryTime** is a modern Android app that allows users to capture a photo and generate a personalized bedtime story based on that image, optionally guided by a text prompt. The app is designed with children and storytelling in mind, providing a soft, clean UI using Jetpack Compose and Material 3.

This project is actively developed as part of a university course in Mobile Application Development and is intended to demonstrate end-to-end integration between CameraX, Firebase, and the OpenAI API via a secure backend.

---

## Features

### Completed

- Capture photos directly within the app using CameraX
- Enter a prompt to guide the story generation
- Preview and remove the attached image before submission
- Light and dark theme support using Material 3
- Placeholder UI for story output

### In Progress

- Integration with OpenAI API (via secure backend proxy)
- Firebase Authentication for secure user access
- Save and retrieve previously generated stories using Firebase Firestore
- Text-to-speech playback for stories
- Upload photos from the device gallery
- Navigation and persistent story history

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Camera:** CameraX with LifecycleCameraController
- **State Management:** ViewModel + StateFlow
- **Backend:** Firebase Functions (OpenAI proxy), Firebase Firestore
- **Authentication:** Firebase Auth (Email/Password, Google Sign-In planned)

---

## Architecture
```
StoryTime/
├── ui/
│   ├── create/            # Camera and CreateStory UI components
│   ├── theme/             # Custom Material 3 themes
├── viewmodel/
│   ├── CreateStoryViewModel.kt
├── MainActivity.kt
├── App.kt
```

- **UI Layer:** Built with Compose, reactive to ViewModel state updates
- **ViewModel Layer:** Handles user input, business logic, and backend communication
- **Backend Integration:** Delegates OpenAI calls to a secure Firebase Function

---

## Screenshots

Screenshots will be added once the UI and backend integration are finalized.

---

## Getting Started

### Prerequisites

- Android Studio Giraffe (or later)
- Kotlin 1.9+
- Compile SDK 34+
- Minimum SDK 29
- Firebase project (for Authentication and Firestore)
- OpenAI API key (see below)

---

## Backend Setup: OpenAI API Integration

Story generation is performed by OpenAI models, but the API key is **never exposed in the Android app**. Instead, requests are routed through a secure Cloud Function or Cloud Run endpoint.

### 1. Get an OpenAI API Key

- Sign up at [https://platform.openai.com](https://platform.openai.com)
- Navigate to API keys and generate a new key

### 2. Deploy a Secure Proxy Backend

To prevent API key exposure, deploy a Cloud Function (Firebase) or Cloud Run service that:

- Accepts requests from the mobile app
- Verifies the Firebase Authentication token
- Sends the prompt + image to OpenAI using your private API key
- Returns the story result to the app

> This protects your key and allows per-user limits, analytics, and abuse prevention.

### 3. Secure Access with Firebase Auth
(Coming soon)
