package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.inject.AbstractModule;
import io.github.kgbis.remotecontrol.tray.RemoteControl;

import java.io.IOException;

public class RemoteControlModule extends AbstractModule {

	@Override
	protected void configure() {
		String rootPackage = RemoteControl.class.getPackage().getName();
		try {
			ClassScanner.findSingletonClasses(rootPackage).forEach(this::bind);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
