package com.assignment;

public class TwitterCase {
  public static void main(String[] args) throws Exception {
  //   final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

  //   Properties twitterCredentials = new Properties();
  //   twitterCredentials.setProperty(TwitterSource.CONSUMER_KEY, "");
  //   twitterCredentials.setProperty(TwitterSource.CONSUMER_SECRET, "");
  //   twitterCredentials.setProperty(TwitterSource.TOKEN, "");
  //   twitterCredentials.setProperty(TwitterSource.TOKEN_SECRET, "");

  //   DataStream < String > twitterData = env.addSource(new TwitterSource(twitterCredentials));

  //   twitterData.flatMap(new TweetParser())
  //     .addSink(StreamingFileSink
  //       .forRowFormat(new Path("/home/jivesh/tweet"),
  //         new SimpleStringEncoder < Tuple2 < String, Integer >> ("UTF-8"))
  //       .withRollingPolicy(DefaultRollingPolicy.builder().build())
  //       .build());

  //   env.execute("Twitter Example");
  // }

  // public static class TweetParser implements FlatMapFunction < String, Tuple2 < String, Integer >> {

  //   public void flatMap(String value, Collector < Tuple2 < String, Integer >> out) throws Exception {
  //     ObjectMapper jsonParser = new ObjectMapper();
  //     JsonNode node = jsonParser.readValue(value, JsonNode.class);

  //     boolean isEnglish =
  //       node.has("user") &&
  //       node.get("user").has("lang") &&
  //       node.get("user").get("lang").asText().equals("en");

  //     boolean hasText = node.has("text");

  //     if (isEnglish && hasText) {
  //       String tweet = node.get("text").asText();

  //       out.collect(new Tuple2 < String, Integer > (tweet, 1));
  //     }
  //   }
  }
}