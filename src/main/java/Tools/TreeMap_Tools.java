package Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;



public class TreeMap_Tools  {
    /**
     * @param args
     * �����Ҫ������д��toString����TreeMap����ʹ��
     */
	 //��Ϊ���﷽���õ�����m ���ж���ȫ�ֵ�
//	static  Map<String, ArrayList<String>> m = new TreeMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> m = new TreeMap<String, ArrayList<String>>();
/*	static Map<String, ArrayList<String>> Ontology1_hasSub = new TreeMap<String, ArrayList<String>>();
	static Map<String, ArrayList<String>> Ontology2_hasSub = new TreeMap<String, ArrayList<String>>();*/
	
	public  void putAdd(String sr, String[] s) {
		if (!m.containsKey(sr)) 
		{
			m.put(sr, new ArrayList<String>());
		}
		for (int i = 0; i < s.length; i++) 
		{
			if(!m.get(sr).contains(s[i].toLowerCase()))//��֤���ظ�
				m.get(sr).add(s[i].toLowerCase());
		}
	}
	
	public  void putAdd(String sr, String s) 
	{
		if (!m.containsKey(sr)) 
			m.put(sr, new ArrayList<String>());
		if(!m.get(sr).contains(s.toLowerCase()))//��֤���ظ�
			m.get(sr).add(s.toLowerCase());
	}
	
	public  void filter(ArrayList<String> object1,  ArrayList<String> object2) 
	{
		for(String ob1:object1)
		{
			if(m.containsKey(ob1.toLowerCase()))
				m.remove(ob1.toLowerCase());
		}
		
		for(String ob2:object2)
		{
			if(m.containsKey(ob2.toLowerCase()))
				m.remove(ob2.toLowerCase());
		}
	

	}
	
	public  void putAdd(String sr, ArrayList<String> s) //ȷ������һ��û�е������
	{
		/*if (!m.containsKey(sr)) 
			m.put(sr, s);*/
		if (!m.containsKey(sr)) 
		{
			m.put(sr, new ArrayList<String>());
		}
		for(int i=0;i<s.size();i++)
		{
			if(!m.get(sr).contains(s.get(i).toLowerCase()))
					m.get(sr).add(s.get(i).toLowerCase());
		}
	}
	
	
	//���ﴫ3������ �������еĸ���1�������еĸ���2 �Լ���Ӧ��Ҫ������TreeMap
	public boolean has_relation(String x1,String x2)
	{
		ArrayList<String> Value=m.get(x1);
		if(Value!=null&&Value.contains(x2))
			return true;
		else
			return false;      
	}
	
	//���������Ҫ��Ϊ�˹���һЩԤ�ȱ��ж���Map��Nomap�ڵ�(����Map�ڵ��Ǿ��Կ��ŵ�)
	public boolean Is_Mapped(String concept)
	{
		ArrayList<String> O1_Mapped=GetKey();
		for(int i=0;i<O1_Mapped.size();i++)
		{
			//��ʹ��O2�еĸ���1�Զ�����Ҳ��O1���Ѿ�������
			//�ж��Ƿ��뱾��1����ƥ��ĸ����غ���
			if(concept.equals(O1_Mapped.get(i)))
			{
				return true;
			}
			ArrayList<String> O2_Mapped=GetKey_Value(O1_Mapped.get(i));
			for(int j=0;j<O2_Mapped.size();j++)
			{
				//�ж��Ƿ��뱾��2����ƥ��ĸ����غ���
				if(concept.equals(O2_Mapped.get(j)))
				{
					return true;
				}			
			}
		}
		//���������򷵻�false
		return false;
	}
	
	//����Ҫ��ӡ�� TreeMap
	public  ArrayList<String> Print_Value()
	{	
		 ArrayList<String> result=new  ArrayList<String>();
		//Print_Value2(m.keySet());
		Iterable<String> iterable=m.keySet();
		Iterator<String> it = iterable.iterator();
		while(it.hasNext())  
		{
		     String key=it.next();
		     ArrayList<String> value=m.get(key);
		     //System.out.printf("(��Ϊ:%s,ֵΪ:%s)%n",key,value);
		     System.out.printf("%s,%s %n",key,value);
		     result.add(key+","+value);
		}
		return result;
	}
	
	
	public Map<String, ArrayList<String>> GetMap()
	{
		return m;    //��ȡMap����Ϣ
	}
	
	public int size()
	{
		return m.size();
	}
	
