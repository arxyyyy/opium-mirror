package org.nrnr.opium.api.module.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.file.ConfigFile;
import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.init.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModuleConfigFile extends ConfigFile {

    /**
     * @param dir
     * @param path
     */
    public ModuleConfigFile(Path dir, String path) {
        super(dir, path);
    }

    @Override
    public void save() {
        try {
            Path filepath = getFilepath();
            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }
            final JsonObject out = new JsonObject();
            final JsonArray array = new JsonArray();
            for (Module module : Managers.MODULE.getModules()) {
                array.add(module.toJson());
            }
            out.add("configs", array);
            write(filepath, serialize(out));
        }
        // error writing file
        catch (IOException e) {
            Opium.error("Could not save file for {}!", getFilepath());
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            Path filepath = getFilepath();
            if (Files.exists(filepath)) {
                String content = read(filepath);
                JsonObject in = parseObject(content);
                if (!in.has("configs")) {
                    return;
                }
                JsonArray array = in.getAsJsonArray("configs");
                for (JsonElement element : array.asList()) {
                    JsonObject object = element.getAsJsonObject();
                    if (object.has("id")) {
                        Module module = Managers.MODULE.getModule(object.get("id").getAsString());
                        if (module == null) {
                            return;
                        }
                        module.fromJson(object);
                    }
                }
            }
        }
        // error writing file
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
