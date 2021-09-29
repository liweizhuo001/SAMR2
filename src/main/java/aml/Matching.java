/******************************************************************************
* Copyright 2013-2016 LASIGE                                                  *
*                                                                             *
* Licensed under the Apache License, Version 2.0 (the "License"); you may     *
* not use this file except in compliance with the License. You may obtain a   *
* copy of the License at http://www.apache.org/licenses/LICENSE-2.0           *
*                                                                             *
* Unless required by applicable law or agreed to in writing, software         *
* distributed under the License is distributed on an "AS IS" BASIS,           *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    *
* See the License for the specific language governing permissions and         *
* limitations under the License.                                              *
*                                                                             *
*******************************************************************************
* Test-runs AgreementMakerLight in Eclipse.                                   *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package aml;

public class Matching
{

//Main Method
	
	public static void main(String[] args) throws Exception
	{
		//Path to input ontology files (edit manually)
		String sourcePath  = "exampleOntology/example1.owl";
	    String targetPath  = "exampleOntology/example2.owl";
	    String alignPath = "exampleOntology/GMap-crs-cmt_test2.rdf";
		String referencePath   = "testdata/conference/references/crs-cmt2.rdf";
		String outputPath="Results/12.rdf";
		
		
		//Conference
		/*String sourcePath = "testdata/conference/ontologies/confOf.owl";
		String targetPath = "testdata/conference/ontologies/edas.owl";
		//String referencePath = "testdata/conference/references/conference-edas.rdf";
		//Path to save output alignment (edit manually, or leave blank for no evaluation)
		String outputPath = "Results/AML/confOf-edas.rdf";*/
		
		//Anatomy
		/*String sourcePath  = "testdata/anatomy/mouse.owl";
		String targetPath  = "testdata/anatomy/human.owl";
		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		String outputPath="Results/mouse-human.rdf";*/
		
		//Phenotype ontology  ³¬Ê±
		/*String sourcePath  = "testdata/phenotype/hp.owl";
		String targetPath  = "testdata/phenotype/mp.owl";	
		String referencePath   = "testdata/phenotype/HP_MP.rdf";
		String outputPath="Results/AML/AML_M-HP-MP-2017.rdf";*/
		
		/*String sourcePath  = "testdata/phenotype/doid.owl";
		String targetPath  = "testdata/phenotype/ordo.owl";	
		String referencePath   = "testdata/phenotype/DOID_ORDO.rdf";
		String outputPath="Results/AML/AML_M-doid-ordo-2017.rdf";*/
		
		//Large-Biology ontology
		/*String sourcePath  = "testdata/FMA-NCI-small/oaei_NCI_small_overlapping_fma.owl";
		String targetPath  = "testdata/FMA-NCI-small/oaei_FMA_small_overlapping_nci.owl";	
		String referencePath   = "testdata/FMA-NCI-small/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		String outputPath="Results/AML/FMA-NCI-small.rdf";*/
		
		/*String sourcePath  = "testdata/FMA-SNOMED-small/oaei_SNOMED_small_overlapping_fma.owl";
		String targetPath  = "testdata/FMA-SNOMED-small/oaei_FMA_small_overlapping_snomed.owl";	
		String referencePath   = "testdata/FMA-SNOMED-small/oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf";
		String outputPath="Results/AML/FMA-SNOMED-small.rdf";*/
		
		/*String sourcePath  = "testdata/SNOMED-NCI-small/oaei_SNOMED_small_overlapping_nci.owl";	
		String targetPath  = "testdata/SNOMED-NCI-small/oaei_NCI_small_overlapping_snomed.owl";	
		String referencePath   = "testdata/SNOMED-NCI-small/oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		String outputPath="Results/AML/SNOMED-NCI-small.rdf";*/
		
		/*String sourcePath  = "testdata/FMA-NCI-whole/oaei_FMA_whole_ontology.owl";
		String targetPath  = "testdata/FMA-NCI-whole/oaei_NCI_whole_ontology.owl";
		//String referencePath   = "testdata/FMA-NCI-whole/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		String outputPath="Results/AML/FMA-NCI-whole.rdf";*/
		
		/*String sourcePath  = "testdata/FMA-SNOMED-whole/oaei_FMA_whole_ontology.owl";
		String targetPath  = "testdata/FMA-SNOMED-whole/oaei_SNOMED_extended_overlapping_fma_nci.owl";
		//String referencePath   = "testdata/FMA-SNOMED-whole/oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf";
		String outputPath="Results/AML/FMA-SNOMED-whole.rdf";*/
		
	/*	String sourcePath  = "testdata/SNOMED-NCI-whole/oaei_SNOMED_extended_overlapping_fma_nci.owl";
		String targetPath  = "testdata/SNOMED-NCI-whole/oaei_NCI_whole_ontology.owl";
		//String referencePath   = "testdata/SNOMED-NCI-whole/oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		String outputPath="Results/AML/SNOMED-NCI-whole.rdf";*/
		
		
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);
		aml.matchAuto();
		
		if(!referencePath.equals(""))
		{
			aml.openReferenceAlignment(referencePath);
			aml.getReferenceAlignment();
			aml.evaluate();
			System.out.println(aml.getEvaluation());
		}
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
	}
}