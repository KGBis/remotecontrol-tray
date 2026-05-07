/*
 * Remote PC Control
 * Copyright (C) 2026 Enrique García (https://github.com/KGBis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.server;

import jakarta.inject.Singleton;

@Singleton
public class ServerLoopRunnerImpl implements ServerLoopRunner {

	private Thread thread;

	@Override
	public void start(Runnable loop) {
		thread = new Thread(loop, "socket-thread");
		thread.start();
	}

	@Override
	public void stop() throws InterruptedException {
		if (thread != null) {
			thread.join(2000);
		}
	}

}
