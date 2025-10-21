

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