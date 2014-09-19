package Coalesce.Common.Helpers;

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

public class MimeHelper {

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    public static String GetExtensionForMimeType(String mimeType)
    {
        String extension;

        switch (mimeType.toLowerCase()) {
        case "application/octet-stream":
            extension = ""; // No extension, this is a blob field of no specific mime type.
            break;
        case "application/envoy":
            extension = "evy";
            break;
        case "application/fractals":
            extension = "fif";
            break;
        case "application/futuresplash":
            extension = "spl";
            break;
        case "application/hta":
            extension = "hta";
            break;
        case "application/internet-property-stream":
            extension = "acx";
            break;
        case "application/mac-binhex40":
            extension = "hqx";
            break;
        case "application/msword":
            extension = "doc";
            break;
        case "application/oda":
            extension = "oda";
            break;
        case "application/olescript":
            extension = "axs";
            break;
        case "application/pdf":
            extension = "pdf";
            break;
        case "application/pics-rules":
            extension = "prf";
            break;
        case "application/pkcs10":
            extension = "p10";
            break;
        case "application/pkix-crl":
            extension = "crl";
            break;
        case "application/postscript":
            extension = "ps";
            break;
        case "application/rtf":
            extension = "rtf";
            break;
        case "application/set-payment-initiation":
            extension = "setpay";
            break;
        case "application/set-registration-initiation":
            extension = "setreg";
            break;
        case "application/vnd.ms-excel":
            extension = "xls";
            break;
        case "application/vnd.ms-outlook":
            extension = "msg";
            break;
        case "application/vnd.ms-pkicertstore":
            extension = "sst";
            break;
        case "application/vnd.ms-pkiseccat":
            extension = "cat";
            break;
        case "application/vnd.ms-pkistl":
            extension = "stl";
            break;
        case "application/vnd.ms-powerpoint":
            extension = "ppt";
            break;
        case "application/vnd.ms-project":
            extension = "mpp";
            break;
        case "application/winhlp":
            extension = "hlp";
            break;
        case "application/x-bcpio":
            extension = "bcpio";
            break;
        case "application/x-cdf":
            extension = "cdf";
            break;
        case "application/x-compress":
            extension = "z";
            break;
        case "application/x-compressed":
            extension = "tgz";
            break;
        case "application/x-cpio":
            extension = "cpio";
            break;
        case "application/x-csh":
            extension = "csh";
            break;
        case "application/x-dvi":
            extension = "dvi";
            break;
        case "application/x-gtar":
            extension = "gtar";
            break;
        case "application/x-gzip":
            extension = "gz";
            break;
        case "application/x-hdf":
            extension = "hdf";
            break;
        case "application/x-iphone":
            extension = "iii";
            break;
        case "application/x-javascript":
            extension = "js";
            break;
        case "application/x-latex":
            extension = "latex";
            break;
        case "application/x-msaccess":
            extension = "mdb";
            break;
        case "application/x-mscardfile":
            extension = "crd";
            break;
        case "application/x-msclip":
            extension = "clp";
            break;
        case "application/x-msdownload":
            extension = "dll";
            break;
        case "application/x-msmetafile":
            extension = "wmf";
            break;
        case "application/x-msmoney":
            extension = "mny";
            break;
        case "application/x-mspublisher":
            extension = "pub";
            break;
        case "application/x-msschedule":
            extension = "scd";
            break;
        case "application/x-msterminal":
            extension = "trm";
            break;
        case "application/x-mswrite":
            extension = "wri";
            break;
        case "application/x-netcdf":
            extension = "cdf";
            break;
        case "application/x-sh":
            extension = "sh";
            break;
        case "application/x-shar":
            extension = "shar";
            break;
        case "application/x-shockwave-flash":
            extension = "swf";
            break;
        case "application/x-stuffit":
            extension = "sit";
            break;
        case "application/x-sv4cpio":
            extension = "sv4cpio";
            break;
        case "application/x-sv4crc":
            extension = "sv4crc";
            break;
        case "application/x-tar":
            extension = "tar";
            break;
        case "application/x-tcl":
            extension = "tcl";
            break;
        case "application/x-tex":
            extension = "tex";
            break;
        case "application/x-troff-man":
            extension = "man";
            break;
        case "application/x-troff-me":
            extension = "me";
            break;
        case "application/x-troff-ms":
            extension = "ms";
            break;
        case "application/x-ustar":
            extension = "ustar";
            break;
        case "application/x-wais-source":
            extension = "src";
            break;
        case "application/x-x509-ca-cert":
            extension = "cer";
            break;
        case "application/ynd.ms-pkipko":
            extension = "pko";
            break;
        case "application/zip":
            extension = "zip";
            break;
        case "audio/basic":
            extension = "au";
            break;
        case "audio/mid":
            extension = "mid";
            break;
        case "audio/mpeg":
            extension = "mp3";
            break;
        case "audio/x-aiff":
            extension = "aif";
            break;
        case "audio/x-mpegurl":
            extension = "m3u";
            break;
        case "audio/x-pn-realaudio":
            extension = "ra";
            break;
        case "audio/x-wav":
            extension = "wav";
            break;
        case "image/bmp":
            extension = "bmp";
            break;
        case "image/cis-cod":
            extension = "cod";
            break;
        case "image/gif":
            extension = "gif";
            break;
        case "image/ief":
            extension = "ief";
            break;
        case "image/jpeg":
        case "image/pjpeg":
            extension = "jpg";
            break;
        case "image/pipeg":
            extension = "jfif";
            break;
        case "image/png":
        case "image/x-png":
            extension = "png";
            break;
        case "image/svg+xml":
            extension = "svg";
            break;
        case "image/tiff":
            extension = "tif";
            break;
        case "image/x-cmu-raster":
            extension = "ras";
            break;
        case "image/x-cmx":
            extension = "cmx";
            break;
        case "image/x-icon":
            extension = "ico";
            break;
        case "image/x-portable-anymap":
            extension = "pnm";
            break;
        case "image/x-portable-bitmap":
            extension = "pbm";
            break;
        case "image/x-portable-graymap":
            extension = "pgm";
            break;
        case "image/x-portable-pixmap":
            extension = "ppm";
            break;
        case "image/x-rgb":
            extension = "rgb";
            break;
        case "image/x-xbitmap":
            extension = "xbm";
            break;
        case "image/x-xpixmap":
            extension = "xpm";
            break;
        case "image/x-xwindowdump":
            extension = "xwd";
            break;
        case "message/rfc822":
            extension = "mht";
            break;
        case "text/css":
            extension = "css";
            break;
        case "text/h323":
            extension = "323";
            break;
        case "text/html":
            extension = "htm";
            break;
        case "text/iuls":
            extension = "uls";
            break;
        case "text/plain":
            extension = "txt";
            break;
        case "text/richtext":
            extension = "rtx";
            break;
        case "text/scriptlet":
            extension = "sct";
            break;
        case "text/tab-separated-values":
            extension = "tsv";
            break;
        case "text/webviewhtml":
            extension = "htt";
            break;
        case "text/x-component":
            extension = "htc";
            break;
        case "text/x-setext":
            extension = "etx";
            break;
        case "text/x-vcard":
            extension = "vcf";
            break;
        case "video/mpeg":
            extension = "mpg";
            break;
        case "video/quicktime":
            extension = "mov";
            break;
        case "video/x-la-asf":
            extension = "lsf";
            break;
        case "video/x-ms-asf":
            extension = "asf";
            break;
        case "video/x-msvideo":
            extension = "avi";
            break;
        case "video/x-sgi-movie":
            extension = "movie";
            break;
        case "x-world/x-vrml":
            extension = "vrml";
            break;
        case "application/vnd.ms-word.document.macroEnabled.12":
            extension = "docm";
            break;
        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            extension = "docx";
            break;
        case "application/vnd.ms-word.template.macroEnabled.12":
            extension = "dotm";
            break;
        case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
            extension = "dotx";
            break;
        case "application/vnd.ms-powerpoint.template.macroEnabled.12":
            extension = "potm";
            break;
        case "application/vnd.openxmlformats-officedocument.presentationml.template":
            extension = "potx";
            break;
        case "application/vnd.ms-powerpoint.addin.macroEnabled.12":
            extension = "ppam";
            break;
        case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12":
            extension = "ppsm";
            break;
        case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
            extension = "ppsx";
            break;
        case "application/vnd.ms-powerpoint.presentation.macroEnabled.12":
            extension = "pptm";
            break;
        case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
            extension = "pptx";
            break;
        case "application/vnd.ms-excel.addin.macroEnabled.12":
            extension = "xlam";
            break;
        case "application/vnd.ms-excel.sheet.binary.macroEnabled.12":
            extension = "xlsb";
            break;
        case "application/vnd.ms-excel.sheet.macroEnabled.12":
            extension = "xlsm";
            break;
        case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            extension = "xlsx";
            break;
        case "application/vnd.ms-excel.template.macroEnabled.12":
            extension = "xltm";
            break;
        case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":
            extension = "xltx";
            break;
        default:
            extension = ""; // Unknown, this is a blob field of an unknown mime type.
        }

        return extension;

    }

