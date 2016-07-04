package pokemon.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pokemon.p1.Pokemon.PType;

public class TeamBuilder {
	private HashMap<Integer,Pokemon> DEX;
	private HashMap<Integer,Move> MOVES;
	private HashMap<PType,HashMap<PType,Double>> TYPECHECK;
	private ArrayList<HashMap<PType, Double>> TYPECHART;
	private Pokemon[] TEAM = new Pokemon[6];

	private static TeamBuilder instance = new TeamBuilder();
	public static TeamBuilder getInstance(){
		return instance;
	}

	public void initTeamBuilder(HashMap<Integer,Pokemon> dex, HashMap<Integer,Move> moves, HashMap<PType,HashMap<PType,Double>> typecheck){
		this.DEX = dex;
		this.MOVES = moves;
		this.TYPECHECK = typecheck;
		for (Pokemon p: dex.values()){
			this.TYPECHART = createTypeChart(TYPECHECK, p.getType());
			p.setAllRelations(TYPECHART);
			p.setMoves(moves);
		}
	}
	/***
	 * filter out pokemon that dont fit any of the criteria
	 * if sorting by defense, all defense above 120 then sorted by pokerank for that list
	 * pokemon of 1 color sorted by pokerank in another list
	 * make a combined list with formula weighting the # of criteria they fill + pokerank or place in previous list?
	 * choose potentially one at a time then determine which 2 get next

	 * **/

	//check vulnerabilities, return array of vulnerable things or score ??, tell u which to change maybe

	public HashMap<PType, Double> initBalance(){
		PType type = null;
		HashMap<PType, Double> balance = new HashMap<PType, Double>();
		for(int i=1;i<=18;i++){
			switch(i){
			case 1: type = PType.NORMAL;
			break;
			case 2: type = PType.FIGHTING;
			break;
			case 3: type = PType.FLYING;
			break;
			case 4: type = PType.POISON;
			break;
			case 5: type = PType.GROUND;
			break;
			case 6: type = PType.ROCK;
			break;
			case 7: type = PType.BUG;
			break;
			case 8: type = PType.GHOST;
			break;
			case 9: type = PType.STEEL;
			break;
			case 10: type = PType.FIRE;
			break;
			case 11: type = PType.WATER;
			break;
			case 12: type = PType.GRASS;
			break;
			case 13: type = PType.ELECTRIC;
			break;
			case 14: type = PType.PSYCHIC;
			break;
			case 15: type = PType.ICE;
			break;
			case 16: type = PType.DRAGON;
			break;
			case 17: type = PType.DARK;
			break;
			case 18: type = PType.FAIRY;
			break;
			}
			balance.put(type, (double) 0);
		}
		return balance;
	}

	//check if a potential pokemon to be added would better or worsen the team balance
	public int checkBal(HashMap<PType, Double> balance, Pokemon p){
		double initBal = 0.0;
		for(PType pt: balance.keySet()){
			initBal += balance.get(pt);
		}
		double newBal = 0.0;
		for(PType weak: p.getWeaknesses().keySet()){
			double bal = balance.get(weak);
			if(p.getWeaknesses().get(weak) == 0){
				//bal -= 2;
			}else{
				bal -= Math.pow(p.getWeaknesses().get(weak),-1);
			}
			balance.put(weak, bal);	
		}
		for(PType weak: p.getStrengths().keySet()){
			double bal = balance.get(weak);
			if(p.getStrengths().get(weak) == 0){
				bal += 1;
			}else{
				bal += p.getStrengths().get(weak);
			}
			balance.put(weak, bal);
		}

		for(PType pt: balance.keySet()){
			if(balance.get(pt) < -4){
				return 0;
			}
			newBal += balance.get(pt);
		}

		if(newBal > initBal){
			return 1;
		}else{
			return 0;
		}
	}

	public boolean arrContains(Pokemon[]arr, Pokemon p){
		for(Pokemon pp: arr){
			if(pp == p){
				return true;
			}
		}
		return false;
	}

