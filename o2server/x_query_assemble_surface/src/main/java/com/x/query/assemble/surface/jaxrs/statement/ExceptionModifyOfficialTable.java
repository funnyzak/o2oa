package com.x.query.assemble.surface.jaxrs.statement;

import com.x.base.core.project.exception.PromptException;

class ExceptionModifyOfficialTable extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionModifyOfficialTable() {
		super("不能修改系统表.");
	}
}
