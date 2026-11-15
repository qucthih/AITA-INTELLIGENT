import java.util.Scanner;

public class Main {
    private static int findInsertPos(int[] nums, int target, int leftIdx, int rightIdx) {
        while (leftIdx <= rightIdx) {
            int center = (leftIdx + rightIdx) >>> 1;
            if (target == nums[center]) {
                return center + 1;
            } else if (target > nums[center]) {
                leftIdx = center + 1;
            } else {
                rightIdx = center - 1;
            }
        }
        return leftIdx;
    }

    private static void sortArray(int[] nums) {
        int len = nums.length;
        for (int idx = 1; idx < len; ++idx) {
            int keyVal = nums[idx];
            int insertPos = findInsertPos(nums, keyVal, 0, idx - 1);

            for (int shift = idx - 1; shift >= insertPos; --shift) {
                nums[shift + 1] = nums[shift];
            }
            nums[insertPos] = keyVal;
        }
    }

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        if (!reader.hasNextInt()) return;

        int count = reader.nextInt();
        int[] data = new int[count];
        for (int k = 0; k < count; k++) {
            data[k] = reader.nextInt();
        }

        sortArray(data);

        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < count; k++) {
            sb.append(data[k]);
            if (k + 1 < count) sb.append(" ");
        }
        System.out.println(sb.toString());
        reader.close();
    }
}