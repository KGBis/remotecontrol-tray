package com.kikegg.remote.pc.control.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShutdownRequest extends Request {

    private Integer delay;

    private TimeUnit unit;
}
