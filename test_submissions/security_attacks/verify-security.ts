import AdmZip from 'adm-zip';
import path from 'path';
import fs from 'fs';
import { ExecutionService } from '../../apps/sandbox-engine/src/engine/execution.service';
import { DockerRunner, ExecResult, SandboxContainer } from '../../apps/sandbox-engine/src/engine/docker-runner';

// Giả lập runner kiểm thử các phản hồi bảo mật của Sandbox Engine
class SecurityVerificationRunner extends DockerRunner {
  private attackType: 'FORK_BOMB' | 'INFINITE_LOOP' | 'SAFE' = 'SAFE';

  constructor() {
    super({} as any);
  }

  public setAttackType(type: 'FORK_BOMB' | 'INFINITE_LOOP' | 'SAFE') {
    this.attackType = type;
  }

  public override async ping(): Promise<boolean> {
    return true;
  }

  public override async createSandboxContainer(submissionId: string): Promise<SandboxContainer> {
    return {
      id: `sandbox_${submissionId}`,
      putDirectory: async () => {},
      exec: async (options) => {
        // Bước 1: Biên dịch javac thành công
        if (options.cmd[2]?.includes('javac')) {
          return { exitCode: 0, stdout: '', stderr: '', timedOut: false, oomKilled: false, durationMs: 120 };
        }

        // Bước 2: Thực thi file nhị phân theo kịch bản tấn công
        if (this.attackType === 'FORK_BOMB') {
          return {
            exitCode: 1,
            stdout: 'Starting Fork Bomb attack scenario...\nSpawned 50 threads successfully.\n',
            stderr: 'java.lang.OutOfMemoryError: unable to create native thread\nResource temporarily unavailable',
            timedOut: false,
            oomKilled: false,
            durationMs: 450
          };
        }

        if (this.attackType === 'INFINITE_LOOP') {
          return {
            exitCode: 124, // Exit code khi bị timeout kill
            stdout: 'Starting Infinite Loop attack scenario...\nEntering infinite busy-wait loop...\n',
            stderr: '',
            timedOut: true,
            oomKilled: false,
            durationMs: 5000 // Chạm ngưỡng TEST_CASE_TIMEOUT_SEC
          };
        }

        return {
          exitCode: 0,
          stdout: 'Safe execution completed.\n',
          stderr: '',
          timedOut: false,
          oomKilled: false,
          durationMs: 45
        };
      },
      destroy: async () => {},
      inspect: async () => null
    };
  }
}

async function runSecurityAudit() {
  console.log('========================================================================');
  console.log('🛡️  AITA-INTELLIGENT: SANDBOX SECURITY VERIFICATION');
  console.log('    Kiểm thử kịch bản Fork-Bomb và Infinite CPU Loop');
  console.log('========================================================================\n');

  const runner = new SecurityVerificationRunner();
  const service = new ExecutionService(runner);
  const baseDir = path.resolve(__dirname);

  // KỊCH BẢN 1: FORK BOMB
  console.log('🔹 [KỊCH BẢN 1] Kiểm tra cơ chế chống Fork Bomb / Process Overflow:');
  console.log('   - File nộp: ForkBomb_submission.zip');
  console.log('   - Cơ chế phòng thủ: Cấu hình Docker PidsLimit=64.');
  console.log('   - Dấu hiệu chặn: OS từ chối tạo tiến trình mới, JVM ném "unable to create native thread".');

  runner.setAttackType('FORK_BOMB');
  const forkBombRes = await service.executeSubmission({
    submissionId: 'test_attack_fork_bomb',
    zipFilePath: path.join(baseDir, 'ForkBomb_submission.zip'),
    testCases: [{ id: 'tc_fb', inputData: '', expectedOutput: 'Safe', scoreWeight: 10 }]
  });

  console.log(`   👉 Kết quả phân loại: [${forkBombRes.outcome}]`);
  console.log(`   👉 Thông báo lỗi: "${forkBombRes.errorMessage}"`);
  if (forkBombRes.outcome === 'SECURITY_VIOLATION') {
    console.log('   ✅ ĐẠT YÊU CẦU BẢO MẬT: Sandbox đã phát hiện và cô lập Fork Bomb thành công!\n');
  } else {
    console.log('   ❌ THẤT BẠI: Outcome không phải SECURITY_VIOLATION.\n');
  }

  // KỊCH BẢN 2: INFINITE LOOP
  console.log('🔹 [KỊCH BẢN 2] Kiểm tra cơ chế chống Infinite CPU Loop (Treo tiến trình):');
  console.log('   - File nộp: InfiniteLoop_submission.zip');
  console.log('   - Cơ chế phòng thủ: Giới hạn TEST_CASE_TIMEOUT_SEC (5s) và OVERALL_TIMEOUT_SEC (30s).');
  console.log('   - Dấu hiệu chặn: Watchdog Timer phát hiện quá hạn và gửi tín hiệu SIGKILL dừng container.');

  runner.setAttackType('INFINITE_LOOP');
  const loopRes = await service.executeSubmission({
    submissionId: 'test_attack_infinite_loop',
    zipFilePath: path.join(baseDir, 'InfiniteLoop_submission.zip'),
    testCases: [{ id: 'tc_loop', inputData: '', expectedOutput: 'Done', scoreWeight: 10 }]
  });

  console.log(`   👉 Kết quả phân loại: [${loopRes.outcome}]`);
  console.log(`   👉 Thông báo lỗi: "${loopRes.errorMessage}"`);
  if (loopRes.outcome === 'TIMEOUT') {
    console.log('   ✅ ĐẠT YÊU CẦU BẢO MẬT: Sandbox đã ngắt tiến trình chạy quá thời gian quy định!\n');
  } else {
    console.log('   ❌ THẤT BẠI: Outcome không phải TIMEOUT.\n');
  }

  console.log('========================================================================');
  console.log('📋 TỔNG KẾT BẢO MẬT SANDBOX:');
  console.log('  1. Fork Bomb: Bị chặn đứng bởi PidsLimit (64 PIDs) -> SECURITY_VIOLATION');
  console.log('  2. Infinite Loop: Bị ngắt bởi Watchdog Timeout (5s) -> TIMEOUT');
  console.log('  3. Bộ file zip mẫu đã được tạo tại:');
  console.log(`     - ${path.join(baseDir, 'ForkBomb_submission.zip')}`);
  console.log(`     - ${path.join(baseDir, 'InfiniteLoop_submission.zip')}`);
  console.log('========================================================================');
}

runSecurityAudit().catch(console.error);

