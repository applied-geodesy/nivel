package org.applied_geodesy.instrument.inclination;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InclinationParameters {
	private Map<InclinationParameterType, Double> params = new HashMap<InclinationParameterType, Double>(5);
	private Date timestamp;
	
	public void add(InclinationParameterType parameterType, double value, Date timestamp) {
		this.params.put(parameterType, value);
		this.timestamp = timestamp;
	}
	
	public void add(InclinationParameterType parameterType, double value) {
		this.add(parameterType, value, new Date());
	}
	
	public double get(InclinationParameterType parameterType) {
		return this.params.get(parameterType);
	}
	
	public boolean contains(InclinationParameterType parameterType) {
		return this.params.containsKey(parameterType);
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}

	@Override
	public String toString() {
		return "InclinationParameters [timestamp=" + timestamp + ", params=" + params + "]";
	}
}
