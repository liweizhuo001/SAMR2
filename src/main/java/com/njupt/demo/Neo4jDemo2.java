package com.njupt.demo;

import static com.njupt.util.GlobalParams.*;


import java.io.File;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;


import com.njupt.util.Timekeeping;

public class Neo4jDemo2 {

	
//	public static String NAMEPROPERTY = "NAMEPROPERTY";
//	public static String COMEFROMPROPERTY = "COMEFROMPROPERTY";
//	public static String TYPEPROPERTY = "TYPEPROPERTY";
//	public static String WEIGHTEDPROPERTY = "WEIGHTEDPROPERTY";
//	public static String NODEINDEX = "NODEINDEX";

	// 图数据库节点的测试
	public static void outputNodes4demo(GraphDatabaseService graphDb) {
		int i = 0; 
		for (Node node : graphDb.getAllNodes()) 
		{
			++i;
			//System.out.println(node.getProperty("name"));
			for(String property: node.getAllProperties().keySet())
			{
				System.out.println(property+ "--"+node.getAllProperties().get(property));
			}
				
			for(Label label : node.getLabels())
			{
				System.out.println(label+"  :  ");
			}
			
			for(Relationship relation :node.getRelationships()){
				System.out.println(node.getProperty("name")+"-->"+relation.getEndNode().getProperty("name"));
			}
			System.out.println("===============================");
		}
			
		System.out.println(String.format("Number of nodes is %d", i));
	}

	// 图数据库关系Demo的测试，因为属性的名称不同
	public static void outputRelationships4demo(GraphDatabaseService graphDb) {
		int i = 0;
		for (Relationship rel : graphDb.getAllRelationships()) {
			++i;
			System.out.println(rel);
			System.out.println(
					rel.getStartNode().getProperty("name") + " " + rel.getEndNode().getProperty("name"));
		}
		System.out.println(String.format("Number of relationship is %d", i));
	}
	
	
    //图数据库结点本体的测试
	public static void outputNodes4owl(GraphDatabaseService graphDb) {
		int i=0;
		for (Node node : graphDb.getAllNodes()) {
			++i;
			for(String s:node.getPropertyKeys())
			{
				System.out.println(s);
				
			}
			if (node.hasProperty(NAMEPROPERTY))
			{
				for(Label label : node.getLabels())
				{
					System.out.print(label+"  :  ");
				}
				System.out.println(node);
				
				System.out.println(node.getProperty(NAMEPROPERTY) +"  "+node.getProperty(COMEFROMPROPERTY));
				//输出实例节点
				/*if(node.hasProperty(TYPEPROPERTY) && node.getProperty(TYPEPROPERTY).equals(INDIVIDUALTYPE)){
					System.out.println(node.getProperty(NAMEPROPERTY));
					for(Relationship relation :node.getRelationships()){
						System.out.println(node.getProperty(NAMEPROPERTY)+"-->"+relation.getEndNode().getProperty(NAMEPROPERTY));
					}
				}*/
			}			
		}
		System.out.println(String.format("Number of nodes is %d", i));
	}
	
	//图数据库关系本体的测试
	public static void outputRelationships4owl(GraphDatabaseService ggo){
		int i=0;
		for(Relationship rel :  ggo.getAllRelationships()){
			++i;
			System.out.println("-----------------------------");
			System.out.println(rel+" "+rel.getId());
			System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY)+" "+rel.getEndNode().getProperty(NAMEPROPERTY));
			System.out.println(rel.getProperty(TYPEPROPERTY).toString()+ "  "+rel.getProperty(COMEFROMPROPERTY)+"  "+rel.getType());
			if(rel.hasProperty(WEIGHTEDPROPERTY))  //判断是否有这个属性
			{
				System.out.println(rel.getProperty(WEIGHTEDPROPERTY).toString());
			}
		}
		System.out.println(String.format("Number of relationship is %d", i));
	}

	// 索引测试
	public static void indexTest(GraphDatabaseService graphDb, String nodeName) {
		System.out.println("In index testing...");
		Index<Node> nodeIndex = graphDb.index().forNodes(NODEINDEX);
		Node node = nodeIndex.query(NAMEPROPERTY, nodeName).getSingle();
		if (node != null) {
			// System.out.println(node.getId());
			System.out.println(node.getProperty(COMEFROMPROPERTY));
			System.out.println(node.getProperty(NAMEPROPERTY));
			System.out.println(node.getProperty(TYPEPROPERTY));
		} else {
			System.out.println("no node found.");
		}
	}

	// 输出所有的否定节点
//	public static void outputNegativeNodes(GraphDatabaseService ggo) {
//		List<String> negativeNodes = GlobalFunct.getNegativeNodes(ggo);
//		for (String negativeNode : negativeNodes) {
//			System.out.println(negativeNode);
//		}
//
//		List<DisjointPair> negMap = GlobalFunct.getDisjointPair(ggo);
//		for (DisjointPair posNode : negMap) {
//			System.out.println(posNode.getFirst() + " --> " + posNode.getSecond());
//		}
//	}

	public static void main(String[] args) {
		Timekeeping.begin();
		//String gDBPath = "Data/test";
		
		//String gDBPath = "neo4j-test/example1";	
		//String gDBPath = "neo4j-db/cmt";
		//String gDBPath = "neo4j-db/edas";
		//String gDBPath = "neo4j-db/mouse";
		//String gDBPath = "neo4j-db/human";
		
		
		String gDBPath = "neo4j-test/Example_1_2_test";	
	
		//String gDBPath = "neo4j-test/Integrated_cmt_edas_test";		
		//String gDBPath = "neo4j-test/Integrated_conference_confof_test";	
		//String gDBPath = "neo4j-test/Integrated_confof_ekaw_test";
		
		//String gDBPath = "neo4j-test/Integrated_mouse-human_test";
		//String gDBPath = "neo4j-test/Integrated_fma-nci_small_test";

		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(gDBPath));
		try (Transaction tx = graphDb.beginTx()) {
			//outputNodes4demo(graphDb); // 输出所有的节点
			//outputRelationships4demo(graphDb); // 输出所有的关系
			
			outputNodes4owl(graphDb); // 输出所有的节点		
			outputRelationships4owl(graphDb); // 输出所有的关系
			// indexTest(graphDb,"chair");
			// outputNegativeNodes(graphDb);
		}

		graphDb.shutdown();
		Timekeeping.end();
		Timekeeping.showInfo("Graph database test in ");
	}

}
