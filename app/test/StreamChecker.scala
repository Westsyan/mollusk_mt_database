package test


class StreamChecker(_words: Array[String]) {
  var array = ""
  val reverseWords: Array[String] = _words.map(_.reverse)
  def query(letter: Char): Boolean = {
    array = letter + array
    if(_words.contains(letter.toString)){
      true
    }else{
      reverseWords.exists(x=> array.startsWith(x))
    }
  }

}

object StreamChecker{
  def main(args: Array[String]): Unit = {
    val streamChecker = new StreamChecker(Array("a","bw","c"))
    println(streamChecker.query('b'))
    println(streamChecker.query('w'))

  }
}
