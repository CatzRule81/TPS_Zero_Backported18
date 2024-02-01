package mod.chloeprime.thirdpersonshooting.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker
    Vec3 invokeCalculateViewVector(float pXRot, float pYRot);
}
