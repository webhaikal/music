package com.sismics.music.rest;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.nio.file.Paths;

/**
 * Test the app resource.
 * 
 * @author jtremeaux
 */
public class TestAppResource extends BaseJerseyTest {
    /**
     * Test the API resource.
     */
    @Test
    public void testAppResource() {
        // Check the application info
        GET("/app");
        assertIsOk();
        JsonObject json = getJsonResult();
        String currentVersion = json.getString("current_version");
        Assert.assertNotNull(currentVersion);
        String minVersion = json.getString("min_version");
        Assert.assertNotNull(minVersion);
        Long freeMemory = json.getJsonNumber("free_memory").longValue();
        Assert.assertTrue(freeMemory > 0);
        Long totalMemory = json.getJsonNumber("total_memory").longValue();
        Assert.assertTrue(totalMemory > 0 && totalMemory > freeMemory);
    }

    /**
     * Test the map port resource.
     */
    @Test
    @Ignore
    public void testMapPortResource() {
        // Login admin
        loginAdmin();

        // Map port using UPnP
        POST("/app/map_port");
        assertIsOk();
    }
    
    /**
     * Test the log resource.
     */
    @Test
    public void testLogResource() {
        // Login admin
        loginAdmin();

        // Check the logs (page 1)
        GET("/app/log", ImmutableMap.of("level", "DEBUG"));
        assertIsOk();
        JsonObject json = getJsonResult();
        JsonArray logs = json.getJsonArray("logs");
        Assert.assertTrue(logs.size() == 10);
        Long date1 = logs.getJsonObject(0).getJsonNumber("date").longValue();
        Long date2 = logs.getJsonObject(9).getJsonNumber("date").longValue();
        Assert.assertTrue(date1 > date2);
        
        // Check the logs (page 2)
        GET("/app/log", ImmutableMap.of(
                "offset",  "10",
                "level", "DEBUG"));
        assertIsOk();
        json = getJsonResult();
        logs = json.getJsonArray("logs");
        Assert.assertTrue(logs.size() == 10);
        Long date3 = logs.getJsonObject(0).getJsonNumber("date").longValue();
        Long date4 = logs.getJsonObject(9).getJsonNumber("date").longValue();
        Assert.assertTrue(date3 > date4);
    }

    /**
     * Test the collection reindexing batch.
     *
     */
    @Test
    public void testReindexBatch() throws Exception {
        // Login users
        loginAdmin();

        // Admin adds a directory to the collection
        PUT("/directory", ImmutableMap.of("location", Paths.get(getClass().getResource("/music/").toURI()).toString()));
        assertIsOk();

        // Check that the albums are correctly added
        GET("/album");
        assertIsOk();
        JsonObject json = getJsonResult();
        JsonArray albums = json.getJsonArray("albums");
        Assert.assertNotNull(albums);
        Assert.assertEquals(2, albums.size());

//        // Admin adds a directory to the collection
//        WebResource appResource = target().path("/app/batch/reindex");
//        appResource.addFilter(.cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken));
//        postParams = new MultivaluedMapImpl();
//        response = appResource.post(ClientResponse.class, postParams);
//        Assert.assertEquals(Status.OK, Status.fromStatusCode(response.getStatus()));
//        json = response.readEntity(JsonObject.class);
//        Assert.assertEquals("ok", json.getString("status"));
//
//        // Check that the albums are correctly indexed
//        albumResource = target().path("/album");
//        albumResource.addFilter(.cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken));
//        response = albumResource.get(ClientResponse.class);
//        Assert.assertEquals(Status.OK, Status.fromStatusCode(response.getStatus()));
//        json = response.readEntity(JsonObject.class);
//        albums = json.getJsonArray("albums");
//        Assert.assertNotNull(albums);
//        Assert.assertEquals(1, albums.length());
    }

}
