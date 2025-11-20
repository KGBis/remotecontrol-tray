package com.kikegg.remote.pc.control.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InfoRequest extends Request {

	private String ip;

}
