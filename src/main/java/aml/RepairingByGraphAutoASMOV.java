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

public class RepairingByGraphAutoASMOV
{

//Main Method
	
	public static void main(String[] args) throws Exception
	{
		
		long time = System.currentTimeMillis()/1000;
		
		//Conference		
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
		
		/*String sourcePath  = "testdata/conference/cmt.owl";
		String targetPath  = "testdata/conference/edas.owl";
		String alignPath = "exampleOntology/GMap-cmt-edas_test.rdf";
		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		String outputPath="exampleOntology/1234.rdf";*/
		
		//Experiment	
		//asmov
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/Conference.owl";
//		String alignPath = "alignment4Experiment/asmov-cmt-conference.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-conference.rdf";
		//String outputPath="Results/test1234.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-conference-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-conference-Enhanced.rdf";
		
			
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "alignment4Experiment/asmov-cmt-confof.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-confof.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-cmt-confof.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-confof.rdf";
		
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-confof-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-confof-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "alignment4Experiment/asmov-cmt-edas.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-cmt-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-edas.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-edas-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-edas-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "alignment4Experiment/asmov-cmt-ekaw.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-ekaw.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-ekaw.rdf-Enhanced";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-ekaw.rdf-Enhanced";

		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "alignment4Experiment/asmov-cmt-iasted.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-cmt-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-iasted.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-iasted-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "alignment4Experiment/asmov-cmt-sigkdd.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-sigkdd.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-cmt-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-cmt-iasted-Enhanced.rdf";
				
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "alignment4Experiment/asmov-conference-confof.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-confOf.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-conference-confof.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-confof.rdf";		

		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-confof-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-confof-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "alignment4Experiment/asmov-conference-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-conference-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-edas.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-edas-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-edas-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "alignment4Experiment/asmov-conference-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-conference-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-ekaw.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-ekaw-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "alignment4Experiment/asmov-conference-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-conference-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-iasted.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-iasted-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "alignment4Experiment/asmov-conference-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-sigkdd.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-conference-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-conference-sigkdd-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "alignment4Experiment/asmov-confof-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-confof-edas.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-edas.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-edas-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-edas-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "alignment4Experiment/asmov-confof-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-confof-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-ekaw.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-ekaw-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "alignment4Experiment/asmov-confof-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-confof-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-iasted.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-iasted-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "alignment4Experiment/asmov-confof-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-confof-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-sigkdd.rdf";

		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-confof-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-confof-sigkdd-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "alignment4Experiment/asmov-edas-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-edas-ekaw.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-edas-ekaw.rdf";

		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-edas-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-edas-ekaw-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "alignment4Experiment/asmov-edas-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-edas-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-edas-iasted.rdf";

		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-edas-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-edas-iasted-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "alignment4Experiment/asmov-edas-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-edas-sigkdd.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-edas-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-edas-sigkdd-Enhanced.rdf";

		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "alignment4Experiment/asmov-ekaw-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-ekaw-iasted.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-ekaw-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-ekaw-iasted-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "alignment4Experiment/asmov-ekaw-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-ekaw-sigkdd.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-ekaw-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-ekaw-sigkdd-Enhanced.rdf";
		 
		
		String sourcePath  = "testdata/conference/iasted.owl";
		String targetPath  = "testdata/conference/sigkdd.owl";
		String alignPath = "alignment4Experiment/asmov-iasted-sigkdd.rdf";
		String referencePath= "testdata/referenceAlignment/iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-asmov-iasted-sigkdd.rdf";
		String outputPath="Results/Oracle/graphImpact3/Revised-asmov-iasted-sigkdd.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-asmov-iasted-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-asmov-iasted-sigkdd-Enhanced.rdf";
		
		
		
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
		//��֤path�ĳ���
		//aml.getPath();		
	
		
		System.out.println("The whole repair by our method is " + (System.currentTimeMillis()/1000-time) + " seconds");
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
			
	}
}