	public void determine(List<Entry<Integer, Pokemon>> critList){
		Pokemon[] team = new Pokemon[6];
		HashMap<PType, Double> balance = initBalance();
		System.out.println(critList.size());

		//add first pokemon from list to team, init the balance
		team[0] = critList.get(0).getValue();
		critList.remove(0);
		for(PType weak: team[0].getWeaknesses().keySet()){
			double bal = balance.get(weak);
			if(team[0].getWeaknesses().get(weak) == 0){
				//bal -= 2;
			}else{
				bal -= Math.pow(team[0].getWeaknesses().get(weak),-1);
			}
			balance.put(weak, bal);
		}
		for(PType weak: team[0].getStrengths().keySet()){
			double bal = balance.get(weak);
			bal += team[0].getStrengths().get(weak);
			balance.put(weak, bal);
		}

		System.out.println(balance.toString());

		//select rest of team
		for(int i=1; i<=5;i++){
			for(Map.Entry<Integer, Pokemon> entry: critList){
				if(!arrContains(team,entry.getValue())){ //if it hasn't already been chosen
					if(checkBal(balance,entry.getValue()) == 1){ //if it improves the team balance, add
						team[i] = entry.getValue();

						for(PType weak: team[i].getWeaknesses().keySet()){
							double bal = balance.get(weak);
							if(team[i].getWeaknesses().get(weak) == 0){
								//bal -= 2;
							}else{
								bal -= Math.pow(team[i].getWeaknesses().get(weak),-1);
							}
							balance.put(weak, bal);
						}
						for(PType weak: team[i].getStrengths().keySet()){
							double bal = balance.get(weak);
							bal += team[i].getStrengths().get(weak);
							balance.put(weak, bal);
						}
						break;
					}
				}
			}
			critList.remove(team[i]);
		}


		for(Pokemon m: team){
			System.out.println(m.getName());
		}
		double retbal=0;
		for(PType pt: balance.keySet()){
			retbal += balance.get(pt);
		}
		System.out.println(retbal);
		System.out.println(balance.toString());


	}


	public void determine2(List<Entry<Integer, Pokemon>> critList){
		Pokemon[] team = new Pokemon[6];
		HashMap<PType, Double> balance = initBalance();

		//add first pokemon from list to team, init the balance
		team[0] = critList.get(0).getValue();
		critList.remove(0);
		for(PType weak: team[0].getWeaknesses().keySet()){
			double bal = balance.get(weak);
			if(team[0].getWeaknesses().get(weak) == 0){
				//bal -= 2;
			}else{
				bal -= Math.pow(team[0].getWeaknesses().get(weak),-1);
			}
			balance.put(weak, bal);
		}
		for(PType weak: team[0].getStrengths().keySet()){
			double bal = balance.get(weak);

			bal += team[0].getStrengths().get(weak);

			balance.put(weak, bal);
		}

		//select rest of team
		for(int i=1; i<=5;i++){
			for(Map.Entry<Integer, Pokemon> entry: critList){
				if(!arrContains(team,entry.getValue())){ //if it hasn't already been chosen
//					if(checkBal(balance,entry.getValue()) == 1){ //if it improves the team balance, add
					team[i] = entry.getValue();

					for(PType weak: team[i].getWeaknesses().keySet()){
						double bal = balance.get(weak);
						if(team[i].getWeaknesses().get(weak) == 0){
							//bal -= 2;
						}else{
							bal -= Math.pow(team[i].getWeaknesses().get(weak),-1);
						}
						balance.put(weak, bal);
					}
					for(PType stren: team[i].getStrengths().keySet()){
						double bal = balance.get(stren);
						bal += team[i].getStrengths().get(stren);
						balance.put(stren, bal);
					}
					break;
//					}
				}
			}
			//critList.remove(team[i]);
		}

		for(int i = 0; i<critList.size(); i++){
			double curBal = balVal(balance);
			double curr = curBal;
			int index = 0;
			for(int j = 0; j<team.length;j++){
				Pokemon [] t = team.clone();
				if(!arrContains(t,critList.get(i).getValue())){
					t[j] = critList.get(i).getValue();
					if(recheck(t)>curr){
						index = j;
						curr = recheck(t);
					}
				}
			}
			if(curr > curBal){
				team[index] = critList.get(i).getValue();
				for(PType weak: team[index].getWeaknesses().keySet()){
					double bal = balance.get(weak);
					if(team[index].getWeaknesses().get(weak) == 0){
						//bal -= 2;
					}else{
						bal -= Math.pow(team[index].getWeaknesses().get(weak),-1);
					}
					balance.put(weak, bal);
				}
				for(PType weak: team[index].getStrengths().keySet()){
					double bal = balance.get(weak);
					if(team[index].getStrengths().get(weak) == 0){
						bal += 1;
					}else{
						bal += team[index].getStrengths().get(weak);
					}
					bal += team[index].getStrengths().get(weak);
					balance.put(weak, bal);
				}
			}
		}
		
		System.out.println("Here's your team!");
		int t = 1;
		for(Pokemon m: team){
			System.out.println("Slot "+t+": "+ m.getName());
			t++;
		}
		System.out.println("Your team's collective balance (positive means more pokemon are strong to the type than weak to it & vice versa):");
		System.out.println(balance.toString());
		System.out.println("Your team's balance score: "+ balVal(balance));



	}


