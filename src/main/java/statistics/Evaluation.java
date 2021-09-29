package statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Evaluation {
	int numOfRulesGold=0;
	int numOfRulesMatcher=0;
	int numOfRulesCorrect=0;
	ArrayList<String> correctMappings=new ArrayList<String>();
	ArrayList<String> wrongMappings=new ArrayList<String>();
	//ArrayList<String> unknowMappings=new ArrayList<String>();
	public Evaluation(ArrayList<String> mapping, ArrayList<String> reference) {
		// Mapping correct = reference.getIntersection(mapping); 
		ArrayList<String> correct=new ArrayList<String>();
		int subNumber=0;
		for (String r : reference) 
		{
			String rParts[]=r.split(",");
			for (String m : mapping) 
			{
				String mParts[]=m.split(",");
				if (rParts[0].equals(mParts[0])&& rParts[1].equals(mParts[1])&&mParts[2].equals("=")) 
				{
					correctMappings.add(m);
					correct.add(r);
				}
				if(mParts[2].equals("sub"))
					subNumber++;
			}
		}		
		this.numOfRulesGold = reference.size();
		this.numOfRulesMatcher = mapping.size()-subNumber/reference.size();
		this.numOfRulesCorrect = correct.size();
	
		wrongMappings.addAll(mapping);	
		wrongMappings.removeAll(correctMappings);
	}	
	
	public String toShortDesc() {
		double precision = this.getPrecision();
		double recall = this.getRecall();
		double f = this.getFMeasure();

		return toDecimalFormat(precision) + "\t" + toDecimalFormat(recall) + "\t" + toDecimalFormat(f);
	}
	
	public double getPrecision() {
		return (double)this.numOfRulesCorrect /  (double)this.numOfRulesMatcher;
	}
	
	public double getRecall() {
		return (double)this.numOfRulesCorrect /  (double)this.numOfRulesGold;
	}
	
	public double getFMeasure() {
		if ((this.getPrecision() == 0.0f) || (this.getRecall() == 0.0f)) { return 0.0f; }
		return (2 * this.getPrecision() * this.getRecall()) / (this.getPrecision() + this.getRecall());
	}
	
	public int getMatcherAlignment() {
		return numOfRulesMatcher;
	}
	
	public int getCorrectAlignment() {
		return numOfRulesCorrect;
	}
	
	public ArrayList<String> getCorrectMapping() {
		return correctMappings;
	}
	
	public ArrayList<String> getWrongMapping() {
		return wrongMappings;
	}
	
	private static String toDecimalFormat(double precision) {
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(precision).replace(',', '.');
	}
		
}
