interface PriceRule {
    fun receiptLine(codes: List<String>): ReceiptLine?
}

class Checkout(private val priceRules: List<PriceRule>) {
    val receiptLines: MutableList<ReceiptLine> = mutableListOf()
    val codes = mutableListOf<String>()
    var total: Int = 0

    fun scan(code: String) {
        codes.add(code)
        receiptLines.addAll(priceRules.mapNotNull { it.receiptLine(codes) })
        total = receiptLines.sumOf { it.amount }
    }
}

data class ReceiptLine(val description: String, val amount: Int)

data class PlainPriceRule(
    private val code: String,
    private val basePrice: Int,
) : PriceRule {
    override fun receiptLine(codes: List<String>): ReceiptLine? =
        if (codes.last() == code) {
            ReceiptLine(code, basePrice)
        } else null
}

data class DiscountedPriceRule(
    private val code: String,
    private val discountAmount: Int,
    private val discountPer: Int,
) : PriceRule {
    override fun receiptLine(codes: List<String>): ReceiptLine? =
        if (codes.last() == code && discountAmount != 0 && codes.count { it == code } % discountPer == 0) {
            ReceiptLine("Discount for $discountPer ${this.code}s", -discountAmount)
        } else {
            null
        }
}