	//get balance number val
	public double balVal(HashMap<PType,Double> balance){
		double retbal=0;
		for(PType pt: balance.keySet()){
			retbal += balance.get(pt);
		}
		return retbal;
	}
	//Ranking algorithm generates list of new rankings based on how well they fit the criteria
	//based on: color, stat, type,
	public List<Entry<Integer, Pokemon>> rankPokemon(SortParam sortparam){
		HashMap<Integer,Pokemon> criteriaList = new HashMap<Integer,Pokemon>();
		List<Entry<Integer, Pokemon>> list = new ArrayList<Entry<Integer, Pokemon>>();

		double tempScore = 0;
		int criteriaMet = 0;
		int numCriteria = sortparam.numParam();
		String color = sortparam.getColor();
		PType type = sortparam.getPt();
		String stat = sortparam.getStat();
		//stat?
		for(Pokemon p: DEX.values()){
			if(numCriteria == 0){ //if no specific criteria input, just base it on pokerank
				tempScore = p.pokeRank();
				criteriaList.put((int) tempScore, p);
			} else if((p.getTotalStat() < 580 && sortparam.isLegendaries() == false) || sortparam.isLegendaries() == true){
				criteriaMet = 0; 
				if(color != null){
					if(p.getColor().equals(color)){
						criteriaMet +=1;
					}
				}
				if(type != null){
					for(PType pt: p.getType()){
						if(pt == type){
							criteriaMet +=1;
						}
					}
				}
				
				if(stat != null){
					if(stat.equals("HP")){
						if(p.getHp()/80 > 1){
							System.out.println(p.getName()+" AAA");
							criteriaMet++;
						}else{
							System.out.println(p.getName()+" BBB");

							criteriaMet+= 1.0*p.getHp()/80;
						}
						//criteriaMet+= 1.0*p.getHp()/120;
					} else if (stat.equals("ATTACK")){
						criteriaMet+= 1.0*p.getAttack()/120;
					} else if (stat.equals("DEFENSE")){
						criteriaMet+= 1.0*p.getDefense()/120;
					} else if (stat.equals("SPATTACK")){
						criteriaMet+= 1.0*p.getSpAtk()/120;
					} else if (stat.equals("SPDEFENSE")){
						criteriaMet+= 1.0*p.getSpDef()/120;
					} else if (stat.equals("SPEED")){
						criteriaMet+= 1.0*p.getSpeed()/110;
					} else if (stat.equals("TOTAL")){
						criteriaMet+= 1.0*p.getTotalStat()/500;
					}
					
				}
				
				if(criteriaMet > 0){
//					if(p.getTotalStat() <580){
//						tempScore = ((1.0*criteriaMet)/numCriteria*1.0)*.35 + p.pokeRank()*.65;
//						criteriaList.put((int) tempScore, p);
//					}
					tempScore = ((1.0*criteriaMet)/numCriteria*1.0)*.35 + p.pokeRank()*.65;
					criteriaList.put((int) tempScore, p);
				}
			}
			
			


			Set<Entry<Integer, Pokemon>> set = criteriaList.entrySet();
			list = new ArrayList<Entry<Integer, Pokemon>>(set);

			Collections.sort( list, new Comparator<Map.Entry<Integer, Pokemon>>()
			{@Override
				public int compare(Entry<Integer, Pokemon> o1, Entry<Integer, Pokemon> o2) {
					if(o1.getKey() == o2.getKey())
						return 0;
					return o1.getKey() > o2.getKey() ? -1 : 1;
				}
			} );
			
			for (Map.Entry<Integer, Pokemon> entry: list){
				//System.out.println(entry.getValue().getName()+" "+entry.getKey()+" "+entry.getValue().pokeRank());
			}


		}



		return list;

	}

