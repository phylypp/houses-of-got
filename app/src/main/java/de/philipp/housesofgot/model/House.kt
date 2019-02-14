package de.philipp.housesofgot.model

import java.io.Serializable

/**
 * Data model of a house.
 */
data class House(
    val name: String?,
    val region: String?,
    val coatOfArms: String?,
    val words: String?,
    val seats: List<String>?,
    val overlord: String?
) : Serializable