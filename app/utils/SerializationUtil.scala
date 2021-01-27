package utils

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.{DateFormat, DefaultFormats, Extraction, Formats, JValue, NoTypeHints, TypeHints}

import java.util.{Date, TimeZone}

object SerializationUtil {

  val formats: Formats = defaultFormats(NoTypeHints)

  def fromJson[T](json: JValue)(implicit m: Manifest[T], formats: Formats = formats): T =
    json.extract[T](formats, m)

  def toJson(a: Any)(implicit formats: Formats = formats): JValue = Extraction.decompose(a)

  def defaultFormats(hints: TypeHints) = new Formats {
    val dateFormat: DateFormat = new DateFormat {
      override def parse(s: String): Option[Date] =
        try {
          Option(
            DateTime
              .parse(s, ISODateTimeFormat.dateTimeParser().withZoneUTC())
              .withZone(DateTimeZone.forID(timezone.getID))
              .toDate
          )
        } catch {
          case e: IllegalArgumentException => None
        }
      override def format(d: Date): String = DefaultFormats.lossless.dateFormat.format(d)
      override def timezone: TimeZone = DefaultFormats.lossless.dateFormat.timezone
    }
    override val typeHints: TypeHints = hints
    override val strictOptionParsing: Boolean = true
  }

}
