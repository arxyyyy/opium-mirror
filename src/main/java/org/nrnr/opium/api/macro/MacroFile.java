package org.nrnr.opium.api.macro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.file.ConfigFile;
import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.init.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MacroFile extends ConfigFile {

    /**
     * @param dir
     */
    public MacroFile(Path dir) {
        super(dir, "macros");
    }

    @Override
    public void save() {
        try {
            Path filepath = getFilepath();
            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }
            JsonArray object = new JsonArray();
            for (Macro macro : Managers.MACRO.getMacros()) {
                object.add(macro.toJson());
            }
            write(filepath, serialize(object));
        }
        // error writing file
        catch (IOException e) {
            Opium.error("Could not save macro file!");
            e.printStackTrace();
        }

    }

    @Override
    public void load() {
        try {
            Path filepath = getFilepath();
            if (Files.exists(filepath)) {
                String content = read(filepath);
                JsonArray object = parseArray(content);
                for (JsonElement element : object.getAsJsonArray()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (jsonObject.has("id")) {
                        String id = jsonObject.get("id").getAsString();
                        Macro macro = Managers.MACRO.getMacro(m -> m.getId().equals(id));
                        if (macro != null) {
                            macro.fromJson(jsonObject);
                            Module module = Managers.MODULE.getModule(id.substring(0, id.length() - 6));
                            if (module instanceof ToggleModule t) {
                                t.keybind(jsonObject.get("value").getAsInt());
                            }
                        }
                    }
                }
            }
        }

        catch (IOException e) {
            Opium.error("Could not read macro file!");
            e.printStackTrace();
        }
    }
}
