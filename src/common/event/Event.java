package common.event;

import java.util.Observable;

public class Event extends Observable {

	String name; // 事件名字

	// 事件要传递的对象
	Object src = null;

	public void setName(String name) {
		this.name = name;
	}

	public void setSrc(Object src) {
		this.src = src;
		setChanged();
		notifyObservers(src);

	}

}
