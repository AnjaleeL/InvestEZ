package com.example.investez

data class Gold(
    var amountOfGold: Double = 0.0,
    var investAmount: Double = 0.0,
    var goldID: String = "",
    var goldRate: String = "",
    var goldWeight: String = "",
    var userID: String = ""

) {
    constructor() : this(0.0, 0.0, "", "", "","")
}