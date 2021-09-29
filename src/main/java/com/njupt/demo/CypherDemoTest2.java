package com.njupt.demo;

import static com.njupt.util.GlobalParams.*;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.internal.javacompat.ExecutionEngine;
import org.neo4j.cypher.internal.javacompat.ExecutionResult;
import org.neo4j.graphalgo.impl.util.PathImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import com.njupt.util.Timekeeping;




public class CypherDemoTest2 {
	//public static String gDB = "Data/Test";
	
	public static String gDB = "neo4j-test/Example_1_2_test13";	 
	
	//public static String gDB = "neo4j-test/Integrated_cmt_edas_test";		
	//public static String gDB = "neo4j-test/Integrated_conference_confof_test";	
	//public static String gDB = "neo4j-test/Integrated_confof_ekaw_test";
	
	//public static String gDB = "neo4j-test/Integrated_GMap-mouse_human_test";
	//public static String gDB = "neo4j-test/Integrated_mouse-human_test";
	//public static String gDB = "neo4j-test/Integrated_fma-nci_small_test";
	
	public static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(gDB));

	/**
	 * 测试带参数的Cypher查询
	 * 
	 * @param query  查询字符串
	 * @param params 参数
	 */
	public static void paramCypher(StringBuilder query, Map<String, Object> params) {
		String _query = query.toString();
		System.out.println(_query);
		try (Transaction tx = graphDb.beginTx()) {
			Result result = graphDb.execute(_query, params);
			System.out.println(result.toString());
			tx.success();
		}
	}

	/**
	 * 简单的查询测试
	 * 
	 * @param query 查询字符串
	 */
	public static Result simpleCypher(StringBuilder query) {
		String _query = query.toString();
		System.out.println(_query);
		Result result = graphDb.execute(_query);
		return result;
		// System.out.println(result.dumpToString());
	}

	/**
	 * 清空StringBuilder
	 * 
	 * @param query 要清空的查询
	 */
	public static void clear(StringBuilder query) {
		query.delete(0, query.length());
	}

	public static void main(String[] args) {
		Timekeeping.begin();
		
		try (Transaction tx = graphDb.beginTx()) {

			// ***************V2.1 Cypher Test*********************

			// PART-1 简单查询，基于Cypher

			// 1. Get all node in the graph
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH (n)  RETURN n;"); 
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );
//	               Node node =(Node) temp.get( key );
//	               System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//	            }
//			}
			
			
	
			// 2. 取出所有的直接关联节点
//			StringBuilder query = new StringBuilder(); 
//			query.append("MATCH (n)-->(m)  RETURN n, m;"); 
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Node node =(Node) temp.get( key );
//	               System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//	            }
//	            System.out.println("===============================");
//			}
			
			 
			// 3. 取出具有关系(出度-outgoing)的节点
//			StringBuilder query = new StringBuilder(); 
//			query.append("MATCH (n)-->() RETURN n;");
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Node node =(Node) temp.get( key );
//	               System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//	            }
//	            System.out.println("===============================");
//			}
			
			
			//3.1 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH (uc)-[r1:INCLUDEDBY*]->(n) ");
//		    query.append("WHERE uc.Name='A1' and n.Name='B1' ");
//		    //query.append("return collect(distinct n) AS nd");
//		    //query.append("return nd");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	ArrayList<Node> nodes=(ArrayList) temp.get( "nd" );
//		        for(Node node: nodes)    	            
//		            System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//		         	
//		    	 System.out.println("===============================");
//		    }
			
			// 4. 返回节点指定的属性值
//			StringBuilder query = new StringBuilder(); 
//			query.append("MATCH (node)-->() RETURN node.Name;");		
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               //Node node =(Node) temp.get( key );
//	               //System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//	            }
//	            System.out.println("===============================");
//			}
			

			// 5. 访问关系 relationship，由关系连接节点时使用 "->",特别注意是一个"-"
//			StringBuilder query = new StringBuilder(); 
//			query.append("MATCH (node1)-[rel]->() RETURN rel;");
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
////	               if((Relationship)temp.get(key))
//	               Relationship rel=(Relationship) temp.get( key );
//	               System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY)+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
//	               System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString()+"  "+rel.getType());
//	               System.out.println(rel.getType());
//	            }
//	            System.out.println("===============================");
//			}
			
	

			// 6. 指定关系的类型
//			StringBuilder query = new StringBuilder(); 
//			query.append("MATCH(node1)-[:INCLUDEDBY]->(node2)");
//			query.append("RETURN node1,node2;");
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Node node =(Node) temp.get( key );
//	               System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//
//	            }
//	            System.out.println("===============================");
//			}
					

			// 7. 通过节点的属性获取节点
//			StringBuilder query = new StringBuilder(); 
//			query.append("MATCH (n) ");
//			query.append("WHERE n.Name='Person' ");
//			query.append("RETURN n");
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Node node =(Node) temp.get( key );
//	               System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//
//	            }
//	            System.out.println("===============================");
//			}
			
			// 8. 条件嵌套来寻找结点，前提是属性label和字符串要正确。
//			StringBuilder query = new StringBuilder();
//			//query.append("MATCH (n { Name: 'Person' }) RETURN n");
//			query.append("MATCH (n{ComeFrom:'First'}) RETURN n");
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Node node =(Node) temp.get( key );
//	               System.out.println(node.getProperty(NAMEPROPERTY) + " " +node.getProperty(COMEFROMPROPERTY));
//
//	            }
//	            System.out.println("===============================");
//			}
			
		
		// ****************** Advanced Cypher *************************
			// 9. Path测试版本
//			StringBuilder query = new StringBuilder();
//			//query.append("MATCH p= (a)-[r{ComeFrom:'First'}]->(b)  RETURN p");
//			query.append("MATCH p= (a)-[r{Type:'Concept'}]->(b)  RETURN p");
//			//query.append("MATCH p= (a)-[r{Type:'INCLUDEDBY'}]->(b)  RETURN p");	
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//		            if (temp.get( key ) instanceof Path) 
//		            {
//		                Path path = (Path) temp.get( key );
//		                printPathS(path);
//		            }           
//	            }	            
//	            System.out.println("===============================");
//			}
		
	        // 10. Path测试版本,闭包的情况，加条件约束
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH p=(s)-[*]->(a) ");
//		    //query.append("WHERE a.Name='negative_Person'   ");
//		    query.append("WHERE s.Name='D2' and a.Name='C2'   ");
//		    query.append("RETURN p");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	for ( String key : result.columns() )  //对应的按列来进行读取
//		    	{
//		            if (temp.get( key ) instanceof Path) 
//		            {
//		                Path path = (Path) temp.get( key );
//		                printPathS(path);
//		            }           
//		    	}
//		    	System.out.println("===============================");
//		    }

		    // 11. Path测试版本,闭包的情况，加条件约束
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH p=(s)-[:INCLUDEDBY*]->(a)");
//		    query.append("WHERE a.Name='negative_Person'   ");
//		    query.append("RETURN p");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	for ( String key : result.columns() )  //对应的按列来进行读取
//		    	{
//		            if (temp.get( key ) instanceof Path) 
//		            {
//		                Path path = (Path) temp.get( key );
//		                printPathS(path);
//		            }               
//		    	}
//		    	System.out.println("===============================");
//		    }
			
			// 12. Path测试版本,单路径加约束，并进一步过滤（保证路径的唯一性）
		    StringBuilder query = new StringBuilder();
		    //query.append("start y.Name='C2' ");
		    //query.append("start y=Node(2) ");
		    //query.append("start x=Node(1), y=Node(2)");	 
		    //query.append("start x=Node(1), y=Node(2)");
		    query.append("MATCH path=(x)-[r1:INCLUDEDBY*]->(y)");
		    query.append("UNWIND NODES(path) AS n ");
		    query.append("WITH path, ");	
		    query.append("SIZE(COLLECT(DISTINCT n)) AS testLength ");		    	        
		    query.append("WHERE testLength = LENGTH(path) + 1  ");	  
//		    query.append(" and y.Name='negative_Person'");
//		    query.append(" and y.Name='C2' ");
		    query.append("RETURN path");
		    System.out.println(query);
		    Result result = graphDb.execute(query.toString());
		    System.out.println(result==null);
		    while (result.hasNext()) 
		    {
		    	Map<String, Object> temp = result.next();
		    	Path pPath = (Path) temp.get("path");
		        printAllPaths(pPath);
		    	System.out.println("===============================");
		    }
			
		    // 13. Path测试版本,双路径加约束，双向路径进行回溯来完成。
			//Conference
//			long ticStart=System.currentTimeMillis();
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    query.append(" and uc.Name='Reviewer' and n.Name='Person' and t.Name='negative_Person' ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    query.append(" and uc.ComeFrom='First' and n.ComeFrom='First' and t.ComeFrom='First' ");
//		    query.append("RETURN pp,np");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//
//		        Path pPath = (Path) temp.get("pp");
//		        Path nPath = (Path) temp.get("np");
//		        printAllPaths(pPath);
//		        printAllPaths(nPath);
//		    	System.out.println("===============================");
//		    }
//		    long tocEnd=System.currentTimeMillis();
//			System.out.println("查询路径消耗的时间为："+ (tocEnd-ticStart)+" ms");	
			
			//Anatomy
//			long ticStart=System.currentTimeMillis();
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    query.append(" and uc.Name='MA_0001763' and n.Name='NCI_C21599' and t.Name='negative_NCI_C21599' ");
//		    query.append(" and uc.ComeFrom='First' and n.ComeFrom='Second' and t.ComeFrom='Second' ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    query.append("RETURN pp,np");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//
//		        Path pPath = (Path) temp.get("pp");
//		        Path nPath = (Path) temp.get("np");
//		        printAllPaths(pPath);
//		        printAllPaths(nPath);
//		    	System.out.println("===============================");
//		    }
//		    long tocEnd=System.currentTimeMillis();
//			System.out.println("查询路径消耗的时间为："+ (tocEnd-ticStart)+" ms");	
					    	    
			// 14. Path测试版本,双路径加约束，最短路径测试
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp= allShortestPaths((uc)-[r1:INCLUDEDBY*]->(n)),");
//		    query.append("np= allShortestPaths((uc)-[r2:INCLUDEDBY*]->(t)) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    query.append(" and uc.Name='Reviewer' and n.Name='Person' and t.Name='negative_Person' ");
//		    query.append(" and uc.ComeFrom='First' and n.ComeFrom='First' and t.ComeFrom='First' ");
//		    query.append("RETURN pp,np");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	
//		    	Path pPath = (Path) temp.get("pp");
//		        Path nPath = (Path) temp.get("np");
//		        printAllPaths(pPath);
//		        printAllPaths(nPath);
//		    	System.out.println("===============================");
//		    }	
		    

			//15. 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n) ");
//		    query.append("WHERE uc.Name='Reviewer' and n.Name='Person' ");
//		    query.append("return collect(distinct pp) AS np ");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	
//		    	ArrayList<Path> pPath = (ArrayList) temp.get("np");
//		    	for(Path p: pPath)
//		    		printAllPaths(p);	
//		    	System.out.println("===============================");
//		    }
		    
			//16. 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n) ");
//		    query.append("WHERE all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) ");
//		    query.append(" and uc.Name='Reviewer' and n.Name='Person' ");
//		    //query.append("return collect(distinct pp) AS np ");
//		    query.append("return pp ");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();	    	
//		    	Path pPath = (Path) temp.get("pp");
//		    		printAllPaths(pPath);	
//		    	System.out.println("===============================");
//		    }
		    

			//16.1 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
//		    query.append("WHERE all(n in NODES(pp) WHERE 1=length(filter(m in NODES(pp) where m=n))) ");
//		    query.append(" and uc.Name='Reviewer' and other.Name='Person' ");
//		    query.append("RETURN LENGTH(pp) AS length, pp,");
//		    query.append("other ORDER BY length ");  //排序消耗时间
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    //Result result2 =graphDb.ex.execute()
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();		    	
//		    	Path pPath = (Path) temp.get("pp");
//		    		printAllPaths(pPath);	
//		    	System.out.println("===============================");
//		    }

				
			//16.2 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
//		    query.append("WHERE ALL(x IN NODES(pp) WHERE SINGLE(y IN NODES(pp) WHERE y = x))");
//		    query.append(" and uc.Name='Reviewer' and other.Name='Person' ");
//		    query.append("RETURN LENGTH(pp) AS length, pp,");
//		    query.append("other ORDER BY length ");  //排序消耗时间
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();		    	
//		    	Path pPath = (Path) temp.get("pp");
//		    		printAllPaths(pPath);	
//		    	System.out.println("===============================");
//		    }
		    
		    
		  //17 找到各条唯一的路径 带参数
//			Map<String, Object> params = new HashMap<>();
//		    params.put( "a", "Reviewer" );	
//		    params.put( "b", "Person" );
//		    StringBuilder query = new StringBuilder();	    
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
//		    query.append("WHERE all(n in NODES(pp) WHERE 1=length(filter(m in NODES(pp) where m=n))) ");
//		    query.append(" and uc.Name=$a and other.Name=$b ");
//		    query.append("RETURN pp");
////		    query.append("RETURN LENGTH(pp) AS length, pp,");
////		    query.append("other ORDER BY length ");  //排序消耗时间
//		    System.out.println(query);
//		    //Result result = graphDb.execute(query.toString());
//		    Result result = graphDb.execute(query.toString(),params);
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();		    	
//		    	Path pPath = (Path) temp.get("pp");
//		    		printAllPaths(pPath);	
//		    	System.out.println("===============================");
//		    }   
		    
		    //17.1 找到各条唯一的路径 带参数 
//			long ticStart=System.currentTimeMillis();
//			Map<String, Object> params = new HashMap<>();
//			params.put( "pos", "Person" );
//			params.put( "neg", "negative_Person" );
//			params.put( "uc", "Reviewer" );		
//			params.put( "comefrom", "First" );
//		    params.put( "pairsource", "First" );	
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    query.append(" and uc.Name=$uc and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    query.append(" and uc.ComeFrom=$comefrom and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
//		    query.append("RETURN pp,np");
//		    System.out.println(query);
//		    //Result result = graphDb.execute(query.toString());
//		    Result result = graphDb.execute(query.toString(),params);
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//
//		        Path pPath = (Path) temp.get("pp");
//		        Path nPath = (Path) temp.get("np");
//		        printAllPaths(pPath);
//		        printAllPaths(nPath);
//		    	System.out.println("===============================");
//		    }
//		    long tocEnd=System.currentTimeMillis();
//			System.out.println("查询路径消耗的时间为："+ (tocEnd-ticStart)+" ms");
			
				    
		 //-> direct,  - undirect
		 //Node property: (p:Person {name: 'Jennifer'})
		 //Relationship property: -[rel:IS_FRIENDS_WITH {since: 2018}]->
			
		// ****************** Deleted Cypher *************************
			
			//基于结点的ID来进行删除
//			StringBuilder query = new StringBuilder();
//			int ID=6;
//	        query.append("start r = Relationship(6)");
//			query.append("MATCH (t) - [r] -> (p)"); 
//			query.append("DELETE r"); 
////			query.append(" RETURN t,p "); 
//			System.out.println(graphDb.execute(query.toString()));

			
			//基于结点的ID来进行删除
//			StringBuilder query = new StringBuilder();
//	        query.append("start t = Node(4), p=Node(0)");
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
////			query.append("WHERE t.Name='D2' and p.Name='A2' ");
////			query.append( "and t.ComeFrom='First' and p.ComeFrom='First'"); 
//			query.append("DELETE r"); 
//			query.append(" RETURN t,p "); 
//			System.out.println(graphDb.execute(query.toString()));
		    
//		   //基于结点的名字或者其他约束来进行删除
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
//			query.append("WHERE t.Name='D2' and p.Name='A2' ");
////			query.append( "and t.ComeFrom='First' and p.ComeFrom='First'"); 
//			query.append("DELETE r"); 
//			query.append(" RETURN t,p "); 
//			System.out.println(graphDb.execute(query.toString()));
			
			
			//基于参数来进行删除
//		    Map<String, Object> params = new HashMap<>();
//		    params.put( "start", "D2" );
//		    params.put( "end", "A2" );
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
//			query.append("WHERE t.Name=$start and p.Name=$end ");
////			query.append( "and t.ComeFrom='First' and p.ComeFrom='First'"); 
//			query.append("DELETE r"); 
////			query.append(" RETURN t,p "); 
//			graphDb.execute(query.toString(),params);
			
//		    Map<String, Object> params = new HashMap<>();
//		    params.put( "comefrom", "Conj" );
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
//			query.append("WHERE r.ComeFrom=$comefrom ");
//			query.append("DELETE r"); 
////			query.append(" RETURN t,p "); 
//			graphDb.execute(query.toString(),params);
			
			//clear all the  data 
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH (n)");
//			query.append("OPTIONAL MATCH (n)-[r]-()");
//			query.append("DELETE r");
////			query.append(" RETURN n "); 
//			System.out.println(graphDb.execute(query.toString()));
							
			//StringBuilder query = new StringBuilder();
			//原始的打印方式
//			query.delete(0, query.length());		
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
//			query.append("WHERE p.Name='A2' ");
//			query.append("RETURN r "); 			
//		    Result result = graphDb.execute(query.toString());
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Relationship rel=(Relationship) temp.get( key );
//	               System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY).toString()+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
//	               System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString());
//	               System.out.println(rel.getType().name());		
//	               System.out.println("===============================");
//	            }
		    }
		    
		    //新的打印方式
			//StringBuilder query = new StringBuilder();
//		    query.delete(0, query.length());	
//		    //int ID=6;
//	        query.append("start r = Relationship("+ID+")");
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 			
//			query.append("RETURN r "); 			
//		    Result result = graphDb.execute(query.toString());
//		    if(result.hasNext()==false)
//		    	System.out.println("This relationship has deleted!");
//		    
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Relationship rel=(Relationship) temp.get( key );
//	               System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY).toString()+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
//	               System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString());
//	               System.out.println(rel.getType().name());		
//	               System.out.println("===============================");
//	            }
//		    }
					

		    			    
		  	
		//}

		graphDb.shutdown();
		Timekeeping.end();
		Timekeeping.showInfo("The time comsumption of cypher is ");
	}
	
	public static List<Relationship> pathToList(PathImpl path){
		List<Relationship> relInPath = new ArrayList<>();
		for(Relationship rel : path.relationships()){
			//System.out.print(GlobalFunct.relToStr(rel)+"->");
			relInPath.add(rel);
		}
		//System.out.println("\n");
		return relInPath;
	}
	
	public static void printPathS(Path p) 
	{
		Iterable<Node> nodes = p.nodes();
        for (Node n:nodes)
        {
        	System.out.println(n.getProperty(NAMEPROPERTY)+"*");
        }
        	
		for(Relationship rel : p.relationships()){
			Node start = rel.getStartNode();
			Node end = rel.getEndNode();
			String comefrom = rel.getProperty(COMEFROMPROPERTY).toString();  //rel.getType().name() 才能输出具体的标签值。
			System.out.println(start.getProperty(NAMEPROPERTY)+"->"+end.getProperty(NAMEPROPERTY)+"  "+comefrom+"  "+rel.getType().name());			
		}
		System.out.println();
	}
	
	public static void printAllPaths(Path p) {     	
		for(Relationship rel : p.relationships()){
			Node start = rel.getStartNode();
			Node end = rel.getEndNode();
			System.out.print(start.getProperty(NAMEPROPERTY)+"->"+end.getProperty(NAMEPROPERTY)+" ");			
		}
		System.out.println();
	}
	
}
