import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DiscountedPriceRuleCheckoutTests {

    private val rules: List<PriceRule> = listOf(
        DiscountedPriceRule("A", 50, 20, 3),
        DiscountedPriceRule("B", 30, 15, 2),
        DiscountedPriceRule("C", 20, 0, 1),
        DiscountedPriceRule("D", 15, 0, 1),
    )

    @Test
    fun `test no items`() {
        assertEquals(0 to emptyList(), rules.priceAndReceiptLines(""))
    }

    @Test
    fun `test As`() {
        assertEquals(
            50 to listOf(
                ReceiptLine("A", 50)
            ), rules.priceAndReceiptLines("A")
        )
        assertEquals(
            100 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
            ), rules.priceAndReceiptLines("AA")
        )
        assertEquals(
            130 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
            ), rules.priceAndReceiptLines("AAA")
        )
        assertEquals(
            180 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("A", 50),
            ), rules.priceAndReceiptLines("AAAA")
        )
        assertEquals(
            260 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
            ), rules.priceAndReceiptLines("AAAAAA")
        )
    }

    @Test
    fun mixed() {
        assertEquals(0 to emptyList(), rules.priceAndReceiptLines(""))

        assertEquals(
            80 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
            ), rules.priceAndReceiptLines("AB")
        )

        assertEquals(
            115 to listOf(
                ReceiptLine("C", 20),
                ReceiptLine("D", 15),
                ReceiptLine("B", 30),
                ReceiptLine("A", 50),
            ), rules.priceAndReceiptLines("CDBA")
        )

        assertEquals(
            160 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("B", 30),
            ), rules.priceAndReceiptLines("AAAB")
        )

        assertEquals(
            175 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("B", 30),
                ReceiptLine("B", 30),
                ReceiptLine("Discount for 2 Bs", -15),
            ), rules.priceAndReceiptLines("AAABB")
        )

        assertEquals(
            190 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("B", 30),
                ReceiptLine("B", 30),
                ReceiptLine("Discount for 2 Bs", -15),
                ReceiptLine("D", 15),
            ), rules.priceAndReceiptLines("AAABBD")
        )

        assertEquals(
            190 to listOf(
                ReceiptLine("D", 15),
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("Discount for 2 Bs", -15),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
            ), rules.priceAndReceiptLines("DABABA")
        )
    }
}

fun List<PriceRule>.priceAndReceiptLines(codes: String): Pair<Int, List<ReceiptLine>> =
    Checkout(this).apply { scanAll(codes) }.let { it.total to it.receiptLines }

private fun Checkout.scanAll(codes: String) {
    codes.forEach { scan(it.toString()) }
}

