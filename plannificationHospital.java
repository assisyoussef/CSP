import java.util.Arrays;
import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
//import java.util.Random;

public class Hospital {

	public static void main(String[] args) {
		
		//declaration des parametres
		int nbpostes = 10;
		int nbinferm = 15;
		int nbjours = 15;
		int chirurgie=1;  //contrainte pas complète
		int soins=2;
		int platre=3;
		int salle_dechoquage=4;
		int nbferiemax=2;
		int maxJoursConge=2;//par nbjours
		int [] jf = new int[nbjours] ;
		
//		for(int i=0;i<nbinferm;i++) {
//			int c=0;
//			for(int j=0;j<nbjours;j++) {
//				int r= new Random().nextInt(8);  // [0...7]
//				if(c < maxJoursConge && r == 1) {
//					C[i][j] = r;
//					c++;
//				}else {
//					C[i][j] = 0;	
//				}
//			}
//		}
		
		for(int i = 0; i < jf.length;i++) {
			if(i%6==0) {
				jf[i]=1;	
			}else {
				int r= new Random().nextInt(8);  // [0...19]
				if(r==1)	jf[i] = 1;
				else	jf[i]=0;
			}
		}
//		
		//Affichage jour feries
		for(int i = 0; i < jf.length; i++) {
			if(jf[i]==1)			System.out.println(i +" - ");
		}
				
//		//Affichage du jour congé pour chaque infermier
//		
//		for(int i=0;i<C.length;i++) {
//			System.out.println("Infermier n:"+(i+1)+" : ");
//			for(int j=0;j<C[i].length;j++) {
//				if(C[i][j]==1)	System.out.println("est en congé le jour: "+(j+1));
//			}
//			System.out.println("\n");
//		}
//		System.out.println("\n\n\n\n");
//		
//Déclaration du modèle
		Model m= new Model("Problème des infermiers");
		
//Déclaration des variables
		IntVar [][][] X = new IntVar[nbinferm][nbjours][nbpostes];
		IntVar [][] C = new IntVar[nbinferm][nbjours];

		for(int i=0; i < nbinferm ;i++) {
			for(int j=0; j < nbjours ;j++) {
				C[i][j]= m.intVar("jour"+(j+1), 0, 1);
				for(int k=0; k < nbpostes ;k++) {
					X[i][j][k]= m.intVar("poste: "+(k+1), 0, 1);
				}
			}
		}

//Contraintes
//		IntVar[][] a = new IntVar[nbinferm][nbjours];
//		for(int jo=0; jo<nbjours; jo++) {
//			for(int i=0; i < nbinferm ;i++) {
//				for(int p=0; i < nbpostes ;p++) {
//					a[i][jo]=X[][][]
//							
//				}
//			}
//		}
		
		for(int i=0; i < nbinferm ;i++) {
			int nb=0;
			IntVar[] ca = new IntVar[1*nbpostes];
			for(int j=0; j < nbjours ;j++) {
				IntVar[] e = new IntVar[nbpostes];				
				IntVar[] e2 = new IntVar[nbpostes+1];
					
				for(int k=0; k<nbpostes; k++) {
					e2[k]=X[i][j][k];
						e[k]=X[i][j][k];
				
					if(Arrays.asList(jf).contains(j) && nb<nbferiemax) {
						if(j==nbjours-1) {
							nb=2;
						}else if(j==nbjours-2) {
							nb=1;
						}
						ca[k]=X[i][j][k];
					}
				}
				nb++;
				e2[nbpostes]= C[i][j];
				m.sum(e, "=", 1).post();
				m.sum(e2, "=", 1).post();
				}
			m.sum(ca, "=", 1).post();
		}

		// deux infermiers sont affectes a salle dechoquage
			for(int j=0; j < nbjours ;j++) {
				IntVar[] e1 = new IntVar[nbinferm];
				IntVar[] e2 = new IntVar[nbinferm];
				IntVar[] e3 = new IntVar[nbinferm];
				IntVar[] e4 = new IntVar[nbinferm];
			
				for(int i=0; i<nbinferm; i++) {
					e1[i]=X[i][j][salle_dechoquage];
					e2[i]=X[i][j][chirurgie];
					e3[i]=X[i][j][soins];
					e4[i]=X[i][j][platre];
				}
				// deux infermiers sont affectes a salle déchoquage
				m.sum(e1, "=", 2).post();
				
				//deux a trois infermiers sont affectés aux postes
				//salle chirurgie
				m.sum(e2, ">=", 2).post();
				m.sum(e2, "<=", 3).post();
				//salle soins
				m.sum(e3, ">=", 2).post();
				m.sum(e3, "<=", 3).post();
				//salle platre
				m.sum(e4, ">=", 2).post();
				m.sum(e4, "<=", 3).post();
			}
		
		//fonction objective
//			m.setObjective(Model.MINIMIZE, Cmax-Cmin);
		
		//Resolution
		Solver solver = m.getSolver();
		if(solver.solve()){
			for(int i=0; i < nbjours ;i++) {
				System.out.println("--	Jour  n"+(i+1)+" : ");
				for(int k=0; k < nbpostes ;k++) {
				//	System.out.println(X[j][k][i].getValue() +"  -  ");
					for(int j=0; j < nbinferm ;j++) {
						int x=X[j][i][k].getValue();
						if(x==1)	System.out.println("Le poste "+(k+1)+" --> l'infermier "+(j+1)+"\n");
					}				
				}
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
