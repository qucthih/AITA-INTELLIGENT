import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    public static int binarySearch(int[] arr, int item, int low, int high) {
        if (high <= low) {
            return (item >= arr[low]) ? (low + 1) : low;
        }
        int mid = low + (high - low) / 2;
        if (item == arr[mid]) {
            return mid + 1;
        }
        if (item > arr[mid]) {
            return binarySearch(arr, item, mid + 1, high);
        }
        return binarySearch(arr, item, low, mid - 1);
    }

    public static void binaryInsertionSort(int[] arr) {
        if (arr == null || arr.length <= 1) return;

        for (int i = 1; i < arr.length; i++) {
            int current = arr[i];
            int insertIndex = binarySearch(arr, current, 0, i - 1);

            if (insertIndex < i) {
                System.arraycopy(arr, insertIndex, arr, insertIndex + 1, i - insertIndex);
                arr[insertIndex] = current;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = null;
        String line;

        // Đọc số lượng phần tử N bất kể nằm riêng dòng hay chung dòng với mảng
        Integer n = null;
        while ((line = br.readLine()) != null) {
            st = new StringTokenizer(line);
            if (st.hasMoreTokens()) {
                n = Integer.parseInt(st.nextToken());
                break;
            }
        }

        if (n == null || n <= 0) return;

        int[] arr = new int[n];
        int idx = 0;

        // Đọc tiếp các token còn lại trên cùng dòng (nếu có)
        while (st != null && st.hasMoreTokens() && idx < n) {
            arr[idx++] = Integer.parseInt(st.nextToken());
        }

        // Đọc các dòng tiếp theo cho đến khi đủ N phần tử
        while (idx < n && (line = br.readLine()) != null) {
            st = new StringTokenizer(line);
            while (st.hasMoreTokens() && idx < n) {
                arr[idx++] = Integer.parseInt(st.nextToken());
            }
        }

        binaryInsertionSort(arr);

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) out.append(' ');
            out.append(arr[i]);
        }
        System.out.println(out);
    }
}