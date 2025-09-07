# Kotlin Coding Conventions for Collaboration (and for Agents) 

## General

*   Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

## Naming

*   **Packages**: Lowercase, avoid underscores (e.g., `com.example.myapp.feature`).
*   **Classes and Objects**: PascalCase (e.g., `MyClass`, `MyObject`).
*   **Interfaces**: PascalCase (e.g., `MyInterface`).
*   **Functions and Properties**: camelCase (e.g., `myFunction`, `myProperty`).
*   **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_COUNT`).
*   **Test Functions**: Use backticks for readability (e.g., `` `given something, when action, then result` ``).

## Formatting

*   Use 4 spaces for indentation.
*   Keep lines under 100 characters.
*   Use blank lines to separate logical blocks of code.

## Documentation

*   Write KDoc for all public APIs.
*   Keep comments concise and focused on *why*, not *what*.

# Koin Usage Guidelines

## Modules

*   Define Koin modules per feature or layer (e.g., `authModule`, `viewModelModule`).
*   Use `single` for singletons, `factory` for new instances, and `viewModel` for ViewModels.

```kotlin
val appModule = module {
    single<MyRepository> { MyRepositoryImpl(get()) }
    viewModel { MyViewModel(get()) }
}
```

## Injection

*   Prefer constructor injection.
*   Use `by inject()` for Android components (Activities, Fragments, Services).
*   Use `get()` within module definitions to resolve dependencies.

## Testing

*   Use `KoinTestRule` for JUnit 5 tests.
*   Utilize `checkModules` to verify your Koin graph.

# OrbitMVI Usage Guidelines

## State

*   Define a unique `State` data class for each screen or feature.
*   Keep the state immutable. Updates should create a new state object.
*   State should represent everything needed to render the UI.

```kotlin
data class MyScreenState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val error: String? = null
)
```

## SideEffect

*   Define a `SideEffect` sealed interface for one-off events (e.g., navigation, showing a Toast).
*   SideEffects should not alter the State directly.

```kotlin
sealed interface MyScreenSideEffect {
    data object NavigateToDetails : MyScreenSideEffect
    data class ShowError(val message: String) : MyScreenSideEffect
}
```

## ViewModel

*   Inherit from `ViewModel` and implement `ContainerHost<State, SideEffect>`.
*   Use `container<State, SideEffect>(initialState)` to create the Orbit container.
*   Use `intent { ... }` to handle user actions and update state or post side effects.
*   Use `reduce { ... }` to update the state.
*   Use `postSideEffect(...)` to send side effects.

```kotlin
class MyViewModel(private val repository: MyRepository) : ViewModel(), ContainerHost<MyScreenState, MyScreenSideEffect> {

    override val container = container<MyScreenState, MyScreenSideEffect>(MyScreenState())

    fun loadData() = intent {
        reduce { state.copy(isLoading = true) }
        try {
            val result = repository.fetchData()
            reduce { state.copy(isLoading = false, data = result) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, error = "Failed to load data") }
            postSideEffect(MyScreenSideEffect.ShowError("Failed to load data"))
        }
    }
}
```