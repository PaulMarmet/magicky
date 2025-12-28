package net.pm.magicky.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.pm.magicky.Magicky;
import net.pm.magicky.screen.AnvilScreenHandlerM;


@Environment(EnvType.CLIENT)
public class AnvilScreenM extends ItemCombinerScreen<AnvilScreenHandlerM> {
    private static final Identifier ERROR_TEXTURE = Identifier.withDefaultNamespace("container/anvil/error");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "textures/gui/container/anvil.png");
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private final Player player;

    public AnvilScreenM(AnvilScreenHandlerM handler, Inventory inventory, Component title) {
        super(handler, inventory, title, TEXTURE);
        this.player = inventory.player;
        this.titleLabelX = 60;
    }

    protected void subInit() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
    }

    public void resize(int i, int j) {
        this.init(i, j);
    }

    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.key() == 256) {
            this.minecraft.player.closeContainer();
        }

        return super.keyPressed(keyEvent);
    }

    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        super.renderLabels(context, mouseX, mouseY);
        int i = ((AnvilScreenHandlerM)this.menu).getLevelCost();
        if (i > 0) {
            int j = 8453920;
            Object text;
            if (!((AnvilScreenHandlerM)this.menu).getSlot(2).hasItem()) {
                text = null;
            } else {
                text = Component.translatable("container.repair.cost", new Object[]{i});
                if (!((AnvilScreenHandlerM)this.menu).getSlot(2).mayPickup(this.player)) {
                    j = 16736352;
                }
            }

            if (text != null) {
                int k = this.imageWidth - 8 - this.font.width((FormattedText)text) - 2;
                boolean l = true;
                context.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                context.drawString(this.font, (Component)text, k, 69, j);
            }
        }

    }

    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        super.renderBg(context, delta, mouseX, mouseY);
    }

    protected void renderErrorIcon(GuiGraphics context, int x, int y) {
        if ((((AnvilScreenHandlerM)this.menu).getSlot(0).hasItem() || ((AnvilScreenHandlerM)this.menu).getSlot(1).hasItem()) && !((AnvilScreenHandlerM)this.menu).getSlot(((AnvilScreenHandlerM)this.menu).getResultSlot()).hasItem()) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED,ERROR_TEXTURE, x + 99, y + 45, 28, 21);
        }

    }

}
