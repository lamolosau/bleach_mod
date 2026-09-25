package dev.lakel.bleach.client.renderer;

import dev.lakel.bleach.client.model.AsauchiModel;
import dev.lakel.bleach.item.AsauchiItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AsauchiRenderer extends GeoItemRenderer<AsauchiItem> {
    public AsauchiRenderer() {
        super(new AsauchiModel());
    }
}
