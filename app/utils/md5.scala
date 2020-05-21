package utils

import play.shaded.oauth.org.apache.commons.codec.digest.DigestUtils


object md5 {


  def  encryption(pwd : String) : Array[Byte] = {
    val md5 = DigestUtils.md5(pwd.getBytes())
    md5.reverse
  }


  def main(args: Array[String]): Unit = {
    println(Utils.date.toString)
  }
}
