interface PriceRule {
    fun receiptLine(currentCode: String, itemsCounts: Map<String, Int>): ReceiptLine?
}

class Checkout(private val priceRules: List<PriceRule>) {
    val receiptLines: MutableList<ReceiptLine> = mutableListOf()
    val codes = mutableListOf<String>()
    var total: Int = 0

    fun scan(code: String) {
        codes.add(code)
        val itemsCounts: Map<String, Int> = codes.groupingBy {
            it
        }.eachCount()
        receiptLines.addAll(
            priceRules.mapNotNull {
                it.receiptLine(code, itemsCounts)
            }
        )
        total = receiptLines.sumOf { it.amount }
    }
}

data class ReceiptLine(val description: String, val amount: Int)

data class PlainPriceRule(
    private val code: String,
    private val basePrice: Int,
) : PriceRule {
    override fun receiptLine(currentCode: String, itemsCounts: Map<String, Int>): ReceiptLine? =
        when (currentCode) {
            code -> ReceiptLine(code, basePrice)
            else -> null
        }
}

data class DiscountedPriceRule(
    private val code: String,
    private val discountAmount: Int,
    private val discountPer: Int,
) : PriceRule {
    override fun receiptLine(currentCode: String, itemsCounts: Map<String, Int>): ReceiptLine? {
        if (currentCode != code)
            return null
        val itemQuantity = (itemsCounts[currentCode] ?: error("Code $currentCode is unexpectedly not in map"))
        return when {
            itemQuantity % discountPer == 0 -> ReceiptLine("Discount for $discountPer ${this.code}s", -discountAmount)
            else -> null
        }
    }
}
