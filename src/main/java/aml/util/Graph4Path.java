package aml.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Graph4Path extends Graph{  
	
	//public Set<Integer> nodes; //存放点的集合  
	//public HashMap<Integer,Set<Integer>> parentMap;
	
	private List<ArrayList<Integer>> result;
	private Map<Integer, Boolean> states ; //记录结点状态的点
	
	
	public Graph4Path() 
	{
		result = new ArrayList<>(); 
		//Path = new ArrayList<>();
		states= new HashMap<Integer, Boolean>(); 
	}
	
	public void init(HashMap<Integer,Set<Integer>> Node2Parent) 
	{
		this.nodes=Node2Parent.keySet();
		this.parentMap = Node2Parent;
	}
	
	/*public void init2() 
	{
		this.nodes=nodes;
		this.parentMap = Node2Parent;
	}*/
	
	public List<ArrayList<Integer>> getPaths(Integer start, Integer end)
	{
		result.clear(); 
		//Path.clear();
	    Map<Integer, Boolean> states = new HashMap<Integer, Boolean>();
	    for (Integer i: nodes) 
	    {
			states.put(i, false);
		}    
	    ArrayList<Integer> path = new ArrayList<>();  
		DFS(start,end,path,states);	
		List<ArrayList<Integer>> Paths=new ArrayList<ArrayList<Integer>>();
		Paths.addAll(result);
		return  Paths;
	}
	
	
	private void DFS(Integer start,Integer end, ArrayList<Integer> path,Map<Integer, Boolean> states) 
	{   
        // 遍历相邻的结点  
		//System.out.println(start==end);
        if (start.intValue() == end.intValue()) 
        {
			// 存储该路径
        	path.add(start);
        	ArrayList<Integer> validatePath=new ArrayList<Integer>();
        	for(Integer node:path)
        		validatePath.add(node);
        	result.add(validatePath);
        	return;
		} 
        if (!states.containsKey(start))  
		{
			//path.remove(start);
			//return;
        	System.out.println("---------------");
        	System.out.println(start);
        	System.out.println(parentMap.keySet().contains(start));
        	System.out.println("---------------");
		} 	
        if(path.contains(start))
		{
			path.add(start); //其实是为了移除
			return;
		}
        else if (!states.containsKey(start)||states.get(start) == true)  
		{
			//path.remove(start);
			return;
		} 	
        else if (parentMap.get(start) == null||!parentMap.keySet().contains(start)) 
        {
        	//path.remove(start);
			return;
		}
		else 
		{
			path.add(start);
			states.put(start, true);		 
			for (Integer next : parentMap.get(start)) // 打印真实的路径
			{
				states.put(next, false);			
				DFS(next, end, path, states);
				//path.remove(next);			
				if(path.size()!=0)
					path.remove(path.size()-1);	
				/*if(path.size()!=0&&path.size()!=1)
				path.remove(path.size()-1);	*/
				states.put(next, true);
			}
		}       
    }
	
	public void printResult()
	{
		for(ArrayList<Integer> path:result)
		{
			StringBuilder sb = new StringBuilder();
			for (Integer i : path) {
				sb.append(i + "->");
			}
			sb.append("#");
			System.out.println(sb.toString().replace("->#", ""));
		}
	}	
	
	public static void main(String[] args) 
	{
		Set<Integer> a=new HashSet<>();
		a.add(1);
		a.add(2);
		a.add(3);
		a.add(4);
		//a.add(4);
		
		Set<Integer> b=new HashSet<>();
		b.add(0);
		b.add(2);
		b.add(3);
		b.add(4);
		
		//Set<Integer> c=new HashSet<>();
		Set<Integer> c=new HashSet<>();
		c.add(0);
		c.add(1);
		c.add(3);
		c.add(4);
		
		Set<Integer> d=new HashSet<>();
		d.add(0);
		d.add(1);
		d.add(2);
		d.add(4);
		
		Set<Integer> e=new HashSet<>();
		e.add(0);
		e.add(1);
		e.add(2);
		e.add(3);
		//d.add(2);
		//d.add(4);
		
		/*Set<Integer> e=new HashSet<>();
		e.add(2);*/
		
		HashMap<Integer,Set<Integer>> parentMap =new HashMap<Integer,Set<Integer>>();
		parentMap.put(0, a);
		parentMap.put(1, b);
		parentMap.put(2, c);
		parentMap.put(3, d);
		parentMap.put(4, e);
		//parentMap.put(3, d);
		//parentMap.put(4, e);
		
		Graph4Path g = new Graph4Path();
		g.init(parentMap);
		g.getPaths(1, 4);	
		g.printResult();
		
	}
}
