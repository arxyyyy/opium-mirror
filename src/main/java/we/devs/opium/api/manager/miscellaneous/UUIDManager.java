package we.devs.opium.api.manager.miscellaneous;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDManager {

    // Änderbare Liste der Spieler-UUIDs, denen das Cape angezeigt werden soll
    public static final List<UUID> ALLOWED_UUIDS = new ArrayList<>(List.of(
            UUID.fromString("11111111-1111-1111-1111-111111111111"), // Beispiel-UUID 1
            UUID.fromString("22222222-2222-2222-2222-222222222222")  // Beispiel-UUID 2
    ));

    // Methode, um die Authentifizierung zu prüfen
    public static boolean hasCustomCape(UUID uuid) {
        return ALLOWED_UUIDS.contains(uuid);
    }

    // Methode, um automatisch die UUID von mc.player hinzuzufügen
    public static void addPlayerUUID(UUID playerUUID) {
        if (playerUUID != null && !ALLOWED_UUIDS.contains(playerUUID)) {
            ALLOWED_UUIDS.add(playerUUID);
        }
    }
}