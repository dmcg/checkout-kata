import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MealDealCheckoutTests {

    private val rules: List<PriceRule> = listOf(
        DiscountedPriceRule("A", 50, 0, 1),
        DiscountedPriceRule("B", 30, 0, 1),
        DiscountedPriceRule("C", 20, 0, 1),
        DiscountedPriceRule("D", 15, 0, 1),
        MealDeal(products = setOf("A", "B", "C"), discount = 20)
    )

    @Test
    fun `test meal deal`() {
        assertEquals(
            0 to emptyList(),
            priceAndReceiptLines(rules, "")
        )
        assertEquals(
            50 to listOf(
                ReceiptLine("A", 50),
            ),
            priceAndReceiptLines(rules, "A")
        )
        assertEquals(
            80 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
            ),
            priceAndReceiptLines(rules, "AB")
        )
        assertEquals(
            80 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("Meal Deal ABC", -20),
            ),
            priceAndReceiptLines(rules, "ABC")
        )
        assertEquals(
            110 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("Meal Deal ABC", -20),
                ReceiptLine("B", 30),
            ),
            priceAndReceiptLines(rules, "ABCB")
        )
        assertEquals(
            160 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("Meal Deal ABC", -20),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("A", 50),
                ReceiptLine("Meal Deal ABC", -20),
            ),
            priceAndReceiptLines(rules, "ABCBCA")
        )
    }
}

