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

public class RepairingByGraphASMOV
{

//Main Method
	
	public static void main(String[] args) throws Exception
	{
		
		long time = System.currentTimeMillis()/1000;		
		//SimpleTest
		/*String sourcePath  = "exampleOntology/example1.owl";
	    String targetPath  = "exampleOntology/example2.owl";
	    String alignPath = "exampleOntology/12.rdf";
	    String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		String outputPath="exampleOntology/123.rdf";*/
		
		/*String sourcePath  = "exampleOntology/example3.owl";
	    String targetPath  = "exampleOntology/example2.owl";
	    String alignPath = "exampleOntology/32.rdf";
	    String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		String outputPath="exampleOntology/123.rdf";*/
		
		/*String sourcePath  = "exampleOntology/cmt_test4.owl";
		String targetPath  = "exampleOntology/confOf_test4.owl";
		String alignPath = "exampleOntology/cmt-confOf_test.rdf";
		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		String outputPath="exampleOntology/1234.rdf";*/
		
//		String sourcePath  = "exampleOntology/cmt_test5.owl";
//		String targetPath  = "exampleOntology/confOf_test5.owl";
//		String alignPath = "exampleOntology/cmt-confOf_test2.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
//		String outputPath="exampleOntology/1234.rdf";
		
		/*String sourcePath  = "testdata/conference/cmt.owl";
		String targetPath  = "testdata/conference/edas.owl";
		String alignPath = "exampleOntology/GMap-cmt-edas_test.rdf";
		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		String outputPath="exampleOntology/1234.rdf";*/
		
		//Experiment	
		//ASMOV
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/Conference.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-conference.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-conference.rdf";
		
			
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-confof.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-confof.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-confof.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-edas.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-edas.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-edas.rdf";
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-ekaw.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-ekaw.rdf";
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-iasted.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-iasted.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-sigkdd.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-sigkdd.rdf";
				
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "testdata/mappings/asmov-conference-confof.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-confOf.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-confof.rdf";	
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/asmov-conference-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-edas.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-edas.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-conference-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-ekaw.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-conference-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-iasted.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-conference-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-sigkdd.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/asmov-confof-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-edas.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-edas.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-confof-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-ekaw.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-confof-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-iasted.rdf";
		
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-confof-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-sigkdd.rdf";

		
	
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-edas-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-ekaw.rdf";
//		String outputPath="Results/Interactive/one2one/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-edas-ekaw.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-edas-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-edas-iasted.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-edas-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-edas-sigkdd.rdf";
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-ekaw-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-ekaw-iasted.rdf";
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-ekaw-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-ekaw-sigkdd.rdf";
		
		String sourcePath  = "testdata/conference/iasted.owl";
		String targetPath  = "testdata/conference/sigkdd.owl";
		String alignPath = "testdata/mappings/asmov-iasted-sigkdd.rdf";
		String referencePath= "testdata/referenceAlignment/iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-iasted-sigkdd.rdf";
		String outputPath="Results/Interactive/graphImpact3/Revised-asmov-iasted-sigkdd.rdf";
				
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);
		if(!alignPath.equals(""))
		{
			aml.openAlignment(alignPath);
			aml.repairByGraph();  //����logical incoherent ���޸�
		}
		//��֤path�ĳ���
		//aml.getPath();		
		if(!referencePath.equals(""))
		{
			aml.openReferenceAlignment(referencePath);
			aml.simpleEvaluate();		
		}
		
		System.out.println("The whole repair by our method is " + (System.currentTimeMillis()/1000-time) + " seconds");
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
			
	}
}