import java.util.Scanner;

public class Main {
    public static int binarySearch(int[] arr, int item, int low, int high) {
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (item == arr[mid]) {
                return mid + 1;
            } else if (item > arr[mid]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }

    public static void binaryInsertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int j = i - 1;
            int selected = arr[i];

            int loc = binarySearch(arr, selected, 0, j);

            while (j >= loc) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = selected;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;

        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }

        binaryInsertionSort(arr);

        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + (i < n - 1 ? " " : ""));
        }
        System.out.println();
        sc.close();
    }
}