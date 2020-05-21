package test

import java.io.File

import utils.ExecCommand

object test2 {

  def main(args: Array[String]): Unit = {
    val p = "D:\\藻类细胞器\\质体基因组"

    new File(p).listFiles().foreach { x =>
      x.listFiles().foreach { z =>
        z.listFiles().filter(_.getName.endsWith("gb")).foreach { y =>
          val command1 = "perl D:/藻类细胞器/藻类细胞器数据库构建/genbank_parser_v4.0.pl --type cds \"" + y.getAbsolutePath + "\""
          val command2 = "perl D:/藻类细胞器/藻类细胞器数据库构建/genbank_parser_v4.0.pl --type pep \"" + y.getAbsolutePath + "\""

          println(command1)
          println(command2)
          val exec = new ExecCommand()
          exec.exect(Array(command1, command2), x.getAbsolutePath)
        }
      }
    }
  }
}
