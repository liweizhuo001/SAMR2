package com.njupt.demo;

import static com.njupt.util.GlobalParams.*;

import java.io.IOException;
import java.util.ArrayList;

import com.njupt.util.Timekeeping;
import com.neo4j.ver3_3_4.DoulbeOWLMappingToGraphDB;
import com.neo4j.ver3_3_4.SingleOWLToGraphDB;
import com.njupt.util.MappingInfo;

@SuppressWarnings("unused")
public class OWLMappingToGraphDBDemo {
	
	public static void createGraphDB(String gDB, String oFile1, String oFile2, String mappingPaths) throws IOException{
		//从单个本体中创建图数据库
		//SingleOWLToGraphDB owlToGraph = new SingleOWLToGraphDB(gDB, oFile1, COMEFROMFIRST,true);
		//DoulbeOWLMappingToGraphDB owlToGraph = new DoulbeOWLMappingToGraphDB(gDB, oFile1, oFile2,COMEFROMFIRST,mappingPaths,true);
		DoulbeOWLMappingToGraphDB owlToGraph = new DoulbeOWLMappingToGraphDB(gDB, oFile1, oFile2,mappingPaths,true);
		
//		MappingInfo MappingInformation=new MappingInfo(mappingPaths);
//		ArrayList<String> mappings=MappingInformation.getMappings();
//		for(String a: mappings)
//		{
//			System.out.println(a);
//		}
		
		owlToGraph.createDbFromOwl();
		owlToGraph.shutdown();		
	}
	
	public static String oFile1 = "exampleOntology/example1.owl";
	public static String oFile2 = "exampleOntology/example2.owl";
	public static String mappingsPath = "exampleOntology/12.rdf";	
	public static String gDB = "neo4j-test/Example_1_2_test12";
	
//	public static String oFile1 = "exampleOntology/cmt_test4.owl";
//	public static String oFile2 = "exampleOntology/confOf_test4.owl";
//	public static String mappingsPath = "alignments/cmt-confOf_test.rdf";	
//	public static String gDB = "neo4j-test/Integrated_cmt_edas_test4";

//	public static String oFile1 = "exampleOntology/cmt.owl";
//	public static String oFile2 = "exampleOntology/edas.owl";
//	public static String mappingsPath = "alignments/GMap-cmt-edas.rdf";	
//	public static String gDB = "neo4j-test/Integrated_cmt_edas_test";
	
	
	
//	public static String oFile1 = "exampleOntology/Conference.owl";
//	public static String oFile2 = "exampleOntology/confOf.owl";
//	public static String mappingsPath = "alignments/HMatch-conference-confof.rdf";	
//	public static String gDB = "neo4j-test/Integrated_conference_confof_test";
	
	
//	public static String oFile1 = "exampleOntology/confOf.owl";
//	public static String oFile2 = "exampleOntology/ekaw.owl";
//	public static String mappingsPath = "alignments/asmov-confof-ekaw.rdf";	
//	public static String gDB = "neo4j-test/Integrated_confof_ekaw_test";
	
//	public static String oFile1 = "exampleOntology/mouse.owl";
//	public static String oFile2 = "exampleOntology/human.owl";
//	public static String mappingsPath = "alignments/GMap-mouse-human.rdf";	
//	public static String gDB = "neo4j-test/Integrated_GMap-mouse_human_test";
	
	
//	public static String oFile1 = "exampleOntology/oaei_FMA_small_overlapping_nci.owl";
//	public static String oFile2 = "exampleOntology/oaei_NCI_small_overlapping_fma.owl";
//	public static String mappingsPath = "alignments/FCA_Map-largebio-fma_nci_small_2016.rdf";	
//	public static String gDB = "neo4j-test/Integrated_fma-nci_small_test";
	
	
	public static void main(String[] args) throws IOException {
		
		Timekeeping.begin();		
		
		
		/*createGraphDB(gDB1,oFile1);
		//插入一个本体到图数据库中
		InsertTBoxToGraph iTBox = new InsertTBoxToGraph(gDB1, oFile2);
		iTBox.InsertOwlIntoGraph();
		iTBox.shutdown();		
		//插入ABox到图数据库中
		InsertABoxToGraph aBox = new InsertABoxToGraph(gDB1, aFile1, COMEFROMFIRST);
		aBox.InsertABoxIntoGraph();*/
		

		createGraphDB(gDB, oFile1, oFile2, mappingsPath);
		
		//InsertABoxToGraph aBox = new InsertABoxToGraph(gDB2, oFile2, COMEFROMFIRST);
		//aBox.InsertABoxIntoGraph();
		
		Timekeeping.end();
		Timekeeping.showInfo("Create a complex graph db");
	}
}
