package com.constructiveproof.sparkstreamingexample

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkContext._

// Necessary for the PairRDDFunctions which allow us to sort (K,V)
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.twitter._
import twitter4j.auth.{Authorization, OAuthAuthorization}

object TwitterHashPopularity {


  /**
   * A List of words to filter Twitter statuses on. Any tweet which
   * contains one of these words will be sampled into the stream.
   */
  val filters = List("vodka", "scotch", "rye", "gin")

  /**
   * The main entry point to our StreamingApp. Will be hit when you type
   * `sbt run`.
   *
   * @param args command line arguments, currently unused.
   */
  def main(args: Array[String]) {

    // Set up the Twitter client so we can connect and stream
    val config = new Settings(ConfigFactory.load())
    val auth = configureTwitterAuth(config)

    // Set up a Spark streaming context
    val ssc = configureStreamContext

    // Start monitoring the Twitter stream. filters are defined down below,
    // a list of words we want to pull out of the stream as it goes by.
    val tweets = TwitterUtils.createStream(ssc, auth, filters)

    // Break the stream down into statuses, words, and hashtags
    val statuses = tweets.map(status => status.getText())
    val words = statuses.flatMap(status => status.split(" "))
    val hashtags = words.filter(word => word.startsWith("#"))

    // Define a Spark streaming window of a bit less than one hour, sample the
    // stream every 5 seconds, and then rank hashtags according to how many
    // times they appeared in the window.
    val windowDuration = Minutes(59)
    val slideDuration = Seconds(5)
    val tagCounts = hashtags.countByValueAndWindow(windowDuration, slideDuration)
    val sortedTags = tagCounts.map { case(tag, cnt) => (cnt, tag) }.transform(rdd => rdd.sortByKey(false))

    // print the current value of sortedTags to the console.
    sortedTags.print()

    // Checkpoints are used to keep streaming state in case of temporary
    // stream failure.
    ssc.checkpoint("/tmp")

    // Everything is now configured. Start the streaming context and make
    // the actual connection.
    ssc.start()
  }


  private


  /**
   * Returns a Spark streaming context object.
   */
  def configureStreamContext: StreamingContext = {
    val master = "local"
    val appName = "Spark Streaming Example"
    val batchInterval = Seconds(5)
    val ssc = new StreamingContext(master, appName, batchInterval)
    ssc
  }

  /**
   * Sets up a Twitter connection using values from a Typesafe Config
   * settings object.
   *
   * @param config a Settings object containing Twitter auth info
   * @return an OAuthAuthorization wrapped in Option
   */
  def configureTwitterAuth(config: Settings): Option[Authorization] = {
    val creds = new twitter4j.conf.ConfigurationBuilder()
      .setOAuthConsumerKey(config.apiKey)
      .setOAuthConsumerSecret(config.apiSecret)
      .setOAuthAccessToken(config.accessToken)
      .setOAuthAccessTokenSecret(config.accessTokenSecret)
      .build
    val auth = new OAuthAuthorization(creds)
    Some(auth)
  }

}
