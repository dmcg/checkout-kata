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

fun parseRules(rulesAsString: String): List<PriceRule> {
    val itemLineRegex = Regex("""^\s*([A-Za-z])\s+(\d+)(?:\s+(\d+)\s*for\s*(\d+))?\s*$""",
        RegexOption.IGNORE_CASE)

    return rulesAsString
        .lines()
        .mapNotNull { line ->
            val match = itemLineRegex.find(line)
            if (match != null) {
                val (codeStr, unitStr, nStr, specialStr) = match.destructured
                val code = codeStr.uppercase()
                val unit = unitStr.toInt()
                if (nStr.isNotEmpty() && specialStr.isNotEmpty()) {
                    val discountPer = nStr.toInt()
                    val special = specialStr.toInt()
                    val discountAmount = (unit * nStr.toInt()) - special
                    DiscountedPriceRule(code, unit, discountAmount, discountPer)
                } else {
                    DiscountedPriceRule(code, unit, 0, 1)
                }
            } else {
                null
            }
        }
}

