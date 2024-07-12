package win.sectorfive.country_on_join.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CountryOnJoinClient implements ClientModInitializer {

    public static final String MOD_ID = "country_on_join";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String IP_API = "http://ip.sectorfive.win/json/";

    private static boolean messageSent = false;  // Flag to ensure the message is sent only once

    @Override
    public void onInitializeClient() {
        initialize();
    }

    public static void initialize() {
        LOGGER.info("Country on Join Client Loaded!");

        CompletableFuture<String> countryFuture = CompletableFuture.supplyAsync(() -> {
            String country = "Unknown";
            try {
                URL url = new URL(IP_API);
                URLConnection con = url.openConnection();
                con.setConnectTimeout(3000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                LOGGER.info("Received response: " + response.toString());

                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                if (jsonResponse.has("country")) {
                    country = jsonResponse.get("country").getAsString();
                    LOGGER.info("Parsed country: " + country);
                }

            } catch (Exception e) {
                LOGGER.warn("Failed to get country for player: " + e.getMessage());
            }
            return country;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance().player != null && !messageSent) {
                try {
                    String country = countryFuture.get();  // Wait for the country fetching to complete
                    MinecraftClient.getInstance().player.sendMessage(Text.of("You've joined from " + country));
                    messageSent = true;  // Set the flag to true to ensure the message is sent only once
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.warn("Failed to get country for player: " + e.getMessage());
                    MinecraftClient.getInstance().player.sendMessage(Text.of("You've joined from an unknown country"));
                    messageSent = true;  // Set the flag to true even if there's an error
                }
            }
        });
    }
}
