package we.devs.opium.api.manager.miscellaneous;

import com.google.gson.*;
import we.devs.opium.Opium;
import we.devs.opium.api.manager.element.Element;
import we.devs.opium.api.manager.friend.Friend;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.client.values.Value;
import we.devs.opium.client.values.impl.*;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConfigManager {
    public void load() {
        try {
            this.loadModules();
            this.loadElements();
            this.loadPrefix();
            this.loadFriends();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    boolean saving = false;
    public void save() {
        if(saving) return;
        saving = true;
        try {
            Path path = Paths.get("Opium/");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                Opium.LOGGER.atInfo().log("Created Opium directory");
            }
            if (!Files.exists(Paths.get(path + "Modules/"))) {
                Files.createDirectories(Paths.get(path + "/Modules/"));
                Opium.LOGGER.atInfo().log("Created Modules directory");
            }
            if (!Files.exists(Paths.get(path + "Elements/"))) {
                Files.createDirectories(Paths.get(path + "/Elements/"));
                Opium.LOGGER.atInfo().log("Created Elements directory");
            }
            if (!Files.exists(Paths.get(path + "Client/"))) {
                Files.createDirectories(Paths.get(path + "/Client/"));
                Opium.LOGGER.atInfo().log("Created Client directory");
            }
            this.saveModules();
            this.saveElements();
            this.savePrefix();
            this.saveFriends();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        saving = false;
    }

    public void attach() {
        Runtime.getRuntime().addShutdownHook(new SaveThread());
    }

    public void loadModules() throws IOException {
        for (Module module : Opium.MODULE_MANAGER.getModules()) {
            JsonObject moduleJson;
            if (!Files.exists(Paths.get("Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json")))
                continue;
            InputStream stream = Files.newInputStream(Paths.get("Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json"));
            try {
                moduleJson = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            } catch (IllegalStateException exception) {
                exception.printStackTrace();
                Opium.LOGGER.error(module.getName());
                continue;
            }
            if (moduleJson.get("Name") == null || moduleJson.get("Status") == null) continue;
            if (moduleJson.get("Status").getAsBoolean()) {
                module.enable(false);
                Opium.LOGGER.atInfo().log("Module " + module.getName() + " Is " + moduleJson.get("Status").getAsBoolean());
            }
            JsonObject valueJson = moduleJson.get("Values").getAsJsonObject();
            this.loadValues(valueJson, module.getValues());
            stream.close();
        }
    }

    public void saveModules() throws IOException {
        for (Module module : Opium.MODULE_MANAGER.getModules()) {
            if (!Files.exists(Paths.get("Opium/Modules/" + module.getCategory().getName()))) {
                Files.createDirectories(Paths.get("Opium/Modules/" + module.getCategory().getName()));
            } else {
                Files.deleteIfExists(Paths.get("Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json"));
                Opium.LOGGER.atInfo().log("Deleted module " + module.getName() + " from file at " + "Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json");
            }
            Files.createFile(Paths.get("Opium/Modules/"+ module.getCategory().getName() + "/" + module.getName() + ".json"));
            Opium.LOGGER.atInfo().log("Created module " + module.getName() + " file at " + "Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json");

            Opium.LOGGER.atInfo().log("Start Saving module " + module.getName() + " to file at " + "Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject moduleJson = new JsonObject();
            JsonObject valueJson = new JsonObject();
            moduleJson.add("Name", new JsonPrimitive(module.getName()));
            moduleJson.add("Status", new JsonPrimitive(module.isToggled()));
            this.saveValues(valueJson, module.getValues());
            moduleJson.add("Values", valueJson);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json"), StandardCharsets.UTF_8);
            writer.write(gson.toJson(new JsonParser().parse(moduleJson.toString())));
            writer.close();
            Opium.LOGGER.atInfo().log("Successfully saved module " + module.getName() + " to file at " + "Opium/Modules/" + module.getCategory().getName() + "/" + module.getName() + ".json");
        }
    }

    public void loadElements() throws IOException {
        for (Element element : Opium.ELEMENT_MANAGER.getElements()) {
            JsonObject elementJson;
            if (!Files.exists(Paths.get("Opium/Elements/" + element.getName() + ".json"))) continue;
            InputStream stream = Files.newInputStream(Paths.get("Opium/Elements/" + element.getName() + ".json"));
            try {
                elementJson = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            } catch (IllegalStateException exception) {
                exception.printStackTrace();
                Opium.LOGGER.error(element.getName());
                continue;
            }
            if (elementJson.get("Name") == null || elementJson.get("Status") == null || elementJson.get("Positions") == null)
                continue;
            if (elementJson.get("Status").getAsBoolean()) {
                element.enable(false);
            }
            JsonObject valueJson = elementJson.get("Values").getAsJsonObject();
            JsonObject positionJson = elementJson.get("Positions").getAsJsonObject();
            this.loadValues(valueJson, element.getValues());
            if (positionJson.get("X") != null && positionJson.get("Y") != null) {
                element.frame.setX(positionJson.get("X").getAsFloat());
                element.frame.setY(positionJson.get("Y").getAsFloat());
            }
            stream.close();
        }
    }

    public void saveElements() throws IOException {
        for (Element element : Opium.ELEMENT_MANAGER.getElements()) {
            if (Files.exists(Paths.get("Opium/Elements/" + element.getName() + ".json"))) {
                File file = new File("Opium/Elements/" + element.getName() + ".json");
                file.delete();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject elementJson = new JsonObject();
            JsonObject valueJson = new JsonObject();
            JsonObject positionJson = new JsonObject();
            elementJson.add("Name", new JsonPrimitive(element.getName()));
            elementJson.add("Status", new JsonPrimitive(element.isToggled()));
            this.saveValues(valueJson, element.getValues());
            positionJson.add("X", new JsonPrimitive(element.frame.getX()));
            positionJson.add("Y", new JsonPrimitive(element.frame.getY()));
            elementJson.add("Values", valueJson);
            elementJson.add("Positions", positionJson);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("Opium/Elements/" + element.getName() + ".json"), StandardCharsets.UTF_8);
            writer.write(gson.toJson(new JsonParser().parse(elementJson.toString())));
            writer.close();
        }
    }

    private void loadValues(JsonObject valueJson, ArrayList<Value> values) {
        for (Value value : values) {
            JsonElement dataObject = valueJson.get(value.getName());
            if (dataObject == null || !dataObject.isJsonPrimitive()) continue;
            if (value instanceof ValueBoolean) {
                ((ValueBoolean) value).setValue(dataObject.getAsBoolean());
                continue;
            }
            if (value instanceof ValueNumber) {
                if (((ValueNumber) value).getType() == 1) {
                    ((ValueNumber) value).setValue(dataObject.getAsInt());
                    continue;
                }
                if (((ValueNumber) value).getType() == 2) {
                    ((ValueNumber) value).setValue(dataObject.getAsDouble());
                    continue;
                }
                if (((ValueNumber) value).getType() != 3) continue;
                ((ValueNumber) value).setValue(Float.valueOf(dataObject.getAsFloat()));
                continue;
            }
            if (value instanceof ValueEnum) {
                ((ValueEnum) value).setValue(((ValueEnum) value).getEnum(dataObject.getAsString()));
                continue;
            }
            if (value instanceof ValueString) {
                ((ValueString) value).setValue(dataObject.getAsString());
                continue;
            }
            if (value instanceof ValueColor) {
                ((ValueColor) value).setValue(new Color(dataObject.getAsInt()));
                if (valueJson.get(value.getName() + "-Rainbow") != null) {
                    ((ValueColor) value).setRainbow(valueJson.get(value.getName() + "-Rainbow").getAsBoolean());
                }
                if (valueJson.get(value.getName() + "-Alpha") != null) {
                    ((ValueColor) value).setValue(new Color(((ValueColor) value).getValue().getRed(), ((ValueColor) value).getValue().getGreen(), ((ValueColor) value).getValue().getBlue(), valueJson.get(value.getName() + "-Alpha").getAsInt()));
                }
                if (valueJson.get(value.getName() + "-Sync") == null) continue;
                ((ValueColor) value).setSync(valueJson.get(value.getName() + "-Sync").getAsBoolean());
                continue;
            }
            if (!(value instanceof ValueBind)) continue;
            ((ValueBind) value).setValue(dataObject.getAsInt());
        }
    }

    private void saveValues(JsonObject valueJson, ArrayList<Value> values) {
        for (Value value : values) {
            if (value instanceof ValueBoolean) {
                valueJson.add(value.getName(), new JsonPrimitive(((ValueBoolean) value).getValue()));
                continue;
            }
            if (value instanceof ValueNumber) {
                valueJson.add(value.getName(), new JsonPrimitive(((ValueNumber) value).getValue()));
                continue;
            }
            if (value instanceof ValueEnum) {
                valueJson.add(value.getName(), new JsonPrimitive(((ValueEnum) value).getValue().name()));
                continue;
            }
            if (value instanceof ValueString) {
                valueJson.add(value.getName(), new JsonPrimitive(((ValueString) value).getValue()));
                continue;
            }
            if (value instanceof ValueColor) {
                valueJson.add(value.getName(), new JsonPrimitive(((ValueColor) value).getValue().getRGB()));
                valueJson.add(value.getName() + "-Alpha", new JsonPrimitive(((ValueColor) value).getValue().getAlpha()));
                valueJson.add(value.getName() + "-Rainbow", new JsonPrimitive(((ValueColor) value).isRainbow()));
                valueJson.add(value.getName() + "-Sync", new JsonPrimitive(((ValueColor) value).isSync()));
                continue;
            }
            if (!(value instanceof ValueBind)) continue;
            valueJson.add(value.getName(), new JsonPrimitive(((ValueBind) value).getValue()));
        }
    }

    public void loadPrefix() throws IOException {
        if (!Files.exists(Paths.get("Opium/Client/Prefix.json"))) {
            return;
        }
        InputStream stream = Files.newInputStream(Paths.get("Opium/Client/Prefix.json"));
        JsonObject prefixJson = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        if (prefixJson.get("Prefix") == null) {
            return;
        }
        Opium.COMMAND_MANAGER.setPrefix(prefixJson.get("Prefix").getAsString());
        stream.close();
    }

    public void savePrefix() throws IOException {
        if (Files.exists(Paths.get("Opium/Client/Prefix.json"))) {
            File file = new File("Opium/Client/Prefix.json");
            file.delete();
        }
        Files.createFile(Paths.get("Opium/Client/Prefix.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject prefixJson = new JsonObject();
        prefixJson.add("Prefix", new JsonPrimitive(Opium.COMMAND_MANAGER.getPrefix()));
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("Opium/Client/Prefix.json"), StandardCharsets.UTF_8);
        writer.write(gson.toJson(new JsonParser().parse(prefixJson.toString())));
        writer.close();
    }

    public void loadFriends() throws IOException {
        if (!Files.exists(Paths.get("Opium/Client/Friends.json"))) {
            return;
        }
        InputStream stream = Files.newInputStream(Paths.get("Opium/Client/Friends.json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        if (mainObject.get("Friends") == null) {
            return;
        }
        JsonArray friendArray = mainObject.get("Friends").getAsJsonArray();
        friendArray.forEach(friend -> Opium.FRIEND_MANAGER.addFriend(friend.getAsString()));
        stream.close();
    }

    public void saveFriends() throws IOException {
        if (Files.exists(Paths.get("Opium/Client/Friends.json"))) {
            File file = new File("Opium/Client/Friends.json");
            file.delete();
        }
        Files.createFile(Paths.get("Opium/Client/Friends.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject mainObject = new JsonObject();
        JsonArray friendArray = new JsonArray();
        for (Friend friend : Opium.FRIEND_MANAGER.getFriends()) {
            friendArray.add(friend.getName());
        }
        mainObject.add("Friends", friendArray);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("Opium/Client/Friends.json"), StandardCharsets.UTF_8);
        writer.write(gson.toJson(new JsonParser().parse(mainObject.toString())));
        writer.close();
    }

    public static class SaveThread
            extends Thread {
        @Override
        public void run() {
            Opium.CONFIG_MANAGER.save();
        }
    }
}
