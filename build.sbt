name := "SparkTest4"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"
//libraryDependencies +="org.apache.hadoop" % "hadoop-aws" % "3.0.0"
//libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.0.0"
//libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.0.0"
libraryDependencies += "com.databricks" % "spark-csv_2.11" % "1.2.0"
//libraryDependencies += "com.databricks" % "spark-redshift_2.11" % "3.0.0-preview1"
libraryDependencies += "com.amazon.deequ" % "deequ" % "1.0.4"
// https://mvnrepository.com/artifact/org.apache.spark/spark-hive
libraryDependencies += "org.apache.spark" %% "spark-hive" % "2.4.7"
//libraryDependencies ++= Seq( "org.spark-project.hive" % "hive-exec" % "0.13.1")

//libraryDependencies += "org.spark-project.hive" % "hive-exec" % "1.2.1.spark2"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.7"
libraryDependencies += "org.apache.xbean" % "xbean-spring" % "4.14"