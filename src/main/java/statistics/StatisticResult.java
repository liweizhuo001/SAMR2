package statistics;


import java.io.IOException;
import java.util.ArrayList;

import statistics.EvaluationLargeBio;
import statistics.MappingInfo;


public class StatisticResult {
	public static void main(String args[]) throws IOException
	{		
		/*String mappingsPath = "alignment4Experiment/GMap-cmt-conference.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-cmt-conference.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-cmt-conference.rdf";
		String referencePath = "testdata/conference/references/cmt-conference.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-cmt-confof.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-cmt-confof.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-cmt-confof.rdf";
		String referencePath = "testdata/conference/references/cmt-confof.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-cmt-edas.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-cmt-edas.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-cmt-edas.rdf";
		String referencePath = "testdata/conference/references/cmt-edas.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-cmt-ekaw.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-cmt-ekaw.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-cmt-ekaw.rdf";
		String referencePath = "testdata/conference/references/cmt-ekaw.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-cmt-iasted.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-cmt-iasted.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-cmt-iasted.rdf";
		String referencePath = "testdata/conference/references/cmt-iasted.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-cmt-sigkdd.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-cmt-sigkdd.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-cmt-sigkdd.rdf";
		String referencePath = "testdata/conference/references/cmt-sigkdd.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-conference-confof.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-conference-confof.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-conference-confof.rdf";
		String referencePath = "testdata/conference/references/conference-confof.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-conference-edas.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-conference-edas.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-conference-edas.rdf";
		String referencePath = "testdata/conference/references/conference-edas.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-conference-ekaw.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-conference-ekaw.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-conference-ekaw.rdf";
		String referencePath = "testdata/conference/references/conference-ekaw.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-conference-iasted.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-conference-iasted.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-conference-iasted.rdf";
		String referencePath = "testdata/conference/references/conference-iasted.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-conference-sigkdd.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-conference-sigkdd.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-conference-sigkdd.rdf";
		String referencePath = "testdata/conference/references/conference-sigkdd.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-confof-edas.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-confof-edas.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-confof-edas.rdf";
		String referencePath = "testdata/conference/references/confof-edas.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-confof-ekaw.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-confof-ekaw.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-confof-ekaw.rdf";
		String referencePath = "testdata/conference/references/confof-ekaw.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-confof-iasted.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-confof-iasted.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-confof-iasted.rdf";
		String referencePath = "testdata/conference/references/confof-iasted.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-confof-sigkdd.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-confof-sigkdd.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-confof-sigkdd.rdf";
		String referencePath = "testdata/conference/references/confof-sigkdd.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-edas-ekaw.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-edas-ekaw.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-edas-ekaw.rdf";
		String referencePath = "testdata/conference/references/edas-ekaw.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-edas-iasted.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-edas-iasted.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-edas-iasted.rdf";
		String referencePath = "testdata/conference/references/edas-iasted.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-edas-sigkdd.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-edas-sigkdd.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-edas-sigkdd.rdf";
		String referencePath = "testdata/conference/references/edas-sigkdd.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-ekaw-iasted.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-ekaw-iasted.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-ekaw-iasted.rdf";
		String referencePath = "testdata/conference/references/ekaw-iasted.rdf";*/
		
		/*String mappingsPath = "alignment4Experiment/GMap-ekaw-sigkdd.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-ekaw-sigkdd.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-ekaw-sigkdd.rdf";
		String referencePath = "testdata/conference/references/ekaw-sigkdd.rdf";*/
		
		String mappingsPath = "alignment4Experiment/GMap-iasted-sigkdd.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-iasted-sigkdd.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-iasted-sigkdd.rdf";
		String referencePath = "testdata/conference/references/iasted-sigkdd.rdf";
		
		/*String mappingsPath = "alignment4Experiment/GMap-mouse-human.rdf";
		//String repairMappingsPath = "Results/PDLMV-GMap-mouse-human.rdf";
		String repairMappingsPath = "Results/OptimalImpactor_Complete/GMap-mouse-human.rdf";
		String referencePath = "testdata/anatomy/reference_2015.rdf";*/
		
		//原始的匹配
		MappingInfo MappingInformation=new MappingInfo(mappingsPath);	
		ArrayList<String> mappings= new ArrayList<String>();
		mappings=MappingInformation.getMappings();
		
		//修复的匹配
		MappingInfo RepairInformation=new MappingInfo(repairMappingsPath);	
		ArrayList<String> Repairedmappings= new ArrayList<String>();
		Repairedmappings=RepairInformation.getMappings();
		
		//参考的匹配
		ArrayList<String> referenceMappings= new ArrayList<String>();
		MappingInfo ReferenceInformation=new MappingInfo(referencePath);
		referenceMappings=ReferenceInformation.getMappings();
		
		System.out.println("--------------------------------------------------------");
		System.out.println("mapping reduced from " + mappings.size() + " to " + Repairedmappings.size() + " correspondences");
		
		ArrayList<String> removedMappings =new ArrayList<String>();
		removedMappings.addAll(mappings);
		removedMappings.removeAll(Repairedmappings);
		System.out.println("Removed the following "+removedMappings.size()+" correspondences:" );
		if(removedMappings.isEmpty())
			System.out.println("No mappings will be removed!");
		for(String s:removedMappings)
		{
			System.out.println(s);
		}
		System.out.println("--------------------------------------------------------");
		Evaluation cBefore = new Evaluation(mappings, referenceMappings);
		Evaluation cAfter = new Evaluation(Repairedmappings, referenceMappings);
		
		System.out.println("--------------------------------------------------------");
		System.out.println("before debugging (pre, rec, f): " + cBefore.toShortDesc());
		System.out.println("The number of total wrong mappings in alignment:  " + (cBefore.getMatcherAlignment()-cBefore.getCorrectAlignment()));
		System.out.println("after debugging (pre, rec, f):  " + cAfter.toShortDesc());
		System.out.println("The number of wrong removed mappings:  " + (cBefore.getCorrectAlignment()-cAfter.getCorrectAlignment()));
	
	}
}
