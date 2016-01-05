package common.notify;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

// 通知中心
// 为数据库表格 某些事件建立监听中转
public class NotifyCenter implements Closeable {

	public static NotifyCenter mNotifyCenter = null;
	// 事件池对象
	private NotifyPool mNotifyPool = new NotifyPool();

	// 构造方法
	private NotifyCenter() {
	}

	// 获取单例实例
	public static NotifyCenter instance() {

		if (mNotifyCenter == null) {
			mNotifyCenter = new NotifyCenter();

		}
		return mNotifyCenter;
	}

	public class Item extends Observable {
		String name; // 事件名字
		int ver; // 事件版本
		// 事件要传递的对象
		Object src = null;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				++ver;
				Object s = src;
				setChanged();
				notifyObservers(s);
				// 防止多线程造成数据通知丢失
				synchronized (NotifyCenter.this) {
					src = null;
				}
			}
		};
	}

	// 注册事件监听
	public static void register(String name, Observer listener) {
		NotifyCenter rNotifyCenter = instance();
		synchronized (rNotifyCenter) {
			// 创建或获取事件，并注册观察者
			Item it = rNotifyCenter.getOrNew(name);
			it.addObserver(listener);
		}
	}

	// 取消事件监听
	public static void unregister(String name, Observer listener) {
		NotifyCenter unNotifyCenter = instance();
		synchronized (unNotifyCenter) {
			Item it = unNotifyCenter.get(name);
			if (it != null) {
				it.deleteObserver(listener);
				// 如果没有监听者 需要清除该项
				if (it.countObservers() == 0) {
					unNotifyCenter.mNotifyPool.removeObj(name);
					it.r = null;
					it.src = null;
				}
			}
		}
	}

	// 激发事件 会覆盖前一次未完成的事件对象
	public static void notify(String name, Object srcObj, boolean bUI) {
		NotifyCenter nc = instance();
		synchronized (nc) {
			Item it = nc.get(name);
			if (it != null) {
				if (bUI) {
					UIThreadPool.removeRunnable(it.r);
					// 更改数据
					it.src = srcObj;
					// 在ui中触发清空数据
					UIThreadPool.postRunnable(it.r);

				} else {
					WorkThreadPool.removeRunnable(it.r);
					it.src = srcObj;
					WorkThreadPool.postDelayed(it.r, 100); // 延时10ms触发
				}
			}
		}
	}

	public Item get(String k) {
		return (Item) mNotifyPool.findObj(k);
	}

	public Item getOrNew(String k) {
		Item it = get(k);
		if (it == null) {
			it = new Item();
			it.name = k;
			it.ver = 0;
			// 添加到事件池
			mNotifyPool.addObj(k, it);
		}
		return it;
	}

	@Override
	// 关闭数据中心
	synchronized public void close() throws IOException {
		Map<String, Object> all = mNotifyPool.getObjs();
		Item it;
		for (Map.Entry<String, Object> i : all.entrySet()) {
			it = (Item) i.getValue();
			it.r = null;
			it.src = null;
			it.deleteObservers();
		}
		all.clear();
	}

}
