package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNotEmpty  extends LanguagePromptException {

    private static final long serialVersionUID = 4862362281353270832L;

    ExceptionNotEmpty(String name) {
        super("属性:{} 不能为空.", name);
    }
}
