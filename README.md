# Personalized Tourism Management System

A Java Swing desktop application featuring rule-based package recommendations, automated itinerary generation, cost estimation, booking management with cancellations, PDF reporting, and an administrative dashboard with visual analytics.

---

## 🚀 Quick Start Guide for Team Members

### 1. Prerequisites
Ensure you have the following installed on your machine:
* **Java Development Kit (JDK 17 LTS)**: Run `java -version`
* **Apache Maven 3.8+**: Run `mvn -version`
* **MySQL Server 8.0+**: Running locally on port `3306`

---

### 2. Database Setup
1. Create the database user (matches `src/main/java/com/tourism/config/DBConnection.java`):
   ```sql
   CREATE USER IF NOT EXISTS 'tourism_user'@'localhost' IDENTIFIED BY 'tourism123';
   GRANT ALL PRIVILEGES ON tourism_db.* TO 'tourism_user'@'localhost';
   FLUSH PRIVILEGES;
   ```
2. Import the schema and seed data:
   ```bash
   mysql -u tourism_user -p tourism_db < src/main/resources/tourism_schema.sql
   ```
   *(Password: `tourism123`)*

---

### 3. Build & Run the Application
From the project root:
```bash
# 1. Compile all Java classes
mvn clean compile

# 2. Launch the application
mvn exec:java
```

---

## 📂 Project Architecture

```
src/main/java/com/tourism/
├── Main.java                        # Application entry point (FlatLaf setup)
├── config/
│   ├── DBConnection.java            # Singleton JDBC connection manager
│   └── ThemeConstants.java          # Shared colors, fonts & padding constants
├── model/                           # Shared POJO entity models
│   ├── User.java
│   ├── Destination.java
│   ├── Package.java
│   ├── ItineraryDay.java
│   ├── Booking.java
│   ├── Review.java
│   └── UserSession.java             # Singleton holding active logged-in user
├── dao/                             # Data Access Objects (SQL queries via PreparedStatement)
├── service/                         # Business logic & algorithms
└── ui/                              # Swing UI panels and dialogs
```

---

## 👥 Team Work Breakdown (What Each Member Does Next)

* **Dev 1 (Tech Lead / DB Architect)**: Foundation complete (Maven, MySQL DDL, Models, DBConnection, FlatLaf).
* **Dev 2 (Auth & User Profile)**:
  * Create `com.tourism.dao.UserDAO` (methods: `findByEmail`, `registerUser`, `updateProfile`).
  * Create `com.tourism.service.AuthService` (methods: `login(email, password)` with `BCrypt.checkpw`, `register(...)`).
  * Create `com.tourism.ui.customer.LoginFrame` and `RegisterFrame`.
* **Dev 3 (Catalog & Package Cards)**:
  * Create `com.tourism.dao.DestinationDAO` and `com.tourism.dao.PackageDAO`.
  * Create `com.tourism.ui.customer.CatalogPanel` with search filter and package card grid.
* **Dev 4 (Recommendation Engine & Itinerary)**:
  * Create `com.tourism.service.RecommendationService` (rule-based scoring by budget, days, tags).
  * Create `com.tourism.service.ItineraryService` and day-wise itinerary schedule view.
* **Dev 5 (Booking, Payments & Cancellation)**:
  * Create `com.tourism.dao.BookingDAO` and `com.tourism.service.BookingService`.
  * Implement cost calculator, mock payment screen, and refund logic ($>48\text{ hrs} \implies 80\%$).
* **Dev 6 (Admin Inventory CRUD)**:
  * Create `com.tourism.ui.admin.InventoryManagementPanel` with `JTable` and Add/Edit/Delete dialogs.
  * Create `com.tourism.ui.admin.BookingOversightPanel`.
* **Dev 7 (PDF Generator & Admin Charts)**:
  * Create `com.tourism.service.PDFExportService` using OpenPDF (Itinerary & Invoice).
  * Create `com.tourism.ui.admin.ReportsPanel` using JFreeChart (Top Destinations & Revenue).
* **Dev 8 (Reviews, In-App Alerts & UI/UX QA)**:
  * Create `com.tourism.dao.ReviewDAO` and `com.tourism.ui.customer.ReviewDialog`.
  * Add in-app trip reminder notification banner upon customer login.
  * Harmonize all colors/buttons using `ThemeConstants.java`.

---

## 🤖 Mandatory AI Prompt Header for Team Members

When asking your AI assistant (ChatGPT, Claude, Gemini, etc.) to write code, **always paste this at the top of your chat**:

```text
[PROJECT CONTEXT & RULES]
You are writing code for a Java 17 desktop application called "Personalized Tourism Management System".
Strict adherence to the following rules is mandatory:
1. Framework: Java Swing with FlatLaf styling. Reference com.tourism.config.ThemeConstants for colors and fonts.
2. Layouts: DO NOT use null layout or setBounds(). Use standard LayoutManagers (BorderLayout, GridBagLayout, BoxLayout, FlowLayout).
3. Database: MySQL with JDBC. Always use PreparedStatement with '?' placeholders to prevent SQL injection.
4. Connection: Retrieve connections exclusively through: com.tourism.config.DBConnection.getInstance().getConnection().
5. Date Handling: Use java.time.LocalDate and java.time.LocalDateTime. Never use java.util.Date.
6. Architecture: Maintain strict separation of concerns (UI -> Service -> DAO -> Database). Never execute SQL inside a JFrame/JPanel.
7. Code Style: Standard Java camelCase, meaningful variable names, clean comments explaining business logic, no robotic/AI placeholder comments.
```

---

## 🌿 Git Branching Workflow
1. Never push directly to `main` or `develop`.
2. Checkout a new branch for your feature:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
   ```
3. Commit and push your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
4. Create a Pull Request (PR) on GitHub to merge into `develop`.
