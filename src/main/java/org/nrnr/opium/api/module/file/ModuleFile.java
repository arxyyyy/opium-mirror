package org.nrnr.opium.api.module.file;

import com.google.gson.JsonObject;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.file.ConfigFile;
import org.nrnr.opium.api.module.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author chronos
 * @see Module
 * @see ConfigFile
 * @since 1.0
 */
public class ModuleFile extends ConfigFile {
    //
    private final Module module;

    /**
     * @param dir
     * @param module
     */
    public ModuleFile(Path dir, Module module) {
        super(dir, module.getId());
        this.module = module;
    }

    /**
     *
     */
    @Override
    public void save() {
        try {
            Path filepath = getFilepath();
            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }
            JsonObject json = module.toJson();
            write(filepath, serialize(json));
        }
        // error writing file
        catch (IOException e) {
            Opium.error("Could not save file for {}!", module.getName());
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void load() {
        try {
            Path filepath = getFilepath();
            if (Files.exists(filepath)) {
                String content = read(filepath);
                module.fromJson(parseObject(content));
            }
        }
        // error writing file
        catch (IOException e) {
            Opium.error("Could not read file for {}!", module.getName());
            e.printStackTrace();
        }
    }
}
