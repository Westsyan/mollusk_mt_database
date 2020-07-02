package test

import java.io.File

import org.apache.commons.io.FileUtils

object test7 {

  def main(args: Array[String]): Unit = {

    val path = "D:\\软体动物线粒体数据库\\img"

      new File("D:\\软体动物线粒体数据库\\2020.5.19数据库\\2020.5.19数据库信息\\数据库照片").listFiles().foreach{x=>
        if(x.isDirectory){
          x.listFiles().foreach{y=>
            if(y.isDirectory){
              y.listFiles().map{z=>
                if(z.isDirectory){
                  z.listFiles().map{q=>
                    FileUtils.copyFile(q,new File(path + "/" + q.getName))
                  }
                }else{
                  FileUtils.copyFile(z,new File(path + "/" + z.getName))
                }
              }
            }else{
              FileUtils.copyFile(y,new File(path + "/" + y.getName))
            }
          }
        }else{
         FileUtils.copyFile(x,new File(path + "/" + x.getName))
        }




    }
  }

}
