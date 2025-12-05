package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.common.reflect.ClassPath;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ClassScanner {

    @SuppressWarnings("UnstableApiUsage")
	public static Set<Class<?>> findSingletonClasses(String packageName) throws IOException {
		return ClassPath.from(ClassLoader.getSystemClassLoader())
			.getTopLevelClassesRecursive(packageName)
			.stream()
			.map(ClassPath.ClassInfo::load)
			.filter(clazz -> clazz.isAnnotationPresent(Singleton.class))
			.collect(Collectors.toSet());
	}

}
