package application.model

data class ApplicationEvent(
    val action: Action,
    val callback: () -> Unit = {}
)