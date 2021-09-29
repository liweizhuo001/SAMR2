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

public class RepairingByGraphcmd
{

//Main Method
	
	public static void main(String[] args) throws Exception
	{
		
		long time = System.currentTimeMillis()/1000;
		
		String sourcePath  = "testdata/"+args[0];
		String targetPath  = "testdata/"+args[1];
		String alignPath = "alignment4Experiment/"+args[2];
		String referencePath   = "testdata/"+args[3];
		String outputPath="Results/"+args[4];	
		
	
		//HMatch
		/*String sourcePath  = "testdata/conference/ontologies/cmt.owl";
		String targetPath  = "testdata/conference/ontologies/Conference.owl";
		String alignPath = "alignment4Experiment/HMatch-cmt-conference.rdf";
		String referencePath   = "testdata/conference/references/cmt-conference.rdf";
		//String outputPath="Results/One2One/HMatch-cmt-conference.rdf";
		//String outputPath="Results/ChristianTool_new/HMatch-cmt-conference.rdf";
		//String outputPath="Results/StructureImpactor_Complete/HMatch-cmt-conference.rdf";
		String outputPath="Results/OptimalImpactor_Complete/HMatch-cmt-conference.rdf";*/
		
			
	
				
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);
		//aml.matchAuto();  //自动做匹配
		//aml.getAlignment();
		if(!alignPath.equals(""))
		{
			aml.openAlignment(alignPath);
			//aml.repair();  //基于logical incoherent 的修复
			aml.repairByGraph();  //基于logical incoherent 的修复
		}
		//验证path的长度
		//aml.getPath();		
		if(!referencePath.equals(""))
		{
			aml.openReferenceAlignment(referencePath);
			//aml.evaluate();
			//aml.evaluate2();
			aml.simpleEvaluate();
			//System.out.println(aml.getEvaluation());			
		}
		
		System.out.println("The whole repair by our method is " + (System.currentTimeMillis()/1000-time) + " seconds");
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
			
	}
}