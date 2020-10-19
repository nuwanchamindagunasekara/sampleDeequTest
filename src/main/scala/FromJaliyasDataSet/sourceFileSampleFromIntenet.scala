package FromJaliyasDataSet

import java.io.File

import com.amazon.deequ.VerificationResult.checkResultsAsDataFrame
import com.amazon.deequ.analyzers.runners.AnalyzerContext.successMetricsAsDataFrame
import com.amazon.deequ.analyzers.runners.{AnalysisRunner, AnalyzerContext}
import com.amazon.deequ.analyzers._
import com.amazon.deequ.checks.{Check, CheckLevel}
import com.amazon.deequ.suggestions.{ConstraintSuggestionRunner, Rules}
import com.amazon.deequ.{VerificationResult, VerificationSuite}
import org.apache.spark.sql.Row.empty.schema
import org.apache.spark.sql.{DataFrame, SparkSession}

object sourceFileSampleFromIntenet {
  def main(args: Array[String]): Unit = {
//Spark session creation
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath
    val spark = SparkSession
      .builder
      .master("local")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .appName("sourceFile")
      .enableHiveSupport()
      .getOrCreate()

    //val datasource=spark.read.json("datafile/1234aa")
//import Dataset
    val datasource: DataFrame = spark.read.json("dataset/Sample-JSON-file-with-multiple-records-download.json")
    datasource.printSchema()
    datasource.show(5)
//analyze data
    val analysisResult: AnalyzerContext = {
      AnalysisRunner
        .onData(datasource)
        .addAnalyzer(Size())
        .addAnalyzer(Completeness("userId"))
        .addAnalyzer(ApproxCountDistinct("userId"))
        .addAnalyzer(Minimum("Amount"))
        .addAnalyzer(Mean("Amount"))
        .addAnalyzer(Maximum("Amount"))
        .addAnalyzer(Entropy("Amount"))
        .run()
    }

    val statistics = successMetricsAsDataFrame(spark, analysisResult)
    statistics.show(truncate = false)

 import spark.implicits._
    val suggestionResult = {
      ConstraintSuggestionRunner()

        .onData(datasource)
        .addConstraintRules(Rules.DEFAULT)
        .run()
    }

    val suggestionDataFrame = suggestionResult.constraintSuggestions.flatMap {
      case (column, suggestions) =>
        suggestions.map { constraint =>
          (column, constraint.description, constraint.codeForConstraint)
        }
    }.toSeq.toDS()
    suggestionDataFrame.show(truncate = false)

    val verificationResult: VerificationResult = {
      VerificationSuite()
        .onData(datasource)
        .addCheck(
          Check(CheckLevel.Error, "Data Validation Check")
            .hasSize(_ == 5)
            .isComplete("userId")
            .isUnique("userId")
            .isNonNegative("Amount"))
        .run()
    }
    val resultDaraFrame = checkResultsAsDataFrame(spark, verificationResult)
    resultDaraFrame.show(truncate = false)


  }
}
