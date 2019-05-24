package services

import java.text.SimpleDateFormat
import java.util.Calendar

trait PriceBase {

  def getHeader = {
    Map("Content-Type" -> "application/x-www-form-urlencoded")
  }

}
