package com.kikegg.remote.pc.control.model;

import lombok.Builder;
import lombok.Data;
import java.time.temporal.ChronoUnit;

@Data
@Builder
public class ShutdownData {

	private Integer delay;

	private ChronoUnit unit;

}
