package statistics;


import java.io.IOException;
import java.util.ArrayList;

import statistics.EvaluationLargeBio;
import statistics.MappingInfo;


public class StatisticResultWhole {
	public static void main(String args[]) throws IOException
	{		
		/*String mappingsPath = "alignment/FCA_Map-FMA-NCI.rdf";
		String referencePath = "ReferenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";*/
		
	/*	String mappingsPath = "alignment/FCA_Map-FMA-SNOMED.rdf";
		String referencePath = "ReferenceAlignment/oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf";*/
		
		/*String mappingsPath = "alignment/FCA_Map-NCI-SNOMED.rdf";
		String referencePath = "ReferenceAlignment/oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf";*/
		
		/*String mappingsPath = "alignment/AML/FMA-NCI-small.rdf";
		//String mappingsPath = "alignment/FMA-NCI-small/AML-largebio-fma_nci_small_2016.rdf";
		String referencePath = "ReferenceAlignment/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";*/
		
		/*//String mappingsPath = "alignment/AML/FMA-SNOMED-small.rdf";
		String mappingsPath = "alignment/FMA-SNOMED-small/AML-largebio-fma_snomed_small_2016.rdf";
		String referencePath = "ReferenceAlignment/oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf";*/
		
		//String mappingsPath = "testdata/AML/SNOMED-NCI-small.rdf";
		String mappingsPath = "testdata/SNOMED-NCI-small/alignments/AML-largebio-snomed_nci_small_2016.rdf";
		String referencePath = "testdata/SNOMED-NCI-small/oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		
		MappingInfo MappingInformation=new MappingInfo(mappingsPath);	
		ArrayList<String> mappings= new ArrayList<String>();
		mappings=MappingInformation.getMappings();
		System.out.println(mappings.size());
		
		
		ArrayList<String> referenceMappings= new ArrayList<String>();
		MappingInfo ReferenceInformation=new MappingInfo(referencePath);
		referenceMappings=ReferenceInformation.getMappings();
		
		System.out.println(referenceMappings.size());
		EvaluationLargeBio cBefore = new EvaluationLargeBio(mappings, referenceMappings);
		
		System.out.println("--------------------------------------------------------");
		System.out.println("before debugging (pre, rec, f): " + cBefore.toShortDesc());
		System.out.println("The number of total correct mappings in alignment:  " + cBefore.getCorrectAlignment());
		System.out.println("The number of total unknow mappings in alignment:  " +cBefore.getUnknownAlignment());
		System.out.println("The number of total incorrect mappings in alignment:  " + cBefore.getInCorrectAlignment());		
		System.out.println("The number of total mappings in alignment:  " + cBefore.getMatcherAlignment());
		
		
		
	}
}
