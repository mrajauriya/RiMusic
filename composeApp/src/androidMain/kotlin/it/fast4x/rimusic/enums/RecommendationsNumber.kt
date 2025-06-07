package it.fast4x.rimusic.enums

enum class RecommendationsNumber {
    `5`,
    `10`,
    `15`,
    `20`;

    val number: Int
        get() = when (this) {
            `5` -> 5
            `10` -> 10
            `15` -> 15
            `20` -> 20

        }
}
