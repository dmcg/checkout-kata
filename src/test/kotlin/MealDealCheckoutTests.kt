import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MealDealCheckoutTests {

    private val rules: List<PriceRule> =  listOf(
        DiscountedPriceRule("A", 50, 0, 1),
        DiscountedPriceRule("B", 30, 0, 1),
        DiscountedPriceRule("C", 20, 0, 1),
        DiscountedPriceRule("D", 15, 0, 1),
        MealDeal(products = setOf("A", "B", "C"), discount = 20)
    )

    @Test
    fun `test no items`() {
        assertEquals(0, price(""))
    }

    @Test
    fun `test meal deal`() {
        assertEquals(0, price(""))
        assertEquals(50, price("A"))
        assertEquals(80, price("AB"))
        assertEquals(80, price("ABC"))
        assertEquals(110, price("ABCB"))
        assertEquals(130, price("ABCBC"))
        assertEquals(160, price("ABCBCA"))
    }

    private fun price(codes: String): Int =
        Checkout(rules).apply { scanAll(codes) }.total
}

class MealDeal(val products: Set<String>, val discount: Int) : PriceRule{
    override fun invoke(codes: List<String>): Int {
        // Count how many of each required product has been scanned
        val countsPerProduct = products.associateWith { code ->
            codes.count { it == code }
        }

        // Number of complete deals is the minimum count across all required products
        val numberOfDeals = countsPerProduct.values.minOrNull() ?: 0

        return -discount * numberOfDeals
    }

}

private fun Checkout.scanAll(codes: String) {
    codes.forEach { scan(it.toString()) }
}

