package dev.itsmeow.betteranimals.client;

import com.google.common.collect.Maps;
import dev.itsmeow.betteranimals.BetterAnimals;
import dev.itsmeow.betteranimals.client.model.*;
import dev.itsmeow.betteranimals.client.render.entity.layer.*;
import dev.itsmeow.betteranimals.compat.QuarkUtil;
import dev.itsmeow.betteranimals.compat.QuarkUtil.VariantTextureType;
import dev.itsmeow.imdlib.client.IMDLibClient;
import dev.itsmeow.imdlib.client.render.ImplRenderer.RenderDef;
import dev.itsmeow.imdlib.client.render.ImplRenderer.SuperCallApplyRotations;
import dev.itsmeow.imdlib.client.util.ModelReplacementHandler;
import dev.itsmeow.imdlib.client.util.ModelReplacementHandler.RegistrationTime;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.ModList;

import java.util.Map;

public class Replacements {

    public static final ModelReplacementHandler H = IMDLibClient.getReplacementHandler(BetterAnimals.MODID);

    public static void addReplaces() {
        // cow
        RenderDef<CowEntity, EntityModel<CowEntity>> cow = r -> r
        .childScale(0.5F)
        .tSingle("cow").mSingle(new ModelNewCow<>());

        // pig
        RenderDef<PigEntity, EntityModel<PigEntity>> pig = r -> r
        .childScale(0.5F)
        .layer(LayerNewPigSaddle::new)
        .tSingle("pig").mSingle(new ModelNewPig<>());

        // chicken
        RenderDef<ChickenEntity, EntityModel<ChickenEntity>> chicken = r -> r
        .childScale(0.45F)
        .handleRotation((e, p) -> {
            float f = e.oFlap + (e.wingRotation - e.oFlap) * p;
            float f1 = e.oFlapSpeed + (e.destPos - e.oFlapSpeed) * p;
            return (MathHelper.sin(f) + 1.0F) * f1;
        })
        .tSingle("chicken").mSingle(new ModelNewChicken<>());

        H.addAction(RegistrationTime.CLIENTSETUP, "minecraft", () -> () -> {
            boolean quarkLoaded = ModList.get().isLoaded("quark");
            if(!quarkLoaded || !BetterAnimalsConfig.enableQuarkOverrideCow.get()) {
                H.addReplace(RegistrationTime.CLIENTSETUP, "minecraft", "cow", () -> () -> H.lambdaReplace(EntityType.COW, 0.7F, cow));
            }
            if(!quarkLoaded || !BetterAnimalsConfig.enableQuarkOverridePig.get()) {
                H.addReplace(RegistrationTime.CLIENTSETUP, "minecraft", "pig", () -> () -> H.lambdaReplace(EntityType.PIG, 0.7F, pig));
            }
            if(!quarkLoaded || !BetterAnimalsConfig.enableQuarkOverrideChicken.get()) {
                H.addReplace(RegistrationTime.CLIENTSETUP, "minecraft", "chicken", () -> () -> H.lambdaReplace(EntityType.CHICKEN, 0.4F, chicken));
            }
        });
        H.addAction(RegistrationTime.CLIENTSETUP, "quark", () -> () -> {
            if(BetterAnimalsConfig.enableQuarkOverrideCow.get())
                H.addReplace(RegistrationTime.CLIENTSETUP, "minecraft", "cow", () -> () -> H.lambdaReplace(EntityType.COW, 0.7F, r -> cow.apply(r).tMappedRaw(e -> QuarkUtil.getTextureOrShiny(e, VariantTextureType.COW))));
            if(BetterAnimalsConfig.enableQuarkOverridePig.get())
                H.addReplace(RegistrationTime.CLIENTSETUP, "minecraft", "pig", () -> () -> H.lambdaReplace(EntityType.PIG, 0.7F, r -> pig.apply(r).tMappedRaw(e -> QuarkUtil.getTextureOrShiny(e, VariantTextureType.PIG))));
            if(BetterAnimalsConfig.enableQuarkOverrideChicken.get())
                H.addReplace(RegistrationTime.CLIENTSETUP, "minecraft", "chicken", () -> () -> H.lambdaReplace(EntityType.CHICKEN, 0.4F, r -> chicken.apply(r).tMappedRaw(e -> QuarkUtil.getTextureOrShiny(e, VariantTextureType.CHICKEN))));
        });

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "sheep", () -> () -> H.lambdaReplace(EntityType.SHEEP, 0.4F, r -> r
        .childScale(0.5F)
        .layer(LayerNewSheepWool::new)
        .tMapped(e -> e.isChild() ? "lamb" : "sheep").mSingle(new ModelNewSheep<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "wolf", () -> () -> H.lambdaReplace(EntityType.WOLF, 0.25F, r -> r
        .preRender((e, s, p) -> {
            s.scale(0.8F, 0.8F, 0.8F);
            if(e.isChild()) {
                s.scale(0.5F, 0.5F, 0.5F);
            }
        })
        .handleRotation((e, p) -> e.getTailRotation())
        .layer(LayerNewWolfCollar::new)
        .tMapped(e -> e.isTamed() ? "wolf/wolf_tame" : (e.func_233678_J__() ? "wolf/wolf_angry" : "wolf/wolf"))
        .mSingle(new ModelNewWolf<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "mooshroom", () -> () -> H.lambdaReplace(EntityType.MOOSHROOM, 0.7F, r -> r
        .preRender((e, s, p) -> {
            if(e.isChild()) {
                s.scale(0.36F, 0.36F, 0.36F);
            }
        })
        .layer(LayerNewMooshroomMushroom::new)
        .tCondition(e -> e.getMooshroomType() == MooshroomEntity.Type.RED, "mooshroom", "mooshroom_brown")
        .mSingle(new ModelNewCow<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "squid", () -> () -> H.lambdaReplace(EntityType.SQUID, 0.7F, r -> r
        .applyRotations((e, s, a, rot, p) -> {
            float f = e.prevSquidPitch + (e.squidPitch - e.prevSquidPitch) * p;
            float f1 = e.prevSquidYaw + (e.squidYaw - e.prevSquidYaw) * p;
            s.translate(0.0F, 0.5F, 0.0F);
            s.rotate(Vector3f.YP.rotationDegrees(180.0F - rot));
            s.rotate(Vector3f.XP.rotationDegrees(f));
            s.rotate(Vector3f.YP.rotationDegrees(f1));
            s.translate(0.0F, -1.2F, 0.0F);
        })
        .handleRotation((e, p) -> -(e.lastTentacleAngle + (e.tentacleAngle - e.lastTentacleAngle) * p))
        .tSingle("squid").mSingle(new ModelNewSquid<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "spider", () -> () -> H.lambdaReplace(EntityType.SPIDER, 1F, r -> r
        .preRender((e, s, p) -> {
            if(e.isBesideClimbableBlock()) {
                s.rotate(Vector3f.XP.rotationDegrees(-90F));
                s.translate(0.0F, 0.75F, -0.5F);
            }
        })
        .layer(LayerNewSpiderEyes::new)
        .tSingle("spider").mSingle(new ModelNewSpider<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "cavespider", () -> () -> H.lambdaReplace(EntityType.CAVE_SPIDER, 0.4F, r -> r
        .preRender((e, s, p) -> {
            if(e.isBesideClimbableBlock()) {
                s.rotate(Vector3f.XP.rotationDegrees(-90F));
                s.translate(0.0F, 0.75F, -0.5F);
            }
            s.scale(0.5F, 0.5F, 0.5F);
        })
        .layer(LayerNewSpiderEyes::new)
        .tSingle("cave_spider").mSingle(new ModelNewSpider<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "silverfish", () -> () -> H.lambdaReplace(EntityType.SILVERFISH, 0.25F, r -> r
        .preRender((e, s, p) -> s.scale(0.5F, 0.5F, 0.5F))
        .tSingle("silverfish").mSingle(new ModelNewSilverfish<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "polarbear", () -> () -> H.lambdaReplace(EntityType.POLAR_BEAR, 0.7F, r -> r
        .childScale(0.7F)
        .tSingle("polarbear").mSingle(new ModelNewBear<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "ocelot", () -> () -> H.lambdaReplace(EntityType.OCELOT, 0.5F, r -> r
        .preRender((e, s, p) -> {
            s.scale(0.8F, 0.8F, 0.8F);
            if(e.isChild()) {
                s.scale(0.5F, 0.5F, 0.5F);
            }
        })
        .tSingle("cat/ocelot").mSingle(new ModelNewCat<>())));

        Map<Integer, String> catTextures = Util.make(Maps.newHashMap(), (map) -> {
            map.put(0, "cat/tabby");
            map.put(1, "cat/black");
            map.put(2, "cat/red");
            map.put(3, "cat/siamese");
            map.put(4, "cat/british_shorthair");
            map.put(5, "cat/calico");
            map.put(6, "cat/persian");
            map.put(7, "cat/ragdoll");
            map.put(8, "cat/white");
            map.put(9, "cat/jellie");
            map.put(10, "cat/all_black");
        });
        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "cat", () -> () -> H.lambdaReplace(EntityType.CAT, 0.5F, r -> r
        .preRender((e, s, p) -> {
            s.scale(0.8F, 0.8F, 0.8F);
            if(e.isTamed()) {
                s.scale(0.9F, 0.9F, 0.9F);
            }
            if(e.isChild()) {
                s.scale(0.5F, 0.5F, 0.5F);
            }
        })
        .layer(LayerNewCatCollar::new)
        .tMapped(e -> catTextures.get(e.getCatType())).mSingle(new ModelNewCat<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "fox", () -> () -> H.<FoxEntity, ModelNewFox<FoxEntity>>lambdaReplace(EntityType.FOX, 0.4F, r -> r
        .preRender((e, s, p) -> {
            if(e.isChild()) {
                s.scale(0.5F, 0.5F, 0.5F);
            }
            if(e.isSleeping()) {
                s.translate(0F, 0.5F, 0F);
            }
            if(e.isSitting()) {
                s.translate(0F, 0.2F, 0F);
            }
        })
        .applyRotations((e, s, a, rot, p) -> {
            if(e.func_213480_dY() || e.isStuck()) {
                s.rotate(Vector3f.XP.rotationDegrees(-MathHelper.lerp(p, e.prevRotationPitch, e.rotationPitch)));
            }
        }, SuperCallApplyRotations.PRE)
        .layer(LayerNewFoxItem::new)
        .tMapped(e -> e.getVariantType() == FoxEntity.Type.RED ? (e.isSleeping() ? "fox/fox_sleep" : "fox/fox") : (e.isSleeping() ? "fox/snow_fox_sleep" : "fox/snow_fox"))
        .mSingle(new ModelNewFox<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "bee", () -> () -> H.lambdaReplace(EntityType.BEE, 0.4F, r -> r
        .childScale(0.5F)
        .tMapped(e -> e.func_233678_J__() ? (e.hasNectar() ? "bee/bee_angry_nectar" : "bee/bee_angry") : (e.hasNectar() ? "bee/bee_nectar" : "bee/bee"))
        .mSingle(new ModelNewBee<>())));

        H.addReplace(RegistrationTime.MODELREGISTRY, "minecraft", "dolphin", () -> () -> H.lambdaReplace(EntityType.DOLPHIN, 0.7F, r -> r
        .layer(LayerNewDolphinItem::new)
        .tSingle("dolphin")
        .mSingle(new ModelNewDolphin<>())));
    }

}
