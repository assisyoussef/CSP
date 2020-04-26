import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Shifts {

	public static void main(String[] args) {
		
		
		int n=17; //nombre d'infermiers
		int p=10; //nombre de postes
		int jours=15;	//nombre de jours
		int [] joursOuvrable = {1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1}; // 1 = jour ouvrable; 0 = feriée
		int maxPersonEnConges=3;	// nombre max d'infirmiers qui peuvent avoir un congés un jour
		int chirurgie=1;
		int soins=2;
		int platre=3;
		int salle_dechoquage=4;

		//Declaration du modele
		Model m= new Model("Affectation des infirmières");

		//Declaration des variables
			IntVar [][][] X = new IntVar[n][p][jours];
			IntVar [][] C = new IntVar[n][jours];
			IntVar [][][] Y = new IntVar[n][p][jours];	// Charges

			IntVar Cmax = m.intVar("Cmax", 2, 4);
			IntVar Cmin = m.intVar("Cmin", 2, 4);
			
			for(int i=0; i < n ;i++) {
				for(int j=0; j < p ;j++) {
					for(int k=0; k < jours ;k++) {
						X[i][j][k]= m.intVar(" X ",0, 1);
						Y[i][j][k]= m.intVar("Y", 2, 4);
					}
				}
			}

			for(int i=0; i<n; i++) {
				for(int j=0; j<jours; j++) {
					C[i][j]= m.intVar(" C ",0, 1);
				}
			}
		
		for(int j=0; j<jours; j++) {	
			IntVar[] e1 = new IntVar[n];
			IntVar[] e2 = new IntVar[n];
			IntVar[] decho = new IntVar[n];
			IntVar[] chir = new IntVar[n];
			IntVar[] soin = new IntVar[n];
			IntVar[] pla = new IntVar[n];
			IntVar[] nbconges = new IntVar[n];
			IntVar[] pasVide = new IntVar[n];
			
			for(int k=0;k<p;k++) {
				for(int i=0; i<n; i++) {
					pasVide[i]=X[i][k][j];	//aucun poste ne doit etre vide
				}
				m.sum(pasVide,">",0).post();
			}
			for(int i=0; i<n; i++) {
				
				e1[i]=X[i][0][j];
				e2[i]=X[i][5][j];

				m.arithm(X[i][5][j], "=", Y[i][5][j], "/", 2);
				m.arithm(X[i][0][j], "=", Y[i][0][j], "/", 2);
				
				decho[i]=X[i][salle_dechoquage][j];
				chir[i]=X[i][chirurgie][j];
				soin[i]=X[i][soins][j];
				pla[i]=X[i][platre][j];
				nbconges[i]=C[i][j];
			}
			m.sum(e1,"=", 1).post();
			m.sum(e2,"=", 1).post();

			m.sum(nbconges,"<=", maxPersonEnConges).post();
			
			m.sum(decho,"=", 2).post();
			m.sum(chir,">=", 2).post();
			m.sum(chir,"<=", 3).post();
			m.sum(soin,">=", 2).post();
			m.sum(soin,"<=", 3).post();
			m.sum(pla,">=", 2).post();
			m.sum(pla,"<=", 3).post();
		}

		for(int j=0; j<jours; j++){
			if(joursOuvrable[j]==1) { //j est un jour ouvrable
				for(int i=0; i<n ;i++) {	
					IntVar[] Tr = new IntVar[p+1];
					for(int k=0; k<p ; k++) {
						Tr[k] = X[i][k][j];
					}
					Tr[p] = C[i][j];
					m.sum(Tr, "=", 1).post();
				}
			}else {	//j est un fériés
				for(int i=0; i<n ;i++) {
					IntVar[] Tr = new IntVar[p*3];
					int cmp=0;
					for(int k=0; k<p ; k++) {	//postes
						Tr[cmp] = X[i][k][j];
						cmp++;
						if(j+1 < jours) {
							Tr[cmp] = X[i][k][j+1];
							cmp++;
							if(j+2 < jours) {
								Tr[cmp] = X[i][k][j+2];
								cmp++;
							}
						}
					}
					m.sum(Tr, ">=", 1).post();
				}
			}
		}
		
	//	un infermier ne peut pas travailler 3 jours successives dans la chirergie
		for(int i=0; i<n ;i++) {
			for(int j=1; j<jours-1 ;j++) {
				IntVar[] Tr = new IntVar[3];
				int cmp=0;
				Tr[cmp]=X[i][chirurgie][j-1];
				Tr[cmp+1]=X[i][chirurgie][j];
				Tr[cmp+2]=X[i][chirurgie][j+1];
				cmp+=3;
				m.sum(Tr,"<=", 2);
			}
		}

		for(int i=0;i<n;i++) {
			IntVar[] charg = new IntVar[p*jours];
			int cmp=0;
			for(int j=0; j<jours; j++) {
				for(int k=0; k<p; k++) {
					charg[cmp++]=Y[i][k][j];
				}
			}
			m.sum(charg,"<=", Cmax);
			m.sum(charg,">=",Cmin);
		}
		
//Resolution
	Solver solver = m.getSolver();

	if(solver.solve()){

		System.out.println("\n---------------------------------------------------------");
		
		System.out.print("Les jours fériés sont: ");
		for(int i=0; i<jours;i++) {
			if(joursOuvrable[i]==0) {
				System.out.print(" "+(i+1));
			}
		}
		

		System.out.println("\n----------------------------------------------------------------------------------");		
		System.out.println("Nombre d'infermier par poste:");

		System.out.println("\n----------------------------------------------------------------------------------");
		for(int j=0;j<jours;j++) {
			System.out.print("\nJOUR:"+(j+1));

			System.out.println("\n----------------------------------------------------------------------------------");
			System.out.print("| 	Poste	     |");
			for(int i=0;i<p;i++) {
				System.out.print("  "+(i+1)+"  |");
			}	
			
			System.out.print("\n| Nombre d'infirmiers|");
			for(int k=0; k<p; k++) {
				int cmp=0;
				for(int i=0;i<n;i++) {
					if(X[i][k][j].getValue()==1) cmp++;
				}
				System.out.print("  "+cmp+"  |");
			}
			System.out.println("\n----------------------------------------------------------------------------------");
			
		}
		System.out.println("Les infirmiers qui sont en congés");
			
			System.out.println("\n---------------------------------------------------------");
			for(int k=0;k<p;k++) {
				System.out.println("Nombre d'infirmiers dans le poste: "+(k+1));
				System.out.println("------------------------------------------------------------------------------------------------------------------------------");
				for(int i=0; i<jours; i++) {
						System.out.print("Jour "+(i+1)+" |");
				}
				System.out.println("\n------------------------------------------------------------------------------------------------------------------------------");
				for(int i=0; i<jours; i++) {
					int cmp=0;
					for(int j=0; j<n; j++) {
						if(X[j][k][i].getValue()==1)	cmp++;
					}
					System.out.print(cmp+"	"+"|    ");
				}
				System.out.println("\n------------------------------------------------------------------------------------------------------------------------------\n");
			}
			System.out.println("Les infirmiers dans chaque poste :");
			for(int j=0;j<jours;j++) {
				System.out.println("- Jour "+(j+1));
				for(int k=0;k<p;k++) {
					System.out.print("		Poste "+(k+1)+" est occupée par l'infirmier(s): ");
					for(int i=0;i<n;i++) {
							if(X[i][k][j].getValue() == 1)	System.out.print(i+" ");
					}
					System.out.println();
				}
			}
			
		}
		else if(solver.hasEndedUnexpectedly()){
			System.out.println("Le solveur n'a pas pu trouver de solution ni prouver qu'il n'en existe aucune dans les limites données");
		}
		else {
			System.out.println("Le solveur a prouvé que le problème n'avait pas de solution");
		}
	}
}