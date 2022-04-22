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

public class RepairingByGraphAutoHMatch
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
		//HMatch
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/Conference.owl";
//		String alignPath = "testdata/mappings/HMatch-cmt-conference.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-conference.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-conference-e10.rdf";
		//String outputPath="Results/test123.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-conference-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-conference-Enhanced.rdf";
		
			
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "testdata/mappings/HMatch-cmt-confof.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-confof.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-confof-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-confof-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-confof-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-confof-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-confof-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-confof-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-confof-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-confof-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-confof-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/HMatch-cmt-edas.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-edas-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-edas-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-edas-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-edas-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-edas-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-edas-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/HMatch-cmt-ekaw.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-ekaw-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-ekaw-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/HMatch-cmt-iasted.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-iasted-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-iasted-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-iasted-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-iasted-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-iasted-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/HMatch-cmt-sigkdd.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-cmt-sigkdd.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-cmt-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-cmt-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-cmt-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-cmt-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-sigkdd-e10.rdf";	
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-cmt-sigkdd-Enhanced.rdf";			
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-cmt-sigkdd-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "testdata/mappings/HMatch-conference-confof.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-confOf.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-confof-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-confof-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-confof-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-confof-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-confof-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-confof-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-confof-e10.rdf";	
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-confof-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-confof-Enhanced.rdf";	
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/HMatch-conference-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-edas-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-eda-e10s.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-edas-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-edas-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-edas-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-edas-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/HMatch-conference-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-ekaw-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-ekaw.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/HMatch-conference-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-iasted-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-iasted-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-iasted-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-iasted-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-iasted-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/HMatch-conference-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-conference-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-conference-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-conference-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-conference-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-conference-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-sigkdd-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-conference-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-conference-sigkdd-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/HMatch-confof-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-edas.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-edas-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-edas-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-edas-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-edas-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-edas-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-edas-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-edas-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/HMatch-confof-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-ekaw-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-ekaw-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/HMatch-confof-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-iasted-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-iasted-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-iasted-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-iasted-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-iasted-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/HMatch-confof-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-confof-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-confof-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-confof-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-confof-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-confof-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-sigkdd-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-confof-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-confof-sigkdd-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/HMatch-edas-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-ekaw.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-edas-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-edas-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-edas-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-edas-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-edas-ekaw-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-ekaw-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-ekaw-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-ekaw-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/HMatch-edas-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-edas-iasted-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-edas-iasted-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-edas-iasted-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-edas-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-edas-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-iasted-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-iasted-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/HMatch-edas-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-edas-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-edas-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-edas-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-edas-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-edas-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-sigkdd-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-edas-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-edas-sigkdd-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/HMatch-ekaw-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-iasted.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-ekaw-iasted-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-ekaw-iasted-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-ekaw-iasted-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-ekaw-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-ekaw-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-ekaw-iasted-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-ekaw-iasted-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-ekaw-iasted-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-ekaw-iasted-Enhanced.rdf";
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/HMatch-ekaw-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-ekaw-sigkdd-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-ekaw-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-ekaw-sigkdd-Enhanced.rdf";
		
		
//		String sourcePath  = "testdata/conference/iasted.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/HMatch-iasted-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/iasted-sigkdd.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-iasted-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-iasted-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-iasted-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-iasted-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-iasted-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-iasted-sigkdd-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-iasted-sigkdd-e10.rdf";
		
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-iasted-sigkdd-Enhanced.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-iasted-sigkdd-Enhanced.rdf";
		
		
		
//		String sourcePath  = "testdata/I3CON/WineA.owl";
//		String targetPath  = "testdata/I3CON/WineB.owl";
//		String alignPath = "testdata/mappings/HMatch-WineA-WineB.rdf";
//		String referencePath= "testdata/referenceAlignment/WineAB.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-WineA-WineB-e10.rdf";
		//String outputPath="Results/test.rdf";
		
		
//		String sourcePath  = "testdata/Benchmark/101-onto.rdf";
//		String targetPath  = "testdata/Benchmark/301-onto.rdf";
//		String alignPath = "testdata/mappings/HMatch-onto101-301.rdf";
//		String referencePath= "testdata/referenceAlignment/refalign-101-301.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact3/Revised-HMatch-onto101-301-e10.rdf";
		//String outputPath="Results/test.rdf";
		
		String sourcePath  = "testdata/Benchmark/101-onto.rdf";
		String targetPath  = "testdata/Benchmark/302-onto.rdf";
		String alignPath = "testdata/mappings/HMatch-onto101-302.rdf";
		String referencePath= "testdata/referenceAlignment/refalign-101-302.rdf";
		//String outputPath="Results/Oracle/one2one/Revised-HMatch-onto101-302-e10.rdf";
		//String outputPath="Results/Oracle/bridgeRule/Revised-HMatch-onto101-302-e10.rdf";
		//String outputPath="Results/Oracle/guaranteedImpact/Revised-HMatch-onto101-302-e10.rdf";
		//String outputPath="Results/Oracle/normImpact/Revised-HMatch-onto101-302-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact/Revised-HMatch-onto101-302-e10.rdf";
		//String outputPath="Results/Oracle/graphImpact2/Revised-HMatch-onto101-302-e10.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-HMatch-onto101-302-e10.rdf";
		String outputPath="Results/test.rdf";
		
		
				
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