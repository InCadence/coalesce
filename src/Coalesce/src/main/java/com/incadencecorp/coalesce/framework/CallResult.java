package com.incadencecorp.coalesce.framework;

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
/**
* @author Jing Yang
* May 13, 2016
*/

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link com.incadencecorp.unity.common.CallResult} is designed for providing uniform error handling and result messages
 * across calling methods. {@link com.incadencecorp.unity.common.CallResult} encapsulates success, failure or cancel states,
 * as well as error messages and class- and method-level resolution to allow the caller to identify the faulting call. A
 * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be passed into the
 * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method to enable
 * logging.
 * 
 * @author InCadence
 *
 */

/**
* This class is copied to the package here from Unity project because of the dependencies  
*/
public class CallResult {

    /**
     * 
     * Enumeration of different result types.
     *
     */
    public enum CallResults
    {
        /**
         * Result types of UNKNOWN will be logged to file. If a debugger is attached to the application, the callResult XML
         * will be printed to console
         */
        UNKNOWN,
        /**
         * Result types of SUCCESS will be logged to file.
         */
        SUCCESS,
        /**
         * Result types of FAILED will be logged to file. If a debugger is attached to the application, the CallResult
         * message will be printed to console.
         */
        FAILED,
        /**
         * Result types of FAILED_ERROR will be logged to file. If a debugger is attached to the application, the callResult
         * XML will be printed to console.
         */
        FAILED_ERROR,
        /**
         * Result types of CANCELED will be logged to file.
         */
        CANCELED,
        /**
         * Result types of LOGIN_STATUS will be logged to file.
         */
        LOGIN_STATUS,
        /**
         * Result types of INFO will be logged to file. If a debugger is attached to the application, the CallResult message
         * will be printed to console.
         */
        INFO,
        /**
         * Result types of DEBUG will print the CallResult message to console if a debugger is attached to the application.
         */
        DEBUG;

    };

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static IConfigurationsConnector _connector = null;
    private static String _appName = null;
    private CallResults _result;
    private Date _dateTimeGMT = new Date();
    private String _message;
    private String _moduleName;
    private String _methodName;
    private int _lineNumber;
    private String _stackTrace;
    private Object _returnValue;
    private Exception _exception;

    /*--------------------------------------------------------------------------
    Initialization / Constructors
    --------------------------------------------------------------------------*/

