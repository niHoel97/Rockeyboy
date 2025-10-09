## **INTRODUCTION**
This document is created for developers who will continue working on the app, get familiar with it, and contribute to its development. It describes the app's architecture, the technologies used, and the key design patterns and principles followed during development. It also provides insight into the coding style and practices used to ensure the app is easy to maintain and further develop.

## **ARCHITECTURE AND STRUCTURE**
The app is built using a **model-view-viewmodel (MVVM)** architecture to separate presentation logic and data storage. This structure makes the app easier to test, maintain, and extend.

### **MAIN COMPONENTS:**

- **View (UI):**  
  Responsible for showing data to the user and handling user interactions.

- **Model:**  
  Represents the data and business logic, including access to data sources like APIs and databases.

- **viewModel:**  
  Contains the app's business logic and prepares data for display in the UI.

- **Repository:**  
  Handles data fetching from different sources and gives the viewModel access to data.

### **FOLDER STRUCTURE:**

- **Data:** Contains files related to data access  
  - **db:** Data related to local storage.  
  - **network:** Handles network-related operations (API calls).  
  - **Settings:** Configuration settings for the app.

- **Model:** Contains data models representing business logic and API responses  
  - **LocationForeCast:** Model for weather forecasts based on geographic location.  
  - **Ratings:** Model for ratings and feedback.  
  - **Settings:** Model for handling changes in settings.

- **UI:** Contains everything related to the user interface.  
  - **Animation:** Rocket animation used when pressing buttons.  
  - **Calendar:** Functionality for using the calendar on the **homeScreen**.  
  - **Components:** Reusable UI components.  
  - **Home:** Screen and logic for the **main-screen**.  
  - **Grib:** Screen and components for the **gribScreen**.  
  - **IntroScreens:** Screens and functionality for **intro screens**.   
  - **Modifiers:** UI modifiers for colors that can be used across different screens.  
  - **Theme:** Color theme for reuse throughout the app.  
  - **Settings:** Screen and components for **settingsScreen**.

## **OBJECT-ORIENTED PRINCIPLES**

The app is designed with a focus on object-oriented principles like **low coupling** and **high cohesion**.

- **Low coupling:**  
  This is achieved by using interfaces and abstractions, so components are not directly dependent on each other. This makes sure that changes in one part of the app don’t necessarily affect other parts.

- **High cohesion:**  
  Each component in the app has a clear and specific responsibility. For example, the **viewModel** handles both the logic and some data storage (such as **byRemember** values), while the **repository** focuses on data handling from external sources. This makes the code more modular and easier to test.

## **DESIGN PATTERNS**

- **MVVM** was chosen as the architectural pattern to separate UI logic from business logic. It offers the following benefits:  
  - Better testability: The logic in a **viewModel** can be tested independently of the UI.  
  - Separation of concerns: The UI is responsible for displaying content, while the **viewModel** handles the logic.

- **UDF (Use Case-Driven):**  
  The app also uses the **UDF design pattern** to represent specific business logic operations as independent units. This makes the app's functionality more modular and reusable across different scenarios.

## **TECHNOLOGIES AND TOOLS**

- **Android API level**  
  - We use a minimum **API level 26** to ensure support for modern Android features and a wide range of devices.

- **Kotlin**  
  - The app is written in **Kotlin**. This gives benefits such as null safety, modern syntax, and good support for the Android SDK.

- **Jetpack Libraries**  
  - We use **Jetpack components** like **Room** and **ViewModel** to handle UI-related business logic.

- **Android Libraries**  
  - We use **Android libraries** like **util.log** for logging.

- **Kotlin Libraries**  
  - The **Kotlin library** **Ktor** is used to handle network requests.

- **Java Libraries**  
  - We also use the **Java library** **java.net.URLEncoder** to encode geographic coordinates for use in API requests.

- **Coroutines**  
  - Used to handle asynchronous operations and make the app more responsive.

- **HttpClientProvider**  
  - Used to manage API requests. This gives us more control over the HTTP connection and allows us to customize configurations for API access.

- **HttpURLConnection**  
  - Used for handling network requests and API communication through **HttpClientProvider**.

## **CODE QUALITY AND STYLE**

- **Coding style:**  
  We’ve tried to code in a **Kotlin-idiomatic** way, so we follow **Kotlin Coding Conventions** to ensure consistency and readable code.

- **Testing:**
    - The app is designed to be highly testable with unit tests.
    - We use JUNIT for unit testing.
    - Unit tests have been created for the rating algorithms, coordinate parsing, location of bad weather factors, and the gribResponse.

- **Error handling:**
    - We use try-catch blocks for solid error handling in a controlled manner and display appropriate error messages to the user.
    - We also use if-else blocks to handle errors and give the user appropriate feedback.

## **SUMMARY**  
This app is built with a focus on good software architecture and principles that make it easy to maintain and extend. The choice of **MVVM** and **UDF** provides clear separation between different layers of the application, and using modern Android technologies like **Kotlin** and **Coroutines** makes the development efficient and future-proof. We also ensured the app is compatible with a wide range of Android devices by setting **API level 24** as the minimum.
