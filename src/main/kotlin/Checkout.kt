interface PriceRule {
    fun receiptLines(codes: List<String>): List<ReceiptLine> = emptyList()
}

class Checkout(private val priceRules: List<PriceRule>) {
    val receiptLines: MutableList<ReceiptLine> = mutableListOf()
    val codes = mutableListOf<String>()
    var total: Int = 0

    fun scan(code: String) {
        codes.add(code)
        receiptLines.addAll(priceRules.flatMap { it.receiptLines(codes) })
        total = receiptLines.sumOf { it.amount }
    }
}

data class ReceiptLine(val description: String, val amount: Int)

data class DiscountedPriceRule(
    private val code: String,
    private val basePrice: Int,
    private val discountAmount: Int,
    private val discountPer: Int,
) : PriceRule {
    override fun receiptLines(codes: List<String>): List<ReceiptLine> =
        if (codes.last() == code) {
            if (discountAmount != 0 && codes.count { it == code } % discountPer == 0) {
                listOf(
                    ReceiptLine(code, basePrice),
                    ReceiptLine("Discount for $discountPer ${this.code}s", -discountAmount)
                )
            } else {
                listOf(ReceiptLine(code, basePrice))
            }
        } else emptyList()
}
