package mod.chloeprime.thirdpersonshooting.client;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static java.lang.Math.*;
import static net.minecraftforge.event.TickEvent.Phase.START;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RotationFixer {
    public static float xRot, yRot;
    public static boolean ready = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void fixRotationToLocalPlayer(TickEvent.ClientTickEvent e) {
        if (e.phase == START) {
            ready = false;
            return;
        }

        if (!ShoulderInstance.getInstance().doShoulderSurfing() || Config.CLIENT.getCrosshairType().isDynamic()) {
            ready = false;
            return;
        }

        var minecraft = Minecraft.getInstance();
        var mainCamera = minecraft.gameRenderer.getMainCamera();
        var player = Minecraft.getInstance().player;

        if (player == null) {
            ready = false;
            return;
        }

        var reach = minecraft.options.renderDistance * 16;
        var hit = min(traceBlock(mainCamera, player, reach), traceEntity(mainCamera, player, reach));
        if (hit != null) {
            var delta = hit.target().subtract(hit.start());
            var distance = 1 / Mth.fastInvSqrt(delta.lengthSqr());

            xRot = Mth.wrapDegrees((float) toDegrees(-(atan2(delta.y, distance))));
            yRot = Mth.wrapDegrees((float) toDegrees(atan2(delta.z, delta.x)) - 90);
            ready = true;
        }
    }

    private static TpsHitResult traceBlock(Camera camera, Entity player, double reach) {
        var partials = Minecraft.getInstance().getFrameTime();
        var blockHit = ShoulderHelper.traceBlocks(camera, player, ClipContext.Fluid.NONE, reach, partials, true);
        if (blockHit == null) {
            return null;
        }
        var start = player.getEyePosition();
        var target = blockHit.getLocation();
        return new TpsHitResult(start, target, target.distanceToSqr(start));
    }

    private static TpsHitResult traceEntity(Camera camera, Entity player, double reach) {
        var partials = Minecraft.getInstance().getFrameTime();
        var entityHit = ShoulderHelper.traceEntities(camera, player, reach, partials, true);
        if (entityHit == null) {
            return null;
        }
        var start = player.getEyePosition();
        var target = entityHit.getLocation();
        return new TpsHitResult(start, target, target.distanceToSqr(start));
    }

    private static TpsHitResult min(TpsHitResult a, TpsHitResult b) {
        if (b == null && a == null) {
            return null;
        }
        if (b == null) return a;
        if (a == null) return b;
        return a.distanceSq() < b.distanceSq() ? a : b;
    }
}
