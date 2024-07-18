package net.pm.magicky.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pm.magicky.Magicky;
import net.pm.magicky.screen.AnvilScreenHandlerM;


@Environment(EnvType.CLIENT)
public class AnvilScreenM extends ForgingScreen<AnvilScreenHandlerM> {
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
    private static final Identifier TEXTURE = Identifier.of(Magicky.MOD_ID, "textures/gui/container/anvil_m.png");
    private static final Text TOO_EXPENSIVE_TEXT = Text.translatable("container.repair.expensive");
    private final PlayerEntity player;

    public AnvilScreenM(AnvilScreenHandlerM handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
        this.player = inventory.player;
        this.titleX = 60;
    }

    protected void setup() {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
    }

    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        int i = ((AnvilScreenHandlerM)this.handler).getLevelCost();
        if (i > 0) {
            int j = 8453920;
            Object text;
            if (!((AnvilScreenHandlerM)this.handler).getSlot(2).hasStack()) {
                text = null;
            } else {
                text = Text.translatable("container.repair.cost", new Object[]{i});
                if (!((AnvilScreenHandlerM)this.handler).getSlot(2).canTakeItems(this.player)) {
                    j = 16736352;
                }
            }

            if (text != null) {
                int k = this.backgroundWidth - 8 - this.textRenderer.getWidth((StringVisitable)text) - 2;
                boolean l = true;
                context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
                context.drawTextWithShadow(this.textRenderer, (Text)text, k, 69, j);
            }
        }

    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
    }

    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((((AnvilScreenHandlerM)this.handler).getSlot(0).hasStack() || ((AnvilScreenHandlerM)this.handler).getSlot(1).hasStack()) && !((AnvilScreenHandlerM)this.handler).getSlot(((AnvilScreenHandlerM)this.handler).getResultSlotIndex()).hasStack()) {
            context.drawGuiTexture(ERROR_TEXTURE, x + 99, y + 45, 28, 21);
        }

    }

}
