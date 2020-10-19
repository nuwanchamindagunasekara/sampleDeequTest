package FromDatasetFoundOnInternet

import java.io.File

import com.amazon.deequ.VerificationResult.checkResultsAsDataFrame
import com.amazon.deequ.analyzers.runners.AnalyzerContext.successMetricsAsDataFrame
import com.amazon.deequ.analyzers.runners.{AnalysisRunner, AnalyzerContext}
import com.amazon.deequ.analyzers._
import com.amazon.deequ.checks.{Check, CheckLevel}
import com.amazon.deequ.{VerificationResult, VerificationSuite}
import org.apache.spark.sql.{DataFrame, SparkSession}

object sourceFileFromJaliyasProject {
  def main(args: Array[String]): Unit = {

    val warehouseLocation = new File("spark-warehouse").getAbsolutePath
    val spark = SparkSession
      .builder
      .master("local")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .appName("sourceFile")
      .enableHiveSupport()
      .getOrCreate()

    //val datasource=spark.read.json("datafile/1234aa")

    val datasource: DataFrame = spark.read.json("dataset/test.json")
    datasource.printSchema()
    datasource.show(20)

    val analysisResult: AnalyzerContext = {
      AnalysisRunner
        .onData(datasource)
        .addAnalyzer(Size())
        .addAnalyzer(Completeness("id"))
        .addAnalyzer(ApproxCountDistinct("id"))
        .addAnalyzer(Minimum("timeSpent"))
        .addAnalyzer(Mean("timeSpent"))
        .addAnalyzer(Maximum("timeSpent"))
        .addAnalyzer(Entropy("timeSpent"))
        .run()
    }

    val statistics = successMetricsAsDataFrame(spark, analysisResult)
    statistics.show(truncate = false)

    /*
    import spark.implicits._
        val suggestionResult={ConstraintSuggestionRunner()

        .onData(datasource)
        .addConstraintRules(Rules.DEFAULT)
        .run()}

       val suggestionDataFrame=suggestionResult.constraintSuggestions.flatMap{
          case (column,suggestions)=>
            suggestions.map{constraint=>
              (column,constraint.description,constraint.codeForConstraint)
            }
        }.toSeq.toDS()
        suggestionDataFrame.show() */

    val verificationResult: VerificationResult = {
      VerificationSuite()
        .onData(datasource)
        .addCheck(
          Check(CheckLevel.Error, "Data Validation Check")
            .hasSize(_ == 2805)
            .isComplete("id")
            .isUnique("id")
            .isNonNegative("timeSpent"))
        .run()
    }
    val resultDaraFrame = checkResultsAsDataFrame(spark, verificationResult)
    resultDaraFrame.show(truncate = false)


  }
}
