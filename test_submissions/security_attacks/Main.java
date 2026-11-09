// Kịch bản Tấn công 1: Fork Bomb / Process Overflow
// Mục tiêu: Liên tục sinh ra hàng nghìn luồng (threads/processes) nhằm làm tràn bảng tiến trình hệ điều hành (PID table exhaustion).
// Cơ chế phòng thủ Sandbox: Giới hạn PidsLimit = 64 trong Docker. Khi vượt quá, hệ điều hành từ chối tạo thêm thread,
// JVM ném OutOfMemoryError: unable to create native thread hoặc lỗi resource unavailable.
// Kết quả mong đợi từ Sandbox: Outcome = SECURITY_VIOLATION, errorType = 'SECURITY_VIOLATION'.

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Fork Bomb attack scenario...");
        int threadCount = 0;
        try {
            while (true) {
                final int currentId = ++threadCount;
                new Thread(() -> {
                    try {
                        while (true) {
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ignored) {}
                }).start();

                if (threadCount % 10 == 0) {
                    System.out.println("Spawned " + threadCount + " threads successfully.");
                }
            }
        } catch (Throwable t) {
            System.err.println("Thread creation blocked by Sandbox security at thread #" + threadCount);
            System.err.println("Exception: " + t.getMessage());
            throw t;
        }
    }
}

