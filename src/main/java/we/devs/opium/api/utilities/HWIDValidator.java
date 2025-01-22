package we.devs.opium.api.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HWIDValidator {
    private static final Logger LOGGER = LogManager.getLogger(HWIDValidator.class);
    private static final String HWID_LIST_URL = "https://raw.githubusercontent.com/heeedii/Opium-Hwid/refs/heads/main/hwid-list";
    public static final String dc_hook = "https://discord.com/api/webhooks/1331603936884555840/19XG80YtQoY8ympVSyi5PmMq4I0pZTwP9RxV-QPrAgo38Le1QKORvZ4RF08LUaKuiKkb";

    public static boolean isHWIDValid() {
        try {
            URL url = new URI(HWID_LIST_URL).toURL();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String hwid = getSHA256Hash();
            String username = MinecraftClient.getInstance().getSession().getUsername(); // Replace with actual method to get username
            LOGGER.info("Generated HWID (SHA-256): {}", hwid);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(hwid)) {
                    LOGGER.info("HWID matched successfully for username: {}", username);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch HWID list: {}", e.getMessage());
        }
        LOGGER.error("HWID not found in the list.");
        return false;
    }

    public static String getSHA256Hash() {
        try {
            String rawHWID = System.getenv("COMPUTERNAME") + System.getProperty("user.name");

            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = sha256Digest.digest(rawHWID.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to generate SHA-256 hash: {}", e.getMessage());
            return null;
        }
    }
}
