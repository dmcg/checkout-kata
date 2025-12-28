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
            PlainPriceRule("A", 50),
            DiscountedPriceRule("A", 20, 3),
            PlainPriceRule("B", 30),
            DiscountedPriceRule("B", 15, 2),
            PlainPriceRule("C", 20),
            PlainPriceRule("D", 15),
        )
        assertEquals(expectedRules, parseRules(rulesAsString))
    }
}