	public  ArrayList<String> GetKey() //��ȡMap�����м�ֵ�����ص���һ������
	{
		ArrayList<String> Key=new ArrayList<String>();
		Iterable<String> iterable=m.keySet();
		Iterator<String> it = iterable.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Key.add(key);
		}		
		return Key;    //��ȡMap����Ϣ
	}
	
	public  ArrayList<String> GetValue() //��ȡMap�����м����valueֵ�����ص���һ������
	{
		ArrayList<String> Value=new ArrayList<String>();
		Iterable<String> iterable=m.keySet();
		Iterator<String> it = iterable.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			ArrayList<String> values=GetKey_Value(key);
			for(int i=0;i<values.size();i++)
			{
				//System.out.println(!Value.contains(values.get(i)));
				if(!Value.contains(values.get(i)))
					Value.add(values.get(i));
			}
		}		
		return Value;    //��ȡMap����Ϣ
	}
	
	public ArrayList<String> GetKey_Value(String key)
	{
		return m.get(key);
	}
	
	
	public ArrayList<String> GetValue_Key(String value)
	{
		ArrayList<String> key=new ArrayList<String>();
		
		ArrayList<String> Keys=GetKey();
		for(int i=0;i<Keys.size();i++)
		{
			ArrayList<String> Value=GetKey_Value(Keys.get(i));
			if(Value.contains(value))
				key.add(Keys.get(i));
		}
		return key;    //��ȡMap����Ϣ
	}
	
	public int getNumberOfMap()
	{
		int num=0;
		ArrayList<String> keySet= GetKey();
		for(int i=0;i<keySet.size();i++)
		{
			if(m.get(keySet.get(i)).size()==1)
				num++;
			else
				num=num+m.get(keySet.get(i)).size();
		}
		return num;
	}
	
	public void Modified_Match()
	{
		//ArrayList<String> map=m;
		//����һ���µ�TreeMap���洢�ṹ
		Map<String, ArrayList<String>> new_Match = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> Key_medium=new ArrayList<String>();
/*		Key_medium.add("Thing2");
		new_Match.put("Thing1", Key_medium);*///������˵����1�е�Thing�뱾��2�е�Thing�ǲ�����ƥ���ϵ��
		ArrayList<String> Key_set=GetKey();
		for(int i=0;i<Key_set.size();i++)
		{
			String Key=Key_set.get(i);
			ArrayList<String> Value=GetKey_Value(Key);
			ArrayList<String> New_Value=new ArrayList<String>();
			for(int j=0;j<Value.size();j++)
			{
				//System.out.println(Value.get(j));
				//String value=Value.get(j)+'1';
				String value=Value.get(j);//����2�еĸ���üӱ�ǩ
				New_Value.add(value+"2");
			}
			new_Match.put(Key+"1", New_Value);
		}
		
		m.clear();
		m=new_Match;		
		//�������
  		Iterable<String> iterable=m.keySet();
		Iterator<String> it = iterable.iterator();
		while(it.hasNext())  
		{
		     String key=it.next();
		     ArrayList<String> value=m.get(key);
		     System.out.printf("(��Ϊ:%s,ֵΪ:%s)%n",key,value);
		}
	}
	
	public void Modified_HasSub(String label) //�����label��Ҫ�Ǳ�ע�������ڱ���1���Ǳ���2
	{
		//ArrayList<String> map=m;
		//����һ���µ�TreeMap���洢�ṹ
		Map<String, ArrayList<String>> new_Match = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> Key_set=GetKey();
		for(int i=0;i<Key_set.size();i++)
		{
			String Key=Key_set.get(i);
			ArrayList<String> Value=GetKey_Value(Key);
			ArrayList<String> New_Value=new ArrayList<String>();
			for(int j=0;j<Value.size();j++)
			{
				//System.out.println(Value.get(j));
				//String value=Value.get(j)+'1';
				String value=Value.get(j)+label;//����2�еĸ���üӱ�ǩ
				New_Value.add(value);
			}
			new_Match.put(Key+label, New_Value);
		}
		m.clear();
		m=new_Match;
		
		//�������
  		Iterable<String> iterable=m.keySet();
		Iterator<String> it = iterable.iterator();
		while(it.hasNext())  
		{
		     String key=it.next();
		     ArrayList<String> value=m.get(key);
		     System.out.printf("(��Ϊ:%s,ֵΪ:%s)%n",key,value);
		}
	}

	
	
	public TreeMap_Tools(String path) throws IOException
	{	
		BufferedReader  Ontology_match_information = new BufferedReader (new FileReader(new File(path)));
		/*BufferedReader  Ontology1_sub_information = new BufferedReader (new FileReader(new File("Data/Ontology1_HasSub.txt")));
		BufferedReader  Ontology2_sub_information = new BufferedReader (new FileReader(new File("Data/Ontology2_HasSub.txt")));*/
		String lineTxt = null;
		  while ((lineTxt = Ontology_match_information.readLine()) != null) {
				String line = lineTxt.trim(); //ȥ���ַ�����λ�Ŀո񣬱�����ո���ɵĴ���		
				String[] part = line.split("--"); //��"--"������key��Value�ķָ�			
				String[] value = part[1].split(",");
				for(int i=0;i<value.length;i++)
				{
					value[i]=value[i].toLowerCase();
				}
				putAdd(part[0].toLowerCase(), value);	
			}
/*		  while ((lineTxt = Ontology1_sub_information.readLine()) != null) {
				String line = lineTxt.trim(); //ȥ���ַ�����λ�Ŀո񣬱�����ո���ɵĴ���		
				String[] part = line.split("--"); //��"--"������key��Value�ķָ�			
				String[] value = part[1].split(",");
				putAdd_O1_Sub(part[0], value);	
			} 
		  while ((lineTxt = Ontology2_sub_information.readLine()) != null) {
				String line = lineTxt.trim(); //ȥ���ַ�����λ�Ŀո񣬱�����ո���ɵĴ���		
				String[] part = line.split("--"); //��"--"������key��Value�ķָ�			
				String[] value = part[1].split(",");
				putAdd_O2_Sub(part[0], value);	
			} */
		 // System.out.println("the TreeMap has been constructed.");
		  
		    //�ó��淽ʽ��ӡ���
/*			System.out.println("�����ʽ2");
			Print_Value(Mapping_information.keySet());*/
			//���ж϶�Ӧ�ļ����Ƿ������Ԫ��
/*			String concept1_1="Form";
			String concept1_2="Author_information_form";
			boolean label=Is_subclassof(concept1_1,concept1_2,Mapping_information.keySet()); //�ж�Map�е�ĳ��Ԫ���Ƿ������Ӧ�Ĺ�ϵ������Ĺ�ϵ��������Ϊ������ϵ��
			System.out.println(label);*/
	}
	
	public TreeMap_Tools(ArrayList<String> array) 
	{		
		for (int i = 0; i < array.size(); i++) 
		{
			//System.out.println(array);
			String line = array.get(i).trim(); // ȥ���ַ�����λ�Ŀո񣬱�����ո���ɵĴ���
			String[] part = line.split("--"); // ��"--"������key��Value�ķָ�
			String[] value = part[1].split(",");
			/*for (int j = 0; j < value.length; j++) 
			{
				value[j] = value[j].toLowerCase();
			}*/
			putAdd(part[0].toLowerCase(), value);
		}
		  //System.out.println("the TreeMap has been constructed.");	
	}
	
	public TreeMap_Tools() 
	{		
		  //System.out.println("the TreeMap has been constructed.");	
	}

	public void remove(String object1,String object2)
	{
		ArrayList<String> objectValue=GetKey_Value(object1);
		objectValue.remove(object2);
		if(objectValue.size()==0)
			m.remove(object1);
	}
	
	public void clear()
	{
		m.clear();
	}

	public static void main(String[] args)throws IOException {	
	// BufferedReader  sub_information = new BufferedReader (new FileReader(new File("src/excise/MAP/test.txt")));
	// BufferedReader  match_information = new BufferedReader (new FileReader(new File("src/excise/MAP/Ontology1_match.txt")));
	// BufferedReader  Ontology1_hasSub = new BufferedReader (new FileReader(new File("src/excise/MAP/Ontology1_HasSub.txt")));
      BufferedReader  Ontology2_hasSub = new BufferedReader (new FileReader(new File("Data/Ontology2_HasSub.txt")));
	  String lineTxt = null;
	  TreeMap_Tools a=new TreeMap_Tools("Data/Ontology2_HasSub.txt");
	  while ((lineTxt = Ontology2_hasSub.readLine()) != null) {
			// System.out.println(lineTxt);
			String line = lineTxt.trim(); //ȥ���ַ�����λ�Ŀո񣬱�����ո���ɵĴ���		
			String[] part = line.split("--"); //��"--"������key��Value�ķָ�		
			String[] value = part[1].split(",");
			a.putAdd(part[0], value);	//ʵ����֮�󼴿ɵ���������еĺ�����������Ҫ��̬���庯���������ݽṹ
		}  
	    //�ó��淽ʽ��ӡ���
		System.out.println("�����ʽ2");
		a.Print_Value();
		//���ж϶�Ӧ�ļ����Ƿ������Ԫ��
		String concept1_1="Form";
		String concept1_2="Author_information_form";
		boolean label=a.has_relation(concept1_1, concept1_2); //�ж�Map�е�ĳ��Ԫ���Ƿ������Ӧ�Ĺ�ϵ������Ĺ�ϵ��������Ϊ������ϵ��
		System.out.println(label);
	
/*	  Iterator<String> it=m.keySet().iterator();//����hashmap�ļ�ֵ����������
	   while(it.hasNext()){
	     String key=(String)it.next();
	     ArrayList<String> value=m.get(key);
	     System.out.println(key+"--"+value);
	   }*/
	
    }
}