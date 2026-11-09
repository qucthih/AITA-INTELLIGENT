// Kịch bản Tấn công 2: Infinite CPU Loop / Hanging Process
// Mục tiêu: Chạy vòng lặp vô tận, chiếm đoạt 100% 1 core CPU và không bao giờ thoát, gây nghẽn worker.
// Cơ chế phòng thủ Sandbox: Giới hạn TEST_CASE_TIMEOUT_SEC (mặc định 5s - 10s) và OVERALL_TIMEOUT_SEC (30s).
// Container sẽ bị cưỡng chế ngắt (SIGKILL) khi hết thời gian chạy.
// Kết quả mong đợi từ Sandbox: Outcome = TIMEOUT, errorType = 'TIMEOUT', status = 'FAILED'.

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Infinite Loop attack scenario...");
        
        // Đọc dữ liệu input nếu có (testcase input)
        try {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextLine()) {
                System.out.println("Received testcase input: " + scanner.nextLine());
            }
        } catch (Exception ignored) {}

        System.out.println("Entering infinite busy-wait loop...");
        long counter = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            counter++;
            // Busy loop chiếm CPU
            if (counter % 500_000_000L == 0) {
                long elapsedSec = (System.currentTimeMillis() - startTime) / 1000;
                System.out.println("Still running infinitely... (" + elapsedSec + "s elapsed, counter=" + counter + ")");
            }
        }
    }
}

