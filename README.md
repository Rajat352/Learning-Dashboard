### 1. Architecture

The application uses **MVI (Model–View–Intent) with Unidirectional Data Flow**, organised into three layers:

- **Presentation:** Compose screens send user actions to ViewModels, which expose UI state through `StateFlow`.
- **Domain:** Models, repository interfaces, and use cases define application behaviour.
- **Data:** Repository implementations coordinate the mock APIs and Room database.

I chose MVI over MVVM because I find it way more practical and readable, and it really scales well when the screen complexity increases which is the case in real production applications.

**Reusable business logic** lives in focused use cases. Email and password validation are separated from the login ViewModel, while `CalculateCourseProgressUseCase` is shared by the dashboard and course details ViewModels. This keeps business rules consistent and makes them easier to test.

**Dependency injection:** Hilt provides ViewModels with their repositories and use cases, and supplies repository implementations with their API and database dependencies. This centralises object creation and keeps dependencies explicit.

The application follows an **offline-first approach**, using Room as the source of truth for displayed course data.

### 2. Offline Support

- **Storage and loading:** Courses and lessons fetched from the mock APIs are saved in Room. ViewModels observe database changes through repository `Flows`.
- **Offline access:** Previously loaded courses remain available without internet access. Lesson details are available after they have been fetched and cached.
- **Local updates:** Completing a cached lesson updates its status and course counts in a database transaction, automatically updating both screens.
- **Refresh behaviour:** Failed network requests preserve cached data. Refreshes retain local completion status for existing lessons; server synchronization is outside the current scope.

### 3. Security

In production, I would store authentication tokens encrypted in `DataStore` using AES-GCM with a key managed by `Android Keystore`. DataStore integrates naturally with coroutines and Flow unlike EncryptedSharedPreferences. I would exclude token storage from backups, clear tokens on logout, and handle token expiry and refresh securely.

### 4. Scale

I would improve:

- **Pagination:** Introduce paginated APIs and integrate Paging 3 with Room to load courses incrementally, reducing unnecessary payloads and memory usage while preserving offline access.
- **Progress synchronization:** Keep Room as the source of truth for locally displayed lesson progress and introduce a robust API to synchronize pending changes with the backend. Use WorkManager with a network constraint and an approximate 30-minute periodic interval, with retries and conflict handling to preserve offline updates. A network-constrained one-time request would complement periodic work to sync pending changes when connectivity becomes available, subject to Android scheduling.
- **Observability and monitoring:** Add PostHog for product events to understand which features users actually use, and Firebase Crashlytics or Sentry to monitor production crashes and errors.

### 5. Second Platform

I would implement the iOS/macOS application in Swift using SwiftUI, retaining the same separation between presentation, domain, and data layers.

- **State and business logic:** Use observable presentation models with explicit screen states and actions, and keep validation and progress calculation in reusable use cases.
- **Networking and offline support:** Use `URLSession` with `async/await` for API calls and SwiftData as the local source of truth. Save lesson completion locally so progress remains available offline.
- **Security and testing:** Store authentication tokens in Keychain, inject dependencies through initializers, and unit-test business rules and repository behaviour.
