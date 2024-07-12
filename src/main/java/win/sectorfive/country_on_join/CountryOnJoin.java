package win.sectorfive.country_on_join;

import net.fabricmc.api.EnvType;
import win.sectorfive.country_on_join.client.CountryOnJoinClient;
import win.sectorfive.country_on_join.server.CountryOnJoinServer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.loader.api.FabricLoader.getInstance;

public class CountryOnJoin implements ModInitializer {

    public static final String MOD_ID = "country_on_join";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        String Environment = String.valueOf(getInstance().getEnvironmentType());

        LOGGER.info("Country on Join Loaded!");
        LOGGER.info("Environment Type:" + Environment);

        if (getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            CountryOnJoinClient.initialize();
        } else if (getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.SERVER) {
            CountryOnJoinServer.initialize();
        }
    }
}
