package we.devs.opium.api.manager.miscellaneous;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDManager {

    // Changeable List of The Players that are allowed to get the cape
    public static final List<UUID> ALLOWED_UUIDS = new ArrayList<>(List.of(
            UUID.fromString("31350a69-f9d9-4a55-bd9f-e037eb8d64e5"), // VoidMatterRules
            UUID.fromString("4054adb3-4515-4ecc-81c2-7617f64741c2"),  // heedii
            UUID.fromString("a8296ac1-4105-456e-b88c-abc9d65dfba8"), // crystal0p1umm
            UUID.fromString("f5d7971d-54c5-4b37-aca9-f7b14511b8a6"), // Cxiy
            UUID.fromString("1ec06979-22e9-450f-aa5e-5d1b24573a98"), // Tinkoprof
            UUID.fromString("272f076c-ac2d-41ed-bffe-da7c304ca6ac"), // FinalMemory
            UUID.fromString("ae48f6e1-a0a2-40d7-8824-7055a317339d"), // goddanger
            UUID.fromString("de829190-b3ef-47c3-ac51-655e7248d20e"), // ItsBookxYT
            UUID.fromString("65c7ba69-5015-44a5-a31c-6684a641387f"), // godmoduleu (goddanger alt)
            UUID.fromString("0010dc04-70e8-4a4a-a8ab-b593fe518ff8"), // Op1umClientLLC
            UUID.fromString("968f9b1f-a1f3-4b1d-be9d-9f18056d57a9") // RoboticModules
    ));

    // Method to check the authentication
    public static boolean isAdded(UUID uuid) {
        return ALLOWED_UUIDS.contains(uuid);
    }

    // Method to automatically add playeruuid to the list
    public static void addPlayerUUID(UUID playerUUID) {
        if (playerUUID != null && !ALLOWED_UUIDS.contains(playerUUID)) {
            ALLOWED_UUIDS.add(playerUUID);
        }
    }

}
