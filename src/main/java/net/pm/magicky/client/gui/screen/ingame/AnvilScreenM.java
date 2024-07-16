package net.pm.magicky.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pm.magicky.screen.AnvilScreenHandlerM;


@Environment(EnvType.CLIENT)
public class AnvilScreenM extends ForgingScreen<AnvilScreenHandlerM> {
//    private static final Identifier TEXT_FIELD_TEXTURE = Identifier.ofVanilla("container/anvil/text_field");
//    private static final Identifier TEXT_FIELD_DISABLED_TEXTURE = Identifier.ofVanilla("container/anvil/text_field_disabled");
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/anvil.png");
    private static final Text TOO_EXPENSIVE_TEXT = Text.translatable("container.repair.expensive");
    private TextFieldWidget nameField;
    private final PlayerEntity player;

    public AnvilScreenM(AnvilScreenHandlerM handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
        this.player = inventory.player;
        this.titleX = 60;
    }

    protected void setup() {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, Text.translatable("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setDrawsBackground(false);
        this.nameField.setMaxLength(50);
//        this.nameField.setChangedListener(this::onRenamed);
        this.nameField.setText("");
        this.addSelectableChild(this.nameField);
        this.nameField.setEditable(((AnvilScreenHandlerM)this.handler).getSlot(0).hasStack());
    }

    protected void setInitialFocus() {
        this.setInitialFocus(this.nameField);
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return !this.nameField.keyPressed(keyCode, scanCode, modifiers) && !this.nameField.isActive() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
    }

//    private void onRenamed(String name) {
//        Slot slot = ((AnvilScreenHandlerM)this.handler).getSlot(0);
//        if (slot.hasStack()) {
//            String string = name;
//            if (!slot.getStack().contains(DataComponentTypes.CUSTOM_NAME) && string.equals(slot.getStack().getName().getString())) {
//                string = "";
//            }
//
//            if (((AnvilScreenHandlerM)this.handler).setNewItemName(string)) {
//                this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
//            }
//
//        }
//    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        int i = ((AnvilScreenHandlerM)this.handler).getLevelCost();
        if (i > 0) {
            int j = 8453920;
            Object text;
            if (i >= 40 && !this.client.player.getAbilities().creativeMode) {
                text = TOO_EXPENSIVE_TEXT;
                j = 16736352;
            } else if (!((AnvilScreenHandlerM)this.handler).getSlot(2).hasStack()) {
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
//        context.drawGuiTexture(((AnvilScreenHandlerM)this.handler).getSlot(0).hasStack() ? TEXT_FIELD_TEXTURE : TEXT_FIELD_DISABLED_TEXTURE, this.x + 59, this.y + 20, 110, 16);
    }

    public void renderForeground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.nameField.render(context, mouseX, mouseY, delta);
    }

    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((((AnvilScreenHandlerM)this.handler).getSlot(0).hasStack() || ((AnvilScreenHandlerM)this.handler).getSlot(1).hasStack()) && !((AnvilScreenHandlerM)this.handler).getSlot(((AnvilScreenHandlerM)this.handler).getResultSlotIndex()).hasStack()) {
            context.drawGuiTexture(ERROR_TEXTURE, x + 99, y + 45, 28, 21);
        }

    }

    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 0) {
            this.nameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
            this.nameField.setEditable(!stack.isEmpty());
            this.setFocused(this.nameField);
        }

    }
}
