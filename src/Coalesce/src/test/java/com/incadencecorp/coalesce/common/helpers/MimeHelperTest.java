package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.incadencecorp.coalesce.common.helpers.MimeHelper;

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

public class MimeHelperTest {

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void getExtensionForMimeTypeTests()
    {
        assertEquals("", MimeHelper.getExtensionForMimeType("application/octet-stream"));
        assertEquals("evy", MimeHelper.getExtensionForMimeType("application/envoy"));
        assertEquals("fif", MimeHelper.getExtensionForMimeType("application/fractals"));
        assertEquals("spl", MimeHelper.getExtensionForMimeType("application/futuresplash"));
        assertEquals("hta", MimeHelper.getExtensionForMimeType("application/hta"));
        assertEquals("acx", MimeHelper.getExtensionForMimeType("application/internet-property-stream"));
        assertEquals("hqx", MimeHelper.getExtensionForMimeType("application/mac-binhex40"));
        assertEquals("doc", MimeHelper.getExtensionForMimeType("application/msword"));
        assertEquals("oda", MimeHelper.getExtensionForMimeType("application/oda"));
        assertEquals("axs", MimeHelper.getExtensionForMimeType("application/olescript"));
        assertEquals("pdf", MimeHelper.getExtensionForMimeType("application/pdf"));
        assertEquals("prf", MimeHelper.getExtensionForMimeType("application/pics-rules"));
        assertEquals("p10", MimeHelper.getExtensionForMimeType("application/pkcs10"));
        assertEquals("crl", MimeHelper.getExtensionForMimeType("application/pkix-crl"));
        assertEquals("ps", MimeHelper.getExtensionForMimeType("application/postscript"));
        assertEquals("rtf", MimeHelper.getExtensionForMimeType("application/rtf"));
        assertEquals("setpay", MimeHelper.getExtensionForMimeType("application/set-payment-initiation"));
        assertEquals("setreg", MimeHelper.getExtensionForMimeType("application/set-registration-initiation"));
        assertEquals("xls", MimeHelper.getExtensionForMimeType("application/vnd.ms-excel"));
        assertEquals("msg", MimeHelper.getExtensionForMimeType("application/vnd.ms-outlook"));
        assertEquals("sst", MimeHelper.getExtensionForMimeType("application/vnd.ms-pkicertstore"));
        assertEquals("cat", MimeHelper.getExtensionForMimeType("application/vnd.ms-pkiseccat"));
        assertEquals("stl", MimeHelper.getExtensionForMimeType("application/vnd.ms-pkistl"));
        assertEquals("ppt", MimeHelper.getExtensionForMimeType("application/vnd.ms-powerpoint"));
        assertEquals("mpp", MimeHelper.getExtensionForMimeType("application/vnd.ms-project"));
        assertEquals("hlp", MimeHelper.getExtensionForMimeType("application/winhlp"));
        assertEquals("bcpio", MimeHelper.getExtensionForMimeType("application/x-bcpio"));
        assertEquals("cdf", MimeHelper.getExtensionForMimeType("application/x-cdf"));
        assertEquals("z", MimeHelper.getExtensionForMimeType("application/x-compress"));
        assertEquals("tgz", MimeHelper.getExtensionForMimeType("application/x-compressed"));
        assertEquals("cpio", MimeHelper.getExtensionForMimeType("application/x-cpio"));
        assertEquals("csh", MimeHelper.getExtensionForMimeType("application/x-csh"));
        assertEquals("dvi", MimeHelper.getExtensionForMimeType("application/x-dvi"));
        assertEquals("gtar", MimeHelper.getExtensionForMimeType("application/x-gtar"));
        assertEquals("gz", MimeHelper.getExtensionForMimeType("application/x-gzip"));
        assertEquals("hdf", MimeHelper.getExtensionForMimeType("application/x-hdf"));
        assertEquals("iii", MimeHelper.getExtensionForMimeType("application/x-iphone"));
        assertEquals("js", MimeHelper.getExtensionForMimeType("application/x-javascript"));
        assertEquals("latex", MimeHelper.getExtensionForMimeType("application/x-latex"));
        assertEquals("mdb", MimeHelper.getExtensionForMimeType("application/x-msaccess"));
        assertEquals("crd", MimeHelper.getExtensionForMimeType("application/x-mscardfile"));
        assertEquals("clp", MimeHelper.getExtensionForMimeType("application/x-msclip"));
        assertEquals("dll", MimeHelper.getExtensionForMimeType("application/x-msdownload"));
        assertEquals("wmf", MimeHelper.getExtensionForMimeType("application/x-msmetafile"));
        assertEquals("mny", MimeHelper.getExtensionForMimeType("application/x-msmoney"));
        assertEquals("pub", MimeHelper.getExtensionForMimeType("application/x-mspublisher"));
        assertEquals("scd", MimeHelper.getExtensionForMimeType("application/x-msschedule"));
        assertEquals("trm", MimeHelper.getExtensionForMimeType("application/x-msterminal"));
        assertEquals("wri", MimeHelper.getExtensionForMimeType("application/x-mswrite"));
        assertEquals("cdf", MimeHelper.getExtensionForMimeType("application/x-netcdf"));
        assertEquals("sh", MimeHelper.getExtensionForMimeType("application/x-sh"));
        assertEquals("shar", MimeHelper.getExtensionForMimeType("application/x-shar"));
        assertEquals("swf", MimeHelper.getExtensionForMimeType("application/x-shockwave-flash"));
        assertEquals("sit", MimeHelper.getExtensionForMimeType("application/x-stuffit"));
        assertEquals("sv4cpio", MimeHelper.getExtensionForMimeType("application/x-sv4cpio"));
        assertEquals("sv4crc", MimeHelper.getExtensionForMimeType("application/x-sv4crc"));
        assertEquals("tar", MimeHelper.getExtensionForMimeType("application/x-tar"));
        assertEquals("tcl", MimeHelper.getExtensionForMimeType("application/x-tcl"));
        assertEquals("tex", MimeHelper.getExtensionForMimeType("application/x-tex"));
        assertEquals("man", MimeHelper.getExtensionForMimeType("application/x-troff-man"));
        assertEquals("me", MimeHelper.getExtensionForMimeType("application/x-troff-me"));
        assertEquals("ms", MimeHelper.getExtensionForMimeType("application/x-troff-ms"));
        assertEquals("ustar", MimeHelper.getExtensionForMimeType("application/x-ustar"));
        assertEquals("src", MimeHelper.getExtensionForMimeType("application/x-wais-source"));
        assertEquals("cer", MimeHelper.getExtensionForMimeType("application/x-x509-ca-cert"));
        assertEquals("pko", MimeHelper.getExtensionForMimeType("application/ynd.ms-pkipko"));
        assertEquals("zip", MimeHelper.getExtensionForMimeType("application/zip"));
        assertEquals("au", MimeHelper.getExtensionForMimeType("audio/basic"));
        assertEquals("mid", MimeHelper.getExtensionForMimeType("audio/mid"));
        assertEquals("mp3", MimeHelper.getExtensionForMimeType("audio/mpeg"));
        assertEquals("aif", MimeHelper.getExtensionForMimeType("audio/x-aiff"));
        assertEquals("m3u", MimeHelper.getExtensionForMimeType("audio/x-mpegurl"));
        assertEquals("ra", MimeHelper.getExtensionForMimeType("audio/x-pn-realaudio"));
        assertEquals("wav", MimeHelper.getExtensionForMimeType("audio/x-wav"));
        assertEquals("bmp", MimeHelper.getExtensionForMimeType("image/bmp"));
        assertEquals("cod", MimeHelper.getExtensionForMimeType("image/cis-cod"));
        assertEquals("gif", MimeHelper.getExtensionForMimeType("image/gif"));
        assertEquals("ief", MimeHelper.getExtensionForMimeType("image/ief"));
        assertEquals("jpg", MimeHelper.getExtensionForMimeType("image/jpeg"));
        assertEquals("jpg", MimeHelper.getExtensionForMimeType("image/pjpeg"));
        assertEquals("jfif", MimeHelper.getExtensionForMimeType("image/pipeg"));
        assertEquals("png", MimeHelper.getExtensionForMimeType("image/png"));
        assertEquals("png", MimeHelper.getExtensionForMimeType("image/x-png"));
        assertEquals("svg", MimeHelper.getExtensionForMimeType("image/svg+xml"));
        assertEquals("tif", MimeHelper.getExtensionForMimeType("image/tiff"));
        assertEquals("ras", MimeHelper.getExtensionForMimeType("image/x-cmu-raster"));
        assertEquals("cmx", MimeHelper.getExtensionForMimeType("image/x-cmx"));
        assertEquals("ico", MimeHelper.getExtensionForMimeType("image/x-icon"));
        assertEquals("pnm", MimeHelper.getExtensionForMimeType("image/x-portable-anymap"));
        assertEquals("pbm", MimeHelper.getExtensionForMimeType("image/x-portable-bitmap"));
        assertEquals("pgm", MimeHelper.getExtensionForMimeType("image/x-portable-graymap"));
        assertEquals("ppm", MimeHelper.getExtensionForMimeType("image/x-portable-pixmap"));
        assertEquals("rgb", MimeHelper.getExtensionForMimeType("image/x-rgb"));
        assertEquals("xbm", MimeHelper.getExtensionForMimeType("image/x-xbitmap"));
        assertEquals("xpm", MimeHelper.getExtensionForMimeType("image/x-xpixmap"));
        assertEquals("xwd", MimeHelper.getExtensionForMimeType("image/x-xwindowdump"));
        assertEquals("mht", MimeHelper.getExtensionForMimeType("message/rfc822"));
        assertEquals("css", MimeHelper.getExtensionForMimeType("text/css"));
        assertEquals("323", MimeHelper.getExtensionForMimeType("text/h323"));
        assertEquals("htm", MimeHelper.getExtensionForMimeType("text/html"));
        assertEquals("uls", MimeHelper.getExtensionForMimeType("text/iuls"));
        assertEquals("txt", MimeHelper.getExtensionForMimeType("text/plain"));
        assertEquals("rtx", MimeHelper.getExtensionForMimeType("text/richtext"));
        assertEquals("sct", MimeHelper.getExtensionForMimeType("text/scriptlet"));
        assertEquals("tsv", MimeHelper.getExtensionForMimeType("text/tab-separated-values"));
        assertEquals("htt", MimeHelper.getExtensionForMimeType("text/webviewhtml"));
        assertEquals("htc", MimeHelper.getExtensionForMimeType("text/x-component"));
        assertEquals("etx", MimeHelper.getExtensionForMimeType("text/x-setext"));
        assertEquals("vcf", MimeHelper.getExtensionForMimeType("text/x-vcard"));
        assertEquals("mpg", MimeHelper.getExtensionForMimeType("video/mpeg"));
        assertEquals("mov", MimeHelper.getExtensionForMimeType("video/quicktime"));
        assertEquals("lsf", MimeHelper.getExtensionForMimeType("video/x-la-asf"));
        assertEquals("asf", MimeHelper.getExtensionForMimeType("video/x-ms-asf"));
        assertEquals("avi", MimeHelper.getExtensionForMimeType("video/x-msvideo"));
        assertEquals("movie", MimeHelper.getExtensionForMimeType("video/x-sgi-movie"));
        assertEquals("vrml", MimeHelper.getExtensionForMimeType("x-world/x-vrml"));
        assertEquals("docm", MimeHelper.getExtensionForMimeType("application/vnd.ms-word.document.macroEnabled.12"));
        assertEquals("docx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        assertEquals("dotm", MimeHelper.getExtensionForMimeType("application/vnd.ms-word.template.macroEnabled.12"));
        assertEquals("dotx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.template"));
        assertEquals("potm", MimeHelper.getExtensionForMimeType("application/vnd.ms-powerpoint.template.macroEnabled.12"));
        assertEquals("potx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.presentationml.template"));
        assertEquals("ppam", MimeHelper.getExtensionForMimeType("application/vnd.ms-powerpoint.addin.macroEnabled.12"));
        assertEquals("ppsm", MimeHelper.getExtensionForMimeType("application/vnd.ms-powerpoint.slideshow.macroEnabled.12"));
        assertEquals("ppsx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.presentationml.slideshow"));
        assertEquals("pptm",
                     MimeHelper.getExtensionForMimeType("application/vnd.ms-powerpoint.presentation.macroEnabled.12"));
        assertEquals("pptx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        assertEquals("xlam", MimeHelper.getExtensionForMimeType("application/vnd.ms-excel.addin.macroEnabled.12"));
        assertEquals("xlsb", MimeHelper.getExtensionForMimeType("application/vnd.ms-excel.sheet.binary.macroEnabled.12"));
        assertEquals("xlsm", MimeHelper.getExtensionForMimeType("application/vnd.ms-excel.sheet.macroEnabled.12"));
        assertEquals("xlsx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertEquals("xltm", MimeHelper.getExtensionForMimeType("application/vnd.ms-excel.template.macroEnabled.12"));
        assertEquals("xltx",
                     MimeHelper.getExtensionForMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.template"));

        assertEquals("", MimeHelper.getExtensionForMimeType("Unknown"));
        assertEquals("", MimeHelper.getExtensionForMimeType(""));
        assertEquals("", MimeHelper.getExtensionForMimeType("  "));

    }

    @Test
    public void getExtensionForMimeTypeNullTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("mimeType");

        MimeHelper.getExtensionForMimeType(null);

    }

    @Test
    public void getMimetypeforExtensionTests()
    {

        assertEquals("application/internet-property-stream", MimeHelper.getMimeTypeForExtension("acx"));
        assertEquals("application/postscript", MimeHelper.getMimeTypeForExtension(".ai"));
        assertEquals("audio/x-aiff", MimeHelper.getMimeTypeForExtension("aif"));
        assertEquals("audio/x-aiff", MimeHelper.getMimeTypeForExtension(".aifc"));
        assertEquals("audio/x-aiff", MimeHelper.getMimeTypeForExtension("aiff"));
        assertEquals("video/x-ms-asf", MimeHelper.getMimeTypeForExtension(".asf"));
        assertEquals("video/x-ms-asf", MimeHelper.getMimeTypeForExtension("asr"));
        assertEquals("video/x-ms-asf", MimeHelper.getMimeTypeForExtension(".asx"));
        assertEquals("audio/basic", MimeHelper.getMimeTypeForExtension(".au"));
        assertEquals("video/x-msvideo", MimeHelper.getMimeTypeForExtension("avi"));
        assertEquals("application/olescript", MimeHelper.getMimeTypeForExtension(".axs"));
        assertEquals("text/plain", MimeHelper.getMimeTypeForExtension("bas"));
        assertEquals("application/x-bcpio", MimeHelper.getMimeTypeForExtension("bcpio"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension(".bin"));
        assertEquals("image/bmp", MimeHelper.getMimeTypeForExtension("bmp"));
        assertEquals("text/plain", MimeHelper.getMimeTypeForExtension(".c"));
        assertEquals("application/vnd.ms-pkiseccat", MimeHelper.getMimeTypeForExtension("cat"));
        assertEquals("application/x-cdf", MimeHelper.getMimeTypeForExtension(".cdf"));
        assertEquals("application/x-x509-ca-cert", MimeHelper.getMimeTypeForExtension("cer"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension(".class"));
        assertEquals("application/x-msclip", MimeHelper.getMimeTypeForExtension("clp"));
        assertEquals("image/x-cmx", MimeHelper.getMimeTypeForExtension("cmx"));
        assertEquals("image/cis-cod", MimeHelper.getMimeTypeForExtension("cod"));
        assertEquals("application/x-cpio", MimeHelper.getMimeTypeForExtension("cpio"));
        assertEquals("application/x-mscardfile", MimeHelper.getMimeTypeForExtension("crd"));
        assertEquals("application/pkix-crl", MimeHelper.getMimeTypeForExtension("crl"));
        assertEquals("application/x-x509-ca-cert", MimeHelper.getMimeTypeForExtension("crt"));
        assertEquals("application/x-csh", MimeHelper.getMimeTypeForExtension("csh"));
        assertEquals("text/css", MimeHelper.getMimeTypeForExtension("css"));
        assertEquals("application/x-director", MimeHelper.getMimeTypeForExtension("dcr"));
        assertEquals("application/x-x509-ca-cert", MimeHelper.getMimeTypeForExtension("der"));
        assertEquals("application/x-director", MimeHelper.getMimeTypeForExtension("dir"));
        assertEquals("application/x-msdownload", MimeHelper.getMimeTypeForExtension("dll"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension("dms"));
        assertEquals("application/msword", MimeHelper.getMimeTypeForExtension("doc"));
        assertEquals("application/msword", MimeHelper.getMimeTypeForExtension("dot"));
        assertEquals("application/x-dvi", MimeHelper.getMimeTypeForExtension("dvi"));
        assertEquals("application/x-director", MimeHelper.getMimeTypeForExtension("dxr"));
        assertEquals("application/postscript", MimeHelper.getMimeTypeForExtension("eps"));
        assertEquals("text/x-setext", MimeHelper.getMimeTypeForExtension("etx"));
        assertEquals("application/envoy", MimeHelper.getMimeTypeForExtension("evy"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension("exe"));
        assertEquals("application/fractals", MimeHelper.getMimeTypeForExtension("fif"));
        assertEquals("x-world/x-vrml", MimeHelper.getMimeTypeForExtension("flr"));
        assertEquals("image/gif", MimeHelper.getMimeTypeForExtension("gif"));
        assertEquals("application/x-gtar", MimeHelper.getMimeTypeForExtension("gtar"));
        assertEquals("application/x-gzip", MimeHelper.getMimeTypeForExtension("gz"));
        assertEquals("text/plain", MimeHelper.getMimeTypeForExtension("h"));
        assertEquals("application/x-hdf", MimeHelper.getMimeTypeForExtension("hdf"));
        assertEquals("application/winhlp", MimeHelper.getMimeTypeForExtension("hlp"));
        assertEquals("application/mac-binhex40", MimeHelper.getMimeTypeForExtension("hqx"));
        assertEquals("application/hta", MimeHelper.getMimeTypeForExtension("hta"));
        assertEquals("text/x-component", MimeHelper.getMimeTypeForExtension("htc"));
        assertEquals("text/html", MimeHelper.getMimeTypeForExtension("htm"));
        assertEquals("text/html", MimeHelper.getMimeTypeForExtension("html"));
        assertEquals("text/webviewhtml", MimeHelper.getMimeTypeForExtension("htt"));
        assertEquals("image/x-icon", MimeHelper.getMimeTypeForExtension("ico"));
        assertEquals("image/ief", MimeHelper.getMimeTypeForExtension("ief"));
        assertEquals("application/x-iphone", MimeHelper.getMimeTypeForExtension("iii"));
        assertEquals("application/x-internet-signup", MimeHelper.getMimeTypeForExtension("ins"));
        assertEquals("application/x-internet-signup", MimeHelper.getMimeTypeForExtension("isp"));
        assertEquals("image/pipeg", MimeHelper.getMimeTypeForExtension("jfif"));
        assertEquals("image/jpeg", MimeHelper.getMimeTypeForExtension("jpe"));
        assertEquals("image/jpeg", MimeHelper.getMimeTypeForExtension("jpeg"));
        assertEquals("image/jpeg", MimeHelper.getMimeTypeForExtension("jpg"));
        assertEquals("application/x-javascript", MimeHelper.getMimeTypeForExtension("js"));
        assertEquals("application/json", MimeHelper.getMimeTypeForExtension("json"));
        assertEquals("application/x-latex", MimeHelper.getMimeTypeForExtension("latex"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension("lha"));
        assertEquals("video/x-la-asf", MimeHelper.getMimeTypeForExtension("lsf"));
        assertEquals("video/x-la-asf", MimeHelper.getMimeTypeForExtension("lsx"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension("lzh"));
        assertEquals("application/x-msmediaview", MimeHelper.getMimeTypeForExtension("m13"));
        assertEquals("application/x-msmediaview", MimeHelper.getMimeTypeForExtension("m14"));
        assertEquals("audio/x-mpegurl", MimeHelper.getMimeTypeForExtension("m3u"));
        assertEquals("application/x-troff-man", MimeHelper.getMimeTypeForExtension("man"));
        assertEquals("application/x-msaccess", MimeHelper.getMimeTypeForExtension("mdb"));
        assertEquals("application/x-troff-me", MimeHelper.getMimeTypeForExtension("me"));
        assertEquals("message/rfc822", MimeHelper.getMimeTypeForExtension("mht"));
        assertEquals("message/rfc822", MimeHelper.getMimeTypeForExtension("mhtml"));
        assertEquals("audio/mid", MimeHelper.getMimeTypeForExtension("mid"));
        assertEquals("application/x-msmoney", MimeHelper.getMimeTypeForExtension("mny"));
        assertEquals("video/quicktime", MimeHelper.getMimeTypeForExtension("mov"));
        assertEquals("video/x-sgi-movie", MimeHelper.getMimeTypeForExtension("movie"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mp2"));
        assertEquals("audio/mpeg", MimeHelper.getMimeTypeForExtension("mp3"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mp4"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mpa"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mpe"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mpeg"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mpg"));
        assertEquals("application/vnd.ms-project", MimeHelper.getMimeTypeForExtension("mpp"));
        assertEquals("video/mpeg", MimeHelper.getMimeTypeForExtension("mpv2"));
        assertEquals("application/x-troff-ms", MimeHelper.getMimeTypeForExtension("ms"));
        assertEquals("application/x-msmediaview", MimeHelper.getMimeTypeForExtension("mvb"));
        assertEquals("message/rfc822", MimeHelper.getMimeTypeForExtension("nws"));
        assertEquals("application/oda", MimeHelper.getMimeTypeForExtension("oda"));
        assertEquals("application/pkcs10", MimeHelper.getMimeTypeForExtension("p10"));
        assertEquals("application/x-pkcs12", MimeHelper.getMimeTypeForExtension("p12"));
        assertEquals("application/x-pkcs7-certificates", MimeHelper.getMimeTypeForExtension("p7b"));
        assertEquals("application/x-pkcs7-mime", MimeHelper.getMimeTypeForExtension("p7c"));
        assertEquals("application/x-pkcs7-mime", MimeHelper.getMimeTypeForExtension("p7m"));
        assertEquals("application/x-pkcs7-certreqresp", MimeHelper.getMimeTypeForExtension("p7r"));
        assertEquals("application/x-pkcs7-signature", MimeHelper.getMimeTypeForExtension("p7s"));
        assertEquals("image/x-portable-bitmap", MimeHelper.getMimeTypeForExtension("pbm"));
        assertEquals("application/pdf", MimeHelper.getMimeTypeForExtension("pdf"));
        assertEquals("application/x-pkcs12", MimeHelper.getMimeTypeForExtension("pfx"));
        assertEquals("image/x-portable-graymap", MimeHelper.getMimeTypeForExtension("pgm"));
        assertEquals("application/ynd.ms-pkipko", MimeHelper.getMimeTypeForExtension("pko"));
        assertEquals("application/x-perfmon", MimeHelper.getMimeTypeForExtension("pma"));
        assertEquals("application/x-perfmon", MimeHelper.getMimeTypeForExtension("pmc"));
        assertEquals("application/x-perfmon", MimeHelper.getMimeTypeForExtension("pml"));
        assertEquals("application/x-perfmon", MimeHelper.getMimeTypeForExtension("pmr"));
        assertEquals("application/x-perfmon", MimeHelper.getMimeTypeForExtension("pmw"));
        assertEquals("image/x-png", MimeHelper.getMimeTypeForExtension("png"));
        assertEquals("image/x-portable-anymap", MimeHelper.getMimeTypeForExtension("pnm"));
        assertEquals("application/vnd.ms-powerpoint", MimeHelper.getMimeTypeForExtension("pot"));
        assertEquals("image/x-portable-pixmap", MimeHelper.getMimeTypeForExtension("ppm"));
        assertEquals("application/vnd.ms-powerpoint", MimeHelper.getMimeTypeForExtension("pps"));
        assertEquals("application/vnd.ms-powerpoint", MimeHelper.getMimeTypeForExtension("ppt"));
        assertEquals("application/pics-rules", MimeHelper.getMimeTypeForExtension("prf"));
        assertEquals("application/postscript", MimeHelper.getMimeTypeForExtension("ps"));
        assertEquals("application/x-mspublisher", MimeHelper.getMimeTypeForExtension("pub"));
        assertEquals("video/quicktime", MimeHelper.getMimeTypeForExtension("qt"));
        assertEquals("audio/x-pn-realaudio", MimeHelper.getMimeTypeForExtension("ra"));
        assertEquals("audio/x-pn-realaudio", MimeHelper.getMimeTypeForExtension("ram"));
        assertEquals("image/x-cmu-raster", MimeHelper.getMimeTypeForExtension("ras"));
        assertEquals("image/x-rgb", MimeHelper.getMimeTypeForExtension("rgb"));
        assertEquals("audio/mid", MimeHelper.getMimeTypeForExtension("rmi"));
        assertEquals("application/x-troff", MimeHelper.getMimeTypeForExtension("roff"));
        assertEquals("application/rtf", MimeHelper.getMimeTypeForExtension("rtf"));
        assertEquals("text/richtext", MimeHelper.getMimeTypeForExtension("rtx"));
        assertEquals("application/x-msschedule", MimeHelper.getMimeTypeForExtension("scd"));
        assertEquals("text/scriptlet", MimeHelper.getMimeTypeForExtension("sct"));
        assertEquals("application/set-payment-initiation", MimeHelper.getMimeTypeForExtension("setpay"));
        assertEquals("application/set-registration-initiation", MimeHelper.getMimeTypeForExtension("setreg"));
        assertEquals("application/x-sh", MimeHelper.getMimeTypeForExtension("sh"));
        assertEquals("application/x-shar", MimeHelper.getMimeTypeForExtension("shar"));
        assertEquals("application/x-stuffit", MimeHelper.getMimeTypeForExtension("sit"));
        assertEquals("audio/basic", MimeHelper.getMimeTypeForExtension("snd"));
        assertEquals("application/x-pkcs7-certificates", MimeHelper.getMimeTypeForExtension("spc"));
        assertEquals("application/futuresplash", MimeHelper.getMimeTypeForExtension("spl"));
        assertEquals("application/x-wais-source", MimeHelper.getMimeTypeForExtension("src"));
        assertEquals("application/vnd.ms-pkicertstore", MimeHelper.getMimeTypeForExtension("sst"));
        assertEquals("application/vnd.ms-pkistl", MimeHelper.getMimeTypeForExtension("stl"));
        assertEquals("text/html", MimeHelper.getMimeTypeForExtension("stm"));
        assertEquals("image/svg+xml", MimeHelper.getMimeTypeForExtension("svg"));
        assertEquals("application/x-sv4cpio", MimeHelper.getMimeTypeForExtension("sv4cpio"));
        assertEquals("application/x-sv4crc", MimeHelper.getMimeTypeForExtension("sv4crc"));
        assertEquals("application/x-shockwave-flash", MimeHelper.getMimeTypeForExtension("swf"));
        assertEquals("application/x-troff", MimeHelper.getMimeTypeForExtension("t"));
        assertEquals("application/x-tar", MimeHelper.getMimeTypeForExtension("tar"));
        assertEquals("application/x-tcl", MimeHelper.getMimeTypeForExtension("tcl"));
        assertEquals("application/x-tex", MimeHelper.getMimeTypeForExtension("tex"));
        assertEquals("application/x-texinfo", MimeHelper.getMimeTypeForExtension("texi"));
        assertEquals("application/x-texinfo", MimeHelper.getMimeTypeForExtension("texinfo"));
        assertEquals("application/x-compressed", MimeHelper.getMimeTypeForExtension("tgz"));
        assertEquals("image/tiff", MimeHelper.getMimeTypeForExtension("tif"));
        assertEquals("image/tiff", MimeHelper.getMimeTypeForExtension("tiff"));
        assertEquals("application/x-troff", MimeHelper.getMimeTypeForExtension("tr"));
        assertEquals("application/x-msterminal", MimeHelper.getMimeTypeForExtension("trm"));
        assertEquals("text/tab-separated-values", MimeHelper.getMimeTypeForExtension("tsv"));
        assertEquals("text/plain", MimeHelper.getMimeTypeForExtension("txt"));
        assertEquals("text/iuls", MimeHelper.getMimeTypeForExtension("uls"));
        assertEquals("application/x-www-form-urlencoded", MimeHelper.getMimeTypeForExtension("urlencode"));
        assertEquals("application/x-ustar", MimeHelper.getMimeTypeForExtension("ustar"));
        assertEquals("text/x-vcard", MimeHelper.getMimeTypeForExtension("vcf"));
        assertEquals("x-world/x-vrml", MimeHelper.getMimeTypeForExtension("vrml"));
        assertEquals("audio/x-wav", MimeHelper.getMimeTypeForExtension("wav"));
        assertEquals("application/vnd.ms-works", MimeHelper.getMimeTypeForExtension("wcm"));
        assertEquals("application/vnd.ms-works", MimeHelper.getMimeTypeForExtension("wdb"));
        assertEquals("application/vnd.ms-works", MimeHelper.getMimeTypeForExtension("wks"));
        assertEquals("application/x-msmetafile", MimeHelper.getMimeTypeForExtension("wmf"));
        assertEquals("application/vnd.ms-works", MimeHelper.getMimeTypeForExtension("wps"));
        assertEquals("application/x-mswrite", MimeHelper.getMimeTypeForExtension("wri"));
        assertEquals("x-world/x-vrml", MimeHelper.getMimeTypeForExtension("wrl"));
        assertEquals("x-world/x-vrml", MimeHelper.getMimeTypeForExtension("wrz"));
        assertEquals("x-world/x-vrml", MimeHelper.getMimeTypeForExtension("xaf"));
        assertEquals("image/x-xbitmap", MimeHelper.getMimeTypeForExtension("xbm"));
        assertEquals("application/vnd.ms-excel", MimeHelper.getMimeTypeForExtension("xla"));
        assertEquals("application/vnd.ms-excel", MimeHelper.getMimeTypeForExtension("xlc"));
        assertEquals("application/vnd.ms-excel", MimeHelper.getMimeTypeForExtension("xlm"));
        assertEquals("application/vnd.ms-excel", MimeHelper.getMimeTypeForExtension("xls"));
        assertEquals("application/vnd.ms-excel", MimeHelper.getMimeTypeForExtension("xlt"));
        assertEquals("application/vnd.ms-excel", MimeHelper.getMimeTypeForExtension("xlw"));
        assertEquals("x-world/x-vrml", MimeHelper.getMimeTypeForExtension("xof"));
        assertEquals("image/x-xpixmap", MimeHelper.getMimeTypeForExtension("xpm"));
        assertEquals("image/x-xwindowdump", MimeHelper.getMimeTypeForExtension("xwd"));
        assertEquals("application/x-compress", MimeHelper.getMimeTypeForExtension("z"));
        assertEquals("application/zip", MimeHelper.getMimeTypeForExtension("zip"));
        assertEquals("application/msaccess", MimeHelper.getMimeTypeForExtension("accdb"));
        assertEquals("application/msaccess", MimeHelper.getMimeTypeForExtension("accde"));
        assertEquals("application/msaccess", MimeHelper.getMimeTypeForExtension("accdt"));
        assertEquals("application/vnd.ms-word.document.macroEnabled.12", MimeHelper.getMimeTypeForExtension("docm"));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                     MimeHelper.getMimeTypeForExtension("docx"));
        assertEquals("application/vnd.ms-word.template.macroEnabled.12", MimeHelper.getMimeTypeForExtension("dotm"));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.template",
                     MimeHelper.getMimeTypeForExtension("dotx"));
        assertEquals("application/vnd.ms-powerpoint.template.macroEnabled.12", MimeHelper.getMimeTypeForExtension("potm"));
        assertEquals("application/vnd.openxmlformats-officedocument.presentationml.template",
                     MimeHelper.getMimeTypeForExtension("potx"));
        assertEquals("application/vnd.ms-powerpoint.addin.macroEnabled.12", MimeHelper.getMimeTypeForExtension("ppam"));
        assertEquals("application/vnd.ms-powerpoint.slideshow.macroEnabled.12", MimeHelper.getMimeTypeForExtension("ppsm"));
        assertEquals("application/vnd.openxmlformats-officedocument.presentationml.slideshow",
                     MimeHelper.getMimeTypeForExtension("ppsx"));
        assertEquals("application/vnd.ms-powerpoint.presentation.macroEnabled.12",
                     MimeHelper.getMimeTypeForExtension("pptm"));
        assertEquals("application/vnd.openxmlformats-officedocument.presentationml.presentation",
                     MimeHelper.getMimeTypeForExtension("pptx"));
        assertEquals("application/vnd.ms-excel.addin.macroEnabled.12", MimeHelper.getMimeTypeForExtension("xlam"));
        assertEquals("application/vnd.ms-excel.sheet.binary.macroEnabled.12", MimeHelper.getMimeTypeForExtension("xlsb"));
        assertEquals("application/vnd.ms-excel.sheet.macroEnabled.12", MimeHelper.getMimeTypeForExtension("xlsm"));
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                     MimeHelper.getMimeTypeForExtension("xlsx"));
        assertEquals("application/vnd.ms-excel.template.macroEnabled.12", MimeHelper.getMimeTypeForExtension("xltm"));
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.template",
                     MimeHelper.getMimeTypeForExtension("xltx"));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension("Else"));

        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension(""));
        assertEquals("application/octet-stream", MimeHelper.getMimeTypeForExtension("  "));

    }

    @Test
    public void getMimeTypeForExtensionNullTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("extension");

        MimeHelper.getMimeTypeForExtension(null);

    }

    @Test
    public void getFileTypeForMimeTypeTest()
    {

        assertEquals("Bitmap (BMP) Image", MimeHelper.getFileTypeForMimeType("image/bmp"));
        assertEquals("Graphics Interchange Format (GIF) Image", MimeHelper.getFileTypeForMimeType("image/gif"));
        assertEquals("Joint Photographic Experts Group (JPEG) Image", MimeHelper.getFileTypeForMimeType("image/jpeg"));
        assertEquals("Portable Network Graphic (PNG) Image", MimeHelper.getFileTypeForMimeType("image/x-png"));
        assertEquals("Tagged Image File Format (TIFF) Image", MimeHelper.getFileTypeForMimeType("image/tiff"));
        assertEquals("ELSE Image", MimeHelper.getFileTypeForMimeType("image/else"));
        assertEquals("Image", MimeHelper.getFileTypeForMimeType("image/"));

        assertEquals("Text Document", MimeHelper.getFileTypeForMimeType("text/plain"));
        assertEquals("HTML Document", MimeHelper.getFileTypeForMimeType("text/html"));
        assertEquals("ELSE Document", MimeHelper.getFileTypeForMimeType("text/else"));
        assertEquals("Document", MimeHelper.getFileTypeForMimeType("text/ "));

        assertEquals("Moving Picture Experts Group (MPEG) Audio", MimeHelper.getFileTypeForMimeType("audio/mpeg"));
        assertEquals("Musical Instrument Digital Interface (MIDI) Audio", MimeHelper.getFileTypeForMimeType("audio/mid"));
        assertEquals("Waveform Audio File (WAV)", MimeHelper.getFileTypeForMimeType("audio/x-wav"));
        assertEquals("Audio File", MimeHelper.getFileTypeForMimeType("audio/basic"));
        assertEquals("ELSE File", MimeHelper.getFileTypeForMimeType("audio/else"));
        assertEquals("File", MimeHelper.getFileTypeForMimeType("audio/  "));

        assertEquals("Quicktime Video", MimeHelper.getFileTypeForMimeType("video/quicktime"));
        assertEquals("Moving Picture Experts Group (MPEG) Video", MimeHelper.getFileTypeForMimeType("video/mpeg"));
        assertEquals("Audio Video Interleave (AVI) Video", MimeHelper.getFileTypeForMimeType("video/x-msvideo"));
        assertEquals("ELSE Video", MimeHelper.getFileTypeForMimeType("video/else"));
        assertEquals("ELSE Video", MimeHelper.getFileTypeForMimeType("video/ else"));

        assertEquals("Microsoft Word 97-2003 Document", MimeHelper.getFileTypeForMimeType("application/msword"));
        assertEquals("Microsoft Word Document",
                     MimeHelper.getFileTypeForMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        assertEquals("Microsoft Access Database", MimeHelper.getFileTypeForMimeType("application/x-msaccess"));
        assertEquals("Microsoft Money", MimeHelper.getFileTypeForMimeType("application/x-msmoney"));
        assertEquals("Adobe Portable Document Format", MimeHelper.getFileTypeForMimeType("application/pdf"));
        assertEquals("Microsoft Project", MimeHelper.getFileTypeForMimeType("application/vnd.ms-project"));
        assertEquals("Microsoft PowerPoint 97-2003 Presentation",
                     MimeHelper.getFileTypeForMimeType("application/vnd.ms-powerpoint"));
        assertEquals("Microsoft PowerPoint Presentation",
                     MimeHelper.getFileTypeForMimeType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        assertEquals("Microsoft Publisher", MimeHelper.getFileTypeForMimeType("application/x-mspublisher"));
        assertEquals("Microsoft Works Document", MimeHelper.getFileTypeForMimeType("application/vnd.ms-works"));
        assertEquals("Microsoft Write", MimeHelper.getFileTypeForMimeType("application/x-mswrite"));
        assertEquals("Rich Text Format Document", MimeHelper.getFileTypeForMimeType("application/rtf"));
        assertEquals("Microsoft Excel 97-2003 Spreadsheet", MimeHelper.getFileTypeForMimeType("application/vnd.ms-excel"));
        assertEquals("Microsoft Excel Spreadsheet",
                     MimeHelper.getFileTypeForMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertEquals("GTAR Archive", MimeHelper.getFileTypeForMimeType("application/x-gtar"));
        assertEquals("GZIP Archive", MimeHelper.getFileTypeForMimeType("application/x-gzip"));
        assertEquals("ZIP Archive", MimeHelper.getFileTypeForMimeType("application/zip"));
        assertEquals("ELSE Document", MimeHelper.getFileTypeForMimeType("application/ElSe"));
        assertEquals("File (Unidentified Format)", MimeHelper.getFileTypeForMimeType("application"));

        assertEquals("SOMETHINGELSE File", MimeHelper.getFileTypeForMimeType("else/somethingElse"));

        assertEquals("File (Unidentified Format)", MimeHelper.getFileTypeForMimeType(""));
        assertEquals("SOMETHINGELSE SOMETHING File", MimeHelper.getFileTypeForMimeType("else/somethingElse Something"));

        assertEquals("File (Unidentified Format)", MimeHelper.getFileTypeForMimeType("/"));
        assertEquals("AMIME File", MimeHelper.getFileTypeForMimeType("/aMime"));
        assertEquals("File (Unidentified Format)", MimeHelper.getFileTypeForMimeType("aMime/"));

    }

    @Test
    public void getFileTypeForMimeTypeNullTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("mimeType");

        MimeHelper.getFileTypeForMimeType(null);

    }

    @Test
    public void getFileTypeForExtensionTest()
    {
        assertEquals("Bitmap (BMP) Image", MimeHelper.getFileTypeForExtension("bmp"));
        assertEquals("Graphics Interchange Format (GIF) Image", MimeHelper.getFileTypeForExtension("gIf"));
        assertEquals("Joint Photographic Experts Group (JPEG) Image", MimeHelper.getFileTypeForExtension("JpEg"));
        assertEquals("Portable Network Graphic (PNG) Image", MimeHelper.getFileTypeForExtension("PnG"));
        assertEquals("Tagged Image File Format (TIFF) Image", MimeHelper.getFileTypeForExtension("tiff"));

        assertEquals("Text Document", MimeHelper.getFileTypeForExtension("txt"));
        assertEquals("HTML Document", MimeHelper.getFileTypeForExtension("html"));

        assertEquals("Moving Picture Experts Group (MPEG) Audio", MimeHelper.getFileTypeForExtension("mp3"));
        assertEquals("Musical Instrument Digital Interface (MIDI) Audio", MimeHelper.getFileTypeForExtension("mid"));
        assertEquals("Waveform Audio File (WAV)", MimeHelper.getFileTypeForExtension("wav"));
        assertEquals("Audio File", MimeHelper.getFileTypeForExtension("au"));

        assertEquals("Quicktime Video", MimeHelper.getFileTypeForExtension("mov"));
        assertEquals("Moving Picture Experts Group (MPEG) Video", MimeHelper.getFileTypeForExtension("mp4"));
        assertEquals("Audio Video Interleave (AVI) Video", MimeHelper.getFileTypeForExtension("avi"));

        assertEquals("Microsoft Word 97-2003 Document", MimeHelper.getFileTypeForExtension("doc"));
        assertEquals("Microsoft Word Document", MimeHelper.getFileTypeForExtension("docx"));
        assertEquals("Microsoft Access Database", MimeHelper.getFileTypeForExtension("mdb"));
        assertEquals("MSACCESS Document", MimeHelper.getFileTypeForExtension("accdb"));
        assertEquals("Microsoft Money", MimeHelper.getFileTypeForExtension("mny"));
        assertEquals("Adobe Portable Document Format", MimeHelper.getFileTypeForExtension("pdf"));
        assertEquals("Microsoft Project", MimeHelper.getFileTypeForExtension("mpp"));
        assertEquals("Microsoft PowerPoint 97-2003 Presentation", MimeHelper.getFileTypeForExtension("ppt"));
        assertEquals("Microsoft PowerPoint Presentation", MimeHelper.getFileTypeForExtension("pptx"));
        assertEquals("Microsoft Publisher", MimeHelper.getFileTypeForExtension("pub"));
        assertEquals("Microsoft Works Document", MimeHelper.getFileTypeForExtension("wcm"));
        assertEquals("Microsoft Write", MimeHelper.getFileTypeForExtension("wri"));
        assertEquals("Rich Text Format Document", MimeHelper.getFileTypeForExtension("rtf"));
        assertEquals("Microsoft Excel 97-2003 Spreadsheet", MimeHelper.getFileTypeForExtension("xls"));
        assertEquals("Microsoft Excel Spreadsheet", MimeHelper.getFileTypeForExtension("xlsx"));
        assertEquals("GTAR Archive", MimeHelper.getFileTypeForExtension("gtar"));
        assertEquals("GZIP Archive", MimeHelper.getFileTypeForExtension("gz"));
        assertEquals("ZIP Archive", MimeHelper.getFileTypeForExtension("zip"));

        assertEquals("OCTET-STREAM Document", MimeHelper.getFileTypeForExtension(""));
        assertEquals("OCTET-STREAM Document", MimeHelper.getFileTypeForExtension("xyz"));
        assertEquals("OCTET-STREAM Document", MimeHelper.getFileTypeForExtension("else/somethingElse Something"));

    }

    @Test
    public void getFileTypeForExtensionNullTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("extension");

        MimeHelper.getFileTypeForExtension(null);

    }
}
