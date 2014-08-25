package Coalesce.Common.Runtime;

import org.apache.commons.io.FilenameUtils;

import Coalesce.Common.Helpers.StringHelper;
import unity.configuration.SettingType;
import unity.connector.rest.RestConfigConnector;

public class CoalesceSettings {

	private static String _useBinaryFileStoreVal;
	private static boolean _useBinaryFileStore = true;
	private static String _useIndexingVal = "";
	private static boolean _useIndexing = true;
	private static String _binaryFileStoreBasePath;
    private static String _defaultApplicationName;
    private static String _defaultApplicationRoot;
	private static int _subDirectoryLength = -1;
	private static String _useEncryptionVal;
	private static boolean _useEncryption = false;
	private static String _passPhrase;
	private static boolean _auditSelectStatements = true;
	private static String _auditSelectStatementsVal;
	
		
	// Made class static
	private CoalesceSettings() {
		
	}
	
	public static String GetConfigurationFileName() {
		return _defaultApplicationName + ".Coalesce.config";
	}
	
	public static boolean GetUseBinaryFileStore() {
		if (StringHelper.IsNullOrEmpty(_useBinaryFileStoreVal)) {
			_useBinaryFileStoreVal = RestConfigConnector.getSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseFileStore", "True", SettingType.stBoolean, true);
			_useBinaryFileStore = Boolean.parseBoolean(_useBinaryFileStoreVal);
		}
		
		return _useBinaryFileStore;
	}
	
	public static void SetUseBinaryFileSTore(boolean value) {
		RestConfigConnector.setSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseFileStore", Boolean.toString(value), SettingType.stBoolean);
		_useBinaryFileStoreVal = Boolean.toString(value);
		_useBinaryFileStore = value;
	}
	
	public static boolean GetUseIndexing() {
		if (StringHelper.IsNullOrEmpty(_useIndexingVal)) {
			_useIndexingVal = RestConfigConnector.getSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseIndexing", "True", SettingType.stBoolean, true);
			_useIndexing = Boolean.parseBoolean(_useIndexingVal);
		}
		
		return _useIndexing;
		
	}
	
	public static void SetUseIndexing(boolean value) {
		RestConfigConnector.setSetting(GetConfigurationFileName(), "Coalesce.FileStore.UseIndexing", Boolean.toString(value), SettingType.stBoolean);
		_useIndexingVal = Boolean.toString(value);
		_useIndexing = value;
	}
	
	public static int GetSubDirectoryLength() {

		if (_subDirectoryLength == -1) {
			
			RestConfigConnector.getSetting(GetConfigurationFileName(), "Coalesce.FileStore.SubDirectoryLength", "2", SettingType.stInteger, true);
			
			if (_subDirectoryLength > 5) {
				_subDirectoryLength = 2;
			}
		}
		
		return _subDirectoryLength;
	}
	
	public static String GetBinaryFileStoreBasePath() {
		if (StringHelper.IsNullOrEmpty(_binaryFileStoreBasePath)) {
			_binaryFileStoreBasePath = RestConfigConnector.getSetting(GetConfigurationFileName(),
			                                                          "Coalesce.FileStore.BasePath",
			                                                          FilenameUtils.concat(GetDefaultApplicationRoot(), "..\\images\\uploads\\"),
			                                                          SettingType.stString,
			                                                          true);
		}
		
		return _binaryFileStoreBasePath;
	}
	
	public static void SetBinaryFileStoreBasePath(String value)
	{
		RestConfigConnector.setSetting(GetConfigurationFileName(), "Coalesce.FileStore.BasePath", value, SettingType.stString);
		_binaryFileStoreBasePath = value;
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
	
	public static boolean GetUseEncryption()
	{
		if (StringHelper.IsNullOrEmpty(_useEncryptionVal)) {
			_useEncryptionVal = RestConfigConnector.getSetting(GetConfigurationFileName(), "Coalesce.Security.UseEncryption", "false", SettingType.stBoolean, true);
			_useEncryption = Boolean.parseBoolean(_useEncryptionVal);
		}
		
		return _useEncryption;
	}
	
	public static void SetUseEncryption(boolean value) {
		RestConfigConnector.setSetting(GetConfigurationFileName(), "Coalesce.Security.UseEncryption", Boolean.toString(value), SettingType.stBoolean);
		_useEncryptionVal = Boolean.toString(value);
		_useEncryption = value;
	}
	
	public static String GetPassPhrase() {
		if (StringHelper.IsNullOrEmpty(_passPhrase)) {
			_passPhrase = RestConfigConnector.getSetting(GetConfigurationFileName(), "Coalesce.Security.PassPhrase", "9UFAF8FI98BDLQEZ", SettingType.stEncryptedString, true);
		}
		
		return _passPhrase;
		
	}
	
	public static void SetPassPhrase(String value) {
		RestConfigConnector.setSetting(GetConfigurationFileName(), "Coalesce.Security.PassPhrase", value, SettingType.stEncryptedString);
		_passPhrase = value;
	}
	
	public static boolean GetAuditSelectStatements() {
		if (StringHelper.IsNullOrEmpty(_auditSelectStatementsVal)) {
			_auditSelectStatementsVal = RestConfigConnector.getSetting(GetConfigurationFileName(), "Coalesce.Security.AuditSelectStatements", "True", SettingType.stBoolean, true);
			_auditSelectStatements = Boolean.getBoolean(_auditSelectStatementsVal);
		}
		
		return _auditSelectStatements;
		
	}
	
	public static void SetAuditSelectStatements(boolean value) {
		RestConfigConnector.setSetting(GetConfigurationFileName(), "Coalesce.Security.AuditSelectStatements", Boolean.toString(value), SettingType.stBoolean);
		_auditSelectStatementsVal = Boolean.toString(value);
		_auditSelectStatements = value;
	}
	
}
