package we.devs.opium.asm.mixins;

import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import we.devs.opium.asm.ducks.ISession;

@Mixin(Session.class)
public class SessionMixin implements ISession {

    @Mutable
    @Shadow @Final private String username;

    @Override
    public void opium$setUsername(String name) {
        this.username = name;
    }
}
