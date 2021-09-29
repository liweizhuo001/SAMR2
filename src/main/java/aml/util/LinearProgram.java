package aml.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.LPOptimizationRequest;
import com.joptimizer.optimizers.LPPrimalDualMethod;

public class LinearProgram {
	LPOptimizationRequest or = new LPOptimizationRequest();
	static double delta=0.5;  //threshold for equality mappings

	ArrayList<double[]> inequalities=new ArrayList<double[]>();
	ArrayList<double[]> equalities=new ArrayList<double[]>();
	//HashMap<String,String> worlds=new HashMap<String,String>();
	//HashMap<String,String> possibleWordIndex=new HashMap<String,String>();
	ArrayList<String> initialMappings=new ArrayList<String>();
	ArrayList<String> hiddenMappings=new ArrayList<String>();
	ArrayList<ArrayList<String>> minmalConflictSets=new ArrayList<ArrayList<String>>();
	ArrayList<String> minmalConflictMappingSets=new ArrayList<String>();
	HashMap<String,ArrayList<ArrayList<String>>> mapConflict=new HashMap<String,ArrayList<ArrayList<String>>>() ;
	HashMap<String,ArrayList<Double>> mapValues=new HashMap<String,ArrayList<Double>> () ; 
	//HashMap<ArrayList<String>,HashMap<String,Double>> ConflictSetChangeValues=new HashMap<ArrayList<String>,HashMap<String,Double>>(); 
	HashMap<ArrayList<String>,HashMap<String,Double[]>> ConflictSetChangeInterval=new HashMap<ArrayList<String>,HashMap<String,Double[]>>(); 
	//前面对应的每个mappings对应冲突子集的改变的大小，倒数第二列是最大的改变值，最后一列是基础的值
	
	int numWorld=0;
	int conceptNum=0;
	ArrayList<Integer> axiomsConstraint=new ArrayList<Integer>();
	HashMap<Integer, Integer> conceptIndex=new HashMap<Integer, Integer>();
	HashMap<Integer,Integer> indexConcept=new HashMap<Integer, Integer>();
	ArrayList<Integer[]> constraints=new ArrayList<Integer[]>();
	ArrayList<Boolean[]> possibleWord=new ArrayList<Boolean[]>();
	HashMap<Integer, String> possibleWordIndex=new HashMap<Integer, String>();
	HashMap<ArrayList<String>, Boolean> conflictDecision=new HashMap<ArrayList<String>, Boolean>();
	ArrayList<ArrayList<String>> maximumSatisfiableSets=new ArrayList<ArrayList<String>>();
	
	String revisedMapping="";
	
	double[]lb;
	double[]ub;
	double[] min_c  ;
	double[] max_c ;
	double[] optimizeObject;
	
	double[][]A;
	double[] b; 
	double[][] G ;
	double[] h ;
	
	
	public void Intial(ArrayList<Integer> concepts) 
	{
		conceptNum=concepts.size();
		for(int i=0;i<conceptNum;i++)
		{
			conceptIndex.put(concepts.get(i), i);
			indexConcept.put(i, concepts.get(i));
		}
	}
	
	public String getRevisedMapping() 
	{			
		return 	revisedMapping;		
	}
	
	public void mergeConcept(ArrayList<Integer> concepts1,ArrayList<Integer> concepts2) 
	{
		conceptNum=concepts1.size()+concepts2.size();
		for(int i=0;i<concepts1.size();i++)
		{
				conceptIndex.put(concepts1.get(i), i);
				indexConcept.put(i, concepts1.get(i));
		}	
		for(int j=0;j<concepts2.size();j++)
		{
				conceptIndex.put(concepts2.get(j), j);
				indexConcept.put(j, concepts2.get(j));	
		}
	}
	
	public void mergePossbileWorldIndex(HashMap<Integer, String> PossbileWorldIndex1,HashMap<Integer, String> PossbileWorldIndex2) 
	{
		int num=0;
		//将两个本体的可能世界进行融合（作笛卡尔乘积）
		for(int i:PossbileWorldIndex1.keySet())
		{
			String conceptString1=PossbileWorldIndex1.get(i);
			for(int j:PossbileWorldIndex2.keySet())
			{
				String mergString=conceptString1+PossbileWorldIndex2.get(j);		
				possibleWordIndex.put(num, mergString);
				num++;
			}
		}		
		System.out.println("The number of possible worlds after merged："+possibleWordIndex.size());
		numWorld=possibleWordIndex.size();
		lb=new double[numWorld];
		ub=new double[numWorld];
		min_c = new double[numWorld] ;
		max_c = new double[numWorld] ;
		optimizeObject=new double[numWorld];
	}
	
	
	
	public void encodingConstraint(ArrayList<String> axioms)  //将不合法的情况全部都解析出来
	{
		for (int i=0;i<axioms.size();i++)
		{
			String parts[]=axioms.get(i).split(",");
			if(parts[2].equals("<="))
			{
				Integer[] restrction=new Integer[4];
				restrction[0] = conceptIndex.get(parts[0]);
				if (parts[1].contains("-")) // 不相交的情况
				{
					restrction[2] = 1;
					restrction[3] = 1;
					restrction[1] = conceptIndex.get(parts[1].replace("-", ""));
				} else 
				{
					restrction[1] = conceptIndex.get(parts[1]);
					restrction[2] = 1;
					restrction[3] = 0;
				}
				constraints.add(restrction);
			}
			else if(parts[2].equals("=="))
			{
				Integer[] restrction1=new Integer[4];
				restrction1[0]=conceptIndex.get(parts[0]);
				restrction1[1]=conceptIndex.get(parts[1]);
				restrction1[2] = 1;
				restrction1[3] = 0;
				Integer[] restrction2=new Integer[4];
				restrction2[0]=conceptIndex.get(parts[0]);
				restrction2[1]=conceptIndex.get(parts[1]);
				restrction2[2] = 0;
				restrction2[3] = 1;		
				constraints.add(restrction1);
				constraints.add(restrction2);
			}
		}
	}
	
	public void encodingConstraint(Map<Integer, Set<Integer>> subRelations,Map<Integer, Set<Integer>> disRelations)  //将不合法的情况全部都解析出来
	{
		for(Integer child: subRelations.keySet())
		{
			for(Integer father: subRelations.get(child))
			{
				Integer[] restrction=new Integer[4];
				restrction[0] = conceptIndex.get(child);
				restrction[1] = conceptIndex.get(father);
				restrction[2] = 1;
				restrction[3] = 0;
				if(!constraints.contains(restrction))
					constraints.add(restrction);
			}			
		}
		
		for(Integer con: disRelations.keySet())
		{
			for(Integer dis: disRelations.get(con))
			{
				Integer[] restrction=new Integer[4];
				restrction[0] = conceptIndex.get(con);
				restrction[1] = conceptIndex.get(dis);
				restrction[2] = 1;
				restrction[3] = 1;
				if(!constraints.contains(restrction))
					constraints.add(restrction);
			}			
		}
	}
	
	public void generatePossbileWorld()
	{	
		Boolean[] initialCode=new Boolean[conceptNum];
		for(int k=0;k<conceptNum;k++)  //必须要初始化一次
		{
			initialCode[k]=false;
		}
		long number=(long) Math.pow(2, conceptNum);  //必须要遍历一遍，以此仍存在上限
		System.out.println("The number of worlds："+number);
		for (int i=0;i<number;i++)
		{
			String a=Integer.toBinaryString(i);  //转换成2进制
			Boolean[] initialCodecopy=initialCode.clone();
			for(int M=conceptNum-1,L=a.length()-1;L>=0;M--,L--)  //有问题,应该从后往前遍历
			{
				if(a.charAt(L)=='1')
					initialCodecopy[M]=true;
			}
			if(Validate(initialCodecopy))
				possibleWord.add(initialCodecopy);
		}
		//System.out.println("The number of possible worlds："+possibleWord.size());
		numWorld=possibleWord.size();
		lb=new double[numWorld];
		ub=new double[numWorld];
		min_c = new double[numWorld] ;
		max_c = new double[numWorld] ;
		optimizeObject=new double[numWorld];
		/*for(Boolean[] a:possibleWord)
		{
			for(int i=0;i<a.length;i++)
			{
				System.out.print(a[i]+" ");
			}
			System.out.println();
		}*/
		
	}
	
	public void generatePossbileWorldIndex()
	{
		for(int i=0;i<possibleWord.size();i++)
		{
			//String conceptString="";
			StringBuilder conceptString=new StringBuilder();
			for(int j=0;j<possibleWord.get(i).length;j++)
			{
				if(possibleWord.get(i)[j]==true)
				{
					//conceptString=conceptString+"+"+"#"+indexConcept.get(j)+"#";
					conceptString.append("+");
					conceptString.append("#");
					conceptString.append(indexConcept.get(j));
					conceptString.append("#");
				}
				else 
				{
					//conceptString=conceptString+"-"+"#"+indexConcept.get(j)+"#";
					conceptString.append("-");
					conceptString.append("#");			
					conceptString.append(indexConcept.get(j));
					conceptString.append("#");
				}
			}
			//possibleWordIndex.put(i, conceptString);
			possibleWordIndex.put(i, conceptString.toString());
		}
		/*for(int i:possibleWordIndex.keySet())
		{
			System.out.println(i+" :"+possibleWordIndex.get(i));
		}*/
		System.out.println("The number of possible worlds："+possibleWordIndex.size());
		System.out.println("+++++++++++++++++++++");
	}

	public boolean  Validate(Boolean[] code)
	{
		for (int i=0;i<constraints.size();i++)
		{
			Integer[] parts=constraints.get(i);
			int headIndex=parts[0];
			int bodyIndex=parts[1];
			if(code[headIndex]==true&&code[bodyIndex]==false&&parts[2]==1&&parts[3]==0)  //包含
					return false;
			if(code[headIndex]==true&&code[bodyIndex]==true&&parts[2]==1&&parts[3]==1)  //不相交
					return false;		
		}
		return true;
	}
	
	public boolean  Validate2(Boolean[] code)
	{
/*		Integer[] disjoint=new Integer[4];
		Integer[] contain=new Integer[4];
		Integer[] equivalent=new Integer[4];*/
				
		for (int i=0;i<constraints.size();i++)
		{
				Integer[] parts=constraints.get(i);
				int headIndex=parts[0];
				int bodyIndex=	parts[1];
			if(code[headIndex]==true&&code[bodyIndex]==false)  //包含
					return false;	
			if(code[headIndex]==true&&code[bodyIndex]==true)  //不相交
				return false;		
			//这个已经包含在第二种情况中了
			/*if(code[headIndex]==true&&code[bodyIndex]==false&&parts[2]==1&&parts[3]==0)  
				return false;*/
			if(code[headIndex]==false&&code[bodyIndex]==true)  //等价
				return false;		
		}
		return true;
	}
	
	public void addhardConstraints(String []conditional_contraints,String []interval_probability)
	{
		possibleWord.size();
		conceptIndex.size();
		for(int i=0;i<conditional_contraints.length;i++)
		{
			String cc[]=conditional_contraints[i].split("\\|");
			String head=cc[0];
			String body=cc[1];
			String bound[]=interval_probability[i].split(",");
			double lower=Double.parseDouble(bound[0]);                                       
			double upper=Double.parseDouble(bound[1]);
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];	
			
			if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
			{
				for(int s:possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
					{
						coefficient1[s]=lower;
					}
					else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
					{
						coefficient1[s]=-(1-lower);
						BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}			
				}
				//将不等式用等式的方式保存起来
				equalities.add(coefficient1);
				//equalities_value.add(lower);
			}
			else   //非点概率区间的情况
			{
				if (lower > 0) // 按照约束1来生成公式
				{
					for (int s: possibleWordIndex.keySet()) {
						if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
						{
							coefficient1[s] = lower;
						} 
						else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
						{
							coefficient1[s] =  - (1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}
					}
					inequalities.add(coefficient1);
				}
				if (upper < 1) {
					for (int s : possibleWordIndex.keySet()) {
						if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")
								&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
							coefficient2[s] = -upper;
						} else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")
								&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
							coefficient2[s] = 1 - upper;
							BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
							coefficient2[s] =  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}
					}
					inequalities.add(coefficient2);
				}
			}
		}
	}
	
	
	public void addhardConstraints(ArrayList<String>conditionalContraints)
	{
		possibleWord.size();
		conceptIndex.size();
		for(int i=0;i<conditionalContraints.size();i++)
		{
			/*String cc[]=conditionalContraints.get(i).split(",");
			String head=cc[0];
			String body=cc[1];
			//String bound[]=interval_probability[i].split(",");
			double lower=Double.parseDouble(cc[2]);                                       
			double upper=Double.parseDouble(cc[3]);
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];*/
			
			String cc[]=conditionalContraints.get(i).split(",");
			//String cc[]=conditionalContraints[i].split("\\|");
			String head=cc[0];
			String body=cc[1];
			//String bound[]=interval_probability[i].split(",");
			double lower=Double.parseDouble(cc[2]);                                       
			double upper=Double.parseDouble(cc[3]); 
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];	
			
			if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
			{
				for(int s:possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
					{
						coefficient1[s]=lower;
					}
					else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
					{
						coefficient1[s]=-(1-lower);
						BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}			
				}
				//将不等式用等式的方式保存起来
				equalities.add(coefficient1);
				//equalities_value.add(lower);
			}
			else   //非点概率区间的情况
			{
				if (lower > 0) // 按照约束1来生成公式
				{
					for (int s: possibleWordIndex.keySet()) {
						if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
						{
							coefficient1[s] = lower;
						} 
						else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
						{
							coefficient1[s] =  - (1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}
					}
					inequalities.add(coefficient1);
				}
				if (upper < 1) {
					for (int s : possibleWordIndex.keySet()) {
						if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")
								&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
							coefficient2[s] = -upper;
						} else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")
								&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
							coefficient2[s] = 1 - upper;
							BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
							coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}
					}
					inequalities.add(coefficient2);
				}
			}
		}
	}
	
