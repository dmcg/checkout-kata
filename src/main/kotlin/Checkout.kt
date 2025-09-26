typealias PriceRule = (List<String>) -> Int

class Checkout(private val priceRules: List<PriceRule>) {
    val codes = mutableListOf<String>()
    var total: Int = 0

    fun scan(code: String) {
        codes.add(code)
        total = priceRules.sumOf { it.invoke(this.codes) }
    }
}

fun discountedPriceRule(
    code: String,
    basePrice: Int,
    discountAmount: Int,
    discountPer: Int,
): PriceRule =
    { codes ->
        val scannedCount = codes.count { it == code }
        val basePrice = basePrice * scannedCount
        val discount = discountAmount * (scannedCount / discountPer)
        basePrice - discount
    }

