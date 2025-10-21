import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AiParsingTests {
    val rulesAsString = """
           A     50       3 for 130
           B     30       buy 2 get a discount of 15
           C     2 times 10
           the base price of B is twice the price of D"""
        .trimIndent()

    val apiKey =
        "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"

    @Test fun parseRules() {
        val expectedRules: List<PriceRule> = listOf(
            DiscountedPriceRule("A", 50, 20, 3),
            DiscountedPriceRule("B", 30, 15, 2),
            DiscountedPriceRule("C", 20, 0, 1),
            DiscountedPriceRule("D", 15, 0, 1),
        )
        assertEquals(expectedRules, parseRulesWithAi(apiKey,rulesAsString))
    }
}