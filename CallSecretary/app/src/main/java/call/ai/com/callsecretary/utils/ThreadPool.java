package call.ai.com.callsecretary.utils;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class ThreadPool {

    private static final String REQUEST_THREAD_NAME = "REJECTED_EXECUTE_THREAD";
    private static HandlerThread sRequestHandlerThread;
    private static Handler sRequestHandler;

    private static int CPU_CORE_COUNT = getNumCores();
    private static int MAX_THREAD_COUNT = 2 * CPU_CORE_COUNT + 1;
    private static ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CPU_CORE_COUNT + 1, MAX_THREAD_COUNT, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(2 * CPU_CORE_COUNT), new ContinueRejectedExecutionHandler());

    private synchronized static Handler getRequestHandler() {
        if (sRequestHandlerThread == null) {
            sRequestHandlerThread = new HandlerThread(REQUEST_THREAD_NAME);
            sRequestHandlerThread.start();
        }

        if (sRequestHandler == null) {
            sRequestHandler = new Handler(sRequestHandlerThread.getLooper());
        }
        return sRequestHandler;
    }

    public static void postRequest(Runnable runnable) {
        if (runnable != null) {
            sExecutor.execute(runnable);
        }
    }

    private static int getNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            return 1;
        }
    }

    public static class ContinueRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            getRequestHandler().post(r);
        }
    }
}
