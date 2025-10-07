package com.transactions;

import java.sql.Date;
import java.util.Properties;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.HttpHost;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.index.IndexRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.Requests;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.common.xcontent.XContentType;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import com.transactions.dto.TransSalesPerCategory;
import com.transactions.dto.TransSalesPerDay;
import com.transactions.dto.Transaction;
import com.udemy.util.MyApp;

public class TransactionApp {
        private JdbcExecutionOptions execOptions;
        private JdbcConnectionOptions connOptions;

        public TransactionApp() {
                execOptions = new JdbcExecutionOptions.Builder()
                                .withBatchSize(1000)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build();

                connOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl(MyApp.jdbcUrl)
                                .withDriverName(MyApp.jdbcDriver)
                                .withUsername(MyApp.jdbcUser)
                                .withPassword(MyApp.jdbcPass)
                                .build();
        }

        public void createAndInsertTrans(DataStream<Transaction> transactionStream) {
                // Setup JDBC Execution and Connection Options
                String createSql = "CREATE TABLE IF NOT EXISTS transactions ("
                                + "transaction_id VARCHAR(255) PRIMARY KEY, "
                                + "product_id VARCHAR(255), "
                                + "product_name VARCHAR(255), "
                                + "product_category VARCHAR(255), "
                                + "product_price DOUBLE PRECISION, "
                                + "product_quantity INTEGER, "
                                + "product_brand VARCHAR(255), "
                                + "total_amount DOUBLE PRECISION, "
                                + "currency VARCHAR(255), "
                                + "customer_id VARCHAR(255), "
                                + "transaction_date TIMESTAMP, "
                                + "payment_method VARCHAR(255) "
                                + ")";
                transactionStream.addSink(JdbcSink.sink(createSql,
                                (JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {
                                }, execOptions, connOptions))
                                .name("Create Transactions Table Sink");

                String insertSql = "INSERT INTO transactions(transaction_id, product_id, product_name, product_category, product_price, product_quantity, product_brand, total_amount, currency, customer_id, transaction_date, payment_method) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                                + "ON CONFLICT (transaction_id) DO UPDATE SET "
                                + "product_id = EXCLUDED.product_id, "
                                + "product_name  = EXCLUDED.product_name, "
                                + "product_category  = EXCLUDED.product_category, "
                                + "product_price = EXCLUDED.product_price, "
                                + "product_quantity = EXCLUDED.product_quantity, "
                                + "product_brand = EXCLUDED.product_brand, "
                                + "total_amount  = EXCLUDED.total_amount, "
                                + "currency = EXCLUDED.currency, "
                                + "customer_id  = EXCLUDED.customer_id, "
                                + "transaction_date = EXCLUDED.transaction_date, "
                                + "payment_method = EXCLUDED.payment_method "
                                + "WHERE transactions.transaction_id = EXCLUDED.transaction_id";
                transactionStream.addSink(JdbcSink.sink(insertSql,
                                (JdbcStatementBuilder<Transaction>) (ps, transaction) -> {
                                        ps.setString(1, transaction.getTransactionId());
                                        ps.setString(2, transaction.getProductId());
                                        ps.setString(3, transaction.getProductName());
                                        ps.setString(4, transaction.getProductCategory());
                                        ps.setDouble(5, transaction.getProductPrice());
                                        ps.setInt(6, transaction.getProductQuantity());
                                        ps.setString(7, transaction.getProductBrand());
                                        ps.setDouble(8, transaction.getTotalAmount());
                                        ps.setString(9, transaction.getCurrency());
                                        ps.setString(10, transaction.getCustomerId());
                                        ps.setTimestamp(11, transaction.getTransactionDate());
                                        ps.setString(12, transaction.getPaymentMethod());
                                }, execOptions, connOptions))
                                .name("Insert into transactions table sink");
        }

        public void createAndInsertSalesPerCat(DataStream<TransSalesPerCategory> salesPerCatStream) {
                String createSql = "CREATE TABLE IF NOT EXISTS sales_per_category ("
                                + "transaction_date DATE "
                                + ",category VARCHAR(255) "
                                + ",total_sales DOUBLE PRECISION "
                                + ",PRIMARY KEY (transaction_date, category)"
                                + ")";
                salesPerCatStream.addSink(JdbcSink.sink(createSql,
                                (JdbcStatementBuilder<TransSalesPerCategory>) (ps, transaction) -> {
                                }, execOptions, connOptions))
                                .name("Create Sales Per Category Table");

                String insertSql = "INSERT INTO sales_per_category(transaction_date, category, total_sales) "
                                + "VALUES (?, ?, ?) "
                                + "ON CONFLICT (transaction_date, category) DO UPDATE SET "
                                + "total_sales = EXCLUDED.total_sales "
                                + "WHERE sales_per_category.category = EXCLUDED.category "
                                + "AND sales_per_category.transaction_date = EXCLUDED.transaction_date";

                salesPerCatStream.addSink(JdbcSink.sink(insertSql,
                                (JdbcStatementBuilder<TransSalesPerCategory>) (ps, rec) -> {
                                        ps.setDate(1, rec.getTransactionDate());
                                        ps.setString(2, rec.getCategory());
                                        ps.setDouble(3, rec.getTotalSales());
                                }, execOptions, connOptions))
                                .name("Insert into sales per category table");
        }

        public void createAndInsertSalesPerDay(DataStream<TransSalesPerDay> salesPerDayStream) {
                String createSql = "CREATE TABLE IF NOT EXISTS sales_per_day ("
                                + "transaction_date DATE PRIMARY KEY "
                                + ",total_sales DOUBLE PRECISION "
                                + ")";
                salesPerDayStream.addSink(JdbcSink.sink(createSql,
                                (JdbcStatementBuilder<TransSalesPerDay>) (ps, transaction) -> {
                                }, execOptions, connOptions))
                                .name("Create Sales Per Category Table");

                String insertSql = "INSERT INTO sales_per_day(transaction_date, total_sales) "
                                + "VALUES (?,?) "
                                + "ON CONFLICT (transaction_date) DO UPDATE SET "
                                + "total_sales = EXCLUDED.total_sales "
                                + "WHERE sales_per_day.transaction_date = EXCLUDED.transaction_date";

                salesPerDayStream.addSink(JdbcSink.sink(insertSql,
                                (JdbcStatementBuilder<TransSalesPerDay>) (ps, rec) -> {
                                        ps.setDate(1, rec.getTransactionDate());
                                        ps.setDouble(2, rec.getTotalSales());
                                }, execOptions, connOptions))
                                .name("Insert into sales per day table");
        }

        public void insertTrans2ES(DataStream<Transaction> transactionStream) {
                transactionStream.sinkTo(
                                new Elasticsearch7SinkBuilder<Transaction>()
                                                .setHosts(new HttpHost("localhost", 9200, "http"))
                                                .setEmitter((transaction, runtimeContext, requestIndexer) -> {
                                                        try {
                                                                String json = Transaction.toJSON(transaction);
                                                                // System.out.println(json);
                                                                IndexRequest indexRequest = Requests.indexRequest()
                                                                                .index("transactions")
                                                                                .id(transaction.getTransactionId())
                                                                                .source(json, XContentType.JSON);
                                                                requestIndexer.add(indexRequest);
                                                        } catch (Exception e) {
                                                                System.err.println("Error indexing transaction: "
                                                                                + e.getMessage());
                                                                e.printStackTrace();
                                                        }
                                                })
                                                .setBulkFlushMaxActions(1)
                                                .setBulkFlushInterval(1000)
                                                .build())
                                .name("Elasticsearch Sink");
        }

        public static void main(String[] args) throws Exception {
                // Read from Kafka
                TransactionApp transactionApp = new TransactionApp();

                final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ConsumerConfig.GROUP_ID_CONFIG, "TransactionApp");
                config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                // config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
                config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

                KafkaSource<String> kfkSource = KafkaSource.<String>builder()
                                .setProperties(config)
                                .setTopics(MyApp.kafkaTopic)
                                .setValueOnlyDeserializer(new SimpleStringSchema())
                                .build();
                DataStream<String> stream = env.fromSource(kfkSource, WatermarkStrategy.noWatermarks(), "Kafka source");

                // DataStream<String> stream = env.addSource(new
                // FlinkKafkaConsumer<>(MyApp.kafkaTopic, new SimpleStringSchema(),config));

                WatermarkStrategy<Transaction> ws = WatermarkStrategy
                                .<Transaction>forMonotonousTimestamps()
                                .withTimestampAssigner((event, timestamp) -> event.getTransactionDate().getTime());

                DataStream<Transaction> transactionStream = stream.map(str -> {
                        return Transaction.fromJSON(str);
                }).assignTimestampsAndWatermarks(ws);

                transactionStream.print();
                // transactionStream.writeAsText("~/out.txt");
                transactionApp.createAndInsertTrans(transactionStream);
                transactionApp.insertTrans2ES(transactionStream);

                DataStream<TransSalesPerCategory> salesPerCatStream = transactionStream.map(t -> {
                        return new TransSalesPerCategory(new Date(t.getTransactionDate().getTime()),
                                        t.getProductCategory(),
                                        t.getTotalAmount());
                })
                                .keyBy(TransSalesPerCategory::getCategory)
                                .reduce((cur, pre) -> {
                                        cur.setTotalSales(cur.getTotalSales() + pre.getTotalSales());
                                        return cur;
                                });
                transactionApp.createAndInsertSalesPerCat(salesPerCatStream);

                DataStream<TransSalesPerDay> salesPerDayStream = transactionStream.map(t -> {
                        return new TransSalesPerDay(new Date(t.getTransactionDate().getTime()),
                                        t.getTotalAmount());
                })
                                .keyBy(TransSalesPerDay::getTransactionDate)
                                .reduce((cur, pre) -> {
                                        cur.setTotalSales(cur.getTotalSales() + pre.getTotalSales());
                                        return cur;
                                });
                transactionApp.createAndInsertSalesPerDay(salesPerDayStream);
                env.execute("Flink Transaction Realtime Streaming");
        }
}
