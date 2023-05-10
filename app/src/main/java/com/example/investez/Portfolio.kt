package com.example.investez

data class Portfolio(
    val name: String = "",
    val investmentType: String = "",
    val investmentAmount: Double = 0.0,
    val profitSoFar: Double = 0.0,
    val notes: String = "",
    val userId: String = "",
    var portfolioId: String = ""
)