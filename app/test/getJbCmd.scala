package test

import java.io.File

import org.apache.commons.io.FileUtils

object getJbCmd {

  def main(args: Array[String]): Unit = {

    val path = "/mnt/sdb/xwq/Jbrowser/apache-tomcat-9.0.0.M11/webapps/jb/my_data/PODB/CP/"

    new File("D:\\藻类细胞器\\cpJbrowser").listFiles().foreach { x =>


      val n = x.getName
      val sh = s"cd /mnt/sdb/xwq/Jbrowser/apache-tomcat-9.0.0.M11/webapps/jb/my_data/PODB/CP/$n;\n"+
      s"../../../../bin/prepare-refseqs.pl -fasta $n.fasta;\n"+
      s"../../../../bin/prepare-refseqs.pl --conf data/trackList.json;\n"+
      s"../../../../bin/flatfile-to-json.pl --gff $n.gff --trackType FeatureTrack --trackLabel Annotation;\n"+
      s"../../../../bin/biodb-to-json.pl --conf data/trackList.json ;\n"

      FileUtils.writeStringToFile(new File("D:\\藻类细胞器/getJB,sh"),sh,true)
    }

  }
}
