package io.github.kgbis.remotecontrol.tray.net.info;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.hardware.NetworkIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkChangeRegistrarTest {

	@Mock
	NetworkChangeListener listener;

	Map<String, String> lastIpMacMap = new HashMap<>();

	@Test
	void testProcessInterfaces_notifiesListenersOnChange() {
		NetworkChangeRegistrar registrar = new NetworkChangeRegistrar(10);
		registrar.addListener(listener);

		// NetworkIF mocks
		NetworkIF net1 = mock(NetworkIF.class);
		when(net1.getIPv4addr()).thenReturn(new String[] { "192.168.1.1" });
		when(net1.getMacaddr()).thenReturn("AA:AA:AA:AA:AA:AA");

		NetworkIF net2 = mock(NetworkIF.class);
		when(net2.getIPv4addr()).thenReturn(new String[] { "192.168.1.2" });
		when(net2.getMacaddr()).thenReturn("BB:BB:BB:BB:BB:BB");

		// First call -> should notify
		processInterfaces(List.of(net1));
		verify(listener, times(1)).onNetworkChange(any());

		// Second call with same map -> shouldn't notify
		processInterfaces(List.of(net1));
		verify(listener, times(1)).onNetworkChange(any());

		// Changes in interfaces -> should notify
		processInterfaces(List.of(net1, net2));
		verify(listener, times(2)).onNetworkChange(any());
	}

	protected void processInterfaces(List<NetworkIF> activeInterfaces) {
		Map<String, String> currentIpMacMap = new HashMap<>();
		for (NetworkIF net : activeInterfaces) {
			for (String ip : net.getIPv4addr()) {
				currentIpMacMap.put(ip, net.getMacaddr());
			}
		}

		if (!currentIpMacMap.equals(lastIpMacMap)) {
			lastIpMacMap = currentIpMacMap;
			listener.onNetworkChange(activeInterfaces);
		}
	}

}
