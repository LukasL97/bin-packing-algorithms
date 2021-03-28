package utils

import org.bson.BsonInt64
import org.bson.BsonString
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonBoolean
import org.mongodb.scala.bson.BsonDecimal128
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonDouble
import org.mongodb.scala.bson.BsonInt32
import org.mongodb.scala.bson.BsonNull
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.bson.BsonValue
import org.{json4s => j4s}
import play.api.libs.{json => pjson}

import scala.jdk.CollectionConverters._

object JsonConversions {

  implicit def toJson4s(json: play.api.libs.json.JsValue): org.json4s.JValue = json match {
    case pjson.JsString(str) => j4s.JString(str)
    case pjson.JsNull => j4s.JNull
    case pjson.JsBoolean(value) => j4s.JBool(value)
    case pjson.JsNumber(value) => j4s.JDecimal(value)
    case pjson.JsArray(items) => j4s.JArray(items.map(toJson4s).toList)
    case pjson.JsObject(items) => j4s.JObject(items.map { case (k, v) => k -> toJson4s(v) }.toList)
  }

  implicit def toPlayJson(json: org.json4s.JValue): play.api.libs.json.JsValue = json match {
    case j4s.JString(str) => pjson.JsString(str)
    case j4s.JNothing => pjson.JsNull
    case j4s.JNull => pjson.JsNull
    case j4s.JDecimal(value) => pjson.JsNumber(value)
    case j4s.JDouble(value) => pjson.JsNumber(value)
    case j4s.JInt(value) => pjson.JsNumber(BigDecimal(value))
    case j4s.JLong(value) => pjson.JsNumber(BigDecimal(value))
    case j4s.JBool(value) => pjson.JsBoolean(value)
    case j4s.JSet(fields) => pjson.JsArray(fields.toList.map(toPlayJson))
    case j4s.JArray(fields) => pjson.JsArray(fields.map(toPlayJson))
    case j4s.JObject(fields) => pjson.JsObject(fields.map { case (k, v) => k -> toPlayJson(v) }.toMap)
  }

  implicit def toPlayJson(bson: BsonValue): play.api.libs.json.JsValue = bson match {
    case id: BsonObjectId => pjson.JsString(id.getValue.toString)
    case str: BsonString => pjson.JsString(str.getValue)
    case n: BsonNull => pjson.JsNull
    case num: BsonDecimal128 => pjson.JsNumber(num.doubleValue())
    case num: BsonDouble => pjson.JsNumber(num.getValue())
    case num: BsonInt32 => pjson.JsNumber(num.getValue)
    case num: BsonInt64 => pjson.JsNumber(num.getValue)
    case b: BsonBoolean => pjson.JsBoolean(b.getValue)
    case array: BsonArray => pjson.JsArray(array.getValues.asScala.map(toPlayJson))
    case doc: BsonDocument =>
      pjson.JsObject(
        doc
          .entrySet()
          .asScala
          .map { entry =>
            entry.getKey -> toPlayJson(entry.getValue)
          }
          .toMap
      )
  }

}
