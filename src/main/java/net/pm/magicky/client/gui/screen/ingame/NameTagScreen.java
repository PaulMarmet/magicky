package net.pm.magicky.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.pm.magicky.packet.RenameNameTagPayload;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class NameTagScreen extends Screen {
	private static final Identifier TEXTURE = Identifier.of("magicky", "textures/gui/name_tag.png");
	private static final Text title = Text.translatable("gui.magicky.name_tag.title");
    private TextFieldWidget nameField;
	private Text name;
	private static int maxLength = 50;
	private final ItemStack stack;
	private final Hand hand;
	@Nullable
	private SelectionManager selectionManager;


	public NameTagScreen(PlayerEntity player, Hand hand) {
		super(title);
		this.hand = hand;
		this.stack = player.getStackInHand(this.hand);
		if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
			this.name = stack.get(DataComponentTypes.CUSTOM_NAME);
		} else {
			this.name = Text.empty();
		}

	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}

		int i = (this.width) / 2;
		int j = (this.height) / 2;
		this.nameField = new TextFieldWidget(this.textRenderer, i - 50, j - 6, 90, 12, title);
		this.nameField.setText("");
		this.nameField.setMaxLength(maxLength);
		this.addSelectableChild(this.nameField);
		this.nameField.setEditable(true);

        ButtonWidget applyButton = ButtonWidget.builder(ScreenTexts.DONE, button -> this.write()).dimensions(this.width / 2 + 25, this.height * 3 / 4, 100, 20).build();
        ButtonWidget cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.finishEditing()).dimensions(this.width / 2 - 125, this.height * 3 / 4, 100, 20).build();

		this.addDrawableChild(this.nameField);
		this.addDrawableChild(applyButton);
		this.addDrawableChild(cancelButton);
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			this.write();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.nameField.charTyped(chr, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		DiffuseLighting.disableGuiDepthLighting();
		context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, 40, 16777215);
		DiffuseLighting.enableGuiDepthLighting();
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(context);
		context.drawTexture(TEXTURE, this.width / 2 - 72, this.height / 2 - 48, 0, 0, 256, 256);
	}

	@Override
	public void close() {
		this.finishEditing();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private void finishEditing() {
		this.client.setScreen(null);
	}

	private void write() {
		ClientPlayNetworking.send(new RenameNameTagPayload(this.nameField.getText(), this.hand == Hand.MAIN_HAND));
		this.finishEditing();
	}
}
