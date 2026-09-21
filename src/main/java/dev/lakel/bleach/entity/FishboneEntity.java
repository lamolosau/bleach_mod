package dev.lakel.bleach.entity;

import dev.lakel.bleach.BleachMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FishboneEntity extends Monster implements GeoEntity {
  private int roarTicks = 0;
  private int attackDelay = 0;
  private boolean hasRoared = false;
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

  public FishboneEntity(EntityType<? extends Monster> entityType, Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Monster.createMonsterAttributes()
      .add(Attributes.MAX_HEALTH, 50.0D)
      .add(Attributes.MOVEMENT_SPEED, 0.28D)
      .add(Attributes.ATTACK_DAMAGE, 15.0D);
  }

  @Override
  protected void registerGoals() {
    this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    this.goalSelector.addGoal(1, new HollowMeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "movement", 5, event -> {
      event.getController().setAnimationSpeed(1.0f);
      if (event.isMoving()) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.fishbone.walk"));
      }
      return event.setAndContinue(RawAnimation.begin().thenLoop("animation.fishbone.idle"));
    }));

    AnimationController<FishboneEntity> actionController = new AnimationController<>(this, "action", 3, event -> PlayState.STOP);
    actionController.triggerableAnim("roar", RawAnimation.begin().thenPlay("animation.fishbone.roar"));
    controllers.add(actionController);

    AnimationController<FishboneEntity> attackController = new AnimationController<>(this, "attack_controller", 0, event -> PlayState.STOP);
    attackController.setAnimationSpeed(2.0f); // On double la vitesse de l'animation d'attaque ici
    attackController.triggerableAnim("attack", RawAnimation.begin().thenPlay("animation.fishbone.attack"));
    controllers.add(attackController);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  @Override
  public void tick() {
    super.tick();

    if (this.roarTicks > 0) {
      this.roarTicks--;
    }
    if (this.attackDelay > 0) {
      this.attackDelay--;
      if (this.attackDelay == 0 && !this.level().isClientSide()) {
        // Seul le serveur gère les dégâts
        LivingEntity target = this.getTarget();
        if (target != null && this.distanceToSqr(target) < 25.0D) {
          super.doHurtTarget(target); 
        }
      }
    }

    if (this.level().isClientSide()) return; 

    LivingEntity target = this.getTarget();
    if (target != null && !this.hasRoared) {
      this.hasRoared = true;
      this.roarTicks = 50; 
      this.triggerAnim("action", "roar");
      this.playSound(BleachMod.FISHBONE_ROAR_EVENT, 1.0F, 1.0F);
    }

    if (target == null) {
      this.hasRoared = false;
    }
  }

  @Override
  public void travel(Vec3 travelVector) {
    if (this.roarTicks > 0 || this.attackDelay > 0) {
      this.setDeltaMovement(0, this.getDeltaMovement().y, 0); 
      super.travel(Vec3.ZERO); 
      return;
    }
    super.travel(travelVector);
  }

  class HollowMeleeAttackGoal extends MeleeAttackGoal {
    public HollowMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
      super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    protected int getAttackInterval() {
      return this.adjustedTickDelay(25);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
      double reach = this.getAttackReachSqr(target);

      if (squaredDistance <= reach && this.isTimeToAttack() && FishboneEntity.this.roarTicks == 0 && FishboneEntity.this.attackDelay == 0) {
        this.resetAttackCooldown();
        FishboneEntity.this.attackDelay = 14; 
        FishboneEntity.this.triggerAnim("attack_controller", "attack");
      }
    }
  }

}
