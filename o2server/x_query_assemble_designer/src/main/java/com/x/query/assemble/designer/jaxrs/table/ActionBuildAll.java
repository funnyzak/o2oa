package com.x.query.assemble.designer.jaxrs.table;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.entity.dynamic.DynamicEntityBuilder;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Enhance;
import com.x.query.core.entity.schema.Table;

class ActionBuildAll extends BaseAction {

	private static final String DOT_JAR = ".jar";

	private static Logger logger = LoggerFactory.getLogger(ActionBuildAll.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			File src = Config.dir_local_temp_dynamic_src(true);
			File target = Config.dir_local_temp_dynamic_target(true);
			FileUtils.cleanDirectory(src);
			FileUtils.cleanDirectory(target);
			List<Table> tables = emc.listAll(Table.class);
			for (Table table : tables) {
				DynamicEntity dynamicEntity = XGsonBuilder.instance().fromJson(table.getData(), DynamicEntity.class);
				dynamicEntity.setName(table.getName());
				DynamicEntityBuilder builder = new DynamicEntityBuilder(dynamicEntity, src);
				builder.build();
			}

			List<File> classPath = new ArrayList<>();
			classPath.addAll(FileUtils.listFiles(Config.dir_commons_ext(), FileFilterUtils.suffixFileFilter(DOT_JAR),
					DirectoryFileFilter.INSTANCE));
			classPath.addAll(FileUtils.listFiles(Config.dir_store_jars(), FileFilterUtils.suffixFileFilter(DOT_JAR),
					DirectoryFileFilter.INSTANCE));

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null,
					DefaultCharset.charset_utf_8);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(target));
			fileManager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(src));
			fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
			Iterable<JavaFileObject> res = fileManager.list(StandardLocation.SOURCE_PATH, DynamicEntity.CLASS_PACKAGE,
					EnumSet.of(JavaFileObject.Kind.SOURCE), true);
			compiler.getTask(null, fileManager, null, null, null, res).call();

			fileManager.close();

			this.enhance();

			File jar = new File(Config.dir_dynamic_jars(true), DynamicEntity.JAR_NAME + DOT_JAR);
			JarTools.jar(target, jar);

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void enhance() throws Exception {

		File commandJavaFile = null;
		if (SystemUtils.IS_OS_AIX) {
			commandJavaFile = new File(Config.dir_jvm_aix(), "bin/java");
		} else if (SystemUtils.IS_OS_LINUX) {
			commandJavaFile = new File(Config.dir_jvm_linux(), "bin/java");
		} else if (SystemUtils.IS_OS_MAC) {
			commandJavaFile = new File(Config.dir_jvm_macos(), "bin/java");
		} else {
			commandJavaFile = new File(Config.dir_jvm_windows(), "bin/java.exe");
		}

		List<String> paths = new ArrayList<>();

		paths.add(Config.dir_store_jars().getAbsolutePath() + File.separator + "*");
		paths.add(Config.dir_commons_ext().getAbsolutePath() + File.separator + "*");
		paths.add(Config.dir_local_temp_dynamic_target().getAbsolutePath());

		String command = commandJavaFile.getAbsolutePath() + " -classpath \""
				+ StringUtils.join(paths, File.pathSeparator) + "\" " + Enhance.class.getName() + " \""
				+ Config.dir_local_temp_dynamic_target() + "\"";

		logger.debug("enhance command:{}.", command);

		ProcessBuilder processBuilder = new ProcessBuilder();

		if (SystemUtils.IS_OS_AIX) {
			processBuilder.command("sh", "-c", command);
		} else if (SystemUtils.IS_OS_LINUX) {
			processBuilder.command("sh", "-c", command);
		} else if (SystemUtils.IS_OS_MAC) {
			processBuilder.command("sh", "-c", command);
		} else {
			processBuilder.command("cmd", "/c", command);
		}

		Process process = processBuilder.start();

		process.waitFor();

	}

	public static class Wo extends WrapBoolean {

	}

}