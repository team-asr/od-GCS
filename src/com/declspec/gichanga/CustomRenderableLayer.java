package com.declspec.gichanga;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.render.Renderable;

public class CustomRenderableLayer extends RenderableLayer implements PreRenderable, Renderable {
	@Override
	public void render(DrawContext dc) {
		if (dc.isPickingMode() && !this.isPickEnabled())
			return;
		if (!this.isEnabled())
			return;

		super.render(dc);
	}
}