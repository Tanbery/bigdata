package com.learned;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.operators.base.JoinOperatorBase.JoinHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;

// @SuppressWarnings("serial")
public class JoinInner {
	
	public static void main(String[] args) throws Exception {
		
		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		final ParameterTool params = ParameterTool.fromArgs(args);
		// make parameters available in the web interface
		env.getConfig().setGlobalJobParameters(params);

		// Read person file and generate tuples out of each string read
		DataSet<Tuple2<Integer, String>> personSet = env.fromCollection(readFile("location"))
				.map(new MapFunction<String, Tuple2<Integer, String>>() // presonSet = tuple of (1 John)
				{
					public Tuple2<Integer, String> map(String value) {
						String[] words = value.split(","); // words = [ {1} {John}]
						return new Tuple2<Integer, String>(Integer.parseInt(words[0]), words[1]);
					}
				});
		
		// Read location file and generate tuples out of each string read
		DataSet<Tuple2<Integer, String>> locationSet = env.fromCollection(readFile("person"))
				.map(new MapFunction<String, Tuple2<Integer, String>>() { // locationSet = tuple of (1 DC)
					public Tuple2<Integer, String> map(String value) {
						String[] words = value.split(",");
						return new Tuple2<Integer, String>(Integer.parseInt(words[0]), words[1]);
					}
				});

		// join datasets on person_id
		// joined format will be <id, person_name, state>
		DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet,JoinHint.OPTIMIZER_CHOOSES).where(0).equalTo(0)
				.with(new JoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple3<Integer, String, String>>() {

					public Tuple3<Integer, String, String> join(Tuple2<Integer, String> person,
							Tuple2<Integer, String> location) {
						return new Tuple3<Integer, String, String>(person.f0, person.f1, location.f1); // returns tuple
																										// of (1 John
																										// DC)
					}
				});
		if (params.has("output")) 
			joined.writeAsCsv(params.get("output"), "\n", " ");
		else 
			joined.print();
		// locationSet.print();
		// personSet.print();

		env.execute("Join example");
	}
	private static ArrayList<String> readFile(String filename) {
        InputStream in = JoinInner.class.getClassLoader().getResourceAsStream(filename);
        ArrayList<String> out = new ArrayList<>();
        Scanner s = new Scanner(in);
        s.useDelimiter("\n");
        while(s.hasNext()) {
            out.add(s.next());
        }
        s.close();
        return out;
    }
	 
}
