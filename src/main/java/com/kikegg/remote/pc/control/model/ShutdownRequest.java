package com.kikegg.remote.pc.control.model;

import lombok.Builder;
import lombok.Data;
import java.time.temporal.ChronoUnit;

@Data
@Builder
public class ShutdownRequest {

	private Integer delay;

	private ChronoUnit unit;

}