	public void test(List<Entry<Integer, Pokemon>> critList){
		double maxBal = -1000;
		Pokemon[] team = new Pokemon[6];
		boolean bool = false;
		while(bool != true){
			for(int i = 0; i < critList.size()-5;i++){
				for(int j = i+1; j<critList.size()-4;j++){
					for(int k = j+1; k<critList.size()-3;k++){
						for(int l = k+1; l < critList.size()-2;l++){
							for(int m = l+1; m< critList.size()-1;m++){
								for(int n = m+1; n < critList.size();n++){
									Pokemon[] t = {critList.get(i).getValue(),
											critList.get(j).getValue(),
											critList.get(k).getValue(),
											critList.get(l).getValue(),
											critList.get(m).getValue(),
											critList.get(n).getValue()};
									double bal = recheck(t);
									if(bal > maxBal){
										System.out.println(bal);
										maxBal = bal;
										team = t;
										if(bal >40){
											for(Pokemon pp: t){
												System.out.print(pp.getName()+" ");
											}
										}
									}

								}
							}
						}
					}
				}
			}
		}

		System.out.println(team.toString()+ " "+ maxBal);
	}


	public double recheck(Pokemon[] team){
		HashMap<PType,Double>balance = initBalance();
		double newBal = 0.0;
		for(Pokemon p: team){
			for(PType weak: p.getWeaknesses().keySet()){
				double bal = balance.get(weak);
				if(p.getWeaknesses().get(weak) == 0){
					//bal -= 2;
				}else{
					bal -= Math.pow(p.getWeaknesses().get(weak),-1);
				}
				balance.put(weak, bal);	
			}
			for(PType weak: p.getStrengths().keySet()){
				double bal = balance.get(weak);
				if(p.getStrengths().get(weak) == 0){
					bal += 1;
				}else{
					bal += p.getStrengths().get(weak);
				}
				balance.put(weak, bal);
			}

		}
		for(PType pt: balance.keySet()){
			if(balance.get(pt) < -5){
				newBal -= 100;
			}
			newBal += balance.get(pt);
		}
		return newBal;
	}

	public ArrayList<HashMap<PType, Double>> createTypeChart(HashMap<PType,HashMap<PType,Double>> typeCheck,PType[] type){
		ArrayList<HashMap<PType,Double>> returnlist = new ArrayList<HashMap<PType,Double>>();
		HashMap<PType, Double> weaknesses = new HashMap<PType, Double>();
		HashMap<PType,Double> strengths = new HashMap<PType, Double>();
		for(PType t: type){
			//if other types work greater than 1 on this type, then add
			for(PType t3: typeCheck.keySet()){

				if(typeCheck.get(t3).get(t) > 1.0){
					if(weaknesses.containsKey(t3)){
						double x = weaknesses.get(t3)*typeCheck.get(t3).get(t);
						weaknesses.put(t3, x);
					}else if(strengths.containsKey(t3)){
						strengths.remove(t3);
					}else{
						weaknesses.put(t3, typeCheck.get(t3).get(t));
					}
				}else if(typeCheck.get(t3).get(t) == 0){
					strengths.put(t3, 0.0);
				}else if(typeCheck.get(t3).get(t) < 1.0 && typeCheck.get(t3).get(t)!= 0){
					if(strengths.containsKey(t3)){
						double x = strengths.get(t3)*typeCheck.get(t3).get(t);
						strengths.put(t3, x);
					}else if(weaknesses.containsKey(t3)){
						weaknesses.remove(t3);
					}else{
						strengths.put(t3, typeCheck.get(t3).get(t));
					}
				}
			}
		}
		returnlist.add(strengths);
		returnlist.add(weaknesses);
		return returnlist;
	}

}
