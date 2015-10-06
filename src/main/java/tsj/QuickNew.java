package tsj;
import java.util.Random;

/**
 * 
 * @author Adjusted implementation of Sedgewick & Wayne Quicksort
 * 
 */
public class QuickNew {

	public static void sort(int[] a) {
		shuffle(a);
		sort(a, 0, a.length - 1);
	}

	public static void sort(long[] a) {
		shuffle(a);
		sort(a, 0, a.length - 1);
	}

	private static void sort(int[] a, int lo, int hi) {
		if (hi <= lo)
			return;
		int j = partition(a, lo, hi);
		sort(a, lo, j - 1);
		sort(a, j + 1, hi);
	}

	private static void sort(long[] a, int lo, int hi) {
		if (hi <= lo)
			return;
		int j = partition(a, lo, hi);
		sort(a, lo, j - 1);
		sort(a, j + 1, hi);
	}

	private static int partition(int[] a, int lo, int hi) {
		int i = lo;
		int j = hi + 1;
		float v = a[lo];
		while (true) {
			// find item on lo to swap
			while (a[++i] < v) {
				if (i == hi) {
					break;
				}
			}
			// find item on hi to swap
			while (v < a[--j]) {
				if (j == lo) {
					break; // redundant since a[lo] acts as sentinel
				}// check if pointers cross
			}
			if (i >= j) {
				break;
			}
			exch(a, i, j);
		}

		// put partitioning item v at a[j]
		exch(a, lo, j);

		// now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
		return j;
	}

	private static int partition(long[] a, int lo, int hi) {
		int i = lo;
		int j = hi + 1;
		float v = a[lo];
		while (true) {
			// find item on lo to swap
			while (a[++i] < v) {
				if (i == hi) {
					break;
				}
			}
			// find item on hi to swap
			while (v < a[--j]) {
				if (j == lo) {
					break; // redundant since a[lo] acts as sentinel
				}// check if pointers cross
			}
			if (i >= j) {
				break;
			}
			exch(a, i, j);
		}

		// put partitioning item v at a[j]
		exch(a, lo, j);

		// now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
		return j;
	}

	/**
	 * Exchange Method - private to this class, to be used for swapping 2 values
	 * in an array
	 * 
	 * @param a
	 *            - the array containing the elements to be sorted
	 * @param i
	 *            - index of one integer to be swapped
	 * @param j
	 *            - index of second integer to be swapped with first
	 */
	private static void exch(int[] a, int i, int j) {
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	private static void exch(long[] a, int i, int j) {
		long temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	/**
	 * Shuffle Method. Randomises the array
	 * 
	 * @param a
	 *            - array to randomise
	 */
	private static void shuffle(int[] a) {
		Random rnd = new Random();
		for (int i = a.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			exch(a, index, i);
		}
	}

	private static void shuffle(long[] a) {
		Random rnd = new Random();
		for (int i = a.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			exch(a, index, i);
		}
	}

	/**
	 * This Method is provided for testing purposes
	 * 
	 * @param b
	 *            - array to be checked if it is sorted
	 * @return true if sorted, false otherwise
	 */
	public static boolean isSorted(int[] b) {
		for (int i = 1; i < b.length; i++) {
			if (b[i] < b[i - 1]) {
				return false;
			}
		}
		return true;
	}
}