/*	public void addhardConstraints(ArrayList<String>conditionalContraints)
	{
		for(int i=0;i<conditionalContraints.size();i++)
		{
			String cc[]=conditionalContraints.get(i).split(",");
			String head=cc[0];
			String body=cc[1];
			//String bound[]=interval_probability[i].split(",");
			double lower=Double.parseDouble(cc[2]);                                       
			double upper=Double.parseDouble(cc[3]);
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];
			if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
			{
				for(int s:possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
					{
						coefficient1[s]=lower;
					}
					else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
					{
						coefficient1[s]=-(1-lower);
						BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}			
				}
				//将不等式用等式的方式保存起来
				equalities.add(coefficient1);
				//equalities_value.add(lower);
			}
			else   //非点概率区间的情况
			{
				if (lower > 0) // 按照约束1来生成公式
				{
					for (int s : possibleWordIndex.keySet()) {
						if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
						{
							coefficient1[s] = lower;
						} 
						else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
						{
							coefficient1[s] =  - (1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}
					}
					inequalities.add(coefficient1);
				}
				if (upper < 1) {
					for (int s : possibleWordIndex.keySet()) {
						if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")
								&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
							coefficient2[s] = -upper;
						} else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")
								&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
							coefficient2[s] = 1 - upper;
							BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
							coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}
					}
					inequalities.add(coefficient2);
				}
			}
		}
	}*/
	
	public void addMappingConstraints(String mappings[])	
	{
		for(int i=0;i<mappings.length;i++)
		{	
			String parts[]=mappings[i].split(",");
			String head=parts[0];
			String body=parts[1];
			String relation=parts[2];
			Double confidence=Double.parseDouble(parts[3]);			
			double coefficient1[]=new double[numWorld];
			if(relation.equals("|") )
			{
				for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);
			}	
			else if(relation.equals("="))
			{
				for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					}
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);
			}	
		}
/*		G = new double[inequalities.size()][numWorld];
		h = new double[inequalities.size()];
		for(int i=0;i<inequalities.size();i++)
		{
			G[i]=inequalities.get(i);
		}		
		//等式的系数赋值，一般都是x0+...+xn=1
		A=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			A[i]=equalities.get(i);
		}		
		 b = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0

		b[equalities.size()]=1.0;*/
	}
	
	public void addMappingConstraints(ArrayList<String> mappings)	
	{
		for(int i=0;i<mappings.size();i++)
		{	
			String parts[]=mappings.get(i).split(",");
			/*String head=parts[0];
			String body=parts[1];*/
			//基于mapping要交换一下
			String body=parts[1];
			String head=parts[0];
			String relation=parts[2];
			Double lower=Double.parseDouble(parts[3]);			
			Double upper=Double.parseDouble(parts[4]);		
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];
			if(relation.equals("|") )
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalities.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient2);
					}
				}
				/*for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);*/
			}	
			else if(relation.equals("="))
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("-"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalities.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient1);
					}
					if (upper < 1) 
					{
						for (int s : possibleWordIndex.keySet()) 
						{
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient2);
					}
				}		
			/*	for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					}
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);*/
				
		}
		}
			
/*		G = new double[inequalities.size()][numWorld];
		h = new double[inequalities.size()];
		for(int i=0;i<inequalities.size();i++)
		{
			G[i]=inequalities.get(i);
		}		
		//等式的系数赋值，一般都是x0+...+xn=1
		A=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			A[i]=equalities.get(i);
		}		
		 b = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0

		b[equalities.size()]=1.0;*/
	}
	
	public void addRevisedMappingConstraints()	
	{
		for(int i=0;i<initialMappings.size();i++)
		{	
			String parts[]=initialMappings.get(i).split(",");
			/*String head=parts[0];
			String body=parts[1];*/
			//基于mapping要交换一下
			String body=parts[0];
			String head=parts[1];
			String relation=parts[2];
			Double lower=Double.parseDouble(parts[3]);			
			Double upper=Double.parseDouble(parts[4]);		
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];
			if(relation.equals("|") )
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalities.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient2);
					}
				}
				/*for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);*/
			}	
			else if(relation.equals("="))
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("-"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalities.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient1);
					}
					if (upper < 1) 
					{
						for (int s : possibleWordIndex.keySet()) 
						{
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient2);
					}
				}		
			/*	for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					}
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);*/
				
		}
			System.out.println();
		}
			
/*		G = new double[inequalities.size()][numWorld];
		h = new double[inequalities.size()];
		for(int i=0;i<inequalities.size();i++)
		{
			G[i]=inequalities.get(i);
		}		
		//等式的系数赋值，一般都是x0+...+xn=1
		A=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			A[i]=equalities.get(i);
		}		
		 b = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0

		b[equalities.size()]=1.0;*/
	}
	
	public void addRevisedMappingConstraints2()	
	{
		for(int i=0;i<initialMappings.size();i++)
		{	
			String parts[]=initialMappings.get(i).split(",");
			/*String head=parts[0];
			String body=parts[1];*/
			//基于mapping要交换一下
			String body=parts[0];
			String head=parts[1];
			String relation=parts[2];
			Double lower=Double.parseDouble(parts[3]);			
			Double upper=Double.parseDouble(parts[4]);		
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];
			if(relation.equals("|") )
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							//coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalities.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								//coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								//coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient2);
					}
				}
				/*for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);*/
			}	
			else if(relation.equals("="))
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("-"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							//coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalities.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								//coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient1);
					}
					if (upper < 1) 
					{
						for (int s : possibleWordIndex.keySet()) 
						{
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								//coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalities.add(coefficient2);
					}
				}		
			/*	for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient1[s] = confidence;
					}
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient1[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalities.add(coefficient1);*/
				
		}
		}
			
/*		G = new double[inequalities.size()][numWorld];
		h = new double[inequalities.size()];
		for(int i=0;i<inequalities.size();i++)
		{
			G[i]=inequalities.get(i);
		}		
		//等式的系数赋值，一般都是x0+...+xn=1
		A=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			A[i]=equalities.get(i);
		}		
		 b = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0

		b[equalities.size()]=1.0;*/
	}
	
	
	public void entailments (String []entailOjects)
	{
		//不等式的系数赋值
		G = new double[inequalities.size()][numWorld];
		h = new double[inequalities.size()];
		for(int i=0;i<inequalities.size();i++)
		{
			G[i]=inequalities.get(i);
		}		
		//等式的系数赋值，一般都是x0+...+xn=1
		A=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			A[i]=equalities.get(i);
		}		
		b = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0
		b[equalities.size()]=1.0;	
		boolean boundFlag=false; //默认求的值都是条件概率
		for(int i=0;i<entailOjects.length;i++)
		{	
			
			String entails[]=entailOjects[i].split(",");	
			if(entails[1].equals("T"))
			{
					for (int k=0;k<numWorld;k++)  //令所有可能世界求和为1 (只是可能世界)
					{
							A[equalities.size()][k]=1.0;
					}
					for (int k : possibleWordIndex.keySet()) 
					{
						if (possibleWordIndex.get(k).contains("+" + entails[0])|| (entails[0].contains("-") && possibleWordIndex.get(k).contains(entails[0]))) // 正数需要额外加上“+”
						{
							min_c[k] = 1.0;
							max_c[k] = -1.0;
						}
					}
					boundFlag=true;
			}
			else if(entails[2].equals("|"))
			{
					for (int k : possibleWordIndex.keySet()) 
					{
						if (possibleWordIndex.get(k).contains("+" + entails[0]))
						{
							A[equalities.size()][k]=1.0;
						}
						if (possibleWordIndex.get(k).contains("+" + entails[0])&&possibleWordIndex.get(k).contains("+" + entails[1])	)	
						{
							min_c[k] = 1.0;
							max_c[k] = -1.0;
						}
					}
			}
		    else if(entails[2].equals("="))
			{
					for (int k : possibleWordIndex.keySet()) 
					{
						//令entails[0]对应的可能世界
						if (possibleWordIndex.get(k).contains("+" + entails[0])||possibleWordIndex.get(k).contains("+" + entails[1]))
						{
							A[equalities.size()][k]=1.0;
						}
						if (possibleWordIndex.get(k).contains("+" + entails[0])&&possibleWordIndex.get(k).contains("+" + entails[1])) 				
						{
							min_c[k] = 1.0;
							max_c[k] = -1.0;
						}
					}
					
				}					
				optimizeObject=min_c.clone();	
		}	
		//针对具体的情况进行修改
		/*if(boundFlag==true)  //求(A|T)的情况
		{
			for (int i=0;i<numWorld;i++)
				ub[i]=1.0;
		}
		else //求(B|C)的情况
		{
			for(int j=0;j<A[0].length;j++)
			{
				if(A[0][j]!=0)
					ub[j]=Integer.MAX_VALUE; //保证大于0即可
			}
		}*/
		for(int i=0;i<G.length;i++)  //不等式系数不为0.那么可能世界的取值上界大于0即可.
		{
			for(int j=0;j<G[0].length;j++)
			{
				if(G[i][j]!=0)
					ub[j]=Integer.MAX_VALUE; //保证大于0即可
				else if(boundFlag==true)
					ub[j]=1.0;
			}
		}
		
		for(int i=0;i<A.length-1;i++)  //等式系数不为0,那么可能世界的取值上界大于0即可.
		{
			for(int j=0;j<A[0].length;j++)
			{
				if(A[i][j]!=0)
					ub[j]=Integer.MAX_VALUE; //保证大于0即可
				else if(boundFlag==true)
					ub[j]=1.0;
			}
		}
		boundFlag=false;
		
		for(int i =0;i<A[equalities.size()].length;i++)
		{	
			boolean flag=false;
			/*for (int s : possibleWordIndex.keySet()) 
			{
				if (s==i)   //属于可能世界的范畴,并且有限制
				{
					flag=true;
					//A[equalities.size()][i]=0.0;
					break;
				}
			}*/
			if(A[equalities.size()][i]==1.0)
				ub[i]=1.0;
			/*if(flag==false)  //不可能世界的等式系数应为0
			{
				ub[i]=0.0;
				A[equalities.size()][i]=0.0;
			}*/
		}
		
