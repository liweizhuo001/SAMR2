package com.njupt.exp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.neo4j.ver3_3_4.DoulbeOWLMappingToGraphDB;
import com.njupt.util.MappingInfo;
import com.njupt.util.ScoringMappingRevision;

import Tools.OAEIAlignmentSave;

import com.njupt.util.Evaluation;



public class GraphBasedRevision4AnatomyOracle {
	static String URI1="",URI2="";
	static ArrayList<String> revisedMappings= new ArrayList<String>();
	static ArrayList<String> removedMappings= new ArrayList<String>();
	static ArrayList<String> approvedMappings= new ArrayList<String>();
	
	static int approvedNum=0;
	static int rejectedNum=0;
	
	public static void main(String[] args) throws Exception
	{
	
		long tic = System.currentTimeMillis()/1000;
//		String oFile1 = "exampleOntology/example1.owl"; //The source ontology
//		String oFile2 = "exampleOntology/example2.owl"; //The target ontology
//		String gPath = "neo4j-test/Example_1_2_test12"; //The store path of integrated graph
//		String mappingsPath = "exampleOntology/12.rdf";	 //The input path of mappings	
//		String referencePath = "alignments/ReferenceAlignment/example_Standard.rdf"; //The reference alignments
		
			
//		String oFile1 = "exampleOntology/cmt_test4.owl";
//		String oFile2 = "exampleOntology/confOf_test4.owl";
//		String gPath = "neo4j-test/Integrated_cmt_confOf_test";	
//		String mappingsPath = "alignments/cmt-confOf_test.rdf";	
//		String referencePath = "alignments/ReferenceAlignment/cmt-confOf_standard.rdf"; //The reference alignments
		
	
		
		//Anatomy
		String oFile1 = "testdata/anatomy/mouse.owl"; 
		String oFile2 = "testdata/anatomy/human.owl";
		String gPath = "neo4j-test/Integrated_mouse_human_test-FCA_Map-O";	
		String mappingsPath = "testdata/mappings/FCA_Map-mouse-human.rdf";	
		String referencePath = "testdata/anatomy/reference_2015.rdf";  //The reference alignments	
		//String outputPath="Results/Oracle/one2one/Revised-FCA_Map-mouse-human";
		//String outputPath="Results/Oracle/bridgeRule/Revised-FCA_Map-mouse-human";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-FCA_Map-mouse-human";
		//String outputPath="Results/Oracle/normImpact/Revised-FCA_Map-mouse-human";
		//String outputPath="Results/Oracle/graphImpact/Revised-FCA_Map-mouse-human";
		//String outputPath="Results/Oracle/graphImpact2/Revised-FCA_Map-mouse-human";
		String outputPath="Results/Oracle/graphImpact3/Revised-FCA_Map-mouse-human";
		
		
//		String oFile1  = "testdata/anatomy/mouse.owl";
//		String oFile2  = "testdata/anatomy/human.owl";
//		String gPath = "neo4j-test/Integrated_mouse_human_test-AROMA-O";	
//		String mappingsPath = "alignment4Experiment/AROMA-mouse-human.rdf";
//		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AROMA-mouse-human";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AROMA-mouse-human";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AROMA-mouse-human";
		//String outputPath="Results/Oracle/normImpact/Revised-AROMA-mouse-human";
		//String outputPath="Results/Oracle/graphImpact/Revised-AROMA-mouse-human";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AROMA-mouse-human";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AROMA-mouse-human";
		
		
//		String oFile1  = "testdata/anatomy/mouse.owl";
//		String oFile2  = "testdata/anatomy/human.owl";
//		String gPath = "neo4j-test/Integrated_mouse_human_test-LogMapLite-O";
//		String mappingsPath = "alignment4Experiment/LogMapLite-mouse-human-C.rdf";
//		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-LogMapLite-mouse-human";
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-mouse-human";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-mouse-human";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-mouse-human";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-mouse-human";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-mouse-human";
		//String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-mouse-human";
		
		
		
//		String oFile1  = "testdata/anatomy/mouse.owl";
//		String oFile2  = "testdata/anatomy/human.owl";
//		String gPath = "neo4j-test/Integrated_mouse_human_test-AML_M-O";
//		String mappingsPath = "alignment4Experiment/AML_M-mouse-human.rdf";
//		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AML_M-mouse-human";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-mouse-human";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-mouse-human";
		//String outputPath="Results/Oracle/normImpact/Revised-AML_M-mouse-human";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-mouse-human";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-mouse-human";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-mouse-human";
		
		
		
		
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
		System.out.println("The number of toal mappings is :  " + mappings.size());
		System.out.println("The number of approved operation is :  " + approvedNum);
		System.out.println("The number of rejected operation is :  " + rejectedNum);
		Evaluation approved = new Evaluation(approvedMappings, referenceMappings);
		System.out.println("The correct mappings in approved mappings: " + (approved.getCorrectAlignment()+"/"+approvedMappings.size()));	
		
		double savedRatio =1-(approvedNum+rejectedNum)*1.0/mappings.size();
		BigDecimal bg = new BigDecimal(savedRatio).setScale(3, RoundingMode.HALF_UP);
//		DecimalFormat df = new DecimalFormat("0.000");
//		return df.format(savedRatio).replace(',', '.');
		
		System.out.println("The saved ratio of this interactive mapping validation is:  " + bg.doubleValue());
		System.out.println("--------------------------------------------------------");
		
		OAEIAlignmentSave out=new OAEIAlignmentSave(outputPath,URI1,URI2);
		for(int i=0;i<revisedMappings.size();i++)
		{
			String parts[]=revisedMappings.get(i).split(",");
				//System.out.println(Alignments.get(i));
			out.addMapping2Output(parts[0],parts[1],parts[2],parts[3]);
		}
		out.saveOutputFile();
		System.out.println("The file is saved in "+outputPath);
		System.out.println("The whole repair by this method is " + (System.currentTimeMillis()/1000-tic) + " seconds");

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
		
		ScoringMappingRevision revision = new ScoringMappingRevision(gPath,oFile1, oFile2, mappingPaths,referenceMappings);	
		
		removedMappings=revision.getRemovedMappings();
		
		approvedMappings=revision.getApprovedMappings();
		
		approvedNum=revision.approvedNums();
		
		rejectedNum=revision.rejectedNums();
		
		URI1=revision.getURI1().replace(".owl", "").replace(".rdf", "");
		URI2=revision.getURI2().replace(".owl", "").replace(".rdf", "");
		
//		System.out.println("End of init graph.");
//		revisor.goRevising();	
//		revisedMappings=revisor.getMappings();
//		removedMappings=revisor.getRemoveMappings();
//		candidateMappings=revisor.getCandidateMappings();
//		revisor.shutdown();
	}
	
	

}
