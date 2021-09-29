package statistics;


import java.io.IOException;
import java.util.ArrayList;





public class simpleStatistic {
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
		
//		String orginalAlignment = "testdata/mappings/LogMapLite-largebio-fma_nci_whole.rdf";
//		String repairedAlignment = "Results/AML-LogMapLite-FMA-NCI-whole-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
//		
//		String orginalAlignment = "testdata/mappings/AML_M-FMA-NCI-whole.rdf";
//		String repairedAlignment = "Results/AML-AML_M-FMA-NCI-whole-repaired.rdf";
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
		
//		String orginalAlignment = "testdata/mappings/LogMapLite-largebio-fma_nci_whole.rdf";
//		String repairedAlignment = "Results/LogMap-LogMapLite-FMA-NCI-whole.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
//		String orginalAlignment = "testdata/mappings/AML_M-FMA-NCI-whole.rdf";
//		String repairedAlignment = "Results/LogMap-AML_M-FMA-NCI-whole.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
		
		//Interative
//		String orginalAlignment = "testdata/mappings/LogMapLite-mouse-human-C.rdf";
//		String repairedAlignment = "Results/Interactive-reliable-LogMapLite-mouse-human-repaired.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
//		String referencePath = "testdata/referenceAlignment/reference_2015.rdf";
		
//		String orginalAlignment = "testdata/mappings/AML_M-FMA-NCI-whole.rdf";
//		String repairedAlignment = "Results/Simple-5-Interactive-AML_M-fma-nci-whole.rdf";
//		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
//		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		
		
		String orginalAlignment = "testdata/mappings/AML_M-FMA-NCI-whole.rdf";
		String repairedAlignment = "Results/LogMap-mouse-human-mapping-ppa.rdf";
		//String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
		String referencePath = "testdata/referenceAlignment/oaei_FMA2NCI_UMLS_reference.rdf";
	
			
		//MappingInfoForLogMapLite MappingInformation2=new MappingInfoForLogMapLite(orginalAlignment);	
		MappingInfo MappingInformation=new MappingInfo(orginalAlignment);	
		ArrayList<String> mappings= new ArrayList<String>();
		mappings=MappingInformation.getMappings();
		System.out.println("The number of original alignment is "+mappings.size());			
		
		
		MappingInfo MappingInformation2=new MappingInfo(repairedAlignment);	
		ArrayList<String> mappings2= new ArrayList<String>();
		mappings2=MappingInformation2.getMappings();
		System.out.println("The number of repaired alignment is "+mappings2.size());
		
		ArrayList<String> referenceMappings= new ArrayList<String>();
		MappingInfo ReferenceInformation=new MappingInfo(referencePath);
		referenceMappings=ReferenceInformation.getMappings();
		
		System.out.println("The number of refenerece alignment is "+referenceMappings.size());
		System.out.println("--------------------------------------------------------");
		System.out.println("mapping reduced from " + mappings.size() + " to " + mappings2.size() + " correspondences");
		
		ArrayList<String> removedMappings =new ArrayList<String>();
		removedMappings.addAll(mappings);
		removedMappings.removeAll(mappings2);
		
		if(removedMappings.isEmpty())
			System.out.println("No mappings will be removed!");
		else
		{
			System.out.println("Removed the following "+removedMappings.size()+" correspondences:" );
//			for(String s:removedMappings)
//			{
//				System.out.println(s);
//			}
		}
		System.out.println("--------------------------------------------------------");
		Evaluation cBefore = new Evaluation(mappings, referenceMappings);
		Evaluation cAfter = new Evaluation(mappings2, referenceMappings);
		System.out.println("before debugging (pre, rec, f): " + cBefore.toShortDesc());
		System.out.println("The number of total correct mappings :  " + (cBefore.getCorrectAlignment()));
		System.out.println("The number of total wrong mappings:  " + (cBefore.getMatcherAlignment()-cBefore.getCorrectAlignment()));
		System.out.println("--------------------------------------------------------");
		System.out.println("after debugging (pre, rec, f):  " + cAfter.toShortDesc());
		System.out.println("The number of total correct mappings :  " + (cAfter.getCorrectAlignment()));
		System.out.println("The number of wrong removed mappings:  " + (cAfter.getMatcherAlignment()-cAfter.getCorrectAlignment()));
	
		
	}
	
}
