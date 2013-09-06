import java.util.Arrays;

public class Percolation {

    private final static int[][] NEIGHBORS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    private final static int[][] NEXT = new int[][]{{0, 1, 2, 3}, {1, 2, 3}, {2, 3, 0}, {3, 0, 1}, {0, 1, 2}};
    private final WeightedQuickUnionUF connections;
    private final int[][] open;
    private final int dim;
    private final int source;
    private final int sink;

    // create N-by-N grid, with all sites blocked
    public Percolation(int N) {
        dim = N;
        open = new int[N][N];
        source = 0;
        sink = N * N + 1;
        connections = new WeightedQuickUnionUF(N * N + 2);
    }

    // open site (row i, column j) if it is not already
    public void open(int i, int j) {
        int x = validateArgument(i);
        int y = validateArgument(j);
        //dump();
        propagate(x, y, (x == 0 ? 2 : 1), NEXT[0]);
        //dump();
    }

    // is site (row i, column j) open?
    public boolean isOpen(int i, int j) {
        int x = validateArgument(i);
        int y = validateArgument(j);
        return open[x][y] > 0;
    }

    // is site (row i, column j) full?
    public boolean isFull(int i, int j) {
        int x = validateArgument(i);
        int y = validateArgument(j);
        return connections.connected(source, coordinatesToIndex(x, y));
    }

    private void propagate(int x, int y, int state, int[] directions) {
        propagate(-1, -1, x, y, state, directions);
    }

    private int propagate(int x0, int y0, int x, int y, int state, int[] directions) {
        StdOut.printf("(%d,%d) -> (%d,%d) [%d] %s%n", x0,y0,x,y,state, Arrays.toString(directions));
        int[] neighborsState = new int[directions.length];
        int globalState = state;

        for (int i = 0; i < directions.length; i++) {
            int xn = x + NEIGHBORS[directions[i]][0];
            int yn = y + NEIGHBORS[directions[i]][1];
            if ((xn >= 0 && xn < dim - 1) && (yn >= 0 && yn < dim - 1)) {
                neighborsState[i] = open[xn][yn];
                if (neighborsState[i] > globalState) {
                    globalState = neighborsState[i];
                }
            }
        }

        for (int i = 0; i < neighborsState.length; i++) {
            if (neighborsState[i] == 0) continue;
            if (neighborsState[i] < globalState) {
                int xn = x + NEIGHBORS[directions[i]][0];
                int yn = y + NEIGHBORS[directions[i]][1];
                int relations = (globalState == state?1 + (i % 4):0);
                StdOut.printf("%s: %s%n", relations, Arrays.toString(NEXT[relations]));

                globalState = propagate(x, y, xn, yn, globalState, NEXT[relations]);

            }
        }

        open[x][y] = globalState;

        if (globalState == 2) {
            connections.union(source, coordinatesToIndex(x, y));
            if (x == dim - 1)
                connections.union(sink, coordinatesToIndex(x, y));
        }

        return globalState;
    }

    // does the system percolate?
    public boolean percolates() {
        return connections.connected(source, sink);
    }

    private int validateArgument(int i) {
        if (i < 0) throw new IndexOutOfBoundsException("Illegal value: <0");
        if (i > dim) throw new IndexOutOfBoundsException("Illegal value: >N");
        return i - 1;
    }

    private int coordinatesToIndex(int x, int y) {
        int result = 1 + x * dim + y;
        return result;
    }

    private void dump() {
        int size = String.valueOf(dim).length();

        StringBuilder b = new StringBuilder();
        for (int x = 0; x < size / 2 + 1; x++) {
            b.append(' ');
        }

        String vpad = b.toString();
        for (int x = 0; x < size; x++) StdOut.printf(" ");

        for (int i = 0; i < dim; i++) {
            StdOut.printf("|%" + size + "d", i + 1);
        }

        for (int i = 0; i < dim; i++) {
            StdOut.printf("%n%" + size + "d", i + 1);
            for (int j = 0; j < dim; j++) {
                if (open[i][j] == 2) {
                    StdOut.printf(vpad + "%s(%d)", "F", open[i][j]);
                } else if (open[i][j] == 1) {
                    StdOut.printf(vpad + "%s(%d)", "O", open[i][j]);
                } else {
                    StdOut.printf(vpad + "%s(%d)", "#", open[i][j]);
                }

            }
        }
        StdOut.printf("%n");
    }
}