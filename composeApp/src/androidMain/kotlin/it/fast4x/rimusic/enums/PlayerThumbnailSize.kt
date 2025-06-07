package it.fast4x.rimusic.enums

enum class PlayerThumbnailSize {
    Small,
    Medium,
    Big,
    Biggest,
    Expanded;

    val size: Int
        get() = when (this) {
            Small -> 90
            Medium -> 55
            Big -> 30
            Biggest -> 20
            Expanded -> 0
        }

}
