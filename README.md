# LIMA: Leave & Health Management System

**LIMA** (Leave & Health Management Application) is a comprehensive, full-stack mobile application designed to digitize and streamline administrative processes within an internal military unit. It provides a secure, role-based platform for personnel to manage leave requests, report health status, and for commanders to oversee unit readiness and make informed decisions.

This project demonstrates a modern, scalable architecture using a **Spring Boot** backend and a **Flutter** mobile frontend.

---

## 📱 App Preview

| Login Screen | Soldier Dashboard | Officer Reports |
| :---: | :---: | :---: |
| ![Login Screen](https://i.imgur.com/g5Yqg8p.png) | ![Soldier Dashboard](https://i.imgur.com/J3hL5k7.png) | ![Officer Reports](https://i.imgur.com/O8tN2bW.png) |

---

## ✨ Features

The application is divided into two primary user experiences based on a complex, hierarchical rank system.

### For Soldiers (Pawns)

* **Secure Login:** Authentication using Army ID and password.
* **Leave Application:** A simple form to apply for leave, specifying dates, reason, and location.
* **Leave Status Tracking:** A real-time visual tracker to see the multi-level approval status of their leave requests.
* **Health Reporting:** Ability to submit daily health reports while on leave.
* **Location Updates:** Ability to update their current location during an active leave period.
* **Emergency Alert:** A dedicated button to instantly notify their chain of command in case of an emergency.

### For Officers (Bishops, Knights, Rooks, Queens, Kings)

* **Hierarchical Dashboards:** The dashboard UI dynamically adapts to the officer's rank, showing only the relevant tools and data they are authorized to see.
* **Account Verification:** High-level officers can review and approve new user registrations to activate their accounts.
* **Multi-Level Leave Approval:** A dedicated interface to review leave requests from subordinates and approve or reject them, advancing the request up the chain of command.
* **Personnel Transfer (Inter-Bty Posting):** Knight-rank officers can transfer subordinates between different batteries (Btys).
* **Advanced Analytics & Reports:**
    * **Leave Data:** A comprehensive view of all subordinates, grouped by rank, showing their leave history and current on-leave status.
    * **On-Leave Personnel:** A summary of all soldiers currently on leave, including their latest health reports.
    * **Data Analysis:** Powerful tools to search for subordinates who have taken leave within a specific period or to analyze individual annual leave balances with advanced sorting and filtering.
* **Legacy Data Entry:** A tool for high command to manually enter past leave records for newly onboarded personnel.

---

## 🛠️ Tech Stack & Architecture

This project is built with a modern, full-stack architecture, separating the backend business logic from the frontend user interface.

### **Backend**

* **Framework:** Spring Boot 3
* **Language:** Java 17
* **Database:** PostgreSQL
* **Security:** Spring Security with JWT (JSON Web Token) based authentication.
* **Push Notifications:** Firebase Cloud Messaging (FCM) via the Firebase Admin SDK.
* **Core Concepts:** REST APIs, Dependency Injection, JPA (Hibernate), Role-Based Access Control (RBAC).

### **Frontend**

* **Framework:** Flutter
* **Language:** Dart
* **State Management:** Riverpod
* **Networking:** HTTP package for REST API communication.
* **Push Notifications:** `firebase_messaging` and `flutter_local_notifications` for handling incoming alerts.
* **UI:** Material Design with a custom, military-inspired theme.

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### **Prerequisites**

* **Backend:** Java JDK 17+, Maven or Gradle, a running PostgreSQL instance.
* **Frontend:** Flutter SDK, Android Studio (for Android SDK/Emulator) or Xcode (for iOS).
* **Firebase:** A Firebase project with an Android and/or iOS app configured.

### **Backend Setup**

1.  Clone the repository.
2.  Navigate to the backend project folder.
3.  Set up your `application.properties` file in `src/main/resources/` with your database credentials, JWT secret, and registration secret keys.
4.  Place your Firebase Admin SDK service account key (JSON file) in the `src/main/resources/` folder and update its path in `application.properties`.
5.  Run the application using your IDE or the command line.

### **Frontend Setup**

1.  Navigate to the frontend project folder.
2.  Place your Firebase `google-services.json` (for Android) or `GoogleService-Info.plist` (for iOS) in the respective platform folders.
3.  Update the `BASE_URL` in `lib/core/utils/constants.dart` to point to your running backend server (e.g., `http://localhost:8080` or your `ngrok` URL).
4.  Run `flutter pub get` to install dependencies.
5.  Run `flutter run` to launch the application on an emulator or a connected device.