/*		for(int i=0;i<optimizeObject.length;i++)
		{
			 if(!possibleWordIndex.keySet().contains(i+""))
				 optimizeObject[i]=0.0;
		}	*/
		String new_object="";
		for(int k=0;k<optimizeObject.length;k++)
		{		
				if(optimizeObject[k]==1.0)
					new_object=new_object+"+i"+k;  //系数*可能世界的标签
		}
		PrintService();
		or.setC(optimizeObject);  //第二个bug因为处理
		or.setG(G);
		or.setH(h);	
		or.setA(A);
		or.setB(b);
		or.setLb(lb);
		or.setUb(ub);
		or.setDumpProblem(true); 
		
		LPPrimalDualMethod opt = new LPPrimalDualMethod();
		
		opt.setLPOptimizationRequest(or);
		try {
			opt.optimize();
			double[] sol_min = opt.getOptimizationResponse().getSolution();
			boolean min_flag = false;
			System.out.println("最小值的解为:");
			for (int i = 0; i < sol_min.length; i++) 
			{
				if (sol_min[i] != 0.0)
				{
					min_flag=true;
					break;
				}		
			}
			if(min_flag==false)  //防止解均为0，但仍然通过.不满足概率之和等于1的等式约束
			{
				System.out.println("This is a bug!");
				System.out.println("The problem of conditional constraints is unsolved because all of value is 0!");
				//System.exit(0);
				throw new RuntimeException("infeasible problem");
			}
			
			else if(min_flag==true)  //虽然给出了解，但是仍存在不满足条件的情况（主要是针对约简之后的部分不等式等价于等式的情况）
			{
				for(int i=0;i<G.length;i++)
				{
					double value=0;
					for(int j=0;j<G[0].length;j++)
					{
						value=sol_min[j]*G[i][j]+value;
					}
					if(value>0)
					{
						System.out.println("This is a bug!");
						System.out.println("The problem of conditional constraints is unsolved because it is not satisfied one of the inequvality constraints!");
					//System.exit(0);
						throw new RuntimeException("infeasible problem");
					}
				}
				
			}
			
			LinearMultivariateRealFunction objectiveFunction_min = new LinearMultivariateRealFunction(optimizeObject, 0);		
			System.out.println("最大下界为:"+objectiveFunction_min.value(sol_min));
			//替换目标函数，求最大值
			for(int i=0;i<optimizeObject.length;i++)
			{
				optimizeObject[i]=-optimizeObject[i];
			}
			//or.setC(max_c);
			or.setC(optimizeObject);
			opt.setLPOptimizationRequest(or);
			opt.optimize();
			double[] sol_max = opt.getOptimizationResponse().getSolution();
			System.out.println("最大值的解为:");
			boolean max_flag = false;
			for (int i = 0; i < sol_max.length; i++) 
			{
				if (sol_max[i] != 0.0)
				{
					max_flag=true;
					break;
				}		
			}
			if(max_flag==false)  //防止解均为0，但仍然通过
			{
				System.out.println("This is a bug!");
				System.out.println("The problem of conditional constraints is unsolved because all of value is 0!");
				//System.exit(0);
				throw new RuntimeException("infeasible problem");
			}		
			else if(max_flag==true)  //虽然给出了解，但是仍存在不满足条件的情况（主要是针对约简之后的部分不等式等价于等式的情况）
			{
				for(int i=0;i<G.length;i++)
				{
					double value=0;
					for(int j=0;j<G[0].length;j++)
					{
						value=sol_max[j]*G[i][j]+value;
					}
					if(value>0)
					{
						System.out.println("This is a bug!");
						System.out.println("The problem of conditional constraints is unsolved because it is not satisfied one of the inequvality constraints!");
					//System.exit(0);
						throw new RuntimeException("infeasible problem");
					}
				}			
			}		
		//	LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(new_objectfunction, 0);	
			LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(max_c, 0);	
			System.out.println("最小上界为:"+-objectiveFunction_max.value(sol_max));
		} catch (Exception e) {
			// TODO: handle exception
			if(e.getMessage().equals("Infeasible problem")); //捕获异常内容
			System.out.println("infedbdfbfgb");
		}	
	}
	
	public void tightEntailments (ArrayList<String>entailOjects)
	{
		for(int n=0;n<entailOjects.size();n++)
		{
			double lowerBounder=0;
			double upperBounder=0;
			double TempG[][] = new double[inequalities.size()][numWorld];
			double Temph[] = new double[inequalities.size()];  //默认是等于0的
			double Tempmin_c[] = new double[numWorld] ;
			double Tempmax_c[] = new double[numWorld] ;
			for(int i=0;i<inequalities.size();i++)
			{
				TempG[i]=inequalities.get(i);
			}		
			//等式的系数赋值，一般都是x0+...+xn=1
			double TempA[][]=new double[1+equalities.size()][numWorld];
			for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
			{
				TempA[i]=equalities.get(i);
			}		
			double Tempb[] = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0
			Tempb[equalities.size()]=1.0;	
			boolean boundFlag=false; //默认求的值都是条件概率
			String entails[]=entailOjects.get(n).split(",");	
			if(entails[1].equals("T"))
			{
					for (int k=0;k<numWorld;k++)  //令所有可能世界求和为1 (只是可能世界)
					{
							TempA[equalities.size()][k]=1.0;
					}
					for (int k : possibleWordIndex.keySet()) 
					{
						if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#")|| (entails[0].contains("-") && possibleWordIndex.get(k).contains("#"+entails[0]+"#"))) // 正数需要额外加上“+”
						{
							Tempmin_c[k] = 1.0;
							Tempmax_c[k] = -1.0;
						}
					}
					boundFlag=true;
			}
			else if(entails[2].equals("|"))
			{
					for (int k : possibleWordIndex.keySet()) 
					{
						if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#"))
						{
							TempA[equalities.size()][k]=1.0;
						}
						if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#")&&possibleWordIndex.get(k).contains("+" +"#"+ entails[1]+"#")	)	
						{
							Tempmin_c[k] = 1.0;
							Tempmax_c[k] = -1.0;
						}
					}
			}
		    else if(entails[2].equals("="))
			{
					for (int k : possibleWordIndex.keySet()) 
					{
						//令entails[0]对应的可能世界
						if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#")||possibleWordIndex.get(k).contains("+" +"#"+ entails[1]+"#"))
						{
							TempA[equalities.size()][k]=1.0;
						}
						if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#")&&possibleWordIndex.get(k).contains("+" +"#"+ entails[1]+"#")) 				
						{
							Tempmin_c[k] = 1.0;
							Tempmax_c[k] = -1.0;
						}
					}
					
			}					
		double TempoptimizeObject[]=Tempmin_c.clone();	
		double Templb[]= new double[numWorld] ;
		double Tempub[]= new double[numWorld] ;
			
		for(int i=0;i<TempG.length;i++)  //不等式系数不为0.那么可能世界的取值上界大于0即可.
		{
			for(int j=0;j<TempG[0].length;j++)
			{
				if(TempG[i][j]!=0)
					Tempub[j]=Integer.MAX_VALUE; //保证大于0即可
				else if(boundFlag==true)
					Tempub[j]=1.0;
			}
		}
		
		for(int i=0;i<TempA.length-1;i++)  //等式系数不为0,那么可能世界的取值上界大于0即可.
		{
			for(int j=0;j<TempA[0].length;j++)
			{
				if(TempA[i][j]!=0)
					Tempub[j]=Integer.MAX_VALUE; //保证大于0即可
				else if(boundFlag==true)
					Tempub[j]=1.0;
			}
		}
		boundFlag=false;
		
		for(int i =0;i<TempA[equalities.size()].length;i++)
		{	
			if(TempA[equalities.size()][i]==1.0)
				Tempub[i]=1.0;
		}
		
		String new_object="";
		for(int k=0;k<TempoptimizeObject.length;k++)
		{		
				if(TempoptimizeObject[k]==1.0)
					new_object=new_object+"+i"+k;  //系数*可能世界的标签
		}
		//PrintService();
		or.setC(TempoptimizeObject);  //第二个bug因为处理
		or.setG(TempG);
		or.setH(Temph);	
		or.setA(TempA);
		or.setB(Tempb);
		or.setLb(Templb);
		or.setUb(Tempub);
		or.setDumpProblem(true); 
		
		LPPrimalDualMethod opt = new LPPrimalDualMethod();
		
		opt.setLPOptimizationRequest(or);
		try {
			opt.optimize();
			double[] sol_min = opt.getOptimizationResponse().getSolution();
			boolean min_flag = false;
			//System.out.println("最小值的解为:");
			for (int i = 0; i < sol_min.length; i++) 
			{
				if (sol_min[i] != 0.0)
				{
					min_flag=true;
					break;
				}		
			}
			if(min_flag==false)  //防止解均为0，但仍然通过.不满足概率之和等于1的等式约束
			{
				System.out.println("This is a bug!");
				System.out.println("The problem of conditional constraints is unsolved because all of value is 0!");
				//System.exit(0);
				throw new RuntimeException("infeasible problem");
			}
			
			else if(min_flag==true)  //虽然给出了解，但是仍存在不满足条件的情况（主要是针对约简之后的部分不等式等价于等式的情况）
			{
				for(int i=0;i<TempG.length;i++)
				{
					double value=0;
					for(int j=0;j<TempG[0].length;j++)
					{
						value=sol_min[j]*TempG[i][j]+value;
					}
					if(value>0)
					{
						System.out.println("This is a bug!");
						System.out.println("The problem of conditional constraints is unsolved because it is not satisfied one of the inequvality constraints!");
					//System.exit(0);
						throw new RuntimeException("infeasible problem");
					}
				}
				
			}
			
			LinearMultivariateRealFunction objectiveFunction_min = new LinearMultivariateRealFunction(TempoptimizeObject, 0);	
			lowerBounder=objectiveFunction_min.value(sol_min);
			//System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的最大下界为:"+objectiveFunction_min.value(sol_min));
			System.out.println("The lower bounder of "+entails[0]+" and "+entails[1]+" based on "+entails[2]+"are: "+objectiveFunction_min.value(sol_min));
			//替换目标函数，求最大值
			for(int i=0;i<TempoptimizeObject.length;i++)
			{
				TempoptimizeObject[i]=-TempoptimizeObject[i];
			}
			//or.setC(max_c);
			or.setC(TempoptimizeObject);
			opt.setLPOptimizationRequest(or);
			opt.optimize();
			double[] sol_max = opt.getOptimizationResponse().getSolution();
			//System.out.println("最大值的解为:");
			boolean max_flag = false;
			for (int i = 0; i < sol_max.length; i++) 
			{
				if (sol_max[i] != 0.0)
				{
					max_flag=true;
					break;
				}		
			}
			if(max_flag==false)  //防止解均为0，但仍然通过
			{
				System.out.println("This is a bug!");
				System.out.println("The problem of conditional constraints is unsolved because all of value is 0!");
				//System.exit(0);
				throw new RuntimeException("infeasible problem");
			}		
			else if(max_flag==true)  //虽然给出了解，但是仍存在不满足条件的情况（主要是针对约简之后的部分不等式等价于等式的情况）
			{
				for(int i=0;i<TempG.length;i++)
				{
					double value=0;
					for(int j=0;j<TempG[0].length;j++)
					{
						value=sol_max[j]*TempG[i][j]+value;
					}
					if(value>0)
					{
						System.out.println("This is a bug!");
						System.out.println("The problem of conditional constraints is unsolved because it is not satisfied one of the inequvality constraints!");
					//System.exit(0);
						throw new RuntimeException("infeasible problem");
					}
				}			
			}		
		//	LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(new_objectfunction, 0);	
			LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(Tempmax_c, 0);	
			upperBounder=-objectiveFunction_max.value(sol_max);
			//System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的最小上界为:"+-objectiveFunction_max.value(sol_max));
			System.out.println("The upper bounder of "+entails[0]+" and "+entails[1]+" based on "+entails[2]+"are: "+-objectiveFunction_max.value(sol_max));
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			if(e.getMessage().equals("Infeasible problem")); //捕获异常内容
			System.out.println("infedbdfbfgb");
		}	
		//最大下界objectiveFunction_min.value(sol_min)
		//最小上界-objectiveFunction_max.value(sol_max)
		if(upperBounder==0.0)
		{
			hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+lowerBounder+","+upperBounder+","+" (overflower)");
			System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的概率区间为："+"已经溢出");
		}
		else 
		{
			BigDecimal   b1   =   new   BigDecimal(lowerBounder);  //四舍五入的方式	
			BigDecimal   b2   =   new   BigDecimal(upperBounder);  //四舍五入的方式
			double lower_Bounder=b1.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
			double upper_Bounder=b2.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue(); 
			boolean hidden=true;
			for(String s:initialMappings)
			{
				if(s.contains(entailOjects.get(n)))
				{
					String parts[]=s.split(",");
					if(parts[2].equals("="))
					{
						if(Math.abs(lower_Bounder-Double.parseDouble(parts[3]))<=0.002&&Math.abs(upper_Bounder-Double.parseDouble(parts[4]))<=0.002)
						{
							hidden=false;
							break;
						}
						if(Math.abs(lower_Bounder-Double.parseDouble(parts[3]))<0.001&&parts[2].equals("=")&&lower_Bounder<Double.parseDouble(parts[3]))
							lower_Bounder=Double.parseDouble(parts[3]);
						if(Math.abs(upper_Bounder-Double.parseDouble(parts[4]))<0.001&&parts[2].equals("=")&&upper_Bounder>Double.parseDouble(parts[4]))
							upper_Bounder=Double.parseDouble(parts[4]);					
					}				
				}
			}
			//if(hidden==true)
			hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+lower_Bounder+","+upper_Bounder);
			System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的概率区间为："+"["+lower_Bounder+","+upper_Bounder+"]");
		}
		
		//if(hidden==true)
		//hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+lower_Bounder+","+upper_Bounder);
		/*if(upperBounder!=0.0)
			hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+lowerBounder+","+upperBounder);
		else 
		{
			hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+lowerBounder+","+upperBounder+","+" (overflower)");
		}*/
		
		}
		
	}
	
	public void entailments (ArrayList<String>entailOjects,Map<Integer, Integer> m ,double Pr[])
	{
		for(int i=0;i<entailOjects.size();i++)
		{	
			ArrayList<Integer> numeratorWorld =new ArrayList<Integer>();   //分子的可能世界
			ArrayList<Integer> denominatorWorld=new ArrayList<Integer>();  //分母的可能世界
			double  numeratorValue=0;
			double  denominatorValue=0;
			String entails[]=entailOjects.get(i).split(",");	
			if(entails[2].equals("|"))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0])&&possibleWordIndex.get(k).contains("+"+entails[1]))
						numeratorWorld.add(k);
					if(possibleWordIndex.get(k).contains("+"+entails[0])) 
						denominatorWorld.add(k);		
				}
			}
			else if (entails[2].equals("="))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0])&&possibleWordIndex.get(k).contains("+"+entails[1]))
						numeratorWorld.add(k);
					/*System.out.println(possibleWordIndex.get(k).contains("-"+entails[0])); 
					System.out.println(possibleWordIndex.get(k).contains(entails[1]));
					System.out.println(possibleWordIndex.get(k).contains("-"+entails[1]));*///有可能存在bug，前缀相同则会存在bug
					//System.out.println(!(possibleWordIndex.get(k).contains("-"+entails[0])&&possibleWordIndex.get(k).contains("-"+entails[1])));
					if(!(possibleWordIndex.get(k).contains("-"+entails[0])&&possibleWordIndex.get(k).contains("-"+entails[1])))
						denominatorWorld.add(k);
				}
			}
			else if (entails[1].equals("T"))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0]))
						numeratorWorld.add(k);
						denominatorWorld.add(k);
				}
			}
			for(int j=0;j<numeratorWorld.size();j++)
			{
				/*numeratorValue=numeratorValue+Pr[getKey(numeratorWorld.get(j),m)];
				System.out.println(Pr[getKey(numeratorWorld.get(j),m)]);
				System.out.println(Pr[numeratorWorld.get(j)]);*/
				
				/*double a=Pr[numeratorWorld.get(j)];
				numeratorValue=numeratorValue+a;*/
				numeratorValue=numeratorValue+Pr[numeratorWorld.get(j)];
			}
			for(int j=0;j<denominatorWorld.size();j++)
			{
				/*denominatorValue=denominatorValue+Pr[getKey(denominatorWorld.get(j),m)];
				System.out.println(Pr[getKey(denominatorWorld.get(j),m)]);
				System.out.println(Pr[denominatorWorld.get(j)]);*/
				
				/*double a=Pr[denominatorWorld.get(j)];
				denominatorValue=denominatorValue+a;*/
				denominatorValue=denominatorValue+Pr[denominatorWorld.get(j)];
			}
			BigDecimal   b   =   new   BigDecimal(numeratorValue/denominatorValue);  //四舍五入的方式
			double confidence=b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
			System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的概率值为："+confidence);
			if(confidence>0.5)
				hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+confidence);
		}
	}
	
