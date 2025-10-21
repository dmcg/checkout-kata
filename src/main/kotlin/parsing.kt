import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun parseRules(rulesAsString: String): List<PriceRule> {
    val itemLineRegex = Regex(
        """^\s*([A-Za-z])\s+(\d+)(?:\s+(\d+)\s*for\s*(\d+))?\s*$""",
        RegexOption.IGNORE_CASE
    )

    return rulesAsString
        .lines()
        .mapNotNull { line ->
            val match = itemLineRegex.find(line)
            if (match != null) {
                val (codeStr, unitStr, nStr, specialStr) = match.destructured
                val code = codeStr.uppercase()
                val unit = unitStr.toInt()
                if (nStr.isNotEmpty() && specialStr.isNotEmpty()) {
                    val discountPer = nStr.toInt()
                    val special = specialStr.toInt()
                    val discountAmount = (unit * nStr.toInt()) - special
                    DiscountedPriceRule(code, unit, discountAmount, discountPer)
                } else {
                    DiscountedPriceRule(code, unit, 0, 1)
                }
            } else {
                null
            }
        }
}

// Calls Anthropic Claude via HTTPS using Java's built-in HttpClient to interpret
// the pricing rules, using kotlinx.serialization to write and read JSON.
fun parseRulesWithAi(apiKey: String, rulesAsString: String): List<PriceRule> {
    val prompt = buildString {
        appendLine("Extract checkout pricing rules from the following table and return ONLY a JSON array with no extra text.")
        appendLine("Each item in the array must have exactly these fields: code (string), unit (int), discountAmount (int), discountPer (int).")
        appendLine("- unit = unit price in integers (e.g., cents)")
        appendLine("- If there's an N for M special, discountAmount = unit*N - M and discountPer = N")
        appendLine("- If there's no special, discountAmount = 0 and discountPer = 1")
        appendLine("Return ONLY the array, e.g.:")
        appendLine("[{\"code\":\"A\",\"unit\":50,\"discountAmount\":20,\"discountPer\":3}]")
        appendLine()
        appendLine("Rules table:")
        appendLine(rulesAsString)
    }

    // Build the request JSON using kotlinx.serialization
    val bodyObj = buildJsonObject {
        put("model", "claude-3-5-sonnet-latest")
        put("max_tokens", 256)
        put("temperature", 0)
        put("system", "You are a precise extraction engine. Output strictly the requested JSON with correct integers and field names only.")
        put("messages", buildJsonArray {
            add(buildJsonObject {
                put("role", "user")
                put("content", prompt)
            })
        })
    }
    val jsonCodec = Json { ignoreUnknownKeys = true }

    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.anthropic.com/v1/messages"))
        .timeout(Duration.ofSeconds(20))
        .header("content-type", "application/json")
        .header("x-api-key", apiKey)
        .header("anthropic-version", "2023-06-01")
        .POST(HttpRequest.BodyPublishers.ofString(jsonCodec.encodeToString<JsonObject>(bodyObj)))
        .build()

    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() !in 200..299) {
        throw IllegalStateException($$"HTTP ${response.statusCode()} from Anthropic: ${response.body().take(500)}")
    }

    // Read the response JSON using kotlinx.serialization
    val message = jsonCodec.decodeFromString<MessageResponse>(response.body())
    val assistantText = message.content.firstOrNull { it.text != null }?.text
        ?: throw IllegalStateException("No assistant text found in response")

    // The assistant returns the pure JSON array of rules; parse it with kotlinx.serialization
    return jsonCodec.decodeFromString<List<RuleDto>>(assistantText).map { dto ->
        DiscountedPriceRule(dto.code.uppercase(), dto.unit, dto.discountAmount, dto.discountPer)
    }
}

@Serializable
private data class ContentBlock(
    val type: String? = null,
    val text: String? = null,
)

@Serializable
private data class MessageResponse(
    val content: List<ContentBlock> = emptyList()
)

@Serializable
private data class RuleDto(
    val code: String,
    val unit: Int,
    val discountAmount: Int,
    val discountPer: Int,
)