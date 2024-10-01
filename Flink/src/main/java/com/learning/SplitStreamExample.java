package com.learning;

public class SplitStreamExample 
{
     public static void main(String[] args) throws Exception
 {
         // set up the stream execution environment
	//  final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
	
    //     // Checking input parameters
	// final ParameterTool params = ParameterTool.fromArgs(args);
	
	// // make parameters available in the web interface
	// env.getConfig().setGlobalJobParameters(params);

	// DataStream<String> text = env.readTextFile("/opt/stream/oddeven");
	
	// SplitStream<Integer> evenOddStream = text.map(new MapFunction<String, Integer>()
	// 		{
	// 	public Integer map(String value)
	// 	{
	// 		return Integer.parseInt(value);
	// 	}})
		
	//    .split(new OutputSelector<Integer>()
	// 		{
	// 	public Iterable<String> select(Integer value)
	// 	{
	// 		List<String> out = new ArrayList<String>();
	// 		if (value%2 == 0)
	// 			out.add("even");              // label element  --> even 454   odd 565 etc
	// 		else
	// 			out.add("odd");
	// 		return out;
	// 	}
	// });
	
	// DataStream<Integer> evenData = evenOddStream.select("even");
	// DataStream<Integer> oddData = evenOddStream.select("odd");
	
	
    // evenData.print();
	// oddData.print();
    // // evenData.writeAsText("/home/jivesh/even");
	// // oddData.writeAsText("/home/jivesh/odd");
	
	// // execute program
	// env.execute("ODD EVEN");
    }
}
    
  
