import java.text.SimpleDateFormat
import java.util.Calendar

import scala.xml.XML


object Main extends App{
  try {
    val currentDate = new SimpleDateFormat("d/M/y").format(Calendar.getInstance().getTime())
    val content: scala.xml.Elem = XML.loadString((get("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + currentDate)))
    val rates = (content \ "Valute").map(x => (x \ "CharCode").text -> (x \ "Value").text.replaceAll(",", ".")).toMap
    println(
      args(0).toInt +
        (args(1).toInt * (rates.get("USD").getOrElse("0")).toDouble) +
        (args(2).toInt * (rates.get("EUR").getOrElse("0")).toDouble))
  } catch {
    case ioe: java.io.IOException => "IOException!!!"
    case ste: java.net.SocketTimeoutException => "Timeout!!!"
  }

  def get(url: String,
          connectTimeout: Int = 5000,
          readTimeout: Int = 5000,
          requestMethod: String = "GET") =
  {
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = io.Source.fromInputStream(inputStream,"windows-1251").mkString
    if (inputStream != null) inputStream.close
    content
  }
}
