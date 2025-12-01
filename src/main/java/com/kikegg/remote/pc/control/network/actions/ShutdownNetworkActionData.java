package com.kikegg.remote.pc.control.network.actions;

import lombok.Builder;
import lombok.Data;
import java.time.temporal.ChronoUnit;

@Data
@Builder
public class ShutdownNetworkActionData {

	private Integer delay;

	private ChronoUnit unit;

}
