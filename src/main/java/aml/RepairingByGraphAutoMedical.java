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

public class RepairingByGraphAutoMedical
{

//Main Method
	
	public static void main(String[] args) throws Exception
	{
		
		long time = System.currentTimeMillis()/1000;
		
		String sourcePath  = "testdata/anatomy/mouse.owl";
		String targetPath  = "testdata/anatomy/human.owl";
		String alignPath = "testdata/mappings/AROMA-mouse-human.rdf";
		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AROMA-mouse-human.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AROMA-mouse-human.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AROMA-mouse-human-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AROMA-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AROMA-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AROMA-mouse-human.rdf";
		String outputPath="Results/Oracle/graphImpact3/Revised-AROMA-mouse-human.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-AROMA-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AROMA-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AROMA-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AROMA-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AROMA-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AROMA-mouse-human-5.rdf";

		
//		String sourcePath  = "testdata/anatomy/mouse.owl";
//		String targetPath  = "testdata/anatomy/human.owl";
//		String alignPath = "testdata/mappings/AML_M-mouse-human.rdf";
//		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AML_M-mouse-human.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-mouse-human.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-mouse-human-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AML_M-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-mouse-human.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AML_M-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-mouse-human.rdf-5";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-mouse-human-10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-mouse-human-10.rdf";
		
		
//		String sourcePath  = "testdata/anatomy/mouse.owl";
//		String targetPath  = "testdata/anatomy/human.owl";
//		String alignPath = "testdata/mappings/LogMapLite-mouse-human-C.rdf";
//		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-LogMapLite-mouse-human.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-mouse-human.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-mouse-human-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-mouse-human.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-mouse-human-10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-mouse-human-10.rdf";
		
		
//		String sourcePath  = "testdata/anatomy/mouse.owl";
//		String targetPath  = "testdata/anatomy/human.owl";
//		String alignPath = "testdata/mappings/FCA_Map-mouse-human.rdf";
//		String referencePath   = "testdata/anatomy/reference_2015.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-FCA_Map-mouse-human.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-FCA_Map-mouse-human.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-FCA_Map-mouse-human-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-FCA_Map-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-FCA_Map-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-FCA_Map-mouse-human.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-FCA_Map-mouse-human.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-FCA_Map-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-FCA_Map-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-FCA_Map-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-FCA_Map-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-FCA_Map-mouse-human-5.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-FCA_Map-mouse-human-10.rdf";
		
	
		//LargeBiomedical
		//不加influence的限制就可以跑了
//		String sourcePath  = "testdata/largeBio/oaei_FMA_small_overlapping_nci.owl";
//		String targetPath  = "testdata/largeBio/oaei_NCI_small_overlapping_fma.owl";
//		String alignPath = "testdata/mappings/AROMA-fma-nci-small.rdf";
//		String referencePath   = "testdata/largeBio/oaei_FMA2NCI_UMLS_reference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AROMA-fma-nci-small.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AROMA-fma-nci-small.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AROMA-fma-nci-small-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AROMA-fma-nci-small.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AROMA-fma-nci-small.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AROMA-fma-nci-small.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AROMA-fma-nci-small.rdf";
		//String outputPath="Results/Medical-fma-nci-small-test1.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-AROMA-fma-nci-small-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AROMA-fma-nci-small-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AROMA-fma-nci-small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AROMA-fma-nci-small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AROMA-fma-nci-small-10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AROMA-fma-nci-small-10.rdf";
		
		
		//不加influence的限制就可以跑了
//		String sourcePath  = "testdata/largeBio/oaei_FMA_small_overlapping_nci.owl";
//		String targetPath  = "testdata/largeBio/oaei_NCI_small_overlapping_fma.owl";
//		String alignPath = "testdata/mappings/AML_M-largebio-fma_nci_small.rdf";
//		String referencePath   = "testdata/largeBio/oaei_FMA2NCI_UMLS_reference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AML_M-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-FMA_small-NCI_small-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AML_M-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-FMA_small-NCI_small.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AML_M-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-FMA_small-NCI_small-10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-FMA_small-NCI_small-10.rdf";

		//可以跑得动
//		String sourcePath  = "testdata/largeBio/oaei_FMA_small_overlapping_nci.owl";
//		String targetPath  = "testdata/largeBio/oaei_NCI_small_overlapping_fma.owl";
//		String alignPath = "testdata/mappings/LogMapLite-largebio-fma_nci_small.rdf";
//		String referencePath   = "testdata/largeBio/oaei_FMA2NCI_UMLS_reference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-LogMapLite-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-FMA_small-NCI_small-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-FMA_small-NCI_small.rdf";

		
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-FMA_small-NCI_small-5.rdf";
//		String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-FMA_small-NCI_small-5.rdf";
		
		
//		String sourcePath  = "testdata/largeBio/oaei_FMA_small_overlapping_nci.owl";
//		String targetPath  = "testdata/largeBio/oaei_NCI_small_overlapping_fma.owl";
//		String alignPath = "testdata/mappings/FCA_Map-FMA_small-NCI_small.rdf";
//		String referencePath   = "testdata/largeBio/oaei_FMA2NCI_UMLS_reference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-FCA_Map-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-FCA_Map-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-FCA_Map-FMA_small-NCI_small-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-FCA_Map-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-FCA_Map-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-FCA_Map-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-FCA_Map-FMA_small-NCI_small.rdf";
		//String outputPath="Results/Medical-fma-nci-small-test4.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-FCA_Map-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-FCA_Map-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-FCA_Map-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-FCA_Map-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-FCA_Map-FMA_small-NCI_small-5.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-FCA_Map-FMA_small-NCI_small-10.rdf";
			
		//测试大规模的情况是否可行
		//不加influence的限制就可以跑了
//		String sourcePath  = "testdata/largeBio/oaei_FMA_whole_ontology.owl";
//		String targetPath  = "testdata/largeBio/oaei_NCI_whole_ontology.owl";
//		String alignPath = "testdata/mappings/AML_M-FMA-NCI-whole.rdf";
//		String referencePath   = "testdata/largeBio/oaei_FMA2NCI_UMLS_reference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-AML_M-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-FMA-NCI-whole-new.rdf";
	    //String outputPath="Results/Oracle/normImpact/Revised-AML_M-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-FMA-NCI-whole.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-AML_M-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-AML_M-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-AML_M-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-AML_M-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-AML_M-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-AML_M-FMA-NCI-whole.rdf-5.rdf";
		

		//不加influence的限制就可以跑了
//		String sourcePath  = "testdata/largeBio/oaei_FMA_whole_ontology.owl";
//		String targetPath  = "testdata/largeBio/oaei_NCI_whole_ontology.owl";
//		String alignPath = "testdata/mappings/LogMapLite-largebio-fma_nci_whole.rdf";
//		String referencePath   = "testdata/largeBio/oaei_FMA2NCI_UMLS_reference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-LogMapLite-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-FMA-NCI_whole-new.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-FMA-NCI-whole.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-FMA-NCI-whole.rdf";
		
		//String outputPath="Results/Oracle/bridgeRule/Revised-LogMapLite-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-LogMapLite-FMA-NCI_whole-5.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-LogMapLite-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-LogMapLite-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-LogMapLite-FMA-NCI-whole-5.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-LogMapLite-FMA-NCI-whole-5-new.rdf";
		
				

		
		
				
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);	
		//aml.matchAuto();  //�Զ���ƥ��
		//aml.getAlignment();
		
		if(!referencePath.equals(""))
		{
			aml.openReferenceAlignment(referencePath);
			//aml.evaluate();
			//aml.evaluate2();
			//System.out.println(aml.getEvaluation());			
		}
		if(!alignPath.equals(""))
		{
			aml.openAlignment(alignPath);
			//aml.repair();  //����logical incoherent ���޸�
			aml.repairByGraph();  //����logical incoherent ���޸�
		}
		aml.simpleEvaluate();
		//aml.getPath();		
	
		 
		System.out.println("The whole repair by our method is " + (System.currentTimeMillis()/1000-time) + " seconds");
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
			
	}
}