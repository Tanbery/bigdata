package com.salesAnalysis;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple6;
import com.salesAnalysis.dto.CategorySalesDTO;
import com.salesAnalysis.dto.OrderItem;
import com.salesAnalysis.dto.Product;

public class DataBatchJob {
	public static void main(String[] args) throws Exception {
		System.out.println("DataBatchJob started...");
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		DataSource<OrderItem> orderItems = env
				.readCsvFile(DataBatchJob.class.getClassLoader().getResource("Datasets/order_items.csv")
						.getPath())
				// .readCsvFile("./Datasets/order_items.csv")
				.ignoreFirstLine()
				.pojoType(OrderItem.class, "orderItemId", "orderId", "productId", "quantity",
						"pricePerUnit");

		DataSource<Product> products = env
				.readCsvFile(DataBatchJob.class.getClassLoader().getResource("Datasets/products.csv")
						.getPath())
				// .readCsvFile("./Datasets/products.csv")
				.ignoreFirstLine()
				.pojoType(Product.class, "productId", "name", "description", "price", "category");

		// join the datasets on the product Id
		DataSet<Tuple6<String, String, Float, Integer, Float, String>> joined = orderItems
				.join(products)
				.where("productId")
				.equalTo("productId")
				.with((JoinFunction<OrderItem, Product, Tuple6<String, String, Float, Integer, Float, String>>) (
						first, second) -> new Tuple6<>(
								second.productId.toString(),
								second.name,
								first.pricePerUnit,
								first.quantity,
								first.pricePerUnit * first.quantity,
								second.category))
				.returns(TypeInformation.of(
						new TypeHint<Tuple6<String, String, Float, Integer, Float, String>>() {
						}));

		// group by category to get the total sales and count
		DataSet<CategorySalesDTO> categorySales = joined
				.map(
						(MapFunction<Tuple6<String, String, Float, Integer, Float, String>, CategorySalesDTO>) record -> new CategorySalesDTO(
								record.f5, record.f4, 1))
				.returns(CategorySalesDTO.class)
				.groupBy("category")
				.reduce((ReduceFunction<CategorySalesDTO>) (value1, value2) -> new CategorySalesDTO(
						value1.getCategory(), value1.getTotalSales() + value2.getTotalSales(),
						value1.getCount() + value2.getCount()));

		// categorySales.print();
		// sort by total sales in descending order
		categorySales.sortPartition("totalSales", Order.DESCENDING).print();
		// categorySales.sortPartition("count", Order.DESCENDING).print();
		// categorySales.print();

		// env.execute("Sales Analysis");
	}
}
