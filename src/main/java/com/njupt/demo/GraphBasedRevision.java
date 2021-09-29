package com.njupt.demo;

import java.io.IOException;
import java.util.ArrayList;

import com.neo4j.ver3_3_4.DoulbeOWLMappingToGraphDB;
import com.njupt.util.MappingInfo;
import com.njupt.util.ScoringMappingRevision;

import com.njupt.util.Evaluation;



public class GraphBasedRevision {
	static String URI1="",URI2="";
	static ArrayList<String> revisedMappings= new ArrayList<String>();
	static ArrayList<String> removedMappings= new ArrayList<String>();
	
	public static void main(String[] args) throws Exception
	{
	
		 
		String oFile1 = "exampleOntology/example1.owl"; //The source ontology
		String oFile2 = "exampleOntology/example2.owl"; //The target ontology
		String gPath = "neo4j-test/Example_1_2_test12"; //The store path of integrated graph
		String mappingsPath = "exampleOntology/12.rdf";	 //The input path of mappings	
		String referencePath = "alignments/ReferenceAlignment/example_Standard.rdf"; //The reference alignments
		
			
//		String oFile1 = "exampleOntology/cmt_test4.owl";
//		String oFile2 = "exampleOntology/confOf_test4.owl";
//		String gPath = "neo4j-test/Integrated_cmt_confOf_test";	
//		String mappingsPath = "alignments/cmt-confOf_test.rdf";	
//		String referencePath = "alignments/ReferenceAlignment/cmt-confOf_standard.rdf"; //The reference alignments
		

//	    String oFile1 = "exampleOntology/mouse.owl";
//		String oFile2 = "exampleOntology/human.owl";
//		String gPath = "neo4j-test/Integrated_GMap-mouse_human_test";
//		String mappingsPath = "alignments/GMap-mouse-human.rdf";		
//		String referencePath = "alignments/ReferenceAlignment/reference_2015.rdf"; //The reference alignments
		
		
		//获取mappings的信息
		MappingInfo MappingInformation=new MappingInfo(mappingsPath);	
		ArrayList<String> mappings= new ArrayList<String>();
		mappings=MappingInformation.getMappings();
		
		for(String s:mappings)
		{
			System.out.println(s);
		}
		
		ArrayList<String> referenceMappings= new ArrayList<String>();
		MappingInfo ReferenceInformation=new MappingInfo(referencePath);
		referenceMappings=ReferenceInformation.getMappings();
				
		//构建图数据库
		//createGraphDB(gPath, oFile1, oFile2, mappingsPath);
		//createGraphDB(gPath, oFile1, oFile2, MappingInformation);
		
		//借助图数据库来修复mappings
		revisingMappings(gPath,oFile1, oFile2, mappingsPath,referenceMappings);
		System.out.println("--------------------------------------------------------");
		
		if(removedMappings.isEmpty())
			System.out.println("No mappings will be removed!");
		System.out.println("The removed mappings are listed as follows: ");
		for(String s:removedMappings)
		{
			System.out.println(s);
		}
		System.out.println("--------------------------------------------------------");		
		// compare against reference alignment
		Evaluation cBefore = new Evaluation(mappings, referenceMappings);
		revisedMappings.addAll(mappings);
		revisedMappings.removeAll(removedMappings);
		System.out.println("The left mappings are listed as follows: ");
		for(String s:revisedMappings)
		{
			System.out.println(s);
		}
		Evaluation cAfter = new Evaluation(revisedMappings, referenceMappings);
		
		System.out.println("--------------------------------------------------------");
		System.out.println("before debugging (pre, rec, f): " + cBefore.toShortDesc());
		System.out.println("The number of total wrong mappings in alignment:  " + (cBefore.getMatcherAlignment()-cBefore.getCorrectAlignment()));
		System.out.println("after debugging (pre, rec, f):  " + cAfter.toShortDesc());
		System.out.println("The number of wrong removed mappings:  " + (cBefore.getCorrectAlignment()-cAfter.getCorrectAlignment()));
		
		System.out.println("--------------------------------------------------------");
	}
	
	//基于量个本体和匹配中创建图数据库
	public static void createGraphDB(String gDB, String oFile1, String oFile2, String mappingPaths) throws IOException
	{	
		DoulbeOWLMappingToGraphDB owlToGraph = new DoulbeOWLMappingToGraphDB(gDB, oFile1, oFile2,mappingPaths,true);
		URI1=owlToGraph.getOwlInfo1().URI.replace(".owl", "").replace(".rdf", "");
		URI2=owlToGraph.getOwlInfo2().URI.replace(".owl", "").replace(".rdf", "");
		owlToGraph.createDbFromOwl();
		owlToGraph.shutdown();		
	}
	
	public static void revisingMappings(String gPath,String oFile1, String oFile2,String mappingPaths, ArrayList<String> referenceMappings) throws IOException
	{
		System.out.println("Loading the constructed graph.");
		
		ScoringMappingRevision revison = new ScoringMappingRevision(gPath,oFile1, oFile2, mappingPaths,referenceMappings);	
		removedMappings=revison.getRemovedMappings();
//		System.out.println("End of init graph.");
//		revisor.goRevising();	
//		revisedMappings=revisor.getMappings();
//		removedMappings=revisor.getRemoveMappings();
//		candidateMappings=revisor.getCandidateMappings();
//		revisor.shutdown();
	}

}
