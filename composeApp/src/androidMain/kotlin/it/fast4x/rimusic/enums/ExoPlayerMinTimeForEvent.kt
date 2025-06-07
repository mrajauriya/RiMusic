package it.fast4x.rimusic.enums

enum class ExoPlayerMinTimeForEvent {
    `10s`,
    `15s`,
    `20s`,
    `30s`,
    `40s`,
    `60s`;

    val ms: Long
        get() = when (this) {
            `10s` -> 10
            `15s` -> 15
            `20s` -> 20
            `30s` -> 30
            `40s` -> 40
            `60s` -> 60
        } * 1000L
}