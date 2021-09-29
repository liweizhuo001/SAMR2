package statistics;


import java.io.IOException;
import java.util.ArrayList;





public class simpleStatisticCompare {
	public static void main(String args[]) throws IOException
	{			
		//Anatomy
		
//		String orginalAlignment = "testdata/mappings/asmov-mouse-human.rdf";
//		String repairedAlignment = "Results/PDLMV-AML_M-mouse-human-repaired.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
		
		//AML 
//		String orginalAlignment = "testdata/mappings/asmov-mouse-human.rdf";
//		String repairedAlignment = "Results/AML-asmov-mouse-human-repaired.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
	
//		String orginalAlignment = "testdata/mappings/FCA_Map-mouse-human.rdf";
//		String repairedAlignment = "Results/AML-FCA_Map-mouse-human-repaired.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
//		String orginalAlignment = "testdata/mappings/LogMapLite-mouse-human-C.rdf";
//		String repairedAlignment = "Results/AML-LogMapLite-mouse-human-repaired.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
//		String orginalAlignment = "testdata/mappings/AML_M-mouse-human.rdf";
//		String repairedAlignment = "Results/AML-AML_M-mouse-human-repaired.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
		
//		String orginalAlignment = "testdata/mappings/asmov-fma-nci-small.rdf";
//		String repairedAlignment = "Results/AML-asmov-FMA-NCI-small-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment = "testdata/mappings/FCA_Map-largebio-fma_nci_small_2016-0.9.rdf";
//		String repairedAlignment = "Results/AML-FCA_Map-FMA-NCI-small-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment = "testdata/mappings/LogMapLite-largebio-fma_nci_small_2016-0.9.rdf";
//		String repairedAlignment = "Results/AML-LogMapLite-FMA-NCI-small-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment = "testdata/mappings/AML_M-largebio-fma_nci_small.rdf";
//		String repairedAlignment = "Results/AML-AML_M-FMA-NCI-small-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
		
		//LogMap 
		
//		String orginalAlignment = "testdata/mappings/asmov-mouse-human.rdf";
//		String repairedAlignment = "Results/LogMap-amsov-mouse-human.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
//		String orginalAlignment = "testdata/mappings/FCA_Map-mouse-human.rdf";
//		String repairedAlignment = "Results/LogMap-FCA_Map-mouse-human.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
//		String orginalAlignment = "testdata/mappings/LogMapLite-mouse-human-C.rdf";
//		String repairedAlignment = "Results/LogMap-LogMapLite-mouse-human.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
//		String orginalAlignment = "testdata/mappings/AML_M-mouse-human.rdf";
//		String repairedAlignment = "Results/LogMap-AML_M-mouse-human.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
		
//		String orginalAlignment = "testdata/mappings/asmov-fma-nci-small.rdf";
//		String repairedAlignment = "Results/LogMap-asmov-FMA-NCI-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment = "testdata/mappings/FCA_Map-largebio-fma_nci_small_2016-0.9.rdf";
//		String repairedAlignment = "Results/LogMap-FCA_Map-FMA-NCI-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
//		
//		String orginalAlignment = "testdata/mappings/LogMapLite-largebio-fma_nci_small_2016-0.9.rdf";
//		String repairedAlignment = "Results/LogMap-LogMapLite-FMA-NCI-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
//		
//		String orginalAlignment = "testdata/mappings/AML_M-largebio-fma_nci_small.rdf";
//		String repairedAlignment = "Results/LogMap-AML_M-FMA-NCI-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
		
//		String orginalAlignment1 = "Results/One2One/Interactive-asmov-mouse-human.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-asmov-mouse-human.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Interactive-asmov-mouse-human.rdf";		
//		String orginalAlignment2 = "Results/Interactive-reliable-asmov-mouse-human-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		

//		String orginalAlignment1 = "Results/One2One/Interactive-FCA_Map-mouse-human.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-FCA_Map-mouse-human.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Interactive-FCA_Map-mouse-human.rdf";		
//		String orginalAlignment2 = "Results/Interactive-reliable-FCA_Map-mouse-human-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment1 = "Results/One2One/Interactive-LogMapLite-mouse-human.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-LogMapLite-mouse-human.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Interactive-LogMapLite-mouse-human.rdf";		
//		String orginalAlignment2 = "Results/Interactive-reliable-LogMapLite-mouse-human-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment1 = "Results/One2One/Interactive-AML_M-mouse-human.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-AML_M-mouse-human.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Interactive-AML_M-mouse-human.rdf";		
//		String orginalAlignment2 = "Results/Interactive-reliable-AML_M-mouse-human.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment1 = "Results/One2One/Interactive-asmov-fma-nci-small.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-asmov-fma-nci-small.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Simple-Interactive-asmov-fma-nci-small.rdf";		
//		String orginalAlignment2 = "Results/Test/Simple-Interactive-asmov-fma-nci-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment1 = "Results/One2One/Interactive-FCA_Map-fma-nci-small.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-FCA_Map-fma-nci-small.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Simple-Interactive-FCA_Map-fma-nci-small.rdf";		
//		String orginalAlignment2 = "Results/Test/Simple-Interactive-FCA_Map-fma-nci-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment1 = "Results/One2One/Interactive-LogMapLite-fma-nci-small.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-LogMapLite-fma-nci-small.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Simple-Interactive-LogMapLite-fma-nci-small.rdf";		
//		String orginalAlignment2 = "Results/Test/Simple-Interactive-LogMapLite-fma-nci-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment1 = "Results/One2One/Interactive-AML_M-fma-nci-small.rdf";
//		String orginalAlignment2 = "Results/ChristianTool_new/Interactive-AML_M-fma-nci-small.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Simple-Interactive-AML_M-fma-nci-small.rdf";		
//		String orginalAlignment2 = "Results/Test/Simple-Interactive-AML_M-fma-nci-small.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		//String orginalAlignment1 = "Results/One2One/Interactive-LogMapLite-fma-nci-whole.rdf";
//		//String orginalAlignment2 = "Results/ChristianTool_new/Interactive-LogMapLite-fma-nci-whole.rdf";
//		String orginalAlignment1 = "Results/StructureImpactor_Complete/Simple-Interactive-LogMapLite-fma-nci-whole.rdf";		
//		String orginalAlignment2 = "Results/Test/Simple-Interactive-LogMapLite-fma-nci-whole.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
		//String orginalAlignment1 = "Results/One2One/Interactive-AML_M-fma-nci-whole.rdf";
		//String orginalAlignment2 = "Results/ChristianTool_new/Interactive-AML_M-fma-nci-whole.rdf";
		//String orginalAlignment1 = "Results/StructureImpactor_Complete/Simple-Interactive-AML_M-fma-nci-whole.rdf";		
		//String orginalAlignment2 = "Results/Test/Simple--Interactive-AML_M-fma-nci-whole.rdf";
		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";		
		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
		String orginalAlignment1 = "Results/Simple-10-Interactive-LogMapLite-fma-nci-whole.rdf";		
		String orginalAlignment2 = "Results/Simple-5-2-Interactive-LogMapLite-fma-nci-whole.rdf";
	
			
		//MappingInfoForLogMapLite MappingInformation2=new MappingInfoForLogMapLite(orginalAlignment);	
		MappingInfo MappingInformation=new MappingInfo(orginalAlignment1);	
		ArrayList<String> mappings= new ArrayList<String>();
		mappings=MappingInformation.getMappings();
		System.out.println("The number of original alignment is "+mappings.size());			
		
		MappingInfo MappingInformation2=new MappingInfo(orginalAlignment2);	
		ArrayList<String> mappings2= new ArrayList<String>();
		mappings2=MappingInformation2.getMappings();
		System.out.println("The number of original alignment2 is "+mappings2.size());	
		
		
		ArrayList<String> referenceMappings= new ArrayList<String>();
		MappingInfo ReferenceInformation=new MappingInfo(referencePath);
		referenceMappings=ReferenceInformation.getMappings();
		
		
		ArrayList<String> removedMappings =new ArrayList<String>();
		removedMappings.addAll(mappings);
		removedMappings.removeAll(mappings2);
		
//		for(String str:removedMappings)
//		{
//			System.out.println(str);
//		}

	
		
	}
	
}
