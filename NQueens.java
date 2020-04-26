import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.stream.IntStream;

public class NQueens {
    public static void main(String[] args) {
        int n = 16;
        if (args.length > 0) {
            try {
                n = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        Model model = new Model(n + "-queens problem");
        IntVar[] queens = model.intVarArray("Q", n, 1, n, false);
        IntVar[] diag1 = IntStream.range(0, n).mapToObj(i -> queens[i].sub(i).intVar()).toArray(IntVar[]::new);
        IntVar[] diag2 = IntStream.range(0, n).mapToObj(i -> queens[i].add(i).intVar()).toArray(IntVar[]::new);

        model.post(
                model.allDifferent(queens),
                model.allDifferent(diag1),
                model.allDifferent(diag2)
        );

        for(int i  = 0; i < n-1; i++){
            for(int j = i + 1; j < n; j++){
                for(int k = j + 1; k < n; k++) {
                    model.arithm(
                            (queens[i].sub(queens[j])).mul(i-k).intVar(),
                            "!=",
                            (queens[i].sub(queens[k])).mul(i-j).intVar()
                    ).post();
                }
            }
        }

        Solver solver = model.getSolver();
        solver.setSearch(Search.domOverWDegSearch(queens));
        Solution solution = solver.findSolution();
        if (solution != null) {
            printBoard(queens);
        } else {
            System.out.println("No solution exists for " + n + "-queens problem");
        }
    }

    private static void printBoard(IntVar[] queens) {
        for(IntVar q : queens) {
            char[] array = new char[queens.length];
            Arrays.fill(array, '-');
            array[q.getValue()-1] = 'Q';
            System.out.println(array);
        }
    }
} 