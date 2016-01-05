package org.xiangbalao;

import java.util.Observable;
import java.util.Observer;

import org.xiangbalao.notifycenter.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import common.LogUtil;
import common.event.EventBus;
import common.notify.NotifyCenter;

/**
 * 观察者设计模式封装的事件中心 测试
 */
public class NotifyCenterActivity extends Activity {

	private TextView notify;
	private TextView notify1;
	private User mUser;
	private String eventname = "NotifyCenterActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		notify = (TextView) findViewById(R.id.notify);
		notify1 = (TextView) findViewById(R.id.notify1);
		mUser = new User();

		// 注册事件
		EventBus.register(eventname, new Observer() {
			@Override
			public void update(Observable observable, Object data) {

				if (data instanceof User) {
					//
					User notifyUser = (User) data;
					LogUtil.i("Notify", "姓名：" + notifyUser.getName()
							+ "\t\t年龄:" + notifyUser.getAge());
					notify.setText("姓名：" + notifyUser.getName() + "\t\t年龄:"
							+ notifyUser.getAge() + "岁");

				}

			}
		});

		NotifyCenter.register(eventname, new Observer() {
			@Override
			public void update(Observable observable, Object data) {

				if (data instanceof User) {
					//
					User notifyUser = (User) data;
					LogUtil.i("Notify", "姓名：" + notifyUser.getName()
							+ "\t\t年龄:" + notifyUser.getAge());
					notify1.setText("姓名：" + notifyUser.getName() + "\t\t年龄:"
							+ notifyUser.getAge() + "岁");

				}

			}
		});

		Button fab = (Button) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (mUser != null) {
					if (mUser.getAge() != null) {
						mUser.setAge(String.valueOf(Integer.parseInt(mUser
								.getAge()) + 1));
					} else {
						mUser.setName("张三");
						mUser.setAge("18");
					}

				} else {
					mUser = new User();
				}

				// 通知事件-修改数据
				// NotifyCenter.notify(eventname, mUser, true);

				EventBus.notify(eventname, mUser);

			}
		});

		Button fab1 = (Button) findViewById(R.id.fab1);
		fab1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (mUser != null) {
					if (mUser.getAge() != null) {
						mUser.setAge(String.valueOf(Integer.parseInt(mUser
								.getAge()) + 1));
					} else {
						mUser.setName("张三");
						mUser.setAge("18");
					}

				} else {
					mUser = new User();
				}

				// 通知事件-修改数据
				NotifyCenter.notify(eventname, mUser, true);

			}
		});
	}

}
