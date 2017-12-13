package com.oppsci.ngraphstore.storage.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.RiotException;
import org.junit.Test;

public class BulkLoaderTest {

	
	@Test
	public void checkBulkLoad() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneBulkLoader.NOTICE_AMOUNT=2;
		LuceneBulkLoader.main(new String[] {"1", "false", uuid, "<urn://graph>", "src/test/resources/bulk/test1.nt", "src/test/resources/bulk/test2.nt" });
		//assertTrue if folders were created and are not empty
		assertTrue(tmpFolder.exists());
		File childFolder = new File(uuid+File.separator+"0");
		assertTrue(childFolder.exists());
		//check if segments were created
		assertTrue(childFolder.list().length>2);
		
		FileUtils.deleteDirectory(tmpFolder);
	}


	
	@Test
	public void errorTest() throws IOException{
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		try {
			LuceneBulkLoader.main(new String[] {"1", "false", uuid, "<urn://graph>", "src/test/resources/bulk/test1.nt", "src/test/resources/bulk/test_false.nt" });
		} catch (Exception e) {
			assertTrue(e instanceof RiotException);
		}	
		FileUtils.deleteDirectory(tmpFolder);
	}
}
