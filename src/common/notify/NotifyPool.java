package common.notify;


import java.util.HashMap;
import java.util.Map;

public class NotifyPool {
	protected Map<String, Object> mObjs = null;


	public Map<String, Object> getObjs() {
		return this.mObjs = new HashMap<String, Object>();
	}

	public Object addObj(String sKey, Object obj) {
		return getObjs().put(sKey, obj);
	}

	public Object findObj(String sKey) {
		return this.mObjs != null ? this.mObjs.get(sKey) : null;
	}

	public Object removeObj(String sKey) {
		return this.mObjs != null ? this.mObjs.remove(sKey) : null;
	}
}