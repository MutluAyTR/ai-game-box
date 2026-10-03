package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.example.data.model.MarketType
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.SocialCoupon
import com.example.data.model.Ticket
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

data class ParsedCouponDeepLink(
  val code: String,
  val title: String,
  val totalOdds: Double,
  val author: String,
  val selections: List<SlipSelection>
)

object CouponDeepLinkManager {

  private const val SCHEME = "tahminarena"
  private const val HOST = "kupon"
  private const val WEB_HOST = "https://tahminarena.com/kupon"

  /**
   * Generates a deep link URI for a placed Ticket
   */
  fun generateDeepLink(ticket: Ticket, author: String = "Kullanıcı"): String {
    val encodedData = encodeSelections(ticket.selections)
    return "$SCHEME://$HOST?code=${ticket.ticketNumber}&title=${Uri.encode(ticket.type.label)}&odds=%.2f&author=${Uri.encode(author)}&data=$encodedData".format(ticket.totalOdds)
  }

  /**
   * Generates a shareable web link fallback
   */
  fun generateWebShareLink(ticket: Ticket, author: String = "Kullanıcı"): String {
    val encodedData = encodeSelections(ticket.selections)
    return "$WEB_HOST?code=${ticket.ticketNumber}&title=${Uri.encode(ticket.type.label)}&odds=%.2f&author=${Uri.encode(author)}&data=$encodedData".format(ticket.totalOdds)
  }

  /**
   * Generates deep link for a SocialCoupon
   */
  fun generateDeepLinkForSocialCoupon(coupon: SocialCoupon): String {
    val encodedData = encodeSelections(coupon.selections)
    return "$SCHEME://$HOST?code=SOC-${coupon.id}&title=${Uri.encode(coupon.title)}&odds=%.2f&author=${Uri.encode(coupon.authorName)}&data=$encodedData".format(coupon.totalOdds)
  }

  /**
   * Encodes SlipSelections into base64 JSON
   */
  fun encodeSelections(selections: List<SlipSelection>): String {
    val jsonArray = JSONArray()
    for (sel in selections) {
      val obj = JSONObject()
      obj.put("matchId", sel.matchId)
      obj.put("matchTeams", sel.matchTeams)
      obj.put("marketType", sel.marketType.name)
      obj.put("selectionId", sel.selectionId)
      obj.put("selectionName", sel.selectionName)
      obj.put("odd", sel.odd)
      obj.put("isLive", sel.isLive)
      jsonArray.put(obj)
    }
    val rawBytes = jsonArray.toString().toByteArray(StandardCharsets.UTF_8)
    return Base64.encodeToString(rawBytes, Base64.URL_SAFE or Base64.NO_WRAP)
  }

  /**
   * Decodes deep link URI into ParsedCouponDeepLink
   */
  fun parseDeepLink(uriString: String): ParsedCouponDeepLink? {
    return try {
      val uri = Uri.parse(uriString)
      val code = uri.getQueryParameter("code") ?: "KPN-${System.currentTimeMillis() % 10000}"
      val title = uri.getQueryParameter("title") ?: "Paylaşılan Kupon"
      val oddsStr = uri.getQueryParameter("odds") ?: "1.00"
      val odds = oddsStr.replace(',', '.').toDoubleOrNull() ?: 1.00
      val author = uri.getQueryParameter("author") ?: "Kupondaş Üyesi"
      val data = uri.getQueryParameter("data") ?: return null

      val decodedBytes = Base64.decode(data, Base64.URL_SAFE or Base64.NO_WRAP)
      val jsonArray = JSONArray(String(decodedBytes, StandardCharsets.UTF_8))
      val selections = mutableListOf<SlipSelection>()

      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val matchId = obj.optString("matchId", "m1")
        val matchTeams = obj.optString("matchTeams", "Maç")
        val marketTypeName = obj.optString("marketType", MarketType.MATCH_RESULT.name)
        val marketType = try {
          MarketType.valueOf(marketTypeName)
        } catch (_: Exception) {
          MarketType.MATCH_RESULT
        }
        val selectionId = obj.optString("selectionId", "sel_1")
        val selectionName = obj.optString("selectionName", "1")
        val odd = obj.optDouble("odd", 1.50)
        val isLive = obj.optBoolean("isLive", false)

        selections.add(
          SlipSelection(
            matchId = matchId,
            matchTeams = matchTeams,
            marketType = marketType,
            selectionId = selectionId,
            selectionName = selectionName,
            odd = odd,
            isLive = isLive,
            matchStatus = if (isLive) MatchStatus.LIVE else MatchStatus.SCHEDULED
          )
        )
      }

      ParsedCouponDeepLink(
        code = code,
        title = title,
        totalOdds = odds,
        author = author,
        selections = selections
      )
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Triggers Android native Share Sheet
   */
  fun shareDeepLink(context: Context, ticketNumber: String, totalOdds: Double, deepLink: String) {
    val shareText = """
      🔥 TahminArena'da hazırladığım kuponu hemen simülasyon sepetine ekle!
      Kupon No: $ticketNumber
      Toplam Oran: %.2f
      
      👇 Kuponu sepete yüklemek için tıkla:
      $deepLink
    """.trimIndent().format(totalOdds)

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    val shareChooser = Intent.createChooser(sendIntent, "Kuponu Paylaş")
    shareChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareChooser)
  }

  /**
   * Copies link to Android Clipboard
   */
  fun copyToClipboard(context: Context, text: String, label: String = "TahminArena Kupon Linki") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard?.setPrimaryClip(clip)
  }
}