    /**
     * Initializes the connector to use for logging {@link com.incadencecorp.unity.common.CallResult.CallResults}.
     * 
     * @param connector the connector to use for logging {@link com.incadencecorp.unity.common.CallResult.CallResults}
     * @param appName the unique name of the application that is generating the logs
     * */
    public static void initialize(final IConfigurationsConnector connector, final String appName)
    {
        _connector = connector;
        _appName = appName;
    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult} with the state unknown.
     */
    public CallResult()
    {
        this(CallResults.UNKNOWN, "", "");
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    /**
     * Returns a new {@link com.incadencecorp.unity.common.CallResult} with a
     * {@link com.incadencecorp.unity.common.CallResult.CallResults} type of SUCCESS.
     * 
     * @return a new CallResult with a CallResults type of SUCCESS.
     */
    public static final CallResult successCallResult()
    {
        return new CallResult(CallResults.SUCCESS);
    }

    /**
     * Returns a new {@link com.incadencecorp.unity.common.CallResult} with a
     * {@link com.incadencecorp.unity.common.CallResult.CallResults} type of FAILED.
     * 
     * @return a new CallResult with a CallResults type of FAILED.
     */
    public static final CallResult failedCallResult()
    {
        return new CallResult(CallResults.FAILED);
    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the call.
     * @param ex an exception to be stored in the {@link com.incadencecorp.unity.common.CallResult}.
     * @param moduleName the name of the module.
     */
    public CallResult(final CallResults result, final Exception ex, final String moduleName)
    {

        this(result, ex.getMessage(), moduleName);
        this._exception = ex;

    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the call.
     * @param message a message to provide details regarding the state of the
     *            {@link com.incadencecorp.unity.common.CallResult}.
     * @param moduleName the name of the module.
     */
    public CallResult(final CallResults result, final String message, final String moduleName)
    {

        this._result = result;
        this._message = message;
        this._moduleName = moduleName;
        this.setDateTimeGMT();

        // get stack trace
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        // need n to get last element in stack trace
        int n = trace.length - 1;

        this._methodName = trace[n].getMethodName();
        this._lineNumber = trace[n].getLineNumber();
        this._stackTrace = stackTracetoString(trace);

        if (result == CallResults.FAILED_ERROR)
        {
            logOut(true, true);
            // LogOut
        }
    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the {@link com.incadencecorp.unity.common.CallResult}.
     * @param ex an exception to be stored in the {@link com.incadencecorp.unity.common.CallResult}.
     * @param moduleObject the module object.
     */
    public CallResult(final CallResults result, final Exception ex, final Object moduleObject)
    {
        this(result, ex, moduleObject.getClass().getName());
    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the {@link com.incadencecorp.unity.common.CallResult}.
     * @param message a message to provide details regarding the state of the
     *            {@link com.incadencecorp.unity.common.CallResult}.
     * @param moduleObject the module object.
     */
    public CallResult(final CallResults result, final String message, final Object moduleObject)
    {
        this(result, message, moduleObject.getClass().getName());
    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public CallResult(final CallResults result)
    {
        this(result, "", "");
    }

    /**
     * Constructs a {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the {@link com.incadencecorp.unity.common.CallResult}.
     * @param value the object to be stored in the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public CallResult(final CallResults result, final Object value)
    {

        this(result, "", "");

        if (result == CallResults.SUCCESS)
        {
            this._returnValue = value;
        }

    }

    /**************
     * Properties *
     **************/

    /**
     * Returns the name of the product.
     * 
     * @return the name of the product.
     */
    public static String productName()
    {
        // TODO: placeholder
        return "My.Application.Info.ProductName";
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Success.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Success;
     *         <code>false</code> otherwise.
     */
    public final boolean isSuccess()
    {
        return (this._result == CallResults.SUCCESS);
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Failed.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Failed;
     *         <code>false</code> otherwise.
     */
    public final boolean isFailed()
    {
        return (this._result == CallResults.FAILED);
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Failed Error.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Failed Error;
     *         <code>false</code> otherwise
     */
    public final boolean isFailedError()
    {
        return (this._result == CallResults.FAILED_ERROR);
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Canceled.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Canceled;
     *         <code>false</code> otherwise
     */
    public final boolean isCanceled()
    {
        return (this._result == CallResults.CANCELED);
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Info.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Info;
     *         <code>false</code> otherwise
     */
    public final boolean isInfo()
    {
        return (this._result == CallResults.INFO);
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Login Status.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Login Status;
     *         <code>false</code> otherwise
     */
    public final boolean isLoginStatus()
    {
        return (this._result == CallResults.LOGIN_STATUS);
    }

    /**
     * Returns <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Debug Status.
     * 
     * @return <code>true</code> if the state of the {@link com.incadencecorp.unity.common.CallResult} is Debug Status;
     *         <code>false</code> otherwise
     */
    public final boolean isDebugStatus()
    {
        return (this._result == CallResults.DEBUG);
    }

    /**
     * Returns the stack trace of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the stack trace of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final String getStackTrace()
    {
        return this._stackTrace;
    }

    /**
     * Returns the state of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the state of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final CallResults getCallResults()
    {
        return this._result;
    }

    /**
     * Returns the object from the calling method.
     * 
     * @return the object from the calling method.
     */
    public final Object getValue()
    {
        return this._returnValue;
    }

    /**
     * Sets the state of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param result the state of the {@link com.incadencecorp.unity.common.CallResult} to be set.
     */
    public final void setCallResults(final CallResults result)
    {
        this._result = result;
    }

    /**
     * Returns the message from the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the message from the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final String getMessage()
    {
        return this._message;
    }

    /**
     * Sets the message in the {@link com.incadencecorp.unity.common.CallResult} .
     * 
     * @param message the message to be stored in the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final void setMessage(final String message)
    {
        this._message = message;
    }

    /**
     * Returns the DateTime stamp of the {@link com.incadencecorp.unity.common.CallResult} in GMT.
     * 
     * @return the DateTime stamp of the {@link com.incadencecorp.unity.common.CallResult} in GMT.
     */
    public final Date getDateTimeGMT()
    {
        return this._dateTimeGMT;
    }

    /**
     * Sets the DateTime stamp of the {@link com.incadencecorp.unity.common.CallResult} as the current instant in GMT.
     */
    public final void setDateTimeGMT()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date tempDate = new Date();
        try
        {
            this._dateTimeGMT = dateFormat.parse(dateFormat.format(tempDate));
        }
        catch (ParseException e)
        {
            this._dateTimeGMT = tempDate;
        }
    }

    /**
     * Sets the DateTime stamp of the {@link com.incadencecorp.unity.common.CallResult} as the input string. The Date string
     * should be in GMT.
     * 
     * @param inputDate the date time string in the format: MM/dd/yyyy HH:mm:ss a
     * @throws ParseException
     */
    public final void setDateTimeGMT(String inputDate) throws ParseException
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        this._dateTimeGMT = dateFormat.parse(inputDate);
    }

    /**
     * Returns the Exception of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the Exception of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final Exception getException()
    {
        return this._exception;
    }

    /**
     * Sets the Exception of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param ex the Exception of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final void setException(final Exception ex)
    {
        this._exception = ex;
        if (ex != null)
        {
            this._stackTrace = ex.getStackTrace().toString();
        }
    }

    /**
     * Returns the Module Name of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the Module Name of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final String getaModuleName()
    {
        return this._moduleName;
    }

    /**
     * Sets the Module Name of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param name the Module Name of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final void setModuleName(final String name)
    {
        this._moduleName = name;
    }

    /**
     * Returns the Method Name of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the Method Name of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final String getMethodName()
    {
        return this._methodName;
    }

    /**
     * Sets the Method Name of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param name the Method Name of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final void setMethodName(final String name)
    {
        this._methodName = name;
    }

    /**
     * Returns the Line Number of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @return the Line Number of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final int getLineNumber()
    {
        return this._lineNumber;
    }

    /**
     * Sets the Line Number of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param lineNumber the Line Number of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final void setLineNumer(final int lineNumber)
    {
        this._lineNumber = lineNumber;
    }

    /**
     * Sets the status of the {@link com.incadencecorp.unity.common.CallResult}.
     * 
     * @param _result the status of the {@link com.incadencecorp.unity.common.CallResult}.
     */
    public final void setResult(final CallResults result)
    {
        this._result = result;
    }

    /*
     * public String toXML(boolean includeDebugInformation){ try { CallResults rslts; String xml = "";
     * 
     * rslts = this.toXML(includeDebugInformation, xml);
     * 
     * if (rslts != CallResults.SUCCESS) { return ""; } else { return xml; } } catch (Exception ex) { return ""; }
     * 
     * } //
     */

    private String stackTracetoString(final StackTraceElement[] trace)
    {

        String result = "";
        for (StackTraceElement element : trace)
        {
            result += element.toString() + "\n";
        }

        return result;
    }

    /**
     * Returns the {@link com.incadencecorp.unity.common.CallResult} formatted as XML.
     * 
     * @param includeDebugInformation whether to include the module name, method name, line number and stacktrace in the
     *            {@link com.incadencecorp.unity.common.CallResult} XML.
     * @return the {@link com.incadencecorp.unity.common.CallResult} formatted as XML.
     */
    public final String toXML(final boolean includeDebugInformation)
    {
        try
        {
            // no variable rst because no need to initialize XML writer - no
            // xmltextwriter class, using documentbuilder
            // instead
            // clear xml
            // String xml = "";

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements and header
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("CallResult");
            doc.appendChild(rootElement);

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            // Basic info
            Element result = doc.createElement("Result");
            result.appendChild(doc.createTextNode(Integer.toString(this._result.ordinal())));
            if (this._message == null)
            {
                this._message = "null";
            }
            Element message = doc.createElement("Message");
            message.appendChild(doc.createTextNode(this._message));
            Element dateTimeGMT = doc.createElement("DateTimeGMT");
            dateTimeGMT.appendChild(doc.createTextNode(dateFormat.format(this._dateTimeGMT).toString()));
            rootElement.appendChild(result);
            rootElement.appendChild(message);
            rootElement.appendChild(dateTimeGMT);

            // Debug info
            if (includeDebugInformation)
            {
                Element moduleName = doc.createElement("ModuleName");
                moduleName.appendChild(doc.createTextNode(this._moduleName));

                Element methodName = doc.createElement("MethodName");
                methodName.appendChild(doc.createTextNode(this._methodName));

                Element lineNumber = doc.createElement("LineNumber");
                lineNumber.appendChild(doc.createTextNode(Integer.toString(this._lineNumber)));

                if (this._stackTrace == null)
                {
                    this._stackTrace = "No Value";
                }
                Element stackTrace = doc.createElement("StackTrace");
                stackTrace.appendChild(doc.createTextNode(this._stackTrace));

                rootElement.appendChild(moduleName);
                rootElement.appendChild(methodName);
                rootElement.appendChild(lineNumber);
                rootElement.appendChild(stackTrace);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StringWriter outWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(outWriter);
            transformer.transform(source, streamResult);
            StringBuffer sb = outWriter.getBuffer();

            return sb.toString();

            // return CallResults.SUCCESS;

        }
        catch (Exception ex)
        {
            // Return failed error
            System.out.println(ex);
            return "";
        }

    }

    // public CallResults toXML(boolean includeDebugInformation, String Xml)
    // {
    // return CallResults.SUCCESS;
    // }
    /**
     * Parses information into the {@link com.incadencecorp.unity.common.CallResult} object from a
     * {@link com.incadencecorp.unity.common.CallResult} formated as XML.
     * 
     * @param xml the callResult in XML format.
     * @return the result of the call returns a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if Parsing is successful; FAILED_ERROR otherwise.
     */
    public final CallResults fromXML(final String xml)
    {
        try
        {

            String val;
            // DocumentBuilderFactory dbFactory =
            // DocumentBuilderFactory.newInstance();
            // DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // Document xmlDoc = dBuilder.parse(new InputSource(new
            // StringReader(xml)));

            SAXBuilder sax = new SAXBuilder();
            org.jdom2.Document xmlDoc = sax.build(new StringReader(xml));

            val = getText(xmlDoc, "//CallResult/Result");

            if (isNumeric(val))
            {
                int temp;
                temp = Integer.parseInt(val);
                this._result = intToEnum(temp);
            }

            val = getText(xmlDoc, "//CallResult/DateTimeGMT");
            if (isDate(val))
            {
                this._dateTimeGMT = intToDate(val);
            }

            val = getText(xmlDoc, "//CallResult/LineNumber");
            if (isNumeric(val))
            {
                this._lineNumber = Integer.parseInt(val);
            }

            // Extended debug info
            this._message = getText(xmlDoc, "//CallResult/Message");
            this._moduleName = getText(xmlDoc, "//CallResult/ModuleName");
            this._methodName = getText(xmlDoc, "//CallResult/MethodName");
            this._stackTrace = getText(xmlDoc, "//CallResult/StackTrace");

            // return success indicator
            return CallResults.SUCCESS;

        }
        catch (Exception ex)
        {
            // return failed error indicator
            return CallResults.FAILED_ERROR;
        }
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult} as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param callResult the {@link com.incadencecorp.unity.common.CallResult} to be logged.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResult callResult)
    {

        return callResult.logOut(true, true);
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status as a
     * {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result)
    {
        return log(result, null, "", "");
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status, message and Module Object as a
     * {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @param message the message to be logged.
     * @param moduleObject the module Object of the calling method.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result, final String message, final Object moduleObject)
    {

        return log(result, message, moduleObject.getClass());
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status, message and class of the calling method
     * as a {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @param message the message to be logged.
     * @param objectType the class of the calling method.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result, final String message, final Class<?> objectType)
    {

        return log(result, null, message, objectType.getName());
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status, message and Module Name of the calling
     * method as a {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @param message the message to be logged.
     * @param moduleName the Module name of the calling method.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result, final String message, final String moduleName)
    {
        return log(result, null, message, moduleName);
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status, exception and the module Object of the
     * calling method as a {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @param ex the exception to be logged.
     * @param moduleObject the module Object of the calling method.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result, final Exception ex, final Object moduleObject)
    {
        return log(result, ex, moduleObject.getClass());
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status, exception and the class of the calling
     * method as a {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @param ex the exception to be logged.
     * @param objectType the class of the calling method.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result, final Exception ex, final Class<?> objectType)
    {
        return log(result, ex, ex.getMessage(), objectType.getName());
    }

    /**
     * Logs the {@link com.incadencecorp.unity.common.CallResult.CallResults} status, exception, message and the module name
     * of the calling method as a {@link com.incadencecorp.unity.common.CallResult} formated as XML. A
     * {@link com.incadencecorp.unity.common.IConfigurationsConnector} must be initialized using the
     * {@link #initialize(IConfigurationsConnector, String) initialize(IConfigurationsConnector, String)} method.
     * 
     * @param result the {@link com.incadencecorp.unity.common.CallResult.CallResults} status.
     * @param ex the exception to be logged.
     * @param message the message from the calling method.
     * @param moduleName the module name of the calling method.
     * @return the result of the call will return a {@link com.incadencecorp.unity.common.CallResult.CallResults} status of
     *         SUCCESS if logging is successful; FAILED_ERROR otherwise.
     */
    public static CallResults log(final CallResults result, final Exception ex, final String message, final String moduleName)
    {
        // Is Debug Result?
        if (result == CallResults.DEBUG)
        {
            // Yes; Is Debugger Attached?
            boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

            if (!isDebug)
            {
                // No; Short Circuit Logic
                return CallResults.SUCCESS;
            }
        }

        CallResult cr = new CallResult();
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        // set values
        cr.setResult(result);
        cr.setException(ex);
        cr.setMessage(message);
        cr.setModuleName(moduleName);
        cr.setDateTimeGMT();

        int n = trace.length - 1;
        cr.setMethodName(trace[n].getMethodName());
        cr.setLineNumer(trace[n].getLineNumber());

        return cr.logOut(true, true);
    }

    private CallResults logOut(final boolean toFile, final boolean toDebugPrint)
    {
        try
        {
            /*
             * MaskExceptionEventArgs MaskArgs = new MaskExceptionEventArgs(); RaiseEvent MaskException(this,
             * MaskArgs);//TODO: find equivalent to raise event in java
             * 
             * if (MaskArgs.MaskException){ return callResults.FAILED; }
             */

            String appName = _appName;
            String xml = this.toXML(true);

            /*
             * if(rst != callResults.SUCCESS){ return rst; }
             */
            boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

            if (toDebugPrint && isDebug)
            {
                switch (_result) {
                case DEBUG:
                    System.out.println("Debug: " + this.getMessage());
                    break;
                case FAILED:
                    System.out.println("Warning: " + this.getMessage());
                    break;
                case INFO:
                    System.out.println("Info: " + this.getMessage());
                    break;
                case FAILED_ERROR:
                case UNKNOWN:
                    System.out.println(xml);
                    break;
                default:
                    break;
                }
            }

            if (toFile && !this.isDebugStatus() && _connector != null)
            {
                // Log to File Log
                _connector.log(appName, xml);
            }

            // Return Success
            return CallResults.SUCCESS;

        }
        catch (Exception e)
        {
            // return Failed error
            return CallResults.FAILED_ERROR;

        }
    }

    protected static String getText(final org.jdom2.Document node, final String xPath)
    {
        try
        {
            XPathExpression<org.jdom2.Element> xpath = XPathFactory.instance().compile(xPath, Filters.element());

            org.jdom2.Element emt = xpath.evaluateFirst(node);

            if (emt == null)
            {
                // node not found
                return "";
            }
            else
            {

                return emt.getText();
            }

        }
        catch (Exception ex)
        {
            // Empty string
            ex.printStackTrace();
            return "";

        }
    }

    private boolean isNumeric(final String string)
    {
        if (string.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isDate(final String dateString)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try
        {
            sdf.parse(dateString);
            return true;
        }
        catch (ParseException e)
        {
            return false;
        }
    }

    private CallResults intToEnum(final int val)
    {
        CallResults enumValue = CallResults.values()[val];
        return enumValue;

    }

    private Date intToDate(final String dateString)
    {

        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try
        {
            date = sdf.parse(dateString);
            return date;
        }
        catch (ParseException ex)
        {
            ex.printStackTrace();
            return null;
        }

    }
    // */

}
