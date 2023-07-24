package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDeleteWithWorkPath6 extends BaseDeleteWithWorkPath {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteWithWorkPath6.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String path1, String path2,
			String path3, String path4, String path5, String path6) throws Exception {
		
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		
		ActionResult<Wo> result = new ActionResult<>();
		Work work = checkWork(effectivePerson, id);
		Wo wo = ThisApplication.context().applications()
				.deleteQuery(x_processplatform_service_processing.class, Applications.joinQueryUri("data", "work",
						work.getId(), path0, path1, path2, path3, path4, path5, path6), work.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionDeleteWithWorkPath6$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -440495876759599426L;

	}

	 

}
