package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/unexpectederrorlog/*", asyncSupported = true)
public class UnexpectedErrorLogJaxrsFilter extends CipherManagerJaxrsFilter {

}
