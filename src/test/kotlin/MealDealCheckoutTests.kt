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
            listOf(
                ReceiptLine("Total", 0)
            ),
            rules.scanAll("").receiptLines
        )
        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("Total", 50)
            ),
            rules.scanAll("A").receiptLines
        )
        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("Total", 80)
            ),
            rules.scanAll("AB").receiptLines
        )
        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("Meal Deal ABC", -20),
                ReceiptLine("Total", 80)
            ),
            rules.scanAll("ABC").receiptLines
        )
        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("C", 20),
                ReceiptLine("Meal Deal ABC", -20),
                ReceiptLine("B", 30),
                ReceiptLine("Total", 110)
            ),
            rules.scanAll("ABCB").receiptLines
        )
        assertEquals(
            listOf(
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
            rules.scanAll("ABCBCA").receiptLines
        )
    }
}

