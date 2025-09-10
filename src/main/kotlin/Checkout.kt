class Checkout {
    var codes = mutableListOf<String>()
    var total: Int = 0

    fun scan(code: String) {
        codes.add(code)
        total = 0

        val aPrice = discountedPriceFor("A", 50, 20, 3)
        total += aPrice

        val bPrice = discountedPriceFor("B", 30, 15, 2)
        total += bPrice

        val cPrice = undiscountedPriceFor("C", 20)
        total += cPrice

        val dPrice = undiscountedPriceFor("D", 15)
        total += dPrice
    }

    private fun undiscountedPriceFor(code: String, basePrice: Int): Int = basePrice * codes.count { it == code }

    private fun discountedPriceFor(code: String, basePrice: Int, discountAmount: Int, discountPer: Int): Int {
        val scannedCount = codes.count { it == code }
        val basePrice = basePrice * scannedCount
        val discount = discountAmount * (scannedCount / discountPer)
        return basePrice - discount
    }
}