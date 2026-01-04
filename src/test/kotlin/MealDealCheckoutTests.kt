import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MealDealCheckoutTests {

    private val rules: List<PriceRule> = listOf(
        PlainPriceRule("A", 50),
        PlainPriceRule("B", 30),
        PlainPriceRule("C", 20),
        PlainPriceRule("D", 15),
        MealDeal(products = setOf("A", "B", "C"), discount = 20)
    )

    @Test
    fun `test meal deal`() {
        assertEquals(
            0 to listOf(
                ReceiptLine("Total", 0)
            ),
            priceAndReceiptLines(rules, "")
        )
        assertEquals(
            50 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("Total", 50)
            ),
            priceAndReceiptLines(rules, "A")
        )
        assertEquals(
            80 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("Total", 80)
            ),
            priceAndReceiptLines(rules, "AB")
        )
        assertEquals(
            80 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("Meal Deal ABC", -20),
                ReceiptLine("Total", 80)
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
                ReceiptLine("Total", 110)
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
                ReceiptLine("Total", 160)
            ),
            priceAndReceiptLines(rules, "ABCBCA")
        )
    }
}

