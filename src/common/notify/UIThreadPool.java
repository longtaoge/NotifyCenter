package common.notify;

import android.os.Handler;
import android.os.Looper;
import java.io.Closeable;

public class UIThreadPool implements Closeable {
	Handler mHandler = null;
	static UIThreadPool ui = null;

	public Handler getHandlerEx() {
		if (this.mHandler == null) {
			this.mHandler = new Handler(Looper.getMainLooper());
		}
		return this.mHandler;
	}

	public static synchronized Handler getHandler() {

		if (ui == null) {
			ui = new UIThreadPool();

		}
		return ui.getHandlerEx();
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

	public void close() {
		if (this.mHandler != null) {
			this.mHandler.removeCallbacksAndMessages(null);
			this.mHandler = null;
		}
	}
}