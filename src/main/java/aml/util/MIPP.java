package aml.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aml.filter.Path;

public class MIPP {
	int unsatConcept=-1;
	List<Integer> sourceConcept;
	List<Integer> targetConcept;
	//List<Integer> Path1;
	//List<Integer> Path2;
	Path mapping1;
	Path mapping2;	
	ArrayList<String> sourceRel;
	ArrayList<String> targetRel;
	HashMap<Path, Set<Integer>> commonEntailment;
	//HashMap<Integer, Set<Integer>> commonEntailment;
	
	public MIPP()
	{
		sourceConcept=new ArrayList<Integer>() ;
		targetConcept=new ArrayList<Integer>() ;
		//Path1=new ArrayList<Integer>();
		//Path2=new ArrayList<Integer>();
		mapping1 =new Path();
		mapping2 =new Path();		
		sourceRel=new ArrayList<String> ();
		targetRel=new ArrayList<String> ();
		commonEntailment=new HashMap<Path, Set<Integer>>();
	}
	
	
	/*public void init(List<Integer> C1,List<Integer>C2, List<Integer> p1, List<Integer> p2, Path m1,Path m2, Map<Integer, Set<Integer>>
	sRel, Map<Integer, Set<Integer>> tRel, HashMap<Path, Set<Integer>> comEntailment)*/
	public void init(int concept,List<Integer> C1,List<Integer>C2, Path m1,Path m2, ArrayList<String> sRel, ArrayList<String> tRel, HashMap<Path, Set<Integer>> comEntailment)
	{
		unsatConcept=concept;
		sourceConcept =C1;
		targetConcept =C2;
		//Path1=p1;
		//Path2=p2;
		mapping1=m1;
		mapping2=m2;
		sourceRel=sRel;
		targetRel=tRel;
		commonEntailment=comEntailment;
	}
	
	public List<Integer> getSourceConcept()
	{
		return sourceConcept;
	}
	
	public List<Integer> getTargetConcept()
	{
		return targetConcept;
	}
	
	/*public List<Integer> getPath1()
	{
		return Path1;
	}*/
	
	/*public List<Integer> getPath2()
	{
		return Path2;
	}*/
	
	public Path getPath1Mapping()
	{
		return mapping1;
	}
	
	public Path getPath2Mapping()
	{
		return mapping2;
	}
	
	public Set<Integer> getMappings()
	{
		Set<Integer> mapping=new HashSet<Integer> ();
		for(Integer t:mapping1)
			mapping.add(t);
		for(Integer t:mapping2)
			mapping.add(t);
		return mapping;
	}
	
	public ArrayList<String> getSourceRel()
	{
		return sourceRel;
	}
	
	public ArrayList<String> getTargetRel()
	{
		return targetRel;
	}
	
	public HashMap<Path, Set<Integer>> getCommonEntailment()
	{
		return commonEntailment;
	}
	
	public void print()
	{
		System.out.println("***************************************");
		System.out.println("The unsatisfied concept is "+ unsatConcept);
		/*System.out.println("The first path is:");
		StringBuilder sb = new StringBuilder();
		for (Integer i : Path1) {
			sb.append(i + "->");
		}
		sb.append("#");
		System.out.println(sb.toString().replace("->#", ""));
		
		sb.setLength(0);
		System.out.println("The second path is:");
		for (Integer i : Path2) {
			sb.append(i + "->");
		}
		sb.append("#");
		System.out.println(sb.toString().replace("->#", ""));*/
	
		System.out.println("The mappings is :");
		for(Integer t : mapping1)
			System.out.print(t+"   ");
		System.out.println();
		for(Integer t : mapping2)
			System.out.print(t+"   ");
		System.out.println();
		
		System.out.println("The source concepts are :");
		for(Integer t : sourceConcept)
			System.out.print(t+"   ");
		System.out.println();
		
		System.out.println("The target concepts are :");
		for(Integer t : targetConcept)
			System.out.print(t+"   ");
		System.out.println();
		
		if(commonEntailment.isEmpty())
			System.out.println("The common entailments among mappings is nothing!");
		else
		{
			System.out.println("The common entailments among mappings are :");
			for (Path t : commonEntailment.keySet())
				System.out.print(t + "   " + commonEntailment.get(t));
			System.out.println();
		}
		System.out.println("***************************************");
	}
}
