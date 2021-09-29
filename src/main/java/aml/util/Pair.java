package aml.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import aml.filter.Path;
import aml.match.Alignment;
import aml.ontology.RelationshipMap;

public class Pair<X, Y> extends Object{

    public final ArrayList<Integer> left;
    public final ArrayList<Integer> right;
    //Set<Integer> mappings;	
    //HashMap<Path, Set<Integer>> commonEntailment;
    HashMap<Integer, Integer> commonEntailment;

    public Pair(ArrayList<Integer> left, ArrayList<Integer> right) {
        this.left = left;
        this.right = right;
        //mappings=new HashSet<Integer>();
        commonEntailment =new HashMap<Integer, Integer>();
    }
    

    @Override
    public String toString() {
        return "(" + left + "," + right + ")";
    }
    
   /* public Set<Integer> getMappings()
    {
    	return mappings;
    }*/
    
    public HashMap<Integer, Integer> getCommonEntailment()
    {
    	return commonEntailment;
    }
     
    public void expand(Alignment a,RelationshipMap rels) 
    {
    	//记录每个冲突的末节点
    	HashMap<Integer, Integer> conflictTailIndex1=new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> conflictTailIndex2=new HashMap<Integer, Integer>();
		for(int i=0;i<left.size()-1;i++)
		{
			int index=-1;
			int node1=left.get(i);
			int node2=left.get(i+1);		
			index=a.getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				conflictTailIndex1.put(index,node2);
			}
		}
		
		for(int i=0;i<right.size()-1;i++)
		{
			int index=-1;
			int node1=right.get(i);
			int node2=right.get(i+1);		
			index=a.getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				conflictTailIndex2.put(index,node2);
			}
		}
		
		for(Integer mapping1:conflictTailIndex1.keySet())
		{
			int tail1=conflictTailIndex1.get(mapping1);
			Set<Integer> ancestor1=rels.getSuperClasses(tail1,false);
			//Set<Integer> ancestor1=rels.getAncestors(tail1, 3);
			for(Integer mapping2:conflictTailIndex2.keySet())
			{
				int tail2=conflictTailIndex2.get(mapping2);
				Set<Integer> ancestor2=rels.getSuperClasses(tail2,false);
				//Set<Integer> ancestor2=rels.getAncestors(tail2,3);
				ancestor2.retainAll(ancestor1);
				if(!ancestor2.isEmpty())
				{
					commonEntailment.put(mapping1, ancestor2.size());
					commonEntailment.put(mapping2, ancestor2.size());
				}			
			}
		}
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof Pair)) {
            return false;
        }
        Pair<X, Y> other_ = (Pair<X, Y>) other;
        return Objects.equals(other_.left, this.left) && Objects.equals(other_.right, this.right);
    }

    @Override
    public int hashCode() {
        final int prime = 13;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

}
