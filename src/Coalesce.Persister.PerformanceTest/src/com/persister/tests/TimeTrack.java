package com.persister.tests;

import java.io.Serializable;

public class TimeTrack implements Serializable {
	private String _appStartTime;
	private String _appStopTime;
	private String _entityID;
	private String _iterationInterval;
	private String _iterationVal;
	private String _startTime;

	private String _stopTime;

	public String getAppStartTime() {
		return _appStartTime;
	}

	public String getAppStopTime() {
		return _appStopTime;
	}

	public String getEntityID() {
		return _entityID;
	}
	public String getIterationInterval() {
		return _iterationInterval;
	}

	public String getIterationVal() {
		return _iterationVal;
	}

	public String getStartTime() {
		return _startTime;
	}

	public String getStopTime() {
		return _stopTime;
	}

	public void setAppStartTime(String _appStartTime) {
		this._appStartTime = _appStartTime;
	}

	public void setAppStopTime(String _appStopTime) {
		this._appStopTime = _appStopTime;
	}

	public void setEntityID(String _entityID) {
		this._entityID = _entityID;
	}

	public void setIterationInterval(String _iterationInterval) {
		this._iterationInterval = _iterationInterval;
	}

	public void setIterationVal(String _iterationVal) {
		this._iterationVal = _iterationVal;
	}

	public void setStartTime(String _startTime) {
		this._startTime = _startTime;
	}

	public void setStopTime(String _stopTime) {
		this._stopTime = _stopTime;
	}
}
