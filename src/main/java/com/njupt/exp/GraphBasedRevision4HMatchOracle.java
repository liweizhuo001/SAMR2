package com.njupt.exp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.neo4j.ver3_3_4.DoulbeOWLMappingToGraphDB;
import com.njupt.util.MappingInfo;
import com.njupt.util.ScoringMappingRevision;

import Tools.OAEIAlignmentSave;


import com.njupt.util.Evaluation;


public class GraphBasedRevision4HMatchOracle {
	static String URI1="",URI2="";
	static ArrayList<String> revisedMappings= new ArrayList<String>();
	static ArrayList<String> removedMappings= new ArrayList<String>();
	static ArrayList<String> approvedMappings= new ArrayList<String>();
	
	static int approvedNum=0;
	static int rejectedNum=0;
	
	public static void main(String[] args) throws Exception
	{
	
		long tic = System.currentTimeMillis()/1000; 
		//Test
//		String oFile1 = "exampleOntology/example1.owl"; //The source ontology
//		String oFile2 = "exampleOntology/example2.owl"; //The target ontology
//		String gPath = "neo4j-test/Example_1_2_test12"; //The store path of integrated graph
//		String mappingsPath = "exampleOntology/12.rdf";	 //The input path of mappings	
//		String referencePath = "alignments/ReferenceAlignment/example_Standard.rdf"; //The reference alignments
		
			
//		String oFile1 = "exampleOntology/cmt_test4.owl";
//		String oFile2 = "exampleOntology/confOf_test4.owl";
//		String gPath = "neo4j-test/Integrated_cmt_confOf_test";	
//		String mappingsPath = "alignments/cmt-confOf_test.rdf";	
//		String referencePath = "alignments/ReferenceAlignment/cmt-confOf_standard.rdf";//The reference alignments
		
//	    String oFile1 = "exampleOntology/mouse.owl";
//		String oFile2 = "exampleOntology/human.owl";
//		String gPath = "neo4j-test/Integrated_GMap-mouse_human_test";
//		String mappingsPath = "alignments/GMap-mouse-human.rdf";		
//		String referencePath = "alignments/ReferenceAlignment/reference_2015.rdf"; //The reference alignments
		
		//Experiments
		//Conference	
//		String oFile1 = "testdata/conference/cmt.owl";
//		String oFile2 = "testdata/conference/Conference.owl";
//		String gPath = "neo4j-test/Integrated_cmt_Conference_test-HMatch-O";	
//		String mappingsPath = "testdata/mappings/HMatch-cmt-conference.rdf";	
//		String referencePath = "testdata/referenceAlignment/cmt-conference.rdf";//The reference alignments	
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-conference";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-conference";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-conference";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-conference";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-conference";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-conference";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-conference";
		
			
//		String oFile1  = "testdata/conference/cmt.owl";
//		String oFile2  = "testdata/conference/confOf.owl";
//		String gPath = "neo4j-test/Integrated_cmt_confOf_test-HMatch-O";			
//		String mappingsPath = "testdata/mappings/HMatch-cmt-confof.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-confof.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-confof";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-confof";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-confof";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-confof";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-confof";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-confof";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-confof";

		
		
//		String oFile1  = "testdata/conference/cmt.owl";
//		String oFile2  = "testdata/conference/edas.owl";
//		String gPath = "neo4j-test/Integrated_cmt_edas_test-HMatch-O";	
//		String mappingsPath = "testdata/mappings/HMatch-cmt-edas.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-edas";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-edas";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-edas";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-edas";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-edas";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-edas";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-edas";
		
		
//		String oFile1  = "testdata/conference/cmt.owl";
//		String oFile2  = "testdata/conference/ekaw.owl";
//		String gPath = "neo4j-test/Integrated_cmt_ekaw_test-HMatch-O";	
//		String mappingsPath = "testdata/mappings/HMatch-cmt-ekaw.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-ekaw";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-ekaw";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-ekaw";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-ekaw";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-ekaw";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-ekaw";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-ekaw";
		
		
//		String oFile1  = "testdata/conference/cmt.owl";
//		String oFile2  = "testdata/conference/iasted.owl";
//		String gPath = "neo4j-test/Integrated_cmt_iasted_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-cmt-iasted.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-iasted";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-iasted";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-iasted";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-iasted";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-iasted";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-iasted";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-iasted";
		
		
//		String oFile1  = "testdata/conference/cmt.owl";
//		String oFile2  = "testdata/conference/sigkdd.owl";
//		String gPath = "neo4j-test/Integrated_cmt_sigkdd_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-cmt-sigkdd.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-sigkdd";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-sigkdd";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-sigkdd";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-sigkdd";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-sigkdd";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-sigkdd";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-sigkdd";
				
//		String oFile1  = "testdata/conference/Conference.owl";
//		String oFile2  = "testdata/conference/confOf.owl";
//		String gPath = "neo4j-test/Integrated_Conference_confOf_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-conference-confof.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-confOf.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-confOf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-confOf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-confOf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-confOf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-confOf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-confOf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-confOf";		


		
//		String oFile1  = "testdata/conference/Conference.owl";
//		String oFile2  = "testdata/conference/edas.owl";
//		String gPath = "neo4j-test/Integrated_Conference_edas_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-conference-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-edas.rdf";
//		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-edas";
//		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-edas";
//		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-edas";
//		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-edas";
//		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-edas";
//		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-edas";
//		String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-edas";
		
		//?????????
//		String oFile1  = "testdata/conference/Conference.owl";
//		String oFile2  = "testdata/conference/ekaw.owl";
//		String gPath = "neo4j-test/Integrated_Conference_ekaw_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-conference-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-ekaw";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-ekaw";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-ekaw";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-ekaw";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-ekaw";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-ekaw";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-ekaw";

		
//		String oFile1  = "testdata/conference/Conference.owl";
//		String oFile2  = "testdata/conference/iasted.owl";
//		String gPath = "neo4j-test/Integrated_Conference_iasted_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-conference-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-iasted";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-iasted";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-iasted";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-iasted";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-iasted";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-iasted";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-iasted";
		
		
		//??????
//		String oFile1  = "testdata/conference/Conference.owl";
//		String oFile2  = "testdata/conference/sigkdd.owl";
//		String gPath = "neo4j-test/Integrated_Conference_sigkdd_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-conference-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-sigkdd.rdf";
//		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-sigkdd";
//		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-sigkdd";
//		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-sigkdd";
//		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-sigkdd";
//		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-sigkdd";
//		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-sigkdd";
//		String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-sigkdd";
		
		
		
//		String oFile1  = "testdata/conference/confOf.owl";
//		String oFile2  = "testdata/conference/edas.owl";
//		String gPath = "neo4j-test/Integrated_confOf_edas_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-confof-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-edas";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-edas";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-edas";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-edas";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-edas";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-edas";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-edas";
		
		
//		String oFile1  = "testdata/conference/confOf.owl";
//		String oFile2  = "testdata/conference/ekaw.owl";
//		String gPath = "neo4j-test/Integrated_confOf_ekaw_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-confof-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-ekaw.rdf";
//		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-ekaw";
//		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-ekaw";
//		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-ekaw";
//		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-ekaw";
//		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-ekaw";
//		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-ekaw";
//		String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-ekaw";
		
		
//		String oFile1  = "testdata/conference/confOf.owl";
//		String oFile2  = "testdata/conference/iasted.owl";
//		String gPath = "neo4j-test/Integrated_confOf_iasted_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-confof-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-iasted";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-iasted";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-iasted";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-iasted";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-iasted";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-iasted";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-iasted";
		
		
//		String oFile1  = "testdata/conference/confOf.owl";
//		String oFile2  = "testdata/conference/sigkdd.owl";
//		String gPath = "neo4j-test/Integrated_confOf_sigkdd_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-confof-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-sigkdd";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-sigkdd";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-sigkdd";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-sigkdd";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-sigkdd";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-sigkdd";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-sigkdd";
		
		
		//?????????????????? 23313 seconds???
//		String oFile1  = "testdata/conference/edas.owl";
//		String oFile2  = "testdata/conference/ekaw.owl";
//		String gPath = "neo4j-test/Integrated_edas_ekaw_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-edas-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-edas-ekaw";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-edas-ekaw";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-edas-ekaw";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-edas-ekaw";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-ekaw";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-edas-ekaw";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-ekaw";
		
		
//		String oFile1  = "testdata/conference/edas.owl";
//		String oFile2  = "testdata/conference/iasted.owl";
//		String gPath = "neo4j-test/Integrated_edas_iasted_test-HMatch-O";
//		String mappingsPath = "testdata/mappings/HMatch-edas-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-edas-iasted";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-edas-iasted";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-edas-iasted";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-edas-iasted";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-iasted";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-edas-iasted";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-iasted";
		
		
		//???????????????
		String oFile1  = "testdata/conference/edas.owl";
		String oFile2  = "testdata/conference/sigkdd.owl";
		String gPath = "neo4j-test/Integrated_edas_sigkdd_test";
		String mappingsPath = "testdata/mappings/HMatch-edas-sigkdd.rdf";
		String referencePath= "testdata/referenceAlignment/edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-edas-sigkdd";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-edas-sigkdd";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-edas-sigkdd";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-edas-sigkdd";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-sigkdd";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-edas-sigkdd";
		String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-sigkdd";
		
//		String oFile1  = "testdata/conference/ekaw.owl";
//		String oFile2  = "testdata/conference/iasted.owl";
//		String gPath = "neo4j-test/Integrated_ekaw_iasted_test";
//		String mappingsPath = "testdata/mappings/HMatch-ekaw-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-ekaw-iasted";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-ekaw-iasted";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-ekaw-iasted";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-ekaw-iasted";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-ekaw-iasted";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-ekaw-iasted";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-ekaw-iasted";
		
//		String oFile1  = "testdata/conference/ekaw.owl";
//		String oFile2  = "testdata/conference/sigkdd.owl";
//		String gPath = "neo4j-test/Integrated_ekaw_sigkdd_test";
//		String mappingsPath = "testdata/mappings/HMatch-ekaw-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-ekaw-sigkdd";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-ekaw-sigkdd";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-ekaw-sigkdd";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-ekaw-sigkdd";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-ekaw-sigkdd";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-ekaw-sigkdd";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-ekaw-sigkdd";
		
//		String oFile1  = "testdata/conference/iasted.owl";
//		String oFile2  = "testdata/conference/sigkdd.owl";
//		String gPath = "neo4j-test/Integrated_iasted_sigkdd_test";
//		String mappingsPath = "testdata/mappings/HMatch-iasted-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-iasted-sigkdd";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-iasted-sigkdd";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-iasted-sigkdd";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-iasted-sigkdd";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-iasted-sigkdd";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-iasted-sigkdd";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-iasted-sigkdd";
		
	
		
		
		//??????mappings?????????
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
				
		//??????????????????
		//createGraphDB(gPath, oFile1, oFile2, mappingsPath);
		//createGraphDB(gPath, oFile1, oFile2, MappingInformation);
		
		//???????????????????????????mappings
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
	
	//????????????????????????????????????????????????
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


