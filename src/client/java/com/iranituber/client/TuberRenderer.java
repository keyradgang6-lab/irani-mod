package com.iranituber.client;

import com.iranituber.IraniTuberMod;
import com.iranituber.entity.TuberEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class TuberRenderer extends BipedEntityRenderer<TuberEntity, BipedEntityModel<TuberEntity>> {
	private static final Identifier[] TEXTURES = {
			Identifier.of(IraniTuberMod.MOD_ID, "textures/entity/arsam.png"),
			Identifier.of(IraniTuberMod.MOD_ID, "textures/entity/pegah.png"),
			Identifier.of(IraniTuberMod.MOD_ID, "textures/entity/bardia.png")
	};

	public TuberRenderer(EntityRendererFactory.Context context) {
		super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
	}

	@Override
	public Identifier getTexture(TuberEntity entity) {
		return TEXTURES[Math.floorMod(entity.getVariant(), TEXTURES.length)];
	}
}
