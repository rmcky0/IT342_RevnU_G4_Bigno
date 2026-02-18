# RevnU: A Sales Management System for Restaurants

**RevnU** is a specialized restaurant management system designed to digitize and streamline daily financial tracking for small and medium-sized food establishments. The system enables restaurant owners and staff to manage sales, track expenses, and monitor staff salaries through a secure, multi-platform ecosystem.

---

## 🚀 Project Overview
RevnU replaces manual, error-prone record-keeping methods like notebooks and spreadsheets with a centralized digital platform. It automates financial calculations and generates daily/monthly profit summaries to support informed business decisions.

### Core Features
* **Authentication & Security:** JWT-based login, Google OAuth 2.0 integration, and BCrypt password hashing.
* **Role-Based Access Control (RBAC):** Admin and Staff roles with API and UI-level restrictions.
* **Financial Tracking:** Full CRUD operations for Sales, Expenses, and Staff Salaries.
* **Digital Record Keeping:** File upload capability for digitizing and linking physical receipts to expense records.
* **System Integrations:** Meaningful consumption of a real public External API and automated SMTP email notifications for daily summaries.

---

## 🛠️ Technology Stack
The system is built using a **three-tier architecture**:

* **Backend:** Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA.
* **Web Frontend:** React 18, TypeScript, Tailwind CSS, Axios.
* **Mobile:** Kotlin, Jetpack Compose, Retrofit, Room.
* **Database:** PostgreSQL 14+.

---

## 📂 Repository Structure
Following the mandatory repository format:

```text
IT342_RevnU_G4_Bigno/
├── backend/        # Spring Boot REST API source code
├── web/            # React TypeScript web application
├── mobile/         # Android Kotlin mobile application
├── docs/           # System Design Document (SDD) and diagrams
└── README.md       # Project documentation

## ⚙️ Setup and Installation
Prerequisites
Java Development Kit (JDK) 17 
Node.js & npm/yarn 
Android Studio 
PostgreSQL 14+ 

### Backend Setup
Navigate to the backend/ directory.
Configure application.properties with your PostgreSQL credentials, SMTP settings, and OAuth credentials.
Run the application using Maven: ./mvnw spring-boot:run.

### Web Setup
Navigate to the web/ directory.
Install dependencies: npm install.
Start the development server: npm start.

### Mobile Setup
Open the mobile/ directory in Android Studio.
Build the project using Gradle.
Run on a physical device or emulator with API Level 24+.

## 🏛️ System Architecture
The backend follows a Layered Architecture pattern:

Controller Layer: Handles RESTful endpoints and HTTP status codes.
Service Layer: Manages business logic and external integrations.
Repository Layer: Handles JPA/PostgreSQL data persistence.
DTO Layer: Ensures secure data transfer without exposing sensitive info (like passwords).

## 📝 Author
Richemmae Valle Bigno 
Section: G4 
Course: IT342 - System Integration and Architecture