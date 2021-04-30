package de.hsaalen.cmt.statistics

object StatsBasic : Stats() {

    /**
     * Connects is an atomic integer which gets a reset after a periodic delay
     */
    val connects by this


    init { // Init default statistic values
        connects.set(0)
    }
}