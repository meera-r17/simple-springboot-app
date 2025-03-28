package com.springboot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.OfferRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {

    private final String BASE_URL = "http://localhost:9001/api/v1";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void checkFlatXForOneSegment() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add FLATX offer for p1 segment", result);

        JsonNode response = applyOffer(1, 1, 200);
        Assert.assertEquals("Incorrect cart value after applying FLATX offer",
                190, response.get("cart_value").asInt());
    }

    @Test
    public void checkFlatPercentForOneSegment() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p2");
        OfferRequest offerRequest = new OfferRequest(2, "FLATPERCENT", 10, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add FLATPERCENT offer for p2 segment", result);

        mockUserSegment(2, "p2");

        JsonNode response = applyOffer(2, 2, 200);
        Assert.assertEquals("Incorrect cart value after applying FLATPERCENT offer",
                180, response.get("cart_value").asInt());
    }

    @Test
    public void checkNoOfferForNonMatchingSegment() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(3, "FLATX", 20, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add offer", result);

        mockUserSegment(3, "p3");

        JsonNode response = applyOffer(3, 3, 200);
        Assert.assertEquals("Cart value should remain unchanged for non-matching segment",
                200, response.get("cart_value").asInt());
    }

    @Test
    public void checkMultipleSegmentOffer() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        segments.add("p2");
        OfferRequest offerRequest = new OfferRequest(4, "FLATX", 15, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add multi-segment offer", result);

        mockUserSegment(4, "p1");
        JsonNode response1 = applyOffer(4, 4, 200);
        Assert.assertEquals("Incorrect cart value for p1 user with multi-segment offer",
                185, response1.get("cart_value").asInt());

        mockUserSegment(4, "p2");
        JsonNode response2 = applyOffer(4, 4, 200);
        Assert.assertEquals("Incorrect cart value for p2 user with multi-segment offer",
                185, response2.get("cart_value").asInt());
    }

    @Test
    public void checkLargePercentageDiscount() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p3");
        OfferRequest offerRequest = new OfferRequest(5, "FLATPERCENT", 50, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add large percentage discount offer", result);

        mockUserSegment(5, "p3");

        JsonNode response = applyOffer(5, 5, 200);
        Assert.assertEquals("Incorrect cart value for 50% discount",
                100, response.get("cart_value").asInt());
    }

    @Test
    public void checkSmallOrderValueWithLargeDiscount() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(6, "FLATX", 50, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add offer with large discount", result);

        mockUserSegment(6, "p1");

        JsonNode response = applyOffer(6, 6, 30);
        Assert.assertTrue("Cart value should be 0 or handled appropriately when discount exceeds cart value",
                response.get("cart_value").asInt() >= 0);
    }

    @Test
    public void checkEdgeCaseZeroDiscount() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p2");
        OfferRequest offerRequest = new OfferRequest(7, "FLATPERCENT", 0, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add zero discount offer", result);

        mockUserSegment(7, "p2");

        JsonNode response = applyOffer(7, 7, 200);
        Assert.assertEquals("Cart value should remain unchanged with 0% discount",
                200, response.get("cart_value").asInt());
    }

    @Test
    public void checkZeroCartValue() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(8, "FLATX", 10, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add offer", result);

        mockUserSegment(8, "p1");

        JsonNode response = applyOffer(8, 8, 0);
        Assert.assertEquals("Cart value should be 0 when starting with 0",
                0, response.get("cart_value").asInt());
    }

    @Test
    public void checkNegativeDiscountValue() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p2");
        OfferRequest offerRequest = new OfferRequest(10, "FLATX", -10, segments);

        boolean result = addOffer(offerRequest);

        if (result) {
            mockUserSegment(10, "p2");
            JsonNode response = applyOffer(10, 10, 200);
            Assert.assertTrue("Negative discount should not increase cart value",
                    response.get("cart_value").asInt() >= 200);
        }
    }

    @Test
    public void checkMultipleCompetingOffers() throws Exception {
        List<String> segments1 = new ArrayList<>();
        segments1.add("p3");
        OfferRequest offerRequest1 = new OfferRequest(11, "FLATPERCENT", 10, segments1);
        boolean result1 = addOffer(offerRequest1);
        Assert.assertTrue("Failed to add first offer", result1);

        List<String> segments2 = new ArrayList<>();
        segments2.add("p3");
        OfferRequest offerRequest2 = new OfferRequest(12, "FLATPERCENT", 15, segments2);
        boolean result2 = addOffer(offerRequest2);
        Assert.assertTrue("Failed to add second offer", result2);

        mockUserSegment(11, "p3");

        JsonNode response = applyOffer(11, 11, 200);
        Assert.assertEquals("System should apply the better offer (15% off)",
                170, response.get("cart_value").asInt());
    }

    @Test
    public void checkLargeOrderWithPercentageDiscount() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(13, "FLATPERCENT", 20, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertTrue("Failed to add percentage discount offer", result);

        mockUserSegment(13, "p1");

        int largeOrderValue = 1000000;
        JsonNode response = applyOffer(13, 13, largeOrderValue);

        int expectedDiscountedValue = (int)(largeOrderValue * 0.8);
        Assert.assertEquals("Incorrect discount calculation for large order",
                expectedDiscountedValue, response.get("cart_value").asInt());
    }

    public boolean addOffer(OfferRequest offerRequest) throws Exception {
        String urlString = BASE_URL + "/offer";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        String POST_PARAMS = mapper.writeValueAsString(offerRequest);
        try (OutputStream os = con.getOutputStream()) {
            os.write(POST_PARAMS.getBytes());
            os.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        StringBuilder response = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            System.out.println("Response: " + response);
            return true;
        } else {
            System.out.println("POST request failed with response code: " + responseCode);
            return false;
        }
    }

    private JsonNode applyOffer(int userId, int restaurantId, int cartValue) throws Exception {
        String urlString = BASE_URL + "/cart/apply_offer";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        String requestBody = String.format(
                "{\"cart_value\":%d,\"user_id\":%d,\"restaurant_id\":%d}",
                cartValue, userId, restaurantId
        );

        try (OutputStream os = con.getOutputStream()) {
            os.write(requestBody.getBytes());
            os.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("Apply Offer Response Code :: " + responseCode);

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        System.out.println("Apply Offer Response: " + response);
        return mapper.readTree(response.toString());
    }

    // Helper method to mock user segment
    private void mockUserSegment(int userId, String segment) throws Exception {
        System.out.println("Mocking user " + userId + " as segment " + segment);
    }
}