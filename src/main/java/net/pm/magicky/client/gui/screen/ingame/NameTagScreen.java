package net.pm.magicky.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pm.magicky.packet.RenameNameTagPayload;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class NameTagScreen extends Screen {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("magicky", "textures/gui/name_tag.png");
	private static final Component title = Component.translatable("gui.magicky.name_tag.title");
    private EditBox nameField;
	private Component name;
	private static int maxLength = 50;
	private final ItemStack stack;
	private final InteractionHand hand;
	@Nullable
	private TextFieldHelper selectionManager;


	public NameTagScreen(Player player, InteractionHand hand) {
		super(title);
		this.hand = hand;
		this.stack = player.getItemInHand(this.hand);
		if (stack.has(DataComponents.CUSTOM_NAME)) {
			this.name = stack.get(DataComponents.CUSTOM_NAME);
		} else {
			this.name = Component.empty();
		}

	}

	@Override
	protected void init() {
		if (this.minecraft == null) {
			return;
		}

		int i = (this.width) / 2;
		int j = (this.height) / 2;
		this.nameField = new EditBox(this.font, i - 50, j - 6, 90, 12, title);
		this.nameField.setValue("");
		this.nameField.setMaxLength(maxLength);
		this.addWidget(this.nameField);
		this.nameField.setEditable(true);

        Button applyButton = Button.builder(CommonComponents.GUI_DONE, button -> this.write()).bounds(this.width / 2 + 25, this.height * 3 / 4, 100, 20).build();
        Button cancelButton = Button.builder(CommonComponents.GUI_CANCEL, button -> this.finishEditing()).bounds(this.width / 2 - 125, this.height * 3 / 4, 100, 20).build();

		this.addRenderableWidget(this.nameField);
		this.addRenderableWidget(applyButton);
		this.addRenderableWidget(cancelButton);
    }

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		if (keyEvent.key() == GLFW.GLFW_KEY_ENTER || keyEvent.key() == GLFW.GLFW_KEY_KP_ENTER) {
			this.write();
			return true;
		} else {
			return super.keyPressed(keyEvent);
		}
	}

	@Override
	public boolean charTyped(CharacterEvent characterEvent) {
		return this.nameField.charTyped(characterEvent);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredString(this.font, title, this.width / 2, 40, 16777215);
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.renderTransparentBackground(context);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.width / 2 - 72, this.height / 2 - 48, 0, 0, this.width, this.height, 256, 256);
	}

	@Override
	public void onClose() {
		this.finishEditing();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void finishEditing() {
		this.minecraft.setScreen(null);
	}

	private void write() {
		ClientPlayNetworking.send(new RenameNameTagPayload(this.nameField.getValue(), this.hand == InteractionHand.MAIN_HAND));
		this.finishEditing();
	}
}