/*	public void entailments (ArrayList<String> entailOjects,Map<Integer, Integer> m ,double Pr[])
	{
		for(int i=0;i<entailOjects.size();i++)
		{	
			ArrayList<Integer> numeratorWorld =new ArrayList<Integer>();   //分子的可能世界
			ArrayList<Integer> denominatorWorld=new ArrayList<Integer>();  //分母的可能世界
			double  numeratorValue=0;
			double  denominatorValue=0;
			String entails[]=entailOjects.get(i).split(",");	
			if(entails[2].equals("|"))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0])&&possibleWordIndex.get(k).contains("+"+entails[1]))
						numeratorWorld.add(k);
					if(possibleWordIndex.get(k).contains("+"+entails[0])) 
						denominatorWorld.add(k);		
				}
			}
			else if (entails[2].equals("="))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0])&&possibleWordIndex.get(k).contains("+"+entails[1]))
						numeratorWorld.add(k);
					if(!(possibleWordIndex.get(k).contains("-"+entails[0])&&possibleWordIndex.get(k).contains("-"+entails[1])))
						denominatorWorld.add(k);
				}
			}
			else if (entails[1].equals("T"))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0]))
						numeratorWorld.add(k);
						denominatorWorld.add(k);
				}
			}
			for(int j=0;j<numeratorWorld.size();j++)
			{
				numeratorValue=numeratorValue+Pr[getKey(numeratorWorld.get(j),m)];
			}
			for(int j=0;j<denominatorWorld.size();j++)
			{
				denominatorValue=denominatorValue+Pr[getKey(denominatorWorld.get(j),m)];
			}
			BigDecimal   b   =   new   BigDecimal(numeratorValue/denominatorValue);  //四舍五入的方式
			double confidence=b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();  
			System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的概率值为："+confidence);
			if(confidence>0.5)
			hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+confidence);
		}
	}*/
	
	public void entailments (ArrayList<String> entailOjects ,double Pr[])
	{
		for(int i=0;i<entailOjects.size();i++)
		{	
			ArrayList<Integer> numeratorWorld =new ArrayList<Integer>();   //分子的可能世界
			ArrayList<Integer> denominatorWorld=new ArrayList<Integer>();  //分母的可能世界
			double  numeratorValue=0;
			double  denominatorValue=0;
			String entails[]=entailOjects.get(i).split(",");	
			if(entails[2].equals("|"))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0])&&possibleWordIndex.get(k).contains("+"+entails[1]))
						numeratorWorld.add(k);
					if(possibleWordIndex.get(k).contains("+"+entails[0])) 
						denominatorWorld.add(k);		
				}
			}
			else if (entails[2].equals("="))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0])&&possibleWordIndex.get(k).contains("+"+entails[1]))
						numeratorWorld.add(k);
					if(!(possibleWordIndex.get(k).contains("-"+entails[0])&&possibleWordIndex.get(k).contains("-"+entails[1])))
						denominatorWorld.add(k);
				}
			}
			else if (entails[1].equals("T"))
			{
				for (int k: possibleWordIndex.keySet())
				{
					if(possibleWordIndex.get(k).contains("+"+entails[0]))
						numeratorWorld.add(k);
					denominatorWorld.add(k);
				}
			}
			for(int j=0;j<numeratorWorld.size();j++)
			{
				numeratorValue=numeratorValue+Pr[j];
			}
			for(int j=0;j<denominatorWorld.size();j++)
			{
				denominatorValue=denominatorValue+Pr[j];
			}
			BigDecimal   b   =   new   BigDecimal(numeratorValue/denominatorValue);  //四舍五入的方式
			double confidence=b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
			System.out.println(entails[0]+"与"+entails[1]+"基于"+entails[2]+"的概率值为："+confidence);
			if(confidence>0.5)
			hiddenMappings.add(entails[0]+","+entails[1]+","+entails[2]+","+confidence);
		}
	}
	
	public int getKey(int index,Map<Integer, Integer> m )
	{
		Set<Integer> keys=m.keySet();
		for (int a:keys)
		{
			//int value=m.get(a);
			if(index==a)
				return m.get(a);
		}
		return -1;	
	}
	
	public boolean isProSatifiable(ArrayList<String> mappings)  //将可能世界作为目标就好了。
	{
		ArrayList<double[]> inequalitiesTemp =new ArrayList<double[]>();
		for (double[] a:inequalities)
		{
			inequalitiesTemp.add(a);
		}
		ArrayList<double[]> equalitiesTemp =new ArrayList<double[]>();
		for (double[] a:equalities)
		{
			equalitiesTemp.add(a);
		}

		for(int i=0;i<mappings.size();i++)
		{	
			String parts[]=mappings.get(i).split(",");
			/*String head=parts[0];
			String body=parts[1];*/
			//根据转换规则这里需要反过来
			String body=parts[0];
			String head=parts[1];
			String relation=parts[2];
			Double lower=Double.parseDouble(parts[3]);			
			Double upper=Double.parseDouble(parts[4]);	
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];
			if(relation.equals("|") )
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalitiesTemp.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient2);
					}
				}
				/*for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalitiesTemp.add(coefficient);*/
			}	
			else if(relation.equals("="))
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
						{
							coefficient1[s] = lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s] =-(1-lower);
							BigDecimal b  =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalitiesTemp.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							}
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient2);
					}
				}
				/*for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient[s] = confidence;
					}
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalitiesTemp.add(coefficient);*/
			}	
		}
		double [][]GTemp = new double[inequalitiesTemp.size()][numWorld];
		double [] hTemp = new double[inequalitiesTemp.size()];
		for(int i=0;i<inequalitiesTemp.size();i++)
		{
			GTemp[i]=inequalitiesTemp.get(i);
		}		
		//等式的系数赋值，一般都是x0+...+xn=1
		double [][]ATemp=new double[1+equalitiesTemp.size()][numWorld];
		for(int i=0;i<equalitiesTemp.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			ATemp[i]=equalitiesTemp.get(i);
		}		
		double []bTemp = new double[1+equalitiesTemp.size()] ;  //前面的equalities.size()-1项默认为0.0
		bTemp[equalitiesTemp.size()]=1.0;	
		
		for (int k=0;k<numWorld;k++)  //令所有可能世界求和为1 (只是可能世界),这是最后一个等式约束
		{
			ATemp[equalitiesTemp.size()][k]=1.0;
		}

		//只需要最小值测试即可
		double minTemp[] = new double[numWorld] ;
		double[]lbTemp=new double[numWorld];
		double[]ubTemp=new double[numWorld];  
		double[]objectTemp=new double[numWorld] ;
		
		/*for (int k : possibleWordIndex.keySet())  //目标函数
		{
			minTemp[k] = 1.0;
			maxTemp[k] = -1.0;
		}*/
		
		for(int i=0;i<numWorld;i++) //因为目标函数就是可能世界
		{
			ubTemp[i]=1.0;
			objectTemp[i]=1.0;
			minTemp[i]=1.0;
		}
		
		//因为求解的时候的目标函数是Y=x1+。。。+xn，因此不需要考虑边界上的定义。
		
		/*for(int i=0;i<GTemp.length;i++)  //不等式系数不为0.即上界大于0即可.
		{
			for(int j=0;j<GTemp[0].length;j++)
			{
				if(GTemp[i][j]!=0)
					ubTemp[j]=1.0;
					//ub[j]=Integer.MAX_VALUE; //保证大于0即可			
			}
		}
		
		for(int i=0;i<A.length-1;i++)  //等式系数不为0.即上界大于0即可.
		{
			for(int j=0;j<A[0].length;j++)
			{
				if(A[i][j]!=0)
					ubTemp[j]=1.0;
					//ub[j]=Integer.MAX_VALUE; //保证大于0即可
			}
		}	
		for(int i =0;i<A[equalities.size()].length;i++)
		{	
			boolean flag=false;
			for (int s : possibleWordIndex.keySet()) 
			{
				if (s==i)   //属于可能世界的范畴,并且有限制
				{
					flag=true;
					//A[equalities.size()][i]=0.0;
					break;
				}
			}
			if(A[equalities.size()][i]==1.0)
				ubTemp[i]=1.0;
			if(flag==false)  //不可能世界的等式系数应为0
			{
				ubTemp[i]=0.0;
				A[equalities.size()][i]=0.0;
			}
		}*/
		
		objectTemp=minTemp.clone();	
		or.setC(objectTemp);  //第二个bug因为处理
		or.setG(GTemp);
		or.setH(hTemp);	
		or.setA(ATemp);
		or.setB(bTemp);
		or.setLb(lbTemp);
		or.setUb(ubTemp);
		or.setDumpProblem(true); 
		or.setMaxIteration(150);  //设置迭代步数。
		LPPrimalDualMethod opt = new LPPrimalDualMethod();
		
		opt.setLPOptimizationRequest(or);
		try {
			opt.optimize();
			boolean incoherent=true;
			double[] sol = opt.getOptimizationResponse().getSolution();
			boolean sol_flag = false;
			for (int i = 0; i < sol.length; i++) 
			{
				if (sol[i] != 0.0)
				{
					sol_flag=true;
					break;
				}		
			}
			if(sol_flag==false)  //防止解均为0，但仍然通过
			{
				System.out.println("This is a bug!");
				System.out.println("The problem of conditional constraints is unsolved because all of value is 0!");
				//System.exit(0);
				throw new RuntimeException("infeasible problem");
			}		
			else if(sol_flag==true)  //虽然给出了解，但是仍存在不满足条件的情况（主要是针对约简之后的部分不等式等价于0的情况）
			{
				for(int i=0;i<GTemp.length;i++)
				{
					double value=0;
					for(int j=0;j<GTemp[0].length;j++)
					{
						value=sol[j]*GTemp[i][j]+value;
					}
					if(value>0)
					{
						System.out.println("This is a bug!");
						System.out.println("The problem of conditional constraints is unsolved because it is not satisfied one of the inequvality constraints!");
					//System.exit(0);
						throw new RuntimeException("infeasible problem");
					}
				}			
			}	
			for(Integer concept: conceptIndex.keySet()) //注意这里不能基于opt的解进行求解，因为这对没个概念的最大值其实是有影响的
			{
				for (int k : possibleWordIndex.keySet()) 
				{
					if (possibleWordIndex.get(k).contains("+#" + concept+"#")) // 正数需要额外加上“+”
					{
						objectTemp[k]=-1.0;
					}	
					else {
						objectTemp[k]=0.0;
					}
				}	
				or.setC(objectTemp);
				opt.setLPOptimizationRequest(or);
				opt.optimize();
				double[] sol_max = opt.getOptimizationResponse().getSolution();				
				LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(objectTemp, 0);	
				double value=-objectiveFunction_max.value(sol_max);
				//System.out.println("最小上界为:"+value);
				if(value==0)
				{
					incoherent=false;
					break;
				}					
			}
			if(incoherent==false)
				return false;			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			if(e.getMessage().equals("Infeasible problem")); //捕获异常内容
				System.out.println("Infeasible problem");
			return false;
		}	
	}
	
	public void minimalConflictSet(ArrayList<String> testMapping)//head头部索引，middle当前索引，tail移动索引
	{
		boolean flag=true;
		for (int i=0;i<testMapping.size();i++)
		{
			ArrayList<String> arrayList=(ArrayList<String>) testMapping.clone();
			arrayList.remove(i);
			if(conflictDecision.get(arrayList)!=null) //这里还可以优化，即可满足的子集也是可满足的
			{
				if (conflictDecision.get(arrayList) == true)
					continue;
				else 
				{
					minimalConflictSet(arrayList);
					flag = false;
				}
			}
			else
			{
				if (isProSatifiable(arrayList)) // 这里可以优化 包含冲突子集的集合也是冲突的
				{
					conflictDecision.put(arrayList, true);
					continue;
				} 
				else 
				{
					conflictDecision.put(arrayList, false);
					minimalConflictSet(arrayList);
					flag = false;
				}
			}
			//arrayList.clear();
		}
		if(flag==true) //所有的子类都可满足，那么上层的父亲就是最小冲突子集
		{
			ArrayList<String> conflicts=(ArrayList<String>) testMapping.clone();
			if(!minmalConflictSets.contains(conflicts))
				minmalConflictSets.add(conflicts);
		}
	}
	
	
	public void NewminimalConflictSet(ArrayList<String> testMapping)//head头部索引，middle当前索引，tail移动索引
	{
		boolean flag=true;
		for (int i=0;i<testMapping.size();i++)
		{
			ArrayList<String> arrayList=(ArrayList<String>) testMapping.clone();
			arrayList.remove(i);
			boolean theoremFlag=false;  //默认定理是否执行的标签
			if(!maximumSatisfiableSets.isEmpty()) //若待检测的mappings是已经是某个可满足的子集。那么它一定是可满足的。
			{	
				//boolean satisfiedContain=false;  //默认是不包含的
				for(ArrayList<String> satisfiableSet:maximumSatisfiableSets)
				{
					boolean isContain=true;
					for(String mapping:arrayList)
					{
						if(!satisfiableSet.contains(mapping))
						{
							isContain=false;
							break;
						}
					}
					if(isContain==true)
					{
						//satisfiedContain=true;
						theoremFlag=true;
						break;
					}
				}
			/*	if(satisfiedContain==true)
					break;*/
			}
		     if(!minmalConflictSets.isEmpty())  //若待检测的mappings包含某个最小冲突子集。那么它一定是不可满足的。
			{			
				boolean conflictContain=false;  //默认是包含的
				for(ArrayList<String> conflictset: minmalConflictSets)
				{
					boolean Contain=true;  //默认是包含的
					for(String conflictmap: conflictset)
					{
						if(!arrayList.contains(conflictmap))
						{
							Contain=false;
							break;
						}
					}
					if(Contain==true)
					{
						conflictContain=true;
						break;
					}
				}
				if(conflictContain==true)
				{
					if(!conflictDecision.keySet().contains(arrayList))
						conflictDecision.put(arrayList, false);
					NewminimalConflictSet(arrayList);
					theoremFlag=true;
					flag = false;
				}	
			}
		    if(theoremFlag==false)   //前面的定理并未执行
		    {
				if (conflictDecision.get(arrayList) != null) // 这里还可以优化，即可满足的子集也是可满足的
				{
					if (conflictDecision.get(arrayList) == true)
						continue;
					else 
					{
						NewminimalConflictSet(arrayList);
						flag = false;
					}
				} 
				else 
				{
					if (isProSatifiable(arrayList)) // 这里可以优化, 尽量用已知的信息来减少判断的次数。
					{
						if (!conflictDecision.keySet().contains(arrayList))
							conflictDecision.put(arrayList, true);
						maximumSatisfiableSets.add(arrayList);// 最大可满足子集
						continue;
					}
					else 
					{
						if (!conflictDecision.keySet().contains(arrayList))
							conflictDecision.put(arrayList, false);
						NewminimalConflictSet(arrayList);
						flag = false;
					}
				}
			}
		 }
		if(flag==true) //所有的子类都可满足，那么上层的父亲就是最小冲突子集
		{
			ArrayList<String> conflicts=(ArrayList<String>) testMapping.clone();
			if(!minmalConflictSets.contains(conflicts))
				minmalConflictSets.add(conflicts);
		}
	}
	
	public void minimalConflictMappingSet()
	{
		for (ArrayList<String> set: minmalConflictSets)
		{
			for (String a:set)
			{
				if(!minmalConflictMappingSets.contains(a))
					minmalConflictMappingSets.add(a);
			}
		}
	}
	
	public void Revised(String mapping)//该方法是ESWC08文章提出的贪心方法
	{
		ArrayList<String>  copyConflictSet=(ArrayList<String>) initialMappings.clone();
		copyConflictSet.remove(mapping);
		double interval[]=minimalChangeMappingOptimized(copyConflictSet,mapping);  	//计算其上界改变的大小即可
		String parts[]=mapping.split(",");
		Double[]change=new Double[2];
		// "The change value of "+mapping+" is："
		int d1 = (int) ((interval[0]) * 100);
		double newLower = d1;
		int d2 = (int) ((interval[1]) * 100);
		double newUpper = d2;
		
		initialMappings.remove(mapping);
		/*for (int i = 0; i < initialMappings.size(); i++) 
		{
			if (initialMappings.get(i).equals(mapping)) 
			{
				initialMappings.remove(i);
				i--;
			}
		}*/	
		initialMappings.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + newLower / 100 + "," + newUpper / 100);
		revisedMapping=parts[0] + "," + parts[1] + "," + parts[2] + "," + newLower / 100 + "," + newUpper / 100;
	}

	
	public void greedyRemove()//该方法是ESWC08文章提出的贪心方法
	{
		//根据mapping的概率区间的均值进行降序排序
		double[] meanValue=new double[initialMappings.size()];  //这里排序可能待扩展，从点到区间仍有一些区别
		for(int i=0;i<initialMappings.size();i++)
		{
			String parts[]=initialMappings.get(i).split(",");
			double value=(Double.parseDouble(parts[3])+Double.parseDouble(parts[4]))/2;
			meanValue[i]=value;	
		}
		Arrays.sort(meanValue);  
		double temp=0;
		for(int i=0;i<meanValue.length;i++)
		{
		   for(int j=i+1;j<meanValue.length;j++)
		   {
			   if(meanValue[j]>meanValue[i])
			   {
				   temp=meanValue[i];
				   meanValue[i]=meanValue[j];
				   meanValue[j]=temp;
			   }
		    }
		}
		ArrayList<String> descendingMapping=new ArrayList<String>();
		ArrayList<String> copymapping=(ArrayList<String>) initialMappings.clone();
		for(int i=0;i<meanValue.length;i++)
		{
			for(int k=0;k<copymapping.size();k++)
			{
				String parts[]=copymapping.get(k).split(",");
				double value=(Double.parseDouble(parts[3])+Double.parseDouble(parts[4]))/2;
				if(meanValue[i]==value)
				{
					descendingMapping.add(copymapping.get(k));
					copymapping.remove(k);
					k--;
					i--;
					break;
				}
			}
		}
		//用贪心的方法来进行判断结果.
		ArrayList<String> testMapping=new ArrayList<String>();
		for(int i=0;i<descendingMapping.size();i++)
		{
			testMapping.add(descendingMapping.get(i));
			if(isProSatifiable(testMapping))
				continue;
			else
			{
				testMapping.remove(testMapping.size()-1);
			}
		}	
		initialMappings.clear();
		for(String a:testMapping)
		{
			initialMappings.add(a);
			//System.out.println();
		}	
	}
	
	public void localMinimalChange()//2011phd文章中提到的方法之一，移除局部变化最小的mappings
	{
		minimalChange();	
		//ArrayList<Double> values=new ArrayList<Double>();
		HashMap<ArrayList<String>,String> removeMappingAllConflictSet =new HashMap<ArrayList<String>, String>();
		for (ArrayList<String> conflictSet: minmalConflictSets)
		{
			double minimalChange=1;
			//String removeMapping=conflictSet.get(0);
			double minimalMean=1.1;
			for(int i=0;i<conflictSet.size();i++)
			{
				String mapping=conflictSet.get(i);
				String parts[]=mapping.split(",");
				//double confidence=Double.parseDouble(parts[3]);
				double mean=(Double.parseDouble(parts[3])+Double.parseDouble(parts[4]))/2;
				Double[] interval=ConflictSetChangeInterval.get(conflictSet).get(mapping);
				BigDecimal b=new   BigDecimal(interval[0]+interval[1]);
				//局部的计算方法
				double change=  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();
				/*BigDecimal b=new   BigDecimal(ConflictSetChangeValues.get(conflictSet).get(mapping));
				double change=  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();		*/	
				if(minimalChange>change)
				{
					minimalChange=change;
				}
				if(minimalMean>mean)
				{
					minimalMean=mean;
				}
			}
			ArrayList<String> removeMappingSingleConflictSet =new ArrayList<String>();
			for(int i=0;i<conflictSet.size();i++)
			{
				String mapping=conflictSet.get(i);
				Double[] interval=ConflictSetChangeInterval.get(conflictSet).get(mapping);
				BigDecimal b=new   BigDecimal(interval[0]+interval[1]);
				double change=  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();
				/*BigDecimal b=new   BigDecimal(ConflictSetChangeValues.get(conflictSet).get(mapping));
				double change=  b.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();	*/
				if(change==minimalChange) //Change的优先级最高
				{
					//minimalChange=change;
					removeMappingSingleConflictSet.add(mapping);
				}
			}
			//HashMap<String,Double> removeMapping =new HashMap<String,Double>();
			if(removeMappingSingleConflictSet.size()>1)  //保证每一个冲突子集中只有一个confidence 最小的冲突子集
			{
				for(int i=0;i<removeMappingSingleConflictSet.size();i++)
				{
					String map=removeMappingSingleConflictSet.get(i);
					String[] parts=map.split(",");
					double mean=(Double.parseDouble(parts[3])+Double.parseDouble(parts[4]))/2;
					if(mean!=minimalMean)	
					{
						removeMappingSingleConflictSet.remove(i);
						i--;
					}
				}
			}	
			//removeMapping.put(removeMappingSingleConflictSet.get(0), minimalConfidence);	
			removeMappingAllConflictSet.put(conflictSet, removeMappingSingleConflictSet.get(0)); //哪个冲突子集里应该移除哪个mappings
			//mapConflict( removeMappingSingleConflictSet.get(0),conflictSet);
		}
		for (ArrayList<String> a: removeMappingAllConflictSet.keySet())
		{
			ArrayList<ArrayList<String>> MappingconflictSet=new ArrayList<ArrayList<String>>();		
			String removeMap=removeMappingAllConflictSet.get(a);
			for (ArrayList<String> conflictSet: minmalConflictSets)
			{
				if(conflictSet.contains(removeMap))
				{
					MappingconflictSet.add(conflictSet);		
				}
			}
			mapConflict.put(removeMap, MappingconflictSet); //从每个mappings找到关联的冲突集合
		}
		ArrayList<ArrayList<String>> copyminmalConflictSets=(ArrayList<ArrayList<String>>) minmalConflictSets.clone();		
		Iterator<Entry<String, ArrayList<ArrayList<String>>>> iter=mapConflict.entrySet().iterator();
		while (iter.hasNext())
		{
			String map=iter.next().getKey();
			for (int j=0;j<mapConflict.get(map).size();j++)  //获取最小冲突子集的集合
			{
				ArrayList<String> conflicetSet=mapConflict.get(map).get(j);
				for(int i=0;i<copyminmalConflictSets.size();i++)  
				{
					if(copyminmalConflictSets.get(i).equals(conflicetSet))
					{
						copyminmalConflictSets.remove(i);
						//copyminmalConflictSets.remove(conflicetSet);
						i--;
					}
				}
				for (int i=0;i<initialMappings.size();i++)
				{
					if(initialMappings.get(i).equals(map))
						initialMappings.remove(i);
				}
			}
			if(copyminmalConflictSets.isEmpty())
				break;		
		}		
	}
	
	public void globalMappingInfluence()//2011phd文章中提到的方法之一，移除影响最大的那个mappings
	{
		minimalChange();		
		for (String a: minmalConflictMappingSets)
		{
			ArrayList<ArrayList<String>> MappingconflictSet=new ArrayList<ArrayList<String>>();		
			double maxLowerChange=0;
			double maxUpperChange=0;	
			ArrayList<Double> values=new ArrayList<Double>();
			for (ArrayList<String> conflictSet: minmalConflictSets)
			{
				if(conflictSet.contains(a))
				{
					MappingconflictSet.add(conflictSet);		
					//double beliefChange=ConflictSetChangeValues.get(conflictSet).get(a);
					Double beliefChange[]=ConflictSetChangeInterval.get(conflictSet).get(a);
				    if(maxLowerChange<beliefChange[0])  //基于每个mappings对所属的conflictset的最大影响值
				    	maxLowerChange=beliefChange[0];
				    if(maxUpperChange<beliefChange[1])  //基于每个mappings对所属的conflictset的最大影响值
				    	maxUpperChange=beliefChange[1];
					values.add(beliefChange[0]+beliefChange[1]);
				}
			}
			//values.add(maxLowerChange);//只需存下界，上界就可以通过下界计算得出
			values.add(maxLowerChange+maxUpperChange);
			String parts[]=a.split(",");
			double mean=(Double.parseDouble(parts[3])+Double.parseDouble(parts[4]))/2;
    		values.add(mean);  //原来存的是confidence,现在改存均值
			mapConflict.put(a, MappingconflictSet); //从每个mappings找到关联的冲突集合
			mapValues.put(a, values); //每个mappings对应的一些重要的值
		}
		
		ArrayList<ArrayList<String>> copyminmalConflictSets=(ArrayList<ArrayList<String>>) minmalConflictSets.clone();		
		while (!copyminmalConflictSets.isEmpty()) 
		{
			ArrayList<String>numMaxMapping= new ArrayList<String>();
			int maxnum=0;
			String optimalMap="";
			for(String a:mapConflict.keySet())
			{
				if(maxnum<mapConflict.get(a).size())
					maxnum=mapConflict.get(a).size();
			}
			for(String a:mapConflict.keySet())
			{
				if(maxnum==mapConflict.get(a).size())
					numMaxMapping.add(a);
			}
			if(numMaxMapping.size()>1)	  ////在数量最优的情况下有多个解
			{
				ArrayList<String>minBeliefChange= new ArrayList<String>();
				ArrayList<String>minMean= new ArrayList<String>();
				double minchange=1.1;  //可能要四舍五入,因为有误差,选取改变最小的值进行移除
				double minmean=1.1;
				for(String m:numMaxMapping)
				{
					BigDecimal   b=new   BigDecimal(mapValues.get(m).get(mapValues.get(m).size()-2)); //获得BeliefChange的最大值
					if(minchange>b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue())
					{
						//BigDecimal   b=new   BigDecimal(mapValues.get(m).get(mapValues.get(m).size()-2)); //获得BeliefChange的最大值
						minchange=   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();				 
						//maxchange=mapValues.get(m).get(mapValues.get(m).size()-2); //获得BeliefChange的最大值
					}
					if(minmean> mapValues.get(m).get(mapValues.get(m).size()-1))
					{
						minmean=mapValues.get(m).get(mapValues.get(m).size()-1); //获得Confidence的最大值
					}
				}
				for(String m:numMaxMapping)
				{
					BigDecimal   b=new   BigDecimal(mapValues.get(m).get(mapValues.get(m).size()-2)); //获得BeliefChange的最大值
					//if(maxchange==mapValues.get(m).get(mapValues.get(m).size()-2))  //获得BeliefChange的最大值的Mapping
					if(minchange==b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue())  //获得BeliefChange的最大值的Mapping
					{
						minBeliefChange.add(m);
					}
					if(minmean==mapValues.get(m).get(mapValues.get(m).size()-1))  //获得Confidence的最大值的Mapping
					{
						minMean.add(m);
					}
				}
				if(minBeliefChange.size()>1)   //获得Confidence的最大值的Mapping不止一个的话，默认取第一个
				{
					optimalMap=minMean.get(0);
				}	
				else if(minBeliefChange.size()==1)  //BeliefChange的最大值的Mapping的情况
				{
					optimalMap=minBeliefChange.get(0);  
				}
			}
			else if (numMaxMapping.size()==1) //在数量最优的情况下只有一个解
			{
				optimalMap=numMaxMapping.get(0);
			}
			//处理	
			//修改该mapping的Confidence
			/*double beliefRevise=mapValues.get(optimalMap).get(mapValues.get(optimalMap).size()-2);    
			String parts[]=optimalMap.split(",");*/
			for( int i=0;i<initialMappings.size();i++)
			{
				if (initialMappings.get(i).equals(optimalMap))
				{
					initialMappings.remove(i);
					i--;
				}
			}
		/*	int d=(int) ((Double.parseDouble(parts[3])-beliefRevise)*10000);
			double value=d;
			initialMappings.add(parts[0]+","+parts[1]+","+parts[2]+","+value/10000);*/
			//initialMappings.add(parts[0]+","+parts[1]+","+parts[2]+","+(Double.parseDouble(parts[3])-beliefRevise));
			
			//移除对应HashMap中的值对应的冲突集合
			for(int i=0;i<copyminmalConflictSets.size();i++)  
			{
				if(copyminmalConflictSets.get(i).contains(optimalMap))
				{
					copyminmalConflictSets.remove(i);
					i--;
				}
			}
			//可能还需要改一些东西
				//Set<String> mappingsets=mapConflict.keySet();
			Iterator<Entry<String, ArrayList<ArrayList<String>>>> iter=mapConflict.entrySet().iterator();
			while (iter.hasNext())
			{
				String map=iter.next().getKey();
				for (int j=0;j<mapConflict.get(map).size();j++)  //获取最小冲突子集的集合
				{
					if(mapConflict.get(map).get(j).contains(optimalMap))  //如果冲突子集中这个mappings则移除
					{
						mapConflict.get(map).remove(j);
						mapValues.get(map).remove(j);
						j--;
					}
				}
				if(mapConflict.get(map).isEmpty())
					iter.remove();
						//mapConflict.remove(map);
				if(mapValues.get(map).size()==2)
						mapValues.remove(map);
			}
				//}
			
			//mapConflict.remove(optimalMap);  //移除mapping与冲突集合的对应关系
			//mapValues.remove(optimalMap);  //移除mapping与取值上的对应关系
		}
	}
	
	
	public void approximateBeliefChange()//基于minimal change的近似方法
	{
		minimalChange();		
		for (String a: minmalConflictMappingSets)
		{
			ArrayList<ArrayList<String>> MappingconflictSet=new ArrayList<ArrayList<String>>();		
			double maxLowerChange=0;
			double maxUpperChange=0;	
			ArrayList<Double> values=new ArrayList<Double>();
			for (ArrayList<String> conflictSet: minmalConflictSets)
			{
				if(conflictSet.contains(a))
				{
					MappingconflictSet.add(conflictSet);		
					//double beliefChange=ConflictSetChangeValues.get(conflictSet).get(a);
					Double beliefChange[]=ConflictSetChangeInterval.get(conflictSet).get(a);
				    if(maxLowerChange<beliefChange[0])  //基于每个mappings对所属的conflictset的最大影响值
				    	maxLowerChange=beliefChange[0];
				    if(maxUpperChange<beliefChange[1])  //基于每个mappings对所属的conflictset的最大影响值
				    	maxUpperChange=beliefChange[1];
					values.add(beliefChange[0]+beliefChange[1]); //存储基于单个冲突子集的改变值
				}
			}
			//做一个标记，1表示只改上界，-1表示只改下界，0表示上下界都需要改
			//values.add(maxLowerChange+maxUpperChange);
			/*values.add(maxLowerChange);
			values.add(maxUpperChange);*/
			values.add(maxLowerChange);//只需存下界，上界就可以通过下界计算得出
			values.add(maxLowerChange+maxUpperChange);
			String parts[]=a.split(",");
			double mean=(Double.parseDouble(parts[3])+Double.parseDouble(parts[4]))/2;
    		values.add(mean);  //原来存的是confidence,现在改存均值
			mapConflict.put(a, MappingconflictSet); //从每个mappings找到关联的冲突集合
			mapValues.put(a, values); //每个mappings对应的一些重要的值
		}
		ArrayList<ArrayList<String>> copyminmalConflictSets=(ArrayList<ArrayList<String>>) minmalConflictSets.clone();		
		while (!copyminmalConflictSets.isEmpty()) 
		{
			ArrayList<String>numMaxMapping= new ArrayList<String>();
			int maxnum=0;
			String optimalMap="";
			for(String a:mapConflict.keySet())
			{
				if(maxnum<mapConflict.get(a).size())
					maxnum=mapConflict.get(a).size();
			}
			for(String a:mapConflict.keySet())
			{
				if(maxnum==mapConflict.get(a).size())
					numMaxMapping.add(a);
			}
			if(numMaxMapping.size()>1)	  ////在数量最优的情况下有多个解
			{
				ArrayList<String>minBeliefChange= new ArrayList<String>();
				ArrayList<String>minMean= new ArrayList<String>();
				double minchange=1.1;  //可能要四舍五入,因为有误差,选取改变最小的值进行移除
				double minmean=1.1;
				for(String m:numMaxMapping)
				{
					BigDecimal   b=new   BigDecimal(mapValues.get(m).get(mapValues.get(m).size()-2)); //获得BeliefChange的最大值
					if(minchange>b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue())
					{
						//BigDecimal   b=new   BigDecimal(mapValues.get(m).get(mapValues.get(m).size()-2)); //获得BeliefChange的最大值
						minchange=   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();				 
						//maxchange=mapValues.get(m).get(mapValues.get(m).size()-2); //获得BeliefChange的最大值
					}
					if(minmean> mapValues.get(m).get(mapValues.get(m).size()-1))
					{
						minmean=mapValues.get(m).get(mapValues.get(m).size()-1); //获得Confidence的最大值
					}
				}
				for(String m:numMaxMapping)
				{
					BigDecimal   b=new   BigDecimal(mapValues.get(m).get(mapValues.get(m).size()-2)); //获得BeliefChange的最大值
					//if(maxchange==mapValues.get(m).get(mapValues.get(m).size()-2))  //获得BeliefChange的最大值的Mapping
					if(minchange==b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue())  //获得BeliefChange的最大值的Mapping
					{
						minBeliefChange.add(m);
					}
					if(minmean==mapValues.get(m).get(mapValues.get(m).size()-1))  //获得Confidence的最大值的Mapping
					{
						minMean.add(m);
					}
				}
				if(minBeliefChange.size()>1)   //获得Confidence的最大值的Mapping不止一个的话，默认取第一个
				{
					optimalMap=minMean.get(0);
				}	
				else if(minBeliefChange.size()==1)  //BeliefChange的最大值的Mapping的情况
				{
					optimalMap=minBeliefChange.get(0);  
				}
			}
			else if (numMaxMapping.size()==1) //在数量最优的情况下只有一个解
			{
				optimalMap=numMaxMapping.get(0);
			}
			//处理	
			//修改该mapping的Confidence
			double lower=mapValues.get(optimalMap).get(mapValues.get(optimalMap).size()-3);    
			double upper=mapValues.get(optimalMap).get(mapValues.get(optimalMap).size()-2)-lower;  
			String parts[]=optimalMap.split(",");
			for( int i=0;i<initialMappings.size();i++)
			{
				if (initialMappings.get(i).equals(optimalMap))
				{
					initialMappings.remove(i);
					i--;
				}
			}
			int d1=(int) ((Double.parseDouble(parts[3])-lower)*100);
			double newLower=d1;
			int d2=(int) ((Double.parseDouble(parts[4])-upper)*100);
			double newUpper=d2;
			initialMappings.add(parts[0]+","+parts[1]+","+parts[2]+","+newLower/100+","+newUpper/100);
			
			/*BigDecimal   b1   =   new   BigDecimal(Double.parseDouble(parts[3])-lower);  //四舍五入的方式			
			double newLower =   b1.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();
			BigDecimal   b2   =   new   BigDecimal(Double.parseDouble(parts[4])-upper);  //四舍五入的方式
			double newUpper =   b2.setScale(3,  BigDecimal.ROUND_HALF_UP).doubleValue();
			initialMappings.add(parts[0]+","+parts[1]+","+parts[2]+","+newLower+","+newUpper);*/

			
			//移除对应HashMap中的值对应的冲突集合
			for(int i=0;i<copyminmalConflictSets.size();i++)  
			{
				if(copyminmalConflictSets.get(i).contains(optimalMap))
				{
					copyminmalConflictSets.remove(i);
					i--;
				}
			}
			//可能还需要改一些东西
				//Set<String> mappingsets=mapConflict.keySet();
				Iterator<Entry<String, ArrayList<ArrayList<String>>>> iter=mapConflict.entrySet().iterator();
				//for (String map: mappingsets)
				while (iter.hasNext())
				{
					String map=iter.next().getKey();
					for (int j=0;j<mapConflict.get(map).size();j++)  //获取最小冲突子集的集合
					{
						if(mapConflict.get(map).get(j).contains(optimalMap))  //如果冲突子集中这个mappings则移除
						{
							mapConflict.get(map).remove(j);
							mapValues.get(map).remove(j);
							j--;
						}
					}
					if(mapConflict.get(map).isEmpty())
						iter.remove();
						//mapConflict.remove(map);
					if(mapValues.get(map).size()==3)
						mapValues.remove(map);
				}
				//}
			
			//mapConflict.remove(optimalMap);  //移除mapping与冲突集合的对应关系
			//mapValues.remove(optimalMap);  //移除mapping与取值上的对应关系
		}
	}
	
	
	public void minimalChange()//head头部索引，middle当前索引，tail移动索引
	{
		for (ArrayList<String> conflictSet: minmalConflictSets)
		{
			//double maxChange=0;
			HashMap<String,Double[]> mappingChanges=new HashMap<String,Double[]> ();
			String mapping="";
			for (int i=0;i<conflictSet.size();i++)
			{
				mapping=conflictSet.get(i);
				ArrayList<String>  copyConflictSet=(ArrayList<String>) conflictSet.clone();
				copyConflictSet.remove(i);
				//double upperbounder=minimalChangeMapping(copyConflictSet,mapping);  	//计算其上界改变的大小即可
				double interval[]=minimalChangeMappingOptimized(copyConflictSet,mapping);  	//计算其上界改变的大小即可
				String entail[]=conflictSet.get(i).split(",");
				//这里还是需要仔细考虑
				Double[]change=new Double[2];
				//"The change value of "+mapping+" is："
				if(Double.parseDouble(entail[3])>=interval[1])  //要求的上界比原始概率区间的下界还要小
				{
					/*System.out.println(mapping+"下界改变值为："+ Math.abs((Double.parseDouble(entail[3])-interval[1])));
					System.out.println(mapping+"上界改变值为："+  0);*/
					System.out.println("The change value of lower bounder"+mapping+" is："+ Math.abs((Double.parseDouble(entail[3])-interval[1])));
					System.out.println("The change value of upper bounder"+mapping+" is："+  0);
					change[0]=Math.abs((Double.parseDouble(entail[3])-interval[1]));
					change[1]=0.0;
				}			
				else if(Double.parseDouble(entail[4])<=interval[0])  //要求的下界比原始概率区间的上界还要大
				{
					/*System.out.println(mapping+"下界改变值为："+  0);
					System.out.println(mapping+"上界改变值为："+  Math.abs(interval[0]-Double.parseDouble(entail[4])));*/
					System.out.println("The change value of lower bounder"+mapping+" is："+  0);
					System.out.println("The change value of upper bounder"+mapping+" is："+  Math.abs(interval[0]-Double.parseDouble(entail[4])));
					change[0]=0.0;
					change[1]=Math.abs(interval[0]-Double.parseDouble(entail[4]));
				}
				//其他情况则表示没有矛盾			
				/*System.out.println(mapping+"下界改变值为："+  Math.abs(Double.parseDouble(entail[3])-interval[0]));
				System.out.println(mapping+"上界改变值为："+  Math.abs(interval[1]-Double.parseDouble(entail[4])));*/
				//double change=Double.parseDouble(entail[3])-interval[0]+interval[1]-Double.parseDouble(entail[4]);		
				/*change[0]=Math.abs(Double.parseDouble(entail[3])-interval[0]);
				change[1]=Math.abs(interval[1]-Double.parseDouble(entail[4]));*/
				mappingChanges.put(mapping, change);

			}
			ConflictSetChangeInterval.put(conflictSet, mappingChanges);
		}		
	}
	
	public double minimalChangeMapping(ArrayList<String>  mappings,String entailment) //得到entailmentMapping的上界
	{
		//添加mappings的约束
		ArrayList<double[]> inequalitiesTemp=(ArrayList<double[]>) inequalities.clone();
		for(int i=0;i<mappings.size();i++)
		{
			String parts[]=mappings.get(i).split(",");
			String head=parts[0];
			String body=parts[1];
			String relation=parts[2];
			Double confidence=Double.parseDouble(parts[3]);			
			double coefficient[]=new double[numWorld];
			if(relation.equals("|") )
			{
				for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalitiesTemp.add(coefficient);
			}	
			else if(relation.equals("="))
			{
				for (int s : possibleWordIndex.keySet()) {
					if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient[s] = confidence;
					} 
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
					{
						coefficient[s] = confidence;
					}
					else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
					{
						coefficient[s] =  - (1-confidence);
						BigDecimal   b   =   new   BigDecimal(confidence-1);  //四舍五入的方式
						coefficient[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
					}
				}
				inequalitiesTemp.add(coefficient);
			}	
		}
		System.out.println("++++++++++++++++++++++");
		
		//推理mapping的上界
		double[][] GTemp = new double[inequalitiesTemp.size()][numWorld];
		double[] hTemp = new double[inequalitiesTemp.size()];
		for(int i=0;i<inequalitiesTemp.size();i++)
		{
			GTemp[i]=inequalitiesTemp.get(i);
		}	
		//判断的时候就已经对等式进行了初始化了
		//等式的系数赋值，一般都是x0+...+xn=1
		double[][]ATemp=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			ATemp[i]=equalities.get(i);
		}		
		double[] bTemp = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0
		bTemp[equalities.size()]=1.0;
		
		//置信区间的推理
		double[]lbTemp=new double[numWorld];
		double[]ubTemp=new double[numWorld];  //这里的上界会发生改变。	
		double[]min_cTemp = new double[numWorld] ; //这里的上界会发生改变。	
		double[]max_cTemp = new double[numWorld] ;
		double[]objectfunctionTemp=new double[numWorld] ;
		
		String entails[]=entailment.split(",");	
		if(entails[2].equals("|"))
		{
			for (int k : possibleWordIndex.keySet()) 
			{
				//令entails[0]对应的可能世界
				if (possibleWordIndex.get(k).contains("+" + entails[0]))
				{
						ATemp[equalities.size()][k]=1.0;
				}
				if(possibleWordIndex.get(k).contains("+" + entails[0])&&possibleWordIndex.get(k).contains("+" + entails[1]))
				{
						min_cTemp[k] = 1.0;
						max_cTemp[k] = -1.0;
				}
			}
		}
	    else if(entails[2].equals("="))
		{
			for (int k : possibleWordIndex.keySet()) 
			{
				//令entails[0]对应的可能世界
				if (possibleWordIndex.get(k).contains("+" + entails[0])||possibleWordIndex.get(k).contains("+" + entails[1]))
				{
						ATemp[equalities.size()][k]=1.0;
				}
				if (possibleWordIndex.get(k).contains("+" + entails[0])&&possibleWordIndex.get(k).contains("+" + entails[1])) 				
				{
					min_cTemp[k] = 1.0;
					max_cTemp[k] = -1.0;
				}
			}
		}		
		String object="";
		for (int k = 0; k < min_cTemp.length; k++) {
			if (min_cTemp[k] == 1.0)
				object = object + "+i" + k; // 系数*可能世界的标签
		}
		if (object.charAt(0) == '+') {
			object = object.replaceFirst("\\+", "");
			System.out.println("目标函数为: y=" + object);
		} else
			System.out.println("目标函数为: y=" + object);					
		
		objectfunctionTemp=min_cTemp.clone();	
			
		for(int i=0;i<GTemp.length;i++)  //不等式系数不为0.即上界大于0即可.
		{
			for(int j=0;j<GTemp[0].length;j++)
			{
				if(GTemp[i][j]!=0)
					ubTemp[j]=Integer.MAX_VALUE; //保证大于0即可
			}
		}
		
		for(int i=0;i<ATemp.length-1;i++)  //等式系数不为0.即上界大于0即可.
		{
			for(int j=0;j<ATemp[0].length;j++)
			{
				if(ATemp[i][j]!=0)
					ubTemp[j]=Integer.MAX_VALUE; //保证大于0即可
			}
		}
		
        System.out.println("精简之后的不等式为：");
        
	//重新打印一次约束不等式
		for(double[]a:GTemp)
		{
			String str="";
			for(int k=0;k<a.length;k++)
			{
				if(a[k]<0)
					str=str+a[k]+"*i"+k;  //系数*可能世界的标签
				else if(a[k]>0)
					str=str+"+"+a[k]+"*i"+k;  //系数*可能世界的标签	
			}
			str=str+"<=0";
			if(str.charAt(0)=='+')
			{
				str=str.replaceFirst("\\+", "");
				System.out.println(str);	
			}
			else if(str.equals("<=0"))
				continue;
			else
				System.out.println(str);	
		}
		
		System.out.println("精简之后的等式为：");
		//重新打印一次约束不等式
		int m=0;
		for(double[]a:ATemp)
		{
			String str="";
			for(int k=0;k<a.length;k++)
			{
				if(a[k]<0)
					str=str+a[k]+"*i"+k;  //系数*可能世界的标签
				else if(a[k]>0)
					str=str+"+"+a[k]+"*i"+k;  //系数*可能世界的标签				
			}
			str=str+"="+b[m];
			m++;
			if(str.charAt(0)=='+')
			{
				str=str.replaceFirst("\\+", "");
				System.out.println(str);	
			}
			else if(str.equals("=0.0"))
				continue;
			else
				System.out.println(str);		
		}
			
		String new_object="";
		for(int k=0;k<objectfunctionTemp.length;k++)
		{		
				if(objectfunctionTemp[k]==1.0)
					new_object=new_object+"+i"+k;  //系数*可能世界的标签
		}
		
	    if(new_object.equals(""))
			 System.out.println("精简之后的目标函数为: y 不存在可能世界与之对应");		
		else if(new_object.charAt(0)=='+')
		{
			new_object=new_object.replaceFirst("\\+", "");
			System.out.println("精简之后的目标函数为: y="+new_object);
		}	
		else
			System.out.println("精简之后的目标函数为: y="+new_object);
		
	    System.out.println("++++++++++++++++++++++");
	    

		LPOptimizationRequest or = new LPOptimizationRequest();

		or.setC(objectfunctionTemp);  //第二个bug因为处理
		or.setG(GTemp);
		or.setH(hTemp);	
		or.setA(ATemp);
		or.setB(bTemp);
		or.setLb(lbTemp);
		or.setUb(ubTemp);
		or.setDumpProblem(true); 
		
		LPPrimalDualMethod opt = new LPPrimalDualMethod();
		
		opt.setLPOptimizationRequest(or);
