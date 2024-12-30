package org.nrnr.opium.impl.manager.client;

import net.minecraft.client.session.Session;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.account.config.AccountFile;
import org.nrnr.opium.api.account.config.EncryptedAccountFile;
import org.nrnr.opium.api.account.msa.MSAAuthenticator;
import org.nrnr.opium.api.account.type.MinecraftAccount;
import org.nrnr.opium.mixin.accessor.AccessorMinecraftClient;
import org.nrnr.opium.util.Globals;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @see MinecraftAccount
 * @since 03/31/24
 */
public final class AccountManager implements Globals {
    // The Microsoft authenticator
    public static final MSAAuthenticator MSA_AUTHENTICATOR = new MSAAuthenticator();
    private final List<MinecraftAccount> accounts = new LinkedList<>();

    private AccountFile configFile;

    public void postInit() {
        // Handle account file encryption
        final Path runDir = Opium.CONFIG.getClientDirectory();
        if (runDir.resolve("accounts_enc.json").toFile().exists()) {
            System.out.println("Encrypted account file exists");
            configFile = new EncryptedAccountFile(runDir);
        } else {
            System.out.println("Normal account file");
            configFile = new AccountFile(runDir);
        }

        Opium.CONFIG.addFile(configFile);
    }

    /**
     * @param account
     */
    public void register(MinecraftAccount account) {
        accounts.add(account);
    }

    /**
     * @param account
     */
    public void unregister(final MinecraftAccount account) {
        accounts.remove(account);
    }

    public void setSession(final Session session) {
        ((AccessorMinecraftClient) mc).setSession(session);
        Opium.info("Set session to {} ({})", session.getUsername(), session.getUuidOrNull());
    }

    /**
     * @return
     */
    public List<MinecraftAccount> getAccounts() {
        return accounts;
    }

    public boolean isEncrypted() {
        return configFile instanceof EncryptedAccountFile;
    }
}
