package com.udemy;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.operators.base.JoinOperatorBase.JoinHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;

import com.udemy.dto.Location;
import com.udemy.dto.Person;
import com.util.MyApp;

public class OperJoins {
	
	public static void main(String[] args) throws Exception {
		
		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		final ParameterTool params = ParameterTool.fromArgs(args);
		// make parameters available in the web interface
		env.getConfig().setGlobalJobParameters(params);

		// Read person file and generate tuples out of each string read
		DataSet<Person> personSet = env.fromCollection(MyApp.readFile("person"))
				.map(new MapFunction<String, Person>() { // locationSet = tuple of (1 DC)
					public Person map(String value) {
						String[] words = value.split(",");
						return new Person(Integer.parseInt(words[0]), words[1]);
					}
				});
				
		// Read location file and generate tuples out of each string read
		DataSet<Location> locationSet = env.fromCollection(MyApp.readFile("location"))
				.map(new MapFunction<String, Location>() // presonSet = tuple of (1 John)
				{
					public Location map(String value) {
						String[] words = value.split(","); // words = [ {1} {John}]
						return new Location(Integer.parseInt(words[0]), words[1]);
					}
				});
				
		// join datasets on person_id
		// joined format will be <id, person_name, state>
		/* 
		public enum JoinHint {
			OPTIMIZER_CHOOSES,
			BROADCAST_HASH_FIRST,
			BROADCAST_HASH_SECOND,
			REPARTITION_HASH_FIRST,
			REPARTITION_HASH_SECOND,
			REPARTITION_SORT_MERGE;
		}*/

		DataSet<Tuple3<Integer, String, String>> innerJoined = personSet.join(locationSet,JoinHint.OPTIMIZER_CHOOSES).where("perId").equalTo("locId")
				.with(new JoinFunction<Person, Location, Tuple3<Integer, String, String>>() {

					public Tuple3<Integer, String, String> join(Person person, Location location) {
						return new Tuple3<Integer, String, String>(person.getPerId(), person.getName(), location.getName()); // returns tuple of (1 John DC)
					}
				});
		innerJoined.print();

		DataSet<Tuple3<Integer, String, String>> leftOuterJoin = personSet.leftOuterJoin(locationSet).where("perId").equalTo("locId")
			.with(new JoinFunction<Person, Location, Tuple3<Integer, String, String>>() {
				public Tuple3<Integer, String, String> join(Person person, Location location) {
					// check for nulls
					if (location == null) {
						return new Tuple3<Integer, String, String>(person.getPerId(), person.getName(), "NULL");
					}

					return new Tuple3<Integer, String, String>(person.getPerId(), person.getName(), location.getName());
				}
			});
		leftOuterJoin.print();

		DataSet<Tuple3<Integer, String, String>> rightOuterJoin = personSet.rightOuterJoin(locationSet).where("perId").equalTo("locId")
				.with(new JoinFunction<Person, Location, Tuple3<Integer, String, String>>() {

					public Tuple3<Integer, String, String> join(Person person, Location location){
						// check for nulls
						if (person == null) {
							return new Tuple3<Integer, String, String>(location.getLocId(), "NULL", location.getName());
						}

						return new Tuple3<Integer, String, String>(person.getPerId(), person.getName(), location.getName());
					}
				});		
		rightOuterJoin.print();

		DataSet<Tuple3<Integer, String, String>> fullOuterJoin = personSet.fullOuterJoin(locationSet).where("perId").equalTo("locId")
		.with(new JoinFunction<Person, Location, Tuple3<Integer, String, String>>(){

			public Tuple3<Integer, String, String> join(Person person, Location location) {
				// check for nulls
				if (location == null) {
					return new Tuple3<Integer, String, String>(person.getPerId(), person.getName(), "NULL");
				}
				// for rightOuterJoin
				else if (person == null)
					return new Tuple3<Integer, String, String>(location.getLocId(), "NULL", location.getName());

				return new Tuple3<Integer, String, String>(person.getPerId(), person.getName(), location.getName());
			}
		});
		fullOuterJoin.print();
		
		// if (params.has("output")) 
		// 	joined.writeAsCsv(params.get("output"), "\n", " ");
		// else 
		// 	joined.print();
		// locationSet.print();
		// personSet.print();

		// env.execute("Join example");
	}
	 
}