/*		try {
			opt.optimize();
		} catch (Exception e) {
			// TODO: handle exception
			if(e.getMessage().equals("Infeasible problem")); //捕获异常内容
				System.out.println("Infeasible problem");
		}	
		double[] sol_min = opt.getOptimizationResponse().getSolution();
		LinearMultivariateRealFunction objectiveFunction_min = new LinearMultivariateRealFunction(objectfunctionTemp, 0);		
		System.out.println("最大下界为:"+objectiveFunction_min.value(sol_min));*/
		
		//替换目标函数，求最大值
		for(int i=0;i<objectfunctionTemp.length;i++)
		{
			objectfunctionTemp[i]=-objectfunctionTemp[i];
		}
		//or.setC(max_c);
		or.setC(objectfunctionTemp);
		opt.setLPOptimizationRequest(or);
		try {
			opt.optimize();
		} catch (Exception e) {
			// TODO: handle exception
			if(e.getMessage().equals("Infeasible problem")); //捕获异常内容
				System.out.println("Infeasible problem");
		}	
		double[] sol_max = opt.getOptimizationResponse().getSolution();
		LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(max_cTemp, 0);	
		System.out.println("最小上界为:"+-objectiveFunction_max.value(sol_max));		
		//return (Double.parseDouble(entails[3])+objectiveFunction_max.value(sol_max));
		return -objectiveFunction_max.value(sol_max);
	}
	
	public double[] minimalChangeMappingOptimized(ArrayList<String>  mappings,String entailment) //得到entailmentMapping的上界
	{
		//添加mappings的约束
		ArrayList<double[]> inequalitiesTemp=(ArrayList<double[]>) inequalities.clone();
		ArrayList<double[]> equalitiesTemp=(ArrayList<double[]>) equalities.clone();
		for(int i=0;i<mappings.size();i++)
		{
			String parts[]=mappings.get(i).split(",");
			/*String head=parts[0];
			String body=parts[1];*/
			String body=parts[0];
			String head=parts[1];
			String relation=parts[2];
			Double lower=Double.parseDouble(parts[3]);		
			Double upper=Double.parseDouble(parts[4]);	
			double coefficient1[]=new double[numWorld];
			double coefficient2[]=new double[numWorld];
			if(relation.equals("|") )
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=-(1-lower);
							BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalitiesTemp.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal   b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")
									&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
								coefficient2[s] = -upper;
							} else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")
									&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) {
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient2);
					}
				}
		}
			else if(relation.equals("="))
			{
				if(lower==upper&&upper!=1.0&&lower!=0.0)  //点概率区间的情况,是将其加入到等式中
				{
					for(int s:possibleWordIndex.keySet())
					{
						if(possibleWordIndex.get(s).contains("-"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s]=lower;
						}
						else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
						{
							coefficient1[s] = lower;
						}
						else if(possibleWordIndex.get(s).contains("+"+"#"+head+"#")&&possibleWordIndex.get(s).contains("+"+"#"+body+"#"))
						{
							coefficient1[s] =-(1-lower);
							BigDecimal b  =   new   BigDecimal(lower-1);  //四舍五入的方式
							coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
						}			
					}
					//将不等式用等式的方式保存起来
					equalitiesTemp.add(coefficient1);
					//equalities_value.add(lower);
				}
				else   //非点概率区间的情况
				{
					if (lower > 0) // 按照约束1来生成公式
					{
						for (int s: possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							} 
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient1[s] = lower;
							}
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient1[s] =  - (1-lower);
								BigDecimal b   =   new   BigDecimal(lower-1);  //四舍五入的方式
								coefficient1[s] =   b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient1);
					}
					if (upper < 1) {
						for (int s : possibleWordIndex.keySet()) {
							if (possibleWordIndex.get(s).contains("-" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("-" +"#"+body+"#")) 
							{
								coefficient2[s] = -upper;
							}
							else if (possibleWordIndex.get(s).contains("+" +"#"+head+"#")&& possibleWordIndex.get(s).contains("+" +"#"+body+"#")) 
							{
								coefficient2[s] = 1 - upper;
								BigDecimal   b   =   new   BigDecimal( 1 - upper);  //四舍五入的方式
								coefficient2[s] =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
							}
						}
						inequalitiesTemp.add(coefficient2);
					}
				}
			}
		}
		System.out.println("++++++++++++++++++++++");
		
		//推理mapping的上界
		double[][] GTemp = new double[inequalitiesTemp.size()][numWorld];
		double[] hTemp = new double[inequalitiesTemp.size()];
		for(int i=0;i<inequalitiesTemp.size();i++)
		{
			GTemp[i]=inequalitiesTemp.get(i);
		}	
		//判断的时候就已经对等式进行了初始化了
		//等式的系数赋值，一般都是x0+...+xn=1
		double[][]ATemp=new double[1+equalities.size()][numWorld];
		for(int i=0;i<equalities.size();i++)  //如果之前出现lower=upper的情况将等式赋值
		{
			ATemp[i]=equalities.get(i);
		}		
		double[] bTemp = new double[1+equalities.size()] ;  //前面的equalities.size()-1项默认为0.0
		bTemp[equalities.size()]=1.0;
		
		//置信区间的推理
		double[]lbTemp=new double[numWorld];
		double[]ubTemp=new double[numWorld];  //这里的上界会发生改变。	
		double[]min_cTemp = new double[numWorld] ; //这里的上界会发生改变。	
		double[]max_cTemp = new double[numWorld] ;
		double[]objectfunctionTemp=new double[numWorld] ;
		
		String entails[]=entailment.split(",");	
		if(entails[2].equals("|"))
		{
			for (int k : possibleWordIndex.keySet()) 
			{
				//令entails[0]对应的可能世界
				if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#"))
				{
						ATemp[equalities.size()][k]=1.0;
				}
				if(possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#")&&possibleWordIndex.get(k).contains("+" +"#"+ entails[1]+"#"))
				{
						min_cTemp[k] = 1.0;
						max_cTemp[k] = -1.0;
				}
			}
		}
	    else if(entails[2].equals("="))
		{
			for (int k : possibleWordIndex.keySet()) 
			{
				//令entails[0]对应的可能世界
				if (possibleWordIndex.get(k).contains("+" +"#"+ entails[0]+"#")||possibleWordIndex.get(k).contains("+" +"#"+ entails[1]+"#"))
				{
						ATemp[equalities.size()][k]=1.0;
				}
				if (possibleWordIndex.get(k).contains("+"+"#" + entails[0]+"#")&&possibleWordIndex.get(k).contains("+" +"#"+ entails[1]+"#")) 				
				{
					min_cTemp[k] = 1.0;
					max_cTemp[k] = -1.0;
				}
			}
		}		
/*		String object="";
		for (int k = 0; k < max_cTemp.length; k++) {
			if (max_cTemp[k] == -1.0)
				object = object + "+i" + k; // 系数*可能世界的标签
		}
		if (object.charAt(0) == '+') {
			object = object.replaceFirst("\\+", "");
			System.out.println("目标函数为: y=" + object);
		} else
			System.out.println("目标函数为: y=" + object);	*/				
		
		objectfunctionTemp=min_cTemp.clone();	
			
		for(int i=0;i<GTemp.length;i++)  //不等式系数不为0.即上界大于0即可.
		{
			for(int j=0;j<GTemp[0].length;j++)
			{
				if(GTemp[i][j]!=0)
					ubTemp[j]=Integer.MAX_VALUE; //保证大于0即可
			}
		}
		
		for(int i=0;i<ATemp.length-1;i++)  //等式系数不为0.即上界大于0即可.
		{
			for(int j=0;j<ATemp[0].length;j++)
			{
				if(ATemp[i][j]!=0)
					ubTemp[j]=Integer.MAX_VALUE; //保证大于0即可
			}
		}
		
        System.out.println("精简之后的不等式为：");
        
	//重新打印一次约束不等式
		for(double[]a:GTemp)
		{
			String str="";
			for(int k=0;k<a.length;k++)
			{
				if(a[k]<0)
					str=str+a[k]+"*i"+k;  //系数*可能世界的标签
				else if(a[k]>0)
					str=str+"+"+a[k]+"*i"+k;  //系数*可能世界的标签	
			}
			str=str+"<=0";
			if(str.charAt(0)=='+')
			{
				str=str.replaceFirst("\\+", "");
				System.out.println(str);	
			}
			else if(str.equals("<=0"))
				continue;
			else
				System.out.println(str);	
		}
		
		System.out.println("精简之后的等式为：");
		//重新打印一次约束不等式
		int m=0;
		for(double[]a:ATemp)
		{
			String str="";
			for(int k=0;k<a.length;k++)
			{
				if(a[k]<0)
					str=str+a[k]+"*i"+k;  //系数*可能世界的标签
				else if(a[k]>0)
					str=str+"+"+a[k]+"*i"+k;  //系数*可能世界的标签				
			}
			str=str+"="+bTemp[m];
			m++;
			if(str.charAt(0)=='+')
			{
				str=str.replaceFirst("\\+", "");
				System.out.println(str);	
			}
			else if(str.equals("=0.0"))
				continue;
			else
				System.out.println(str);		
		}
			
		String new_object="";
		for(int k=0;k<objectfunctionTemp.length;k++)
		{		
				if(objectfunctionTemp[k]==1.0)
					new_object=new_object+"+i"+k;  //系数*可能世界的标签
		}
		
	    if(new_object.equals(""))
			 System.out.println("精简之后的目标函数为: y 不存在可能世界与之对应");		
		else if(new_object.charAt(0)=='+')
		{
			new_object=new_object.replaceFirst("\\+", "");
			System.out.println("精简之后的目标函数为: y="+new_object);
		}	
		else
			System.out.println("精简之后的目标函数为: y="+new_object);
		
	    System.out.println("++++++++++++++++++++++");
	    

		LPOptimizationRequest or = new LPOptimizationRequest();

		or.setC(objectfunctionTemp);  //第二个bug因为处理
		or.setG(GTemp);
		or.setH(hTemp);	
		or.setA(ATemp);
		or.setB(bTemp);
		or.setLb(lbTemp);
		or.setUb(ubTemp);
		or.setDumpProblem(true); 
		
		LPPrimalDualMethod opt = new LPPrimalDualMethod();
		double probabilityInterval[]=new double[2];
		opt.setLPOptimizationRequest(or);
		boolean incoherentFlag=false;
		try {
			opt.optimize();
			incoherentFlag=true;
		} catch (Exception e) {
			// TODO: handle exception
			if(e.getMessage().equals("Infeasible problem")); //捕获异常内容
				System.out.println("Infeasible problem");
			incoherentFlag=false;
		}	
		if(incoherentFlag)
		{
			double[] sol_min = opt.getOptimizationResponse().getSolution();
			LinearMultivariateRealFunction objectiveFunction_min = new LinearMultivariateRealFunction(
					objectfunctionTemp, 0);
			System.out.println("最大下界为:" + objectiveFunction_min.value(sol_min));
			probabilityInterval[0] = objectiveFunction_min.value(sol_min);

			// 替换目标函数，求最大值
			for (int i = 0; i < objectfunctionTemp.length; i++) {
				objectfunctionTemp[i] = -objectfunctionTemp[i];
			}
			// or.setC(max_c);
			or.setC(objectfunctionTemp);
			opt.setLPOptimizationRequest(or);
			try {
				opt.optimize();
			} catch (Exception e) {
				// TODO: handle exception
				if (e.getMessage().equals("Infeasible problem"))
					; // 捕获异常内容
				System.out.println("Infeasible problem");
			}
			double[] sol_max = opt.getOptimizationResponse().getSolution();
			LinearMultivariateRealFunction objectiveFunction_max = new LinearMultivariateRealFunction(max_cTemp, 0);
			System.out.println("最小上界为:" + -objectiveFunction_max.value(sol_max));
			// return
			// (Double.parseDouble(entails[3])+objectiveFunction_max.value(sol_max));
			probabilityInterval[1] = -objectiveFunction_max.value(sol_max);
			return probabilityInterval;
		}
		else {
			return probabilityInterval;
		}
	}
	
	
	public void PrintService()
	{
		System.out.println("精简之后的不等式为：");
		//重新打印一次约束不等式
		for(double[]a:G)
		{
			String str="";
			for(int k=0;k<a.length;k++)
			{
				if(a[k]<0)
					str=str+a[k]+"*i"+k;  //系数*可能世界的标签
				else if(a[k]>0)
					str=str+"+"+a[k]+"*i"+k;  //系数*可能世界的标签	
			}
			str=str+"<=0";
			if(str.charAt(0)=='+')
			{
				str=str.replaceFirst("\\+", "");
				System.out.println(str);	
			}
			else if(str.equals("<=0"))
				continue;
			else
				System.out.println(str);	
		}
		
		System.out.println("精简之后的等式为：");
		//重新打印一次约束不等式
		int m=0;
		for(double[]a:A)
		{
			String str="";
			for(int k=0;k<a.length;k++)
			{
				if(a[k]<0)
					str=str+a[k]+"*i"+k;  //系数*可能世界的标签
				else if(a[k]>0)
					str=str+"+"+a[k]+"*i"+k;  //系数*可能世界的标签				
			}
			str=str+"="+b[m];
			m++;
			if(str.charAt(0)=='+')
			{
				str=str.replaceFirst("\\+", "");
				System.out.println(str);	
			}
			else if(str.equals("=0.0"))
				continue;
			else
				System.out.println(str);		
		}
		String new_object="";
		
		//根据可能世界进一步进行约简
		/*for(int i=0;i<optimizeObject.length;i++)
		{
			 if(!possibleWordIndex.keySet().contains(i+""))
				 optimizeObject[i]= 0.0;
		}*/
		for(int k=0;k<optimizeObject.length;k++)
		{		
				if(optimizeObject[k]==1.0)
					new_object=new_object+"+i"+k;  //系数*可能世界的标签
		}
	    if(new_object.equals(""))
			 System.out.println("精简之后的目标函数为: y 不存在可能世界与之对应");		
		else if(new_object.charAt(0)=='+')
		{
			new_object=new_object.replaceFirst("\\+", "");
			System.out.println("精简之后的目标函数为: y="+new_object);
		}	
		else
			System.out.println("精简之后的目标函数为: y="+new_object);	
	}
	
	public void addMapping(String[] maps) 
	{
		for (int i =0;i<maps.length;i++)  //将mappings转换成链表存储
		{
			initialMappings.add(maps[i]);
		}	
	}
	
	public void addMapping(ArrayList<String> maps) 
	{	
		for (int i =0;i<maps.size();i++)  //将mappings转换成链表存储
		{
			initialMappings.add(maps.get(i));
		}	
	}
	
	public ArrayList<String> getMappings() 
	{
			return initialMappings;	
	}
	
	public ArrayList<String> getHiddenMappings() 
	{
			return hiddenMappings;	
	}
	
	public ArrayList<ArrayList<String>> getMinmalconflictSet() 
	{
			return minmalConflictSets;	
	}
	
	public HashMap<Integer,String> possibleWordIndex() 
	{
			return possibleWordIndex;	
	}
	
	public ArrayList<double[]> getInequalities()
	{
		return inequalities;
	}
	
	public HashMap<Integer, String>getPossibleWordIndex()
	{		
			return possibleWordIndex;
	}
	
	public void printMinmalconflictmappingSet() 
	{
		System.out.println("+++++++++++");
		for (String a: minmalConflictMappingSets)
		{
			System.out.println(a);
		}
	}
	
	public void printMinmalconflictSet() 
	{
		System.out.println("+++++++++++");
		for (ArrayList<String> set: minmalConflictSets)
		{
			for (String a:set)
			{
				System.out.print(a+"  ");			
			}
			System.out.println();
		}
	}
	
	public void printRevisedMapppingSet() 
	{
		System.out.println("+++++++++++");
		for (int i =0;i<initialMappings.size();i++)  //将mappings转换成链表存储
		{
			System.out.println(initialMappings.get(i));
		}	
	}
	
	

}
