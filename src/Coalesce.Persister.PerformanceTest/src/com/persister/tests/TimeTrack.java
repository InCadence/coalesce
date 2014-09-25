package com.persister.tests;

import java.io.Serializable;

public class TimeTrack implements Serializable {
	private String _entityID;
	private String _startTime;
	private String _stopTime;
	public String getEntityID() {
		return _entityID;
	}
	public void setEntityID(String _entityID) {
		this._entityID = _entityID;
	}
	public String getStartTime() {
		return _startTime;
	}
	public void setStartTime(String _startTime) {
		this._startTime = _startTime;
	}
	public String getStopTime() {
		return _stopTime;
	}
	public void setStopTime(String _stopTime) {
		this._stopTime = _stopTime;
	}
}
