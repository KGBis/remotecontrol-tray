/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.actions;

import lombok.Builder;
import lombok.Data;
import java.time.temporal.ChronoUnit;

@Data
@Builder
public class ShutdownNetworkActionData {

	private Integer delay;

	private ChronoUnit unit;

}
