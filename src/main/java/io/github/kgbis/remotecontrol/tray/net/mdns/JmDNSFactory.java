/*
 * Copyright (c) Enrique García
 *
 * This file is part of RemoteControlTray.
 *
 * RemoteControlTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RemoteControlTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RemoteControlTray.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.mdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * @noinspection RedundantThrows
 */
@SuppressWarnings("Deprecated")
public interface JmDNSFactory {

	JmDNS create(InetAddress inetAddress) throws IOException;

	default JmDNS createDummy(InetAddress inetAddress) throws IOException {
		return new JmDNS() {
			@Override
			public String getName() {
				return "dummy-" + inetAddress.getHostAddress();
			}

			@Override
			public String getHostName() {
				return inetAddress.getHostName();
			}

			@Override
			public InetAddress getInetAddress() throws IOException {
				return inetAddress;
			}

			@Override
			public InetAddress getInterface() throws IOException {
				return inetAddress;
			}

			@Override
			public ServiceInfo getServiceInfo(String s, String s1) {
				return null;
			}

			@Override
			public ServiceInfo getServiceInfo(String s, String s1, long l) {
				return null;
			}

			@Override
			public ServiceInfo getServiceInfo(String s, String s1, boolean b) {
				return null;
			}

			@Override
			public ServiceInfo getServiceInfo(String s, String s1, boolean b, long l) {
				return null;
			}

			@Override
			public void requestServiceInfo(String s, String s1) {
				// Not implemented
			}

			@Override
			public void requestServiceInfo(String s, String s1, boolean b) {
				// Not implemented
			}

			@Override
			public void requestServiceInfo(String s, String s1, long l) {
				// Not implemented
			}

			@Override
			public void requestServiceInfo(String s, String s1, boolean b, long l) {
				// Not implemented
			}

			@Override
			public void addServiceTypeListener(ServiceTypeListener serviceTypeListener) throws IOException {
				// Not implemented
			}

			@Override
			public void removeServiceTypeListener(ServiceTypeListener serviceTypeListener) {
				// Not implemented
			}

			@Override
			public void addServiceListener(String s, ServiceListener serviceListener) {
				// Not implemented
			}

			@Override
			public void removeServiceListener(String s, ServiceListener serviceListener) {
				// Not implemented
			}

			@Override
			public void registerService(ServiceInfo serviceInfo) throws IOException {
				// Not implemented
			}

			@Override
			public void unregisterService(ServiceInfo serviceInfo) {
				// Not implemented
			}

			@Override
			public void unregisterAllServices() {
				// Not implemented
			}

			@Override
			public boolean registerServiceType(String s) {
				return false;
			}

			@SuppressWarnings("Deprecated")
			@Override
			public void printServices() {
				// Not implemented
			}

			@Override
			public ServiceInfo[] list(String s) {
				return new ServiceInfo[0];
			}

			@Override
			public ServiceInfo[] list(String s, long l) {
				return new ServiceInfo[0];
			}

			@Override
			public Map<String, ServiceInfo[]> listBySubtype(String s) {
				return Map.of();
			}

			@Override
			public Map<String, ServiceInfo[]> listBySubtype(String s, long l) {
				return Map.of();
			}

			@Override
			public Delegate getDelegate() {
				return null;
			}

			@Override
			public Delegate setDelegate(Delegate delegate) {
				return null;
			}

			@Override
			public void close() throws IOException {
				// Not implemented
			}
		};
	}

}
