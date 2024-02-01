package mod.chloeprime.thirdpersonshooting.mixin.client;

import mod.chloeprime.thirdpersonshooting.client.TpsPlayer;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Camera.class, priority = 1080)
public class MixinCamera {
    @Redirect(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F")
    )
    private float redirectViewRotX(Entity entity, float partial) {
        if (entity instanceof TpsPlayer player) {
            return player.TPSMOD_getVirtualRotX(partial);
        }
        return entity.getViewXRot(partial);
    }

    @Redirect(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F")
    )
    private float redirectViewRotY(Entity entity, float partial) {
        if (entity instanceof TpsPlayer player) {
            return player.TPSMOD_getVirtualRotY(partial);
        }
        return entity.getViewYRot(partial);
    }
}
