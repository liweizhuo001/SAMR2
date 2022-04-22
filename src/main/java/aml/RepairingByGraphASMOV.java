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

import aml.match.Mapping;

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
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-conference-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-conference-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-conference-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-conference-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-conference-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-conference-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-conference-new.rdf";
		
			
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-confof.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-confof.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-confof-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-confof-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-confof-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-confof-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-confof-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-confof-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-confof-new.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-edas.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-edas.rdf";
//		String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-edas-new.rdf";
//		String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-edas-new.rdf";
//		String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-edas-new.rdf";
//		String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-edas-new.rdf";
//		String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-edas-new.rdf";
//		String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-edas-new.rdf";
//		String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-edas-new.rdf";
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-ekaw.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-ekaw-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-ekaw-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-ekaw-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-ekaw-new.rdf";
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-iasted.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-iasted-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-iasted-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-iasted-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-iasted-new.rdf";
		
		
//		String sourcePath  = "testdata/conference/cmt.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-cmt-sigkdd.rdf";
//		String referencePath   = "testdata/referenceAlignment/cmt-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-cmt-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-cmt-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-cmt-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-cmt-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-cmt-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-cmt-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-cmt-sigkdd-new.rdf";
				
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/confOf.owl";
//		String alignPath = "testdata/mappings/asmov-conference-confof.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-confOf.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-confof-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-confof-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-confof-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-confof-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-confof-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-confof-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-confof-new.rdf";	
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/asmov-conference-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-edas.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-edas-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-edas-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-edas-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-edas-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-edas-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-edas-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-edas-new.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-conference-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-ekaw-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-ekaw-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-ekaw-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-ekaw-new.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-conference-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-iasted-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-iasted-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-iasted-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-iasted-new.rdf";
		
//		String sourcePath  = "testdata/conference/Conference.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-conference-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/conference-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-conference-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-conference-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-conference-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-conference-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-conference-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-conference-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-conference-sigkdd-new.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/edas.owl";
//		String alignPath = "testdata/mappings/asmov-confof-edas.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-edas.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-edas-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-edas-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-edas-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-edas-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-edas-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-edas-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-edas-new.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-confof-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-ekaw-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-ekaw-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-ekaw-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-ekaw-new.rdf";
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-confof-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-iasted-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-iasted-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-iasted-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-iasted-new.rdf";
		
		
//		String sourcePath  = "testdata/conference/confOf.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-confof-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/confOf-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-confof-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-confof-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-confof-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-confof-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-confof-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-confof-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-confof-sigkdd-new.rdf";

		
	
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/ekaw.owl";
//		String alignPath = "testdata/mappings/asmov-edas-ekaw.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-ekaw.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-edas-ekaw-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-edas-ekaw-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-edas-ekaw-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-edas-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-edas-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-edas-ekaw-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-edas-ekaw-new.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-edas-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-edas-iasted-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-edas-iasted-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-edas-iasted-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-edas-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-edas-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-edas-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-edas-iasted-new.rdf";
		
		
//		String sourcePath  = "testdata/conference/edas.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-edas-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/edas-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-edas-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-edas-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-edas-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-edas-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-edas-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-edas-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-edas-sigkdd-new.rdf";
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/iasted.owl";
//		String alignPath = "testdata/mappings/asmov-ekaw-iasted.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-iasted.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-ekaw-iasted-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-ekaw-iasted-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-ekaw-iasted-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-ekaw-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-ekaw-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-ekaw-iasted-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-ekaw-iasted-new.rdf";
		
//		String sourcePath  = "testdata/conference/ekaw.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-ekaw-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/ekaw-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-ekaw-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-ekaw-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-ekaw-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-ekaw-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-ekaw-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-ekaw-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-ekaw-sigkdd-new.rdf";
		
//		String sourcePath  = "testdata/conference/iasted.owl";
//		String targetPath  = "testdata/conference/sigkdd.owl";
//		String alignPath = "testdata/mappings/asmov-iasted-sigkdd.rdf";
//		String referencePath= "testdata/referenceAlignment/iasted-sigkdd.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-iasted-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-iasted-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-iasted-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-iasted-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-iasted-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-iasted-sigkdd-new.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-iasted-sigkdd-new.rdf";
		
				
//		String sourcePath  = "testdata/I3CON/WineA.owl";
//		String targetPath  = "testdata/I3CON/WineB.owl";
//		String alignPath = "testdata/mappings/asmov-WineA-WineB.rdf";
//		String referencePath= "testdata/referenceAlignment/WineAB.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-WineA-WineB.rdf";
		//String outputPath="Results/test.rdf";
		
//		String sourcePath  = "testdata/Benchmark/101-onto.rdf";
//		String targetPath  = "testdata/Benchmark/301-onto.rdf";
//		String alignPath = "testdata/mappings/asmov-101-301.rdf";
//		String referencePath= "testdata/referenceAlignment/refalign-101-301.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/Interactive/graphImpact3/Revised-asmov-onto101-301.rdf";
		//String outputPath="Results/test.rdf";
		
		String sourcePath  = "testdata/Benchmark/101-onto.rdf";
		String targetPath  = "testdata/Benchmark/302-onto.rdf";
		String alignPath = "testdata/mappings/asmov-101-302.rdf";
		String referencePath= "testdata/referenceAlignment/refalign-101-302.rdf";
		//String outputPath="Results/Interactive/one2one/Revised-asmov-onto101-302.rdf";
		//String outputPath="Results/Interactive/bridgeRule/Revised-asmov-onto101-302.rdf";
		//String outputPath="Results/Interactive/guaranteedImpact/Revised-asmov-onto101-302.rdf";
		//String outputPath="Results/Interactive/normImpact/Revised-asmov-onto101-302.rdf";
		//String outputPath="Results/Interactive/graphImpact/Revised-asmov-onto101-302.rdf";
		//String outputPath="Results/Interactive/graphImpact2/Revised-asmov-onto101-302.rdf";
//		String outputPath="Results/Interactive/graphImpact3/Revised-asmov-onto101-302.rdf";
		String outputPath="Results/test.rdf";
		
				
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);
		if(!referencePath.equals(""))
		{
			aml.openReferenceAlignment(referencePath);
//			for(Mapping s:aml.getReferenceAlignment())
//			{
//				System.out.println(s.toString());
//			}
//			System.out.println("=================");
			//aml.simpleEvaluate();		
		}
		if(!alignPath.equals(""))
		{
			aml.openAlignment(alignPath);
			for(Mapping s:aml.getAlignment())
			{
				//System.out.println(s.toString());
				System.out.println(s.getSourceURI()+"  "+s.getTargetURI());
			}
			System.out.println("=================");
			aml.repairByGraph();  //����logical incoherent ���޸�
		}
		//��֤path�ĳ���
		//aml.getPath();		
		if(!referencePath.equals(""))
		{
			//aml.openReferenceAlignment(referencePath);
			aml.simpleEvaluate();		
		}
		
		System.out.println("The whole repair by our method is " + (System.currentTimeMillis()/1000-time) + " seconds");
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
			
	}
}