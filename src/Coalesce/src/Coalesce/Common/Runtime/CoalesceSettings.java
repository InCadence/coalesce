package Coalesce.Common.Runtime;

import org.apache.commons.io.FilenameUtils;

import unity.core.runtime.SettingsBase;
import Coalesce.Common.Helpers.StringHelper;

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

public class CoalesceSettings extends SettingsBase {

    /*--------------------------------------------------------------------------
		Private Member Variables
	--------------------------------------------------------------------------*/

	private static String _defaultApplicationName;
    private static String _defaultApplicationRoot;
		
    /*--------------------------------------------------------------------------
		Public Configuration Functions
	--------------------------------------------------------------------------*/

	public static String GetConfigurationFileName() {
		return _defaultApplicationName + ".Coalesce.config";
	}
	
	public static boolean GetUseBinaryFileStore() {
		return CoalesceSettings.GetSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseFileStore", true, true);
	}
	
	public static boolean SetUseBinaryFileStore(boolean value) {
		return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseFileStore", value);
	}
	
	public static boolean GetUseIndexing() {
		return CoalesceSettings.GetSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseIndexing", true, true);
	}
	
	public static boolean SetUseIndexing(boolean value) {
		return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseIndexing", value);
	}
	
	public static int GetSubDirectoryLength() {
		return CoalesceSettings.GetSettingWithMax(GetConfigurationFileName(), "Coalesce.FileStore.SubDirectoryLength", 2, 5, true);
	}
	
	public static String GetBinaryFileStoreBasePath() {
		return CoalesceSettings.GetSetting(GetConfigurationFileName(), "Coalesce.FileStore.BasePath", FilenameUtils.concat(GetDefaultApplicationRoot(), "..\\images\\uploads\\"), true);
	}
	
	public static boolean SetBinaryFileStoreBasePath(String value)
	{
		return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.FileStore.BasePath", value);
	}
	
	public static void SetDefaultApplicationName(String value)
	{
		_defaultApplicationName = value;
	}
	
	public static String GetDefaultApplicationRoot() {
		if (StringHelper.IsNullOrEmpty(_defaultApplicationRoot)) {
			// TODO: find executing application path
		}
		
		return _defaultApplicationRoot;

	}
	
	public static void SetDefaultApplicationRoot(String value) {
		_defaultApplicationRoot = value;
	}
	
	public static boolean GetUseEncryption() {
		return CoalesceSettings.GetSetting(GetConfigurationFileName(), "Coalesce.Security.UseEncryption", false, true);
	}
	
	public static boolean SetUseEncryption(boolean value) {
		return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.Security.UseEncryption", value);
	}
	
	public static String GetPassPhrase() {
		return CoalesceSettings.GetSetting(GetConfigurationFileName(), "Coalesce.Security.PassPhrase", "9UFAF8FI98BDLQEZ", true);
	}
	
	public static boolean SetPassPhrase(String value) {
		return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.Security.PassPhrase", value);
	}
	
	public static boolean GetAuditSelectStatements() {
		return CoalesceSettings.GetSetting(GetConfigurationFileName(), "Coalesce.Security.AuditSelectStatements", true, true);
	}
	
	public static boolean SetAuditSelectStatements(boolean value) {
		return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.Security.AuditSelectStatements", value);
	}
	
}
