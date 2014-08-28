package Coalesce.Framework.DataModel;

import java.util.HashMap;

import org.joda.time.DateTime;

import Coalesce.Common.Helpers.JodaDateTimeHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public abstract class XsdDataObject {

	protected XsdDataObject _parent;
	protected HashMap<String, XsdDataObject> _childDataObjects = new HashMap<String, XsdDataObject>();

	public String GetKey() {
		return GetObjectKey();
	}

	public abstract void SetKey(String value);

	public abstract String GetName();

	public abstract void SetName(String value);

	public abstract DateTime GetDateCreated();

	public abstract CallResult SetDateCreated(DateTime value);

	public abstract DateTime GetLastModified();

	public boolean SetLastModified(DateTime value) {

		CallResult rst = null;

		rst = SetObjectLastModified(value);
		if (!rst.getIsSuccess()) {
			return false;
		}

		if (this._parent != null) {
			this._parent.SetLastModified(value);
		}

		return true;

	}

	public boolean GetNoIndex() {
		try {
			CallResult rst;

			// Try-Parse the "noindex" attribute. If it's the empty string the
			// result is considered false.
			boolean bool = false;
			String val = "";
			rst = GetObjectNoIndex(val);
			if (!rst.getIsSuccess())
				return false;

			if (val.equals("")) {
				return false;
			} else {
				bool = Boolean.parseBoolean(val);
			}

			return bool;

		} catch (Exception ex) {
			return false;
		}
	}

	public void SetNoIndex(boolean value) {
		try {
			String val = "false";
			if (value)
				val = "true";

			SetObjectNoIndex(val);

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public ECoalesceDataObjectStatus GetStatus() {
		CallResult rst;

		// Get
		String statusString = "";
		rst = GetObjectStatus(statusString);

		// Evaluate
		if (rst.getIsSuccess()) {

			if (statusString.equals("")) {

				// Return Active
				return ECoalesceDataObjectStatus.ACTIVE;
			} else {
				// Return Status
				return (ECoalesceDataObjectStatus.fromLabel(statusString));
			}
		} else {
			// Return Active (Default)
			return ECoalesceDataObjectStatus.ACTIVE;
		}

	}

	public void SetStatus(ECoalesceDataObjectStatus value) {
		// Set
		CallResult rst;
		rst = SetObjectStatus(value.toLabel());
		if (!rst.getIsSuccess()) {
			return;
		}

		SetLastModified(new DateTime());
	}

	public abstract String ToXml();

	protected abstract String GetObjectKey();

	protected abstract CallResult SetObjectLastModified(DateTime value);

	protected CallResult GetObjectNoIndex(String value) {

		value = "false";

		return CallResult.successCallResult;
	}

	protected CallResult SetObjectNoIndex(String value) {

		return CallResult.successCallResult;

	}

	protected abstract CallResult GetObjectStatus(String status);

	protected abstract CallResult SetObjectStatus(String status);

	protected boolean Initialize() {

		if (GetKey() == null || GetKey().equals("")) {
			SetKey(java.util.UUID.randomUUID().toString());
		}

		DateTime utcDate = JodaDateTimeHelper.NowInUtc();

		if (GetDateCreated() == null) {
			SetDateCreated(utcDate);
		}
		if (GetLastModified() == null) {
			SetLastModified(utcDate);
		}

		return true;

	}

	// TODO: Need to test this logic
	protected XsdDataObject GetDataObjectForNamePath(String namePath) {
		try {

			String[] names = namePath.split("/");

			switch (names.length) {
			case 0:

				// No path. Object not found.
				break;

			case 1:

				// End of the path, is our Base Object named the Name Path?
				if (GetName().equals(names[0])) {
					return this;
				}

				// No object found
				break;

			default:

				// Find next child

				XsdDataObject dataObject = null;

				for (XsdDataObject child : _childDataObjects.values()) {
					if (child.GetName().equals(names[1])) {
						dataObject = child;
						break;
					}
				}

				if (dataObject != null) {

					String newPath = namePath.substring(namePath.indexOf("/") + 1);

					return dataObject.GetDataObjectForNamePath(newPath);

				}

				// No object found
				break;
			}

			return null;

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

}
