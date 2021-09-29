package aml.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KG {
	List<Integer> sourceConcept;
	List<Integer> targetConcept;
	Set<Integer> mappings;	
	ArrayList<String> sourceRel;
	ArrayList<String> targetRel;

	
	public KG()
	{
		sourceConcept=new ArrayList<Integer>() ;
		targetConcept=new ArrayList<Integer>() ;
		mappings =new HashSet<Integer>();		
		sourceRel=new ArrayList<String> ();
		targetRel=new ArrayList<String> ();
	}
	
	public void init(List<Integer> C1,List<Integer>C2, Set<Integer> m,ArrayList<String> sRel, ArrayList<String> tRel)
	{
		sourceConcept =C1;
		targetConcept =C2;
		mappings=m;
		sourceRel=sRel;
		targetRel=tRel;
	}
	
	public List<Integer> getSourceConcept()
	{
		return sourceConcept;
	}
	
	public List<Integer> getTargetConcept()
	{
		return targetConcept;
	}
	
	public Set<Integer> getPathMapping()
	{
		return mappings;
	}
	
	public Set<Integer> getMappings()
	{
		Set<Integer> mapping=new HashSet<Integer> ();
		for(Integer t:mappings)
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
}
