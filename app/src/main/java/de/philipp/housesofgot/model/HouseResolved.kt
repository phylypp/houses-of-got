package de.philipp.housesofgot.model

/**
 * Model of a house with the overlord name resolved.
 */
class HouseResolved(
    house: House?,
    overlordResolved: String?
) {
    val name = house?.name
    val region = house?.region
    val coatOfArms = house?.coatOfArms
    val words = house?.words
    val seats = house?.seats
    val overlord = overlordResolved
}