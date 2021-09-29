package com.njupt.demo;

import static com.njupt.util.GlobalParams.*;
import com.njupt.util.Timekeeping;
import com.neo4j.ver3_3_4.SingleOWLToGraphDB;

@SuppressWarnings("unused")
public class OWLToGraphDBDemo {
	
	public static void createGraphDB(String gDB, String oFile1){
		//从单个本体中创建图数据库
		SingleOWLToGraphDB owlToGraph = new SingleOWLToGraphDB(gDB, oFile1, COMEFROMFIRST,true);
		owlToGraph.createDbFromOwl();
		owlToGraph.shutdown();		
	}
	 
		
//	public static String gDB2 = "neo4j-db/ma_new_injected";
//	public static String oFile2 = "ClassicOntology/ma_new_injected.rdf";
	
//	public static String gDB2 = "neo4j-db/km1500-i500";
//	public static String oFile2 = "ontologies debugs/km1500-i500.owl";
	
//	public static String gDB2 = "neo4j-db/km1500-4000";
//	public static String oFile2 = "ontologies debugs/km1500-4000.owl";
	
	
//	public static String gDB2 = "neo4j-test/example1";
//	public static String oFile2 = "exampleOntology/example1.owl";
	
//	public static String gDB2 = "neo4j-db/cmt";
//	public static String oFile2 = "exampleOntology/cmt.owl";
	
//	public static String gDB2 = "neo4j-db/edas";
//	public static String oFile2 = "exampleOntology/edas.owl";
	
//	public static String gDB2 = "neo4j-db/mouse";
//	public static String oFile2 = "exampleOntology/mouse.owl";
	
	public static String gDB2 = "neo4j-db/human";
	public static String oFile2 = "exampleOntology/human.owl";
	
	
	public static void main(String[] args) {
					
		Timekeeping.begin();
		/*createGraphDB(gDB1,oFile1);
		//插入一个本体到图数据库中
		InsertTBoxToGraph iTBox = new InsertTBoxToGraph(gDB1, oFile2);
		iTBox.InsertOwlIntoGraph();
		iTBox.shutdown();		
		//插入ABox到图数据库中
		InsertABoxToGraph aBox = new InsertABoxToGraph(gDB1, aFile1, COMEFROMFIRST);
		aBox.InsertABoxIntoGraph();*/
		
		createGraphDB(gDB2, oFile2);		
		//InsertABoxToGraph aBox = new InsertABoxToGraph(gDB2, oFile2, COMEFROMFIRST);
		//aBox.InsertABoxIntoGraph();
		
		Timekeeping.end();
		Timekeeping.showInfo("Graph database test in ");

	}
}
