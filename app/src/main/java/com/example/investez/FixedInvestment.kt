package com.example.investez

class FixedInvestment {
    var userID: String? = null
    var bank: String? = null
    var timePlan: String? = null
    var investAmount: Double? = null
    var maturity: Double? = null
    var profit: Double? = null
    var fixedID: String? = null

    constructor() {
        // Empty constructor required for Firebase deserialization
    }

    constructor(userID: String?, bank: String?, timePlan: String?, investAmount: Double?, maturity: Double?, profit: Double?, fixedID: String?) {
        this.userID = userID
        this.bank = bank
        this.timePlan = timePlan
        this.investAmount = investAmount
        this.maturity = maturity
        this.profit = profit
        this.fixedID = fixedID
    }
}
