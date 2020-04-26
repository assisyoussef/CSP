import java.util.Arrays;
import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.variables.IntVar;

public class SolveProblem {

	public static int somme(int []tab) {
		int s=0;
		for(int i=0;i<tab.length;i++) {
			s+=tab[i];
		}
		return s;
	}
	
	
	public static void main(String[] args) {

		//declaration des parametres
		int O = 5;
		
		//nombre d'exemplaires des voitures dans chaque catégorie
		int[] Vcat = {1,1,2,2,2,2};
		
		int nbCategories=Vcat.length;
			
		int V = somme(Vcat);
		
		//q: le nombre de voitures à produire avec l'option i ->O
		int [] q = {1, 2, 1, 2 ,1};
		
		//p: le nombre max des voitures à produire avec l'option i->O
		int [] p = {2, 3, 3, 5 ,5};

		//caque categorie possede des options
		int[][] R = new int[][]{
			  { 1, 0, 1, 1, 0 },
			  { 0, 0, 0, 1, 0 },
			  { 0, 1, 0, 0, 1 },
			  { 0, 1, 0, 1, 0 },
			  { 1, 0, 1, 0, 0 },
			  { 1, 1, 0, 0, 0 }
		};
			
//Declaration du modele:
		Model m= new Model("Car Sequencing");

// Déclaration des variables:
		IntVar [][] X = new IntVar[V][O];
		
		for(int i=0; i < V; i++) {
			for(int j=0; j < O; j++) {
				X[i][j]= m.intVar("voiture", 0, 1);
			}
		}
		
//Contraintes

// 	1/	Chaque catégorie de voitures possède au moins une option

		for(int i=0; i<V; i++) {
			IntVar[] op=new IntVar[O];
			for(int j=0; j < O ;j++) {
				op[j] = X[i][j];
			}
			m.sum(op, ">=",1).post();
		}
		
//	2/ Contraintes de capacité
		
		
		
	for(int i=0; i<O; i++) {	//option
		System.out.println("option: "+i);

		for(int j=0; j < Vcat.length; j++) {
			for(int z=0; z<Vcat[j] ;z++) {
				
			}
		}
		System.out.println("\n");
	}

//Resolution
		Solver solver = m.getSolver();
		if(solver.solve()){
			System.out.println("Nombre de classes de voiture: "+ V +", nombre d'options: "+ O);
			System.out.println("");
			System.out.println("Affichage: *********************");
			
			for(int j=0; j < V ;j++) {
				System.out.print(j+"   "); 
				for(int i=0; i < O ;i++) {
						int r=X[i][j].getValue();
						System.out.print("  "+r);
					}
				System.out.println("\n");
			}
			
			
			
	}
		else if(solver.hasEndedUnexpectedly()){
			System.out.println("The solver could not find a solution nor prove that none exists in the given limits");
		}
		else {
			System.out.println("The solver has proved the problem has no solution");
		}
	}
}