class MealDeal(val products: Set<String>, val discount: Int) : PriceRule {
    override fun receiptLine(currentCode: String, itemsCounts: Map<String, Int>): ReceiptLine? {
        if (currentCode !in products) return null

        val countsOfMealDealItems = products.associateWith { code ->
            itemsCounts.getOrDefault(code, 0)
        }
        val numberOfDealsWithCurrentItem = countsOfMealDealItems.values.minOrNull() ?: 0

        val countsBeforeCurrentItem = countsOfMealDealItems.toMutableMap().apply {
            this[currentCode] = this.getOrDefault(currentCode, 0) - 1
        }
        val numberOfDealsBeforeCurrentItem = countsBeforeCurrentItem.values.minOrNull() ?: 0

        return if (numberOfDealsWithCurrentItem > numberOfDealsBeforeCurrentItem) {
            val name = products.joinToString("")
            ReceiptLine("Meal Deal $name", -discount)
        } else {
            null
        }
    }
}