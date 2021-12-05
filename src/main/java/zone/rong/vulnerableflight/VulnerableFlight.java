package zone.rong.vulnerableflight;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VulnerableFlight implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("VulnerableFlight");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing VulnerableFlight.");
    }

}
