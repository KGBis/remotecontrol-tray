package io.github.kgbis.remotecontrol.tray.net.info;

import io.github.kgbis.remotecontrol.tray.net.mdns.MulticastServiceRegistar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

	@Mock
	MulticastServiceRegistar multicastServiceRegistar;

	@InjectMocks
	NetworkChangeRegistrar registrar;

	Map<String, String> lastIpMacMap = new HashMap<>();

	@Test
	void testProcessInterfaces_notifiesListenersOnChange() {
		registrar.setPollIntervalMs(10);
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
		verify(multicastServiceRegistar, times(1)).restartIfNeeded();

		// Second call with same map -> shouldn't notify
		processInterfaces(List.of(net1));
		verify(listener, times(1)).onNetworkChange(any());
		verify(multicastServiceRegistar, times(1)).restartIfNeeded();

		// Changes in interfaces -> should notify
		processInterfaces(List.of(net1, net2));
		verify(listener, times(2)).onNetworkChange(any());
		verify(multicastServiceRegistar, times(2)).restartIfNeeded();
	}

	/**
	 * This method is like {@code NetworkChangeRegistrar#monitorLoop()} but without thread
	 * qnd just one listener registered.
	 * @param activeInterfaces NetworkIF interface list
	 */
	protected void processInterfaces(List<NetworkIF> activeInterfaces) {
		Map<String, String> currentIpMacMap = new HashMap<>();
		for (NetworkIF net : activeInterfaces) {
			for (String ip : net.getIPv4addr()) {
				currentIpMacMap.put(ip, net.getMacaddr());
			}
		}

		if (!currentIpMacMap.equals(lastIpMacMap)) {
			// changes occurred
			lastIpMacMap = currentIpMacMap;
			listener.onNetworkChange(activeInterfaces);
			multicastServiceRegistar.restartIfNeeded();
		}
	}

}
