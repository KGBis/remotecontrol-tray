package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.inject.AbstractModule;
import io.github.kgbis.remotecontrol.tray.RemoteControl;
import lombok.SneakyThrows;

import java.io.IOException;

public class RemoteControlModule extends AbstractModule {

	@SneakyThrows(IOException.class)
	@Override
	protected void configure() {
		String rootPackage = RemoteControl.class.getPackage().getName();
		ClassScanner.findSingletonClasses(rootPackage).forEach(this::bind);
	}

}
