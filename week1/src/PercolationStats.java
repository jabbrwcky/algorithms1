/**
 * PercolationStats
 *
 * @author Jens Hausherr (jens.hausherr@xing.com)
 */
public class PercolationStats {

    private int iterations;
    private int dim;
    private int sites;
    private double[] percolatingFractions;
    private double mean = -1;
    private double stddev = -1;

    // perform T independent computational experiments on an N-by-N grid
    public PercolationStats(int N, int T) {
        if (N < 1 || T < 1) throw new IllegalArgumentException("N and T must be > 0");
        percolatingFractions = new double[T];
        dim = N;
        sites = dim * dim;
        iterations = T;
        run();
    }

    public static void main(String[] args) {
        int n = parseInt(args[0]);
        int t = parseInt(args[1]);
        PercolationStats stats = new PercolationStats(n, t);
        StdOut.printf("mean                    =  %f%n", stats.mean());
        StdOut.printf("stddev                  =  %f%n", stats.stddev());
        StdOut.printf("95%% confidence interval = [%f, %f] %n", stats.confidenceLo(), stats.confidenceHi());
    }

    private static int parseInt(String arg) {
        try {
            int i = Integer.valueOf(arg);
            if (i < 0) throw new IllegalArgumentException("must be >0");
            return i;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Parameter");
        }
    }

    private void run() {
        Stopwatch total = new Stopwatch();
        for (int t = 0; t < iterations; t++) {
            StdOut.printf("Test %04d: ", t);
            Stopwatch s = new Stopwatch();
            Percolation grid = new Percolation(dim);
            double count = 0;
            while (!grid.percolates()) {
                boolean found = false;
                int i = 0;
                int j = 0;
                while (!found) {
                    i = 1 + StdRandom.uniform(dim);
                    j = 1 + StdRandom.uniform(dim);
                    found = !(grid.isOpen(i, j));
                }
                grid.open(i, j);
                count += 1;
            }
            percolatingFractions[t] = count / sites;
            StdOut.printf("%f (%d/%d) in %f seconds.%n", percolatingFractions[t], (int) count, sites, s.elapsedTime());
        }
        StdOut.printf("Total execution time: %f seconds%n", total.elapsedTime());
    }

    // sample mean of percolation threshold
    public double mean() {
        if (mean == -1) {
            double result = 0d;
            mean = StdStats.mean(percolatingFractions);
        }
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        if (iterations == 1) return Double.NaN;
        if (stddev == -1) {
            stddev = StdStats.stddev(percolatingFractions);
        }
        return stddev;

    }

    // returns lower bound of the 95% confidence interval
    public double confidenceLo() {
        return mean() - 1.96 * stddev() / Math.sqrt(iterations);
    }

    // returns upper bound of the 95% confidence interval
    public double confidenceHi() {
        return mean() + 1.96 * stddev() / Math.sqrt(iterations);
    }

}
