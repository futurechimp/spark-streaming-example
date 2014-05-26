package com.constructiveproof.sparkstreamingexample

import com.typesafe.config.Config

class Settings(config: Config) {
  val apiKey =       config.getString("twitter4j.oauth.apiKey")
  val apiSecret =    config.getString("twitter4j.oauth.apiSecret")
  val accessToken =       config.getString("twitter4j.oauth.accessToken")
  val accessTokenSecret = config.getString("twitter4j.oauth.accessTokenSecret")
}