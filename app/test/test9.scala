package test

import java.io.File

import org.apache.commons.io.FileUtils


object test9 {

  def main(args: Array[String]): Unit = {

    val path = "I:\\mollusk_mt_database\\fasta\\pep"

    new File(path).listFiles().foreach{x=>
      val name = x.getName.split('.').head
      FileUtils.moveFile(x,new File(s"$path/$name.pep"))
    }

  }
}
