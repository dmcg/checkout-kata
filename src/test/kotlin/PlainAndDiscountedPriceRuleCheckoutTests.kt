import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlainAndDiscountedPriceRuleCheckoutTests {

    private val rules: List<PriceRule> = listOf(
        PlainPriceRule("A", 50),
        PlainPriceRule("B", 30),
        PlainPriceRule("C", 20),
        PlainPriceRule("D", 15),
        DiscountedPriceRule("A", 20, 3),
        DiscountedPriceRule("B", 15, 2),
    )

    @Test
    fun `test no items`() {
        assertEquals(
            0 to listOf(
                ReceiptLine("Total", 0)
            ),
            priceAndReceiptLines(rules, "")
        )
    }

    @Test
    fun `test As`() {
        assertEquals(
            50 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("Total", 50)
            ),
            priceAndReceiptLines(rules, "A")
        )
        assertEquals(
            100 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Total", 100)
            ),
            priceAndReceiptLines(rules, "AA")
        )
        assertEquals(
            130 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("Total", 130)
            ),
            priceAndReceiptLines(rules, "AAA")
        )
        assertEquals(
            180 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("A", 50),
                ReceiptLine("Total", 180)
            ),
            priceAndReceiptLines(rules, "AAAA")
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
                ReceiptLine("Total", 260)
            ),
            priceAndReceiptLines(rules, "AAAAAA")
        )
    }

    @Test
    fun mixed() {
        assertEquals(
            80 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("B", 30),
                ReceiptLine("Total", 80)
            ),
            priceAndReceiptLines(rules, "AB")
        )

        assertEquals(
            115 to listOf(
                ReceiptLine("C", 20),
                ReceiptLine("D", 15),
                ReceiptLine("B", 30),
                ReceiptLine("A", 50),
                ReceiptLine("Total", 115)
            ),
            priceAndReceiptLines(rules, "CDBA")
        )

        assertEquals(
            160 to listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("B", 30),
                ReceiptLine("Total", 160)
            ),
            priceAndReceiptLines(rules, "AAAB")
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
                ReceiptLine("Total", 175)
            ),
            priceAndReceiptLines(rules, "AAABB")
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
                ReceiptLine("Total", 190)
            ),
            priceAndReceiptLines(rules, "AAABBD")
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
                ReceiptLine("Total", 190)
            ),
            priceAndReceiptLines(rules, "DABABA")
        )
    }

}

fun priceAndReceiptLines(
    rules: List<PriceRule>,
    codes: String,
): Pair<Int, List<ReceiptLine>> =
    Checkout(rules).apply {
        scanAll(codes)
    }.let {
        it.total to it.receiptLines
    }

private fun Checkout.scanAll(codes: String) {
    codes.forEach { scan(it.toString()) }
}

