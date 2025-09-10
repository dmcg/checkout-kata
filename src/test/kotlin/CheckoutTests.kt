import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CheckoutTests {

    @Test
    fun `test no items`() {
        assertEquals(0, price(""))
    }

    @Test
    fun `test As`() {
        assertEquals(50, price("A"))
        assertEquals(100, price("AA"))
        assertEquals(130, price("AAA"))
        assertEquals(180, price("AAAA"))
        assertEquals(230, price("AAAAA"))
        assertEquals(260, price("AAAAAA"))
        assertEquals(390, price("AAAAAAAAA"))
    }

    @Test
    fun `test Bs`() {
        assertEquals(30, price("B"))
        assertEquals(45, price("BB"))
        assertEquals(75, price("BBB"))
    }

    @Test
    fun `test Cs`() {
        assertEquals(20, price("C"))
        assertEquals(40, price("CC"))
        assertEquals(60, price("CCC"))
    }

    @Test
    fun `test Ds`() {
        assertEquals(15, price("D"))
        assertEquals(30, price("DD"))
        assertEquals(45, price("DDD"))
    }

    @Test
    fun mixed() {
        assertEquals(  0, price(""))
        assertEquals( 50, price("A"))
        assertEquals( 80, price("AB"))
        assertEquals(115, price("CDBA"))

        assertEquals(100, price("AA"))
        assertEquals(130, price("AAA"))
        assertEquals(180, price("AAAA"))
        assertEquals(230, price("AAAAA"))
        assertEquals(260, price("AAAAAA"))

        assertEquals(160, price("AAAB"))
        assertEquals(175, price("AAABB"))
        assertEquals(190, price("AAABBD"))
        assertEquals(190, price("DABABA"))
    }
}

private fun price(codes: String): Int {
    val checkout = Checkout()
    checkout.scanAll(codes)
    return checkout.total
}


private fun Checkout.scanAll(codes: String) {
    codes.forEach { scan(it.toString()) }
}

