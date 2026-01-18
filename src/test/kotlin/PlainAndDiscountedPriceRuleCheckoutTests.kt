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
            listOf(
                ReceiptLine("Total", 0)
            ),
            rules.scanAll("").receiptLines
        )
    }

    @Test
    fun `test As`() {
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
                ReceiptLine("A", 50),
                ReceiptLine("Total", 100)
            ),
            rules.scanAll("AA").receiptLines
        )
        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("Total", 130)
            ),
            rules.scanAll("AAA").receiptLines
        )
        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("A", 50),
                ReceiptLine("Total", 180)
            ),
            rules.scanAll("AAAA").receiptLines
        )
        assertEquals(
            listOf(
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
            rules.scanAll("AAAAAA").receiptLines
        )
    }

    @Test
    fun mixed() {
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
                ReceiptLine("C", 20),
                ReceiptLine("D", 15),
                ReceiptLine("B", 30),
                ReceiptLine("A", 50),
                ReceiptLine("Total", 115)
            ),
            rules.scanAll("CDBA").receiptLines
        )

        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("B", 30),
                ReceiptLine("Total", 160)
            ),
            rules.scanAll("AAAB").receiptLines
        )

        assertEquals(
            listOf(
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("A", 50),
                ReceiptLine("Discount for 3 As", -20),
                ReceiptLine("B", 30),
                ReceiptLine("B", 30),
                ReceiptLine("Discount for 2 Bs", -15),
                ReceiptLine("Total", 175)
            ),
            rules.scanAll("AAABB").receiptLines
        )

        assertEquals(
            listOf(
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
            rules.scanAll("AAABBD").receiptLines
        )

        assertEquals(
            listOf(
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
            rules.scanAll("DABABA").receiptLines
        )
    }
}

fun List<PriceRule>.scanAll(codes: String): CheckoutState {
    val checkoutState = codes
        .map { it.toString() }
        .fold(CheckoutState(), this::scan)
    assertEquals(
        ReceiptLine("Total", checkoutState.total),
        checkoutState.receiptLines.last()
    )
    return checkoutState
}

