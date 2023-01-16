package mod.chloeprime.elytrabooster.mixin.client;

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi;
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig;
import mod.chloeprime.elytrabooster.common.item.ElytraBoostingHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow
    public abstract double getAttributeValue(Attribute pAttribute);

    private Vec3 remMotion = Vec3.ZERO;

    @Inject(
            id = "ElytraBooster.MixinLivingEntity.beginLivingTravel",
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z")
    )
    private void beginLivingTravel(Vec3 pTravelVector, CallbackInfo ci) {
        if (shouldOverrideTravel()) {
            ElytraBoostingHandler.onPlayerTravel((Player) thiz());

            boolean hardcore = !ElyBoosterModConfig.INSTANCE.getCAUSAL_MODE().get();
            if (hardcore) {
                processGravity();
                remMotion = thiz().getDeltaMovement();
            }
        }
    }

    @Redirect(
            method = "travel",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"),
                    to = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/LivingEntity;calculateEntityAnimation(Lnet/minecraft/world/entity/LivingEntity;Z)V")
            ),
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    private void onLivingTravelSpeedSet(LivingEntity instance, Vec3 vec3) {
        if (remMotion != null) {
            instance.setDeltaMovement(remMotion);
            remMotion = null;
        } else {
            instance.setDeltaMovement(vec3);
        }
    }

    private boolean shouldOverrideTravel() {
        return level.isClientSide && thiz() instanceof LocalPlayer && ElytraBoosterApi.isFlyingWithBooster(thiz());
    }

    private void processGravity() {
        double gravity = getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        setDeltaMovement(getDeltaMovement().add(0, -gravity, 0));
    }

    private LivingEntity thiz() {
        return (LivingEntity) (Object) this;
    }
}
