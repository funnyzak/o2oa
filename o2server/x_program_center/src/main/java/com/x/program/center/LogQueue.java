package com.x.program.center;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class LogQueue extends AbstractQueue<NameValuePair> {

	protected void execute(NameValuePair pair) {
		try {
			if (StringUtils.equals(pair.getName(), PromptErrorLog.class.getName())) {
				PromptErrorLog o = pair.getValue(PromptErrorLog.class);
				if (null != o) {
					this.concrete(PromptErrorLog.class, o);
				} else {
					this.clean(PromptErrorLog.class);
				}
			} else if (StringUtils.equals(pair.getName(), UnexpectedErrorLog.class.getName())) {
				UnexpectedErrorLog o = pair.getValue(UnexpectedErrorLog.class);
				if (null != o) {
					this.concrete(UnexpectedErrorLog.class, o);
				} else {
					this.clean(UnexpectedErrorLog.class);
				}
			} else if (StringUtils.equals(pair.getName(), WarnLog.class.getName())) {
				WarnLog o = pair.getValue(WarnLog.class);
				if (null != o) {
					this.concrete(WarnLog.class, o);
				} else {
					this.clean(WarnLog.class);
				}
			} else if (StringUtils.equals(pair.getName(), ScheduleLog.class.getName())) {
				ScheduleLog o = pair.getValue(ScheduleLog.class);
				if (null != o) {
					this.concrete(ScheduleLog.class, o);
				} else {
					this.clean(ScheduleLog.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <T extends JpaObject> void concrete(Class<T> cls, T o) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			T t = emc.find(o.getId(), cls);
			if (null != t) {
				o.copyTo(t, JpaObject.FieldsUnmodify);
				emc.beginTransaction(cls);
				emc.commit();
			} else {
				emc.beginTransaction(cls);
				emc.persist(o, CheckPersistType.all);
				emc.commit();
			}
		}
	}

	private <T extends JpaObject> void clean(Class<T> cls) throws Exception {
		List<String> ids = new ArrayList<>();
		do {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Calendar threshold = Calendar.getInstance();
				threshold.add(Calendar.DATE, -7);
				ids = emc.idsLessThanMax(cls, JpaObject.createTime_FIELDNAME, threshold.getTime(), 500);
				if (!ids.isEmpty()) {
					emc.beginTransaction(cls);
					emc.delete(cls, ids);
					emc.commit();
				}
			}
		} while (!ids.isEmpty());
	}

}
