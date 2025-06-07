package it.fast4x.rimusic.enums

enum class LibraryItemSize {
    Small,
    Medium,
    Big;

    val size: Int
        get() = when (this) {
            Small -> 80
            Medium -> 108
            Big -> 140
        }

}
