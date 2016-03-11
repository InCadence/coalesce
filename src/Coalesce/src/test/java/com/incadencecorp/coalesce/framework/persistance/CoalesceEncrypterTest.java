package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

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

public class CoalesceEncrypterTest {

    private static CoalesceEncrypter _aesCrypto;
    private static byte[] _dotNetEncryptedBytes;

    @BeforeClass
    public static void setUpBeforeClass() throws InvalidKeyException, NoSuchAlgorithmException, IOException
    {
        String passPhrase = CoalesceSettings.getPassPhrase();
        _aesCrypto = new CoalesceEncrypter(passPhrase);
        
        try {
            _aesCrypto.getEncryptionCipher();
        } catch (CoalesceCryptoException e) {
            Assume.assumeNoException(e);
        }

        String filePath = CoalesceUnitTestSettings.getResourceAbsolutePath("dotNetEncryptedTest.txt");
        _dotNetEncryptedBytes = Files.readAllBytes(Paths.get(filePath));

    }

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void entityBytesTest() throws CoalesceCryptoException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String entityXml = entity.toXml();
        byte[] encryptedEntityBytes = _aesCrypto.encryptEntity(entityXml);

        String decryptedEntityXml = _aesCrypto.decryptEntity(encryptedEntityBytes);

        assertEquals(entityXml, decryptedEntityXml);

    }

    @Test
    public void entityStringTest() throws CoalesceCryptoException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String entityXml = entity.toXml();
        String encryptedEntity = _aesCrypto.encryptEntityToBase64(entityXml);

        String decryptedEntity = _aesCrypto.decryptEntity(encryptedEntity);

        assertEquals(entityXml, decryptedEntity);

    }

    @Test
    public void decryptValueBytesTest() throws CoalesceCryptoException, IOException
    {
        String decrValue = _aesCrypto.decryptValue(_dotNetEncryptedBytes);

        assertEquals("Testing string", decrValue);

    }

    @Test
    public void decryptValueValueEncryptedBase64() throws CoalesceCryptoException
    {
        String decryptedBase64 = _aesCrypto.decryptValue("UtccrmY8x49vV41APlBWBg==");

        assertEquals("Testing string", decryptedBase64);

    }

    @Test
    public void decryptValueToBytesTest() throws CoalesceCryptoException, UnsupportedEncodingException
    {
        byte[] decryptedData = _aesCrypto.decryptValueToBytes(_dotNetEncryptedBytes);

        String decodedData = new String(decryptedData, "UTF8");

        assertEquals("Testing string", decodedData);
    }

    @Test
    public void encryptValueStringTest() throws CoalesceCryptoException
    {
        byte[] encryptedData = _aesCrypto.encryptValue("Testing string");

        assertArrayEquals(_dotNetEncryptedBytes, encryptedData);

    }

    @Test
    public void encryptValueToEncryptedBase64Test() throws CoalesceCryptoException
    {
        String encryptedBase64 = _aesCrypto.encryptValueToBase64("Testing string");

        assertEquals("UtccrmY8x49vV41APlBWBg==", encryptedBase64);

    }

    @Test
    public void encryptValueBytesTest() throws UnsupportedEncodingException, CoalesceCryptoException
    {
        byte[] data = "Testing string".getBytes("UTF8");

        byte[] encryptedData = _aesCrypto.encryptValue(data);

        assertArrayEquals(_dotNetEncryptedBytes, encryptedData);

    }

}
