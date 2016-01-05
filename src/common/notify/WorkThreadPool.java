package common.notify;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import java.io.Closeable;



public class WorkThreadPool implements Closeable {
	private Handler mHandler = null;
	private HandlerThread mThread = null;
	private static WorkThreadPool wt = null;

	public Looper getLooper() {
		if (this.mThread == null) {
			this.mThread = new HandlerThread(WorkThreadPool.class.getName()
					+ "-Thread");
			this.mThread.start();
		}
		return this.mThread.getLooper();
	}

	public Handler getHandlerEx() {
		return this.mHandler = new Handler(getLooper());
	}

	public void close() {
		if (this.mHandler != null) {
			this.mHandler.removeCallbacksAndMessages(null);
			this.mHandler = null;
		}
		if (this.mThread != null) {
			if (this.mThread.isAlive()) {
				this.mThread.quit();
				try {
					this.mThread.join(200L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.mThread = null;
		}
	}

	public static synchronized Handler getHandler() {

		if (wt == null) {
			wt = new WorkThreadPool();

		}
		return wt.getHandlerEx();
	}

	public static boolean postRunnable(Runnable r) {
		return getHandler().post(r);
	}

	public static boolean postAtTime(Runnable r, long uptimeMillis) {
		return getHandler().postAtTime(r, uptimeMillis);
	}

	public static boolean postDelayed(Runnable r, long delayMillis) {
		return getHandler().postDelayed(r, delayMillis);
	}

	public static void removeRunnable(Runnable r) {
		getHandler().removeCallbacks(r);
	}
}