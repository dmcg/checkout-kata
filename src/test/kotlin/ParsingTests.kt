import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParsingTests {
    val rulesAsString = """
         Item   Unit      Special
                Price     Price
         --------------------------
           A     50       3 for 130
           B     30       2 for 45
           C     20
           D     15"""
        .trimIndent()

    @Test fun parseRules() {
        val expectedRules: List<PriceRule> = listOf(
            DiscountedPriceRule("A", 50, 20, 3),
            DiscountedPriceRule("B", 30, 15, 2),
            DiscountedPriceRule("C", 20, 0, 1),
            DiscountedPriceRule("D", 15, 0, 1),
        )
        assertEquals(expectedRules, parseRules(rulesAsString))
    }
}