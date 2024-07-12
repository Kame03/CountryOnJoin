package win.sectorfive.country_on_join.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sectorfive.country_on_join.utils.IpToCountryResolver;

import java.util.Map;

public class CountryOnJoinServer implements DedicatedServerModInitializer {
    public static final String MOD_ID = "country_on_join";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String MSG_TO_PLAYERS = "joined from";
    private static final String MSG_TO_PLAYER = "joined from";

    private static IpToCountryResolver resolver;

    @Override
    public void onInitializeServer() {
        initialize();
    }

    public static void initialize() {
        LOGGER.info("Country on Join Server Loaded!");

        resolver = new IpToCountryResolver();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            if (player != null) {
                sendJoinMessage(server, player);
            }
        });
    }

    private static void sendJoinMessage(MinecraftServer server, ServerPlayerEntity player) {
        String playerName = player.getName().getString();
        String playerIp = player.getIp().toString();
        String country = "Unknown";

        if (isLocalIp(playerIp)) {
            country = "Local Network";
        } else {
            try {
                country = resolver.getCountryName(playerIp);
            } catch (Exception e) {
                LOGGER.warn("Failed to get country for player " + playerName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        player.sendMessage(Text.of("You've " + MSG_TO_PLAYER + " " + country));

        for (ServerPlayerEntity targetPlayer : PlayerLookup.all(server)) {
            targetPlayer.sendMessage(Text.of(playerName + " " + MSG_TO_PLAYERS + " " + country));
        }
    }

    private static boolean isLocalIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }

        return ipAddress.startsWith("127.") ||
                ipAddress.startsWith("10.") ||
                (ipAddress.startsWith("172.") && (Integer.parseInt(ipAddress.split("\\.")[1]) >= 16 && Integer.parseInt(ipAddress.split("\\.")[1]) <= 31)) ||
                (ipAddress.startsWith("192.168."));
    }
}
