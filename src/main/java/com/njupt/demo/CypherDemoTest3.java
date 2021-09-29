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




public class CypherDemoTest3 { 
	//public static String gDB = "Data/Test";
	
	public static String gDB = "neo4j-test/Example_1_2_test12";	
	
	//public static String gDB = "neo4j-test/Integrated_cmt_edas_test4";		
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

			// 1. Get all nodes in the graph
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
		

			// 3. 访问关系 relationship，由关系连接节点时使用 "->",特别注意是一个"-"
//			StringBuilder query = new StringBuilder(); 
//			//query.append("MATCH (node1)-[rel]->() RETURN rel;");
//			query.append("MATCH ()-[rel]->() RETURN rel;");
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
//	               System.out.println("ID is "+rel.getId());
//	            }
//	            System.out.println("===============================");
//			}
			
			// 4. 指定关系的类型
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
					

	
		// ****************** Advanced Cypher *************************
			// 1. Path测试版本
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH p=(s)-[*]->(a) ");
//		    //query.append("MATCH p=(s)-[:INCLUDEDBY*]->(a)");
//		    query.append("WHERE a.Name='negative_Person'   ");
//		    //query.append("WHERE s.Name='Author' and a.Name='negative_Person'   ");
//		    //query.append("WHERE s.Name='D2' and a.Name='C2'   ");
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
			
			
			
		 // 2. Path测试版本,闭包的情况，加条件约束
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
		
			// 3.1 Path测试版本,单路径加约束，并进一步过滤（保证路径的唯一性，即节点的名称是唯一的）
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH path=(x)-[r1:INCLUDEDBY*]->(y)");
//		    //query.append("WHERE x.Name='A1' and y.Name='C2' ");
//		    query.append("WHERE x.Name='existence_writtenBy' and y.Name='Person' ");
//		    query.append(" and x.ComeFrom='Second' and y.ComeFrom='First' ");
//		    query.append("UNWIND NODES(path) AS n ");
//		    query.append("WITH path, ");	
//		    query.append("SIZE(COLLECT(DISTINCT n)) AS testLength ");		    	        
//		    query.append("WHERE testLength = LENGTH(path) + 1  ");	  
//		    //query.append(" and y.Name='C2' ");
//		    query.append("RETURN path");
//		    System.out.println(query);
//		    Result result = graphDb.execute(query.toString());
//		    System.out.println(result==null);
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	Path pPath = (Path) temp.get("path");
//		        printAllPaths(pPath);
//		    	System.out.println("===============================");
//		    }
		    
			//3.2 Path测试版本,单路径加约束，并进一步过滤（保证路径的唯一性，即节点的名称是唯一的）
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n) ");
//		    query.append("WHERE all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) ");
//		    query.append(" and uc.Name='existence_writtenBy' and n.Name='Person' ");
//		    query.append(" and uc.ComeFrom='Second' and n.ComeFrom='First' ");
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
		    

			//3.3 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n) ");
//		    query.append("WHERE all(n in NODES(pp) WHERE 1=length(filter(m in NODES(pp) where m=n))) ");
//		    query.append(" and uc.Name='existence_writtenBy' and n.Name='Person' ");
//		    query.append(" and uc.ComeFrom='Second' and n.ComeFrom='First' ");
////		    query.append("RETURN pp ");
//		    query.append("RETURN LENGTH(pp) AS length, pp,");
//		    query.append("n ORDER BY length ");  //排序消耗时间
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
			
			//3.4 找到各条唯一的路径
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
//		    query.append("WHERE ALL(x IN NODES(pp) WHERE SINGLE(y IN NODES(pp) WHERE y = x))");
//		    query.append(" and uc.Name='existence_writtenBy' and other.Name='Person' ");
//		    query.append(" and uc.ComeFrom='Second' and other.ComeFrom='First' ");
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
		    
		    
		    //3.5 找到各条唯一的路径 带参数
//			Map<String, Object> params = new HashMap<>();
//		    params.put( "a", "existence_writtenBy" );	
//		    params.put( "b", "Person" );
//		    StringBuilder query = new StringBuilder();	    
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
//		    query.append("WHERE all(n in NODES(pp) WHERE 1=length(filter(m in NODES(pp) where m=n))) ");
//		    query.append(" and uc.Name=$a and other.Name=$b ");
//		    query.append(" and uc.ComeFrom='Second' and other.ComeFrom='First' ");
//		    query.append("RETURN pp");
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
		    

		    
		    // 4. Path测试版本,双路径加约束，双向路径进行回溯来完成。（非常重要）
//			long ticStart=System.currentTimeMillis();
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    //query.append(" and uc.Name='A1' and n.Name='B1' and t.Name='C1' ");
//		    query.append(" and uc.Name='A1' and n.Name='C1' and t.Name='negative_C1' ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    //query.append(" and uc.ComeFrom='First' and n.ComeFrom='First' and t.ComeFrom='First' ");
//		    query.append(" and n.ComeFrom='First' and t.ComeFrom='First' ");
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
			
		    // 4.1 Path测试版本,双路径加约束，双向路径进行回溯来完成。
			//Conference
//			long ticStart=System.currentTimeMillis();
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    //query.append(" and uc.Name='Author' and n.Name='Person' and t.Name='negative_Person' ");
//		    query.append(" and uc.Name='existence_writtenBy' and n.Name='Reviewer' and t.Name='negative_Reviewer' ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    query.append(" and uc.ComeFrom='Second' and n.ComeFrom='First' and t.ComeFrom='First' ");
//		    //query.append(" n.ComeFrom='First' and t.ComeFrom='First' ");
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
			
			
			// 4.2 Path测试版本,双路径加约束，最短路径测试
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp= allShortestPaths((uc)-[r1:INCLUDEDBY*]->(n)),");
//		    query.append("np= allShortestPaths((uc)-[r2:INCLUDEDBY*]->(t)) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		   // query.append(" and uc.Name='Reviewer' and n.Name='Person' and t.Name='negative_Person' ");
//		    query.append(" and uc.Name='existence_writtenBy' and n.Name='Reviewer' and t.Name='negative_Reviewer' ");
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
			
		    //4.3 找到各条唯一的路径 带参数 
//			long ticStart=System.currentTimeMillis();
//			Map<String, Object> params = new HashMap<>();
//			params.put( "pos", "Reviewer" );
//			params.put( "neg", "negative_Reviewer" );
//			params.put( "uc", "existence_writtenBy" );		
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
					    	    

		   
			
		// ****************** Deleted Cypher *************************
			//基于结点的ID来进行删除
//			StringBuilder query = new StringBuilder();
//	        query.append("start r = Relationship(4)");
//			query.append("MATCH (t) - [r] -> (p)"); 
//			query.append("DELETE r"); 
//			query.append(" RETURN t,p "); 
//			System.out.println(graphDb.execute(query.toString()));

			
			//基于结点的ID来进行删除
//			StringBuilder query = new StringBuilder();
//	        query.append("start t = Node(6), p=Node(1)");
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
////			query.append("WHERE t.Name='D2' and p.Name='A2' ");
////			query.append( "and t.ComeFrom='First' and p.ComeFrom='First'"); 
//			query.append("DELETE r"); 
//			query.append(" RETURN t,p "); 
//			System.out.println(graphDb.execute(query.toString()));
			
			

		    
		   //基于结点的名字或者其他约束来进行删除
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
//			query.append("WHERE t.Name='D2' and p.Name='A2' ");
////			query.append( "and t.ComeFrom='First' and p.ComeFrom='First'"); 
//			query.append("DELETE r"); 
//			query.append(" RETURN t,p "); 
//			System.out.println(graphDb.execute(query.toString()));
			
			
//			//基于参数来进行删除
		    Map<String, Object> params = new HashMap<>();
		    params.put( "start", "A2" );
		    params.put( "end", "C2" );
		    params.put( "source", "Second" );
		    params.put( "target", "Second" );
			StringBuilder query = new StringBuilder();
			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
			query.append("WHERE t.Name=$start and p.Name=$end ");
			query.append( "and t.ComeFrom=$source and p.ComeFrom=$target "); 
			query.append("DELETE r"); 
//			query.append(" RETURN t,p "); 
			System.out.println(graphDb.execute(query.toString(),params));
			
			
			//基于快捷参数来进行删除			
//			StringBuilder query = new StringBuilder();
//			String start="C2";
//			String end="C1";	
//			String source="Second";
//			String target="First";				
//			String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
//			query.append("MATCH (n)-[r]->(m) ");
//			query.append(String.format(formatter, start, end, source, target));
//			query.append(" DELETE r");
//			System.out.println(graphDb.execute(query.toString()));
			
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
//			query.append(" RETURN n "); 
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
			
				StringBuilder query1 = new StringBuilder(); 
				query1.append("MATCH ()-[rel]->() RETURN rel;");
				System.out.println(query1);
				Result result = graphDb.execute(query1.toString());
				while (result.hasNext()) 
				{
					Map<String, Object> temp = result.next();
					for ( String key : result.columns() )  //对应的按列来进行读取
					{
						System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               	if((Relationship)temp.get(key))
						Relationship rel=(Relationship) temp.get( key );
						System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY)+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
						System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString()+"  "+rel.getType());
						System.out.println("ID is "+rel.getId());
					}
	            System.out.println("===============================");
				}
			
			
//		    }
		    
		    //新的打印方式
//			StringBuilder query = new StringBuilder();
//		    query.delete(0, query.length());	
//		    int ID=4;
//	        query.append("start r = Relationship("+ID+")");
//			query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 			
//			query.append("RETURN r "); 			
//		    Result result = graphDb.execute(query.toString());
//		    if(result.hasNext()==false)
//		    	System.out.println("This relationship has deleted!");    
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//	            for ( String key : result.columns() )  //对应的按列来进行读取
//	            {
//	               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//	               Relationship rel=(Relationship) temp.get( key );
//	               System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY).toString()+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
//	               System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString());
//	               System.out.println(rel.getId()+" "+rel.getType().name());		
//	               System.out.println("===============================");
//	            }
//		    }
		
//			StringBuilder query = new StringBuilder();
//			query.append("MATCH pp= allShortestPaths((uc)-[r1:INCLUDEDBY*]->(n)),");
//			query.append("np= allShortestPaths((uc)-[r2:INCLUDEDBY*]->(t)) ");
//			query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//			// query.append(" and uc.Name='Reviewer' and n.Name='Person' and t.Name='negative_Person' ");
//			query.append(" and uc.Name='existence_writtenBy' and n.Name='Reviewer' and t.Name='negative_Reviewer' ");
//			query.append(" and uc.ComeFrom='First' and n.ComeFrom='First' and t.ComeFrom='First' ");
//			query.append("RETURN pp,np");
//			System.out.println(query);
//			Result result = graphDb.execute(query.toString());
//			while (result.hasNext()) 
//			{
//				Map<String, Object> temp = result.next();
//	    	
//				Path pPath = (Path) temp.get("pp");
//				Path nPath = (Path) temp.get("np");
//				printAllPaths(pPath);
//				printAllPaths(nPath);
//				System.out.println("===============================");
//			}
			
			

			
			
			// ****************** Find MIPSs Cypher *************************
//			long ticStart=System.currentTimeMillis();
			//Map<String, Object> params = new HashMap<>();
			//获取本体的不相交公理
			
//			Map<String, Object> params = new HashMap<String, Object>();
//			String pos = Information.getFirst();
//			String neg = usInformation.getSecond();
//			String unsatNode = tetrad.getUnsat();
//			System.out.println("The triple is "+pos+", "+neg+", "+unsatNode);
//			String comefrom = tetrad.getSource();  //不可满足点的来源
//			String pairSource=tetrad.getPairSoucre();	//Pair的来源
			
//			long ticStart=System.currentTimeMillis();
//			Map<String, Object> params = new HashMap<>();
//			params.put( "pos", "C1" );
//			params.put( "neg", "negative_C1" );
////			params.put( "uc", "A2" );		
////			params.put( "comefrom", "Second" );
//		    params.put( "pairsource", "First" );	
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    //query.append(" and uc.Name=$uc and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    //query.append(" and uc.ComeFrom=$comefrom and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
//		    query.append(" and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
//		    query.append("RETURN uc,pp,np");
//		    System.out.println(query);
//		    //Result result = graphDb.execute(query.toString());
//		    Result result = graphDb.execute(query.toString(),params);
//		    while (result.hasNext()) 
//		    {
//		    	Map<String, Object> temp = result.next();
//		    	Node uc= (Node) temp.get("uc");
//		        Path pPath = (Path) temp.get("pp");
//		        Path nPath = (Path) temp.get("np");
//		        System.out.println(uc.getProperty(NAMEPROPERTY));
//		        printAllPaths(pPath);
//		        printAllPaths(nPath);
//		    	System.out.println("===============================");
//		    }
//		    long tocEnd=System.currentTimeMillis();
//			System.out.println("查询路径消耗的时间为："+ (tocEnd-ticStart)+" ms");
//			
			//Conference
//			long ticStart=System.currentTimeMillis();
//			Map<String, Object> params = new HashMap<>();
//			params.put( "pos", "Reviewer" );
//			params.put( "neg", "negative_Reviewer" );
////			params.put( "uc", "existence_writtenBy" );		
////			params.put( "comefrom", "Second" );
//		    params.put( "pairsource", "First" );	
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    //query.append(" and uc.Name=$uc and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    //query.append(" and uc.ComeFrom=$comefrom and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
//		    query.append(" and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
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
			
			
			//Anatomy
//			int n=0;
//			long ticStart=System.currentTimeMillis();
//			Map<String, Object> params = new HashMap<>();
//			params.put( "pos", "NCI_C21599" );
//			params.put( "neg", "negative_NCI_C21599" );
//			//params.put( "uc", "MA_0001763" );		
//			//params.put( "comefrom", "First" );
//		    params.put( "pairsource", "Second" );	
//		    StringBuilder query = new StringBuilder();
//		    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
//		    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
//		    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
//		    //query.append(" and uc.Name=$uc and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and n.Name=$pos and t.Name=$neg ");
//		    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
//		    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
//		    //query.append(" and uc.ComeFrom=$comefrom and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
//		    query.append(" and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
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
//		        n++;
//		    	System.out.println("===============================");
//		    }
//		    long tocEnd=System.currentTimeMillis();
//		    System.out.println("number of MIPS is "+n);
//			System.out.println("查询路径消耗的时间为："+ (tocEnd-ticStart)+" ms");
//			
//		  	
		}

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