    public static String getMimeTypeForExtension(String extension)
    {
        String mimeType;

        // Remove leading "." if there is one.
        extension = extension.replace(".", "");

        switch (extension.toUpperCase()) {
        case "ACX":
            mimeType = "application/internet-property-stream";
            break;
        case "AI":
            mimeType = "application/postscript";
            break;
        case "AIF":
            mimeType = "audio/x-aiff";
            break;
        case "AIFC":
            mimeType = "audio/x-aiff";
            break;
        case "AIFF":
            mimeType = "audio/x-aiff";
            break;
        case "ASF":
            mimeType = "video/x-ms-asf";
            break;
        case "ASR":
            mimeType = "video/x-ms-asf";
            break;
        case "ASX":
            mimeType = "video/x-ms-asf";
            break;
        case "AU":
            mimeType = "audio/basic";
            break;
        case "AVI":
            mimeType = "video/x-msvideo";
            break;
        case "AXS":
            mimeType = "application/olescript";
            break;
        case "BAS":
            mimeType = "text/plain";
            break;
        case "BCPIO":
            mimeType = "application/x-bcpio";
            break;
        case "BIN":
            mimeType = "application/octet-stream";
            break;
        case "BMP":
            mimeType = "image/bmp";
            break;
        case "C":
            mimeType = "text/plain";
            break;
        case "CAT":
            mimeType = "application/vnd.ms-pkiseccat";
            break;
        case "CDF":
            mimeType = "application/x-cdf";
            break;
        case "CER":
            mimeType = "application/x-x509-ca-cert";
            break;
        case "CLASS":
            mimeType = "application/octet-stream";
            break;
        case "CLP":
            mimeType = "application/x-msclip";
            break;
        case "CMX":
            mimeType = "image/x-cmx";
            break;
        case "COD":
            mimeType = "image/cis-cod";
            break;
        case "CPIO":
            mimeType = "application/x-cpio";
            break;
        case "CRD":
            mimeType = "application/x-mscardfile";
            break;
        case "CRL":
            mimeType = "application/pkix-crl";
            break;
        case "CRT":
            mimeType = "application/x-x509-ca-cert";
            break;
        case "CSH":
            mimeType = "application/x-csh";
            break;
        case "CSS":
            mimeType = "text/css";
            break;
        case "DCR":
            mimeType = "application/x-director";
            break;
        case "DER":
            mimeType = "application/x-x509-ca-cert";
            break;
        case "DIR":
            mimeType = "application/x-director";
            break;
        case "DLL":
            mimeType = "application/x-msdownload";
            break;
        case "DMS":
            mimeType = "application/octet-stream";
            break;
        case "DOC":
            mimeType = "application/msword";
            break;
        case "DOT":
            mimeType = "application/msword";
            break;
        case "DVI":
            mimeType = "application/x-dvi";
            break;
        case "DXR":
            mimeType = "application/x-director";
            break;
        case "EPS":
            mimeType = "application/postscript";
            break;
        case "ETX":
            mimeType = "text/x-setext";
            break;
        case "EVY":
            mimeType = "application/envoy";
            break;
        case "EXE":
            mimeType = "application/octet-stream";
            break;
        case "FIF":
            mimeType = "application/fractals";
            break;
        case "FLR":
            mimeType = "x-world/x-vrml";
            break;
        case "GIF":
            mimeType = "image/gif";
            break;
        case "GTAR":
            mimeType = "application/x-gtar";
            break;
        case "GZ":
            mimeType = "application/x-gzip";
            break;
        case "H":
            mimeType = "text/plain";
            break;
        case "HDF":
            mimeType = "application/x-hdf";
            break;
        case "HLP":
            mimeType = "application/winhlp";
            break;
        case "HQX":
            mimeType = "application/mac-binhex40";
            break;
        case "HTA":
            mimeType = "application/hta";
            break;
        case "HTC":
            mimeType = "text/x-component";
            break;
        case "HTM":
            mimeType = "text/html";
            break;
        case "HTML":
            mimeType = "text/html";
            break;
        case "HTT":
            mimeType = "text/webviewhtml";
            break;
        case "ICO":
            mimeType = "image/x-icon";
            break;
        case "IEF":
            mimeType = "image/ief";
            break;
        case "III":
            mimeType = "application/x-iphone";
            break;
        case "INS":
            mimeType = "application/x-internet-signup";
            break;
        case "ISP":
            mimeType = "application/x-internet-signup";
            break;
        case "JFIF":
            mimeType = "image/pipeg";
            break;
        case "JPE":
            mimeType = "image/jpeg";
            break;
        case "JPEG":
            mimeType = "image/jpeg";
            break;
        case "JPG":
            mimeType = "image/jpeg";
            break;
        case "JS":
            mimeType = "application/x-javascript";
            break;
        case "JSON":
            mimeType = "application/json";
            break;
        case "LATEX":
            mimeType = "application/x-latex";
            break;
        case "LHA":
            mimeType = "application/octet-stream";
            break;
        case "LSF":
            mimeType = "video/x-la-asf";
            break;
        case "LSX":
            mimeType = "video/x-la-asf";
            break;
        case "LZH":
            mimeType = "application/octet-stream";
            break;
        case "M13":
            mimeType = "application/x-msmediaview";
            break;
        case "M14":
            mimeType = "application/x-msmediaview";
            break;
        case "M3U":
            mimeType = "audio/x-mpegurl";
            break;
        case "MAN":
            mimeType = "application/x-troff-man";
            break;
        case "MDB":
            mimeType = "application/x-msaccess";
            break;
        case "ME":
            mimeType = "application/x-troff-me";
            break;
        case "MHT":
            mimeType = "message/rfc822";
            break;
        case "MHTML":
            mimeType = "message/rfc822";
            break;
        case "MID":
            mimeType = "audio/mid";
            break;
        case "MNY":
            mimeType = "application/x-msmoney";
            break;
        case "MOV":
            mimeType = "video/quicktime";
            break;
        case "MOVIE":
            mimeType = "video/x-sgi-movie";
            break;
        case "MP2":
            mimeType = "video/mpeg";
            break;
        case "MP3":
            mimeType = "audio/mpeg";
            break;
        case "MP4":
            mimeType = "video/mpeg";
            break;
        case "MPA":
            mimeType = "video/mpeg";
            break;
        case "MPE":
            mimeType = "video/mpeg";
            break;
        case "MPEG":
            mimeType = "video/mpeg";
            break;
        case "MPG":
            mimeType = "video/mpeg";
            break;
        case "MPP":
            mimeType = "application/vnd.ms-project";
            break;
        case "MPV2":
            mimeType = "video/mpeg";
            break;
        case "MS":
            mimeType = "application/x-troff-ms";
            break;
        case "MVB":
            mimeType = "application/x-msmediaview";
            break;
        case "NWS":
            mimeType = "message/rfc822";
            break;
        case "ODA":
            mimeType = "application/oda";
            break;
        case "P10":
            mimeType = "application/pkcs10";
            break;
        case "P12":
            mimeType = "application/x-pkcs12";
            break;
        case "P7B":
            mimeType = "application/x-pkcs7-certificates";
            break;
        case "P7C":
            mimeType = "application/x-pkcs7-mime";
            break;
        case "P7M":
            mimeType = "application/x-pkcs7-mime";
            break;
        case "P7R":
            mimeType = "application/x-pkcs7-certreqresp";
            break;
        case "P7S":
            mimeType = "application/x-pkcs7-signature";
            break;
        case "PBM":
            mimeType = "image/x-portable-bitmap";
            break;
        case "PDF":
            mimeType = "application/pdf";
            break;
        case "PFX":
            mimeType = "application/x-pkcs12";
            break;
        case "PGM":
            mimeType = "image/x-portable-graymap";
            break;
        case "PKO":
            mimeType = "application/ynd.ms-pkipko";
            break;
        case "PMA":
            mimeType = "application/x-perfmon";
            break;
        case "PMC":
            mimeType = "application/x-perfmon";
            break;
        case "PML":
            mimeType = "application/x-perfmon";
            break;
        case "PMR":
            mimeType = "application/x-perfmon";
            break;
        case "PMW":
            mimeType = "application/x-perfmon";
            break;
        case "PNG":
            mimeType = "image/x-png";
            break;
        case "PNM":
            mimeType = "image/x-portable-anymap";
            break;
        case "POT,":
            mimeType = "application/vnd.ms-powerpoint";
            break;
        case "PPM":
            mimeType = "image/x-portable-pixmap";
            break;
        case "PPS":
            mimeType = "application/vnd.ms-powerpoint";
            break;
        case "PPT":
            mimeType = "application/vnd.ms-powerpoint";
            break;
        case "PRF":
            mimeType = "application/pics-rules";
            break;
        case "PS":
            mimeType = "application/postscript";
            break;
        case "PUB":
            mimeType = "application/x-mspublisher";
            break;
        case "QT":
            mimeType = "video/quicktime";
            break;
        case "RA":
            mimeType = "audio/x-pn-realaudio";
            break;
        case "RAM":
            mimeType = "audio/x-pn-realaudio";
            break;
        case "RAS":
            mimeType = "image/x-cmu-raster";
            break;
        case "RGB":
            mimeType = "image/x-rgb";
            break;
        case "RMI":
            mimeType = "audio/mid";
            break;
        case "ROFF":
            mimeType = "application/x-troff";
            break;
        case "RTF":
            mimeType = "application/rtf";
            break;
        case "RTX":
            mimeType = "text/richtext";
            break;
        case "SCD":
            mimeType = "application/x-msschedule";
            break;
        case "SCT":
            mimeType = "text/scriptlet";
            break;
        case "SETPAY":
            mimeType = "application/set-payment-initiation";
            break;
        case "SETREG":
            mimeType = "application/set-registration-initiation";
            break;
        case "SH":
            mimeType = "application/x-sh";
            break;
        case "SHAR":
            mimeType = "application/x-shar";
            break;
        case "SIT":
            mimeType = "application/x-stuffit";
            break;
        case "SND":
            mimeType = "audio/basic";
            break;
        case "SPC":
            mimeType = "application/x-pkcs7-certificates";
            break;
        case "SPL":
            mimeType = "application/futuresplash";
            break;
        case "SRC":
            mimeType = "application/x-wais-source";
            break;
        case "SST":
            mimeType = "application/vnd.ms-pkicertstore";
            break;
        case "STL":
            mimeType = "application/vnd.ms-pkistl";
            break;
        case "STM":
            mimeType = "text/html";
            break;
        case "SVG":
            mimeType = "image/svg+xml";
            break;
        case "SV4CPIO":
            mimeType = "application/x-sv4cpio";
            break;
        case "SV4CRC":
            mimeType = "application/x-sv4crc";
            break;
        case "SWF":
            mimeType = "application/x-shockwave-flash";
            break;
        case "T":
            mimeType = "application/x-troff";
            break;
        case "TAR":
            mimeType = "application/x-tar";
            break;
        case "TCL":
            mimeType = "application/x-tcl";
            break;
        case "TEX":
            mimeType = "application/x-tex";
            break;
        case "TEXI":
            mimeType = "application/x-texinfo";
            break;
        case "TEXINFO":
            mimeType = "application/x-texinfo";
            break;
        case "TGZ":
            mimeType = "application/x-compressed";
            break;
        case "TIF":
            mimeType = "image/tiff";
            break;
        case "TIFF":
            mimeType = "image/tiff";
            break;
        case "TR":
            mimeType = "application/x-troff";
            break;
        case "TRM":
            mimeType = "application/x-msterminal";
            break;
        case "TSV":
            mimeType = "text/tab-separated-values";
            break;
        case "TXT":
            mimeType = "text/plain";
            break;
        case "ULS":
            mimeType = "text/iuls";
            break;
        case "URLENCODE":
            mimeType = "application/x-www-form-urlencoded";
            break;
        case "USTAR":
            mimeType = "application/x-ustar";
            break;
        case "VCF":
            mimeType = "text/x-vcard";
            break;
        case "VRML":
            mimeType = "x-world/x-vrml";
            break;
        case "WAV":
            mimeType = "audio/x-wav";
            break;
        case "WCM":
            mimeType = "application/vnd.ms-works";
            break;
        case "WDB":
            mimeType = "application/vnd.ms-works";
            break;
        case "WKS":
            mimeType = "application/vnd.ms-works";
            break;
        case "WMF":
            mimeType = "application/x-msmetafile";
            break;
        case "WPS":
            mimeType = "application/vnd.ms-works";
            break;
        case "WRI":
            mimeType = "application/x-mswrite";
            break;
        case "WRL":
            mimeType = "x-world/x-vrml";
            break;
        case "WRZ":
            mimeType = "x-world/x-vrml";
            break;
        case "XAF":
            mimeType = "x-world/x-vrml";
            break;
        case "XBM":
            mimeType = "image/x-xbitmap";
            break;
        case "XLA":
            mimeType = "application/vnd.ms-excel";
            break;
        case "XLC":
            mimeType = "application/vnd.ms-excel";
            break;
        case "XLM":
            mimeType = "application/vnd.ms-excel";
            break;
        case "XLS":
            mimeType = "application/vnd.ms-excel";
            break;
        case "XLT":
            mimeType = "application/vnd.ms-excel";
            break;
        case "XLW":
            mimeType = "application/vnd.ms-excel";
            break;
        case "XOF":
            mimeType = "x-world/x-vrml";
            break;
        case "XPM":
            mimeType = "image/x-xpixmap";
            break;
        case "XWD":
            mimeType = "image/x-xwindowdump";
            break;
        case "Z":
            mimeType = "application/x-compress";
            break;
        case "ZIP":
            mimeType = "application/zip";
            break;
        case "ACCDB":
            mimeType = "application/msaccess";
            break;
        case "ACCDE":
            mimeType = "application/msaccess";
            break;
        case "ACCDT":
            mimeType = "application/msaccess";
            break;
        case "DOCM":
            mimeType = "application/vnd.ms-word.document.macroEnabled.12";
            break;
        case "DOCX":
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            break;
        case "DOTM":
            mimeType = "application/vnd.ms-word.template.macroEnabled.12";
            break;
        case "DOTX":
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.template";
            break;
        case "POTM":
            mimeType = "application/vnd.ms-powerpoint.template.macroEnabled.12";
            break;
        case "POTX":
            mimeType = "application/vnd.openxmlformats-officedocument.presentationml.template";
            break;
        case "PPAM":
            mimeType = "application/vnd.ms-powerpoint.addin.macroEnabled.12";
            break;
        case "PPSM":
            mimeType = "application/vnd.ms-powerpoint.slideshow.macroEnabled.12";
            break;
        case "PPSX":
            mimeType = "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
            break;
        case "PPTM":
            mimeType = "application/vnd.ms-powerpoint.presentation.macroEnabled.12";
            break;
        case "PPTX":
            mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            break;
        case "XLAM":
            mimeType = "application/vnd.ms-excel.addin.macroEnabled.12";
            break;
        case "XLSB":
            mimeType = "application/vnd.ms-excel.sheet.binary.macroEnabled.12";
            break;
        case "XLSM":
            mimeType = "application/vnd.ms-excel.sheet.macroEnabled.12";
            break;
        case "XLSX":
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            break;
        case "XLTM":
            mimeType = "application/vnd.ms-excel.template.macroEnabled.12";
            break;
        case "XLTX":
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.template";
            break;
        default:
            // Use application/octet-stream
            mimeType = "application/octet-stream";
        }

        return mimeType;
    }

    public static String getFileTypeForMimeType(String mimeType)
    {
        String fileType = "";

        if (mimeType.toLowerCase().startsWith("image/"))
        {

            switch (mimeType.toLowerCase()) {

            case "image/bmp":

                fileType = "Bitmap (BMP) Image";
                break;

            case "image/gif":

                fileType = "Graphics Interchange Format (GIF) Image";
                break;

            case "image/jpeg":

                fileType = "Joint Photographic Experts Group (JPEG) Image";
                break;

            case "image/x-png":

                fileType = "Portable Network Graphic (PNG) Image";
                break;

            case "image/tiff":

                fileType = "Tagged Image File Format (TIFF) Image";
                break;

            default:

                fileType = mimeType.replace("image/", "").toUpperCase() + " Image";

            }
        }

        if (mimeType.toLowerCase().startsWith("text/"))
        {

            switch (mimeType.toLowerCase()) {

            case "text/plain":

                fileType = "Text Document";
                break;

            case "text/html":

                fileType = "HTML Document";
                break;

            default:

                fileType = mimeType.replace("text/", "").toUpperCase() + " Document";

            }

        }

        if (mimeType.toLowerCase().startsWith("audio/"))
        {

            switch (mimeType.toLowerCase()) {

            case "audio/mpeg":

                fileType = "Moving Picture Experts Group (MPEG) Audio";
                break;

            case "audio/mid":

                fileType = "Musical Instrument Digital Interface (MIDI) Audio";
                break;

            case "audio/x-wav":

                fileType = "Waveform Audio File (WAV)";
                break;

            case "audio/basic":

                fileType = "Audio File";
                break;

            default:

                fileType = mimeType.replace("audio/", "").toUpperCase() + " File";

            }
        }

        if (mimeType.toLowerCase().startsWith("video/"))
        {

            switch (mimeType.toLowerCase()) {
            case "video/quicktime":

                fileType = "Quicktime Video";
                break;

            case "video/mpeg":

                fileType = "Moving Picture Experts Group (MPEG) Video";
                break;

            case "video/x-msvideo":

                fileType = "Audio Video Interleave (AVI) Video";
                break;

            default:

                fileType = mimeType.replace("video/", "").toUpperCase() + " Video";

            }
        }

        if (mimeType.toLowerCase().startsWith("application/"))
        {

            switch (mimeType.toLowerCase()) {

            case "application/msword":

                fileType = "Microsoft Word 97-2003 Document";
                break;

            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":

                fileType = "Microsoft Word Document";
                break;

            case "application/x-msaccess":

                fileType = "Microsoft Access Database";
                break;

            case "application/x-msmoney":

                fileType = "Microsoft Money";
                break;

            case "application/pdf":

                fileType = "Adobe Portable Document Format";
                break;

            case "application/vnd.ms-project":

                fileType = "Microsoft Project";
                break;

            case "application/vnd.ms-powerpoint":

                fileType = "Microsoft PowerPoint 97-2003 Presentation";
                break;

            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":

                fileType = "Microsoft PowerPoint Presentation";
                break;

            case "application/x-mspublisher":

                fileType = "Microsoft Publisher";
                break;

            case "application/vnd.ms-works":

                fileType = "Microsoft Works Document";
                break;

            case "application/x-mswrite":

                fileType = "Microsoft Write";
                break;

            case "application/rtf":

                fileType = "Rich Text Format Document";
                break;

            case "application/vnd.ms-excel":

                fileType = "Microsoft Excel 97-2003 Spreadsheet";
                break;

            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":

                fileType = "Microsoft Excel Spreadsheet";
                break;

            case "application/x-gtar":

                fileType = "GTAR Archive";
                break;

            case "application/x-gzip":

                fileType = "GZIP Archive";
                break;

            case "application/zip":

                fileType = "ZIP Archive";
                break;

            default:

                fileType = mimeType.replace("application/", "").toUpperCase() + " Document";

            }
        }

        // Catch-all
        if (StringHelper.IsNullOrEmpty(fileType))
        {
            String[] split = mimeType.split("/");
            if (split.length == 2)
            {
                fileType = split[1].toUpperCase() + " File";
            }
            else
            {
                fileType = "File (Unidentified Format)";
            }
        }

        return fileType;

    }

    public static String getFileTypeForExtension(String Extension)
    {

        // Get Mime Type
        String MimeType = getMimeTypeForExtension(Extension);

        // Get Descriptive Name for Mime Type
        return getFileTypeForMimeType(MimeType);

    }

}
