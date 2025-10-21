

typealias PriceRule = (List<String>) -> Int

class Checkout(private val priceRules: List<PriceRule>) {
    val codes = mutableListOf<String>()
    var total: Int = 0

    fun scan(code: String) {
        codes.add(code)
        total = priceRules.sumOf { it.invoke(this.codes) }
    }
}

data class DiscountedPriceRule(
    private val code: String,
    private val basePrice: Int,
    private val discountAmount: Int,
    private val discountPer: Int,
) : PriceRule {
    override fun invoke(codes: List<String>): Int {
        val scannedCount = codes.count { it == code }
        val basePrice = basePrice * scannedCount
        val discount = discountAmount * (scannedCount / discountPer)
        return basePrice - discount
    }
}