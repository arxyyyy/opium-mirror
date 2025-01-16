package we.devs.opium.api.manager.miscellaneous;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDManager {

    // Änderbare Liste der Spieler-UUIDs, denen das Cape angezeigt werden soll
    public static final List<UUID> ALLOWED_UUIDS = new ArrayList<>(List.of(
            UUID.fromString("31350a69-f9d9-4a55-bd9f-e037eb8d64e5"), // VoidMatter
            UUID.fromString("22222222-2222-2222-2222-222222222222")  // Beispiel-UUID 2
    ));

    // Methode, um die Authentifizierung zu prüfen
    public static boolean isAdded(UUID uuid) {
        return ALLOWED_UUIDS.contains(uuid);
    }

    // Methode, um automatisch UUIDs Hinzuzufügen
    public static void addPlayerUUID(UUID playerUUID) {
        if (playerUUID != null && !ALLOWED_UUIDS.contains(playerUUID)) {
            ALLOWED_UUIDS.add(playerUUID);
        }
    }

}