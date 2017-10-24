/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.api;


public interface ICoalesceBinaryField {

    /**
     * Return the Field's MimeType. MIME types form a standard way of
     * classifying file types on the Internet.
     * 
     * @return String of the Field's MimeType attribute.
     */
    String getMimeType();

    /**
     * Return the Field's File name attribute.
     * 
     * @return String of the Field's Filename attribute.
     */
    String getFilename();

    /**
     * Return the value of the Field's extension attribute which corresponds to
     * the filename attribute.
     * 
     * @return String of the Field's extension attribute.
     */
    String getExtension();

    /**
     * Return the value of the Field's Hash attribute.
     * 
     * @return String of the Field's hash value attribute.
     */
    String getHash();

    /**
     * Return the value of the Field's Size attribute.
     * 
     * @return integer of the Field's size attribute.
     */
    int getSize();
    
    /**
     * Sets the Field's MimeType. MIME types form a standard way of classifying
     * file types on the Internet.
     * 
     * @param value String to be the Field's MimeType attribute.
     */
    void setMimeType(String value);

    /**
     * Sets the Field's File name attribute.
     * 
     * @param value String to be the Field's Filename attribute.
     */
    void setFilename(String value);

    /**
     * Sets the value of the Field's extension attribute which corresponds to
     * the filename attribute.
     * 
     * @param value String to be the Field's extension attribute.
     */
    void setExtension(String value);

    /**
     * Sets the value of the Field's Hash attribute.
     * 
     * @param value String to be the Field's hash value attribute.
     */
    void setHash(String value);

    /**
     * Sets the value of the Field's Size attribute.
     * 
     * @param value integer to be the Field's size attribute.
     */
    void setSize(int value);
}
