package common.event;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import java.util.Observer;

// 通知中心
// 为数据库表格 某些事件建立监听中转
public class EventBus implements Closeable {

	public static EventBus mNotifyCenter = null;
	// 事件池对象
	private EventPool mNotifyPool = new EventPool();

	// 构造方法
	private EventBus() {
	}

	// 获取单例实例
	public static EventBus instance() {

		if (mNotifyCenter == null) {
			mNotifyCenter = new EventBus();

		}
		return mNotifyCenter;
	}



	// 注册事件监听
	public static void register(String name, Observer listener) {
		EventBus rEventBus = instance();
		synchronized (rEventBus) {
			// 创建或获取事件，并注册观察者
			Event it = rEventBus.getOrNew(name);
			it.addObserver(listener);
		}
	}

	// 取消事件监听
	public static void unregister(String name, Observer listener) {
		EventBus unEventBus = instance();
		synchronized (unEventBus) {
			Event it = unEventBus.get(name);
			if (it != null) {
				it.deleteObserver(listener);
				// 如果没有监听者 需要清除该项
				if (it.countObservers() == 0) {
					unEventBus.mNotifyPool.removeObj(name);
					it.src = null;
				}
			}
		}
	}

	// 激发事件 会覆盖前一次未完成的事件对象
	public static void notify(String name, Object srcObj) {
		EventBus nc = instance();
		synchronized (nc) {
			Event it = nc.get(name);
			if (it != null) {

				// 更改数据 
				it.setSrc(srcObj);

			}
		}
	}

	public Event get(String k) {
		return (Event) mNotifyPool.findObj(k);
	}

	public Event getOrNew(String k) {
		Event it = get(k);
		if (it == null) {
			it = new Event();
			it.name = k;
			// 添加到事件池
			mNotifyPool.addObj(k, it);
		}
		return it;
	}

	@Override
	// 关闭数据中心
	synchronized public void close() throws IOException {
		Map<String, Object> all = mNotifyPool.getObjs();
		Event it;
		for (Map.Entry<String, Object> i : all.entrySet()) {
			it = (Event) i.getValue();

			it.src = null;
			it.deleteObservers();
		}
		all.clear();
	}

}
