class MealDeal(val products: Set<String>, val discount: Int) : PriceRule {
    private fun discountFor(codes: List<String>): Int {
        // Count how many of each required product has been scanned
        val countsPerProduct = products.associateWith { code ->
            codes.count { it == code }
        }

        // Number of complete deals is the minimum count across all required products
        val numberOfDeals = countsPerProduct.values.minOrNull() ?: 0

        return -discount * numberOfDeals
    }

    override fun receiptLine(codes: List<String>): ReceiptLine? {
        val oldDiscount = - discountFor(codes.dropLast(1))
        val currentDiscount = - discountFor(codes)
        return if (currentDiscount > oldDiscount) {
            val name = products.joinToString("")
            ReceiptLine("Meal Deal $name", -(currentDiscount - oldDiscount))
        } else
            null
    }
}