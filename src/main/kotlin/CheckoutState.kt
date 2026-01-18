
data class ReceiptLine(val description: String, val amount: Int)

data class CheckoutState(
    val codes: List<String> = emptyList(),
    val itemLines: List<ReceiptLine> = emptyList(),
) {
    val total = itemLines.sumOf { it.amount }
    val receiptLines: List<ReceiptLine>
        get() = itemLines + ReceiptLine("Total", total)
}

interface PriceRule {
    fun receiptLine(currentCode: String, itemsCounts: Map<String, Int>): ReceiptLine?
}

fun List<PriceRule>.scan(
    state: CheckoutState,
    code: String,
): CheckoutState {
    val newCodes = state.codes + code
    val itemsCounts: Map<String, Int> = newCodes.groupingBy {
        it
    }.eachCount()
    val newItemLines = state.itemLines.plus(mapNotNull {
        it.receiptLine(code, itemsCounts)
    })
    return state.copy(codes = newCodes, itemLines = newItemLines)
}

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
