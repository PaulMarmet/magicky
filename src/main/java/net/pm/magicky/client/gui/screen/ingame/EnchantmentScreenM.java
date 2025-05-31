package net.pm.magicky.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.pm.magicky.Magicky;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;

import java.util.List;

@Environment(EnvType.CLIENT)
public class EnchantmentScreenM extends HandledScreen<EnchantmentScreenHandlerM> {
//    private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1"), Identifier.ofVanilla("container/enchanting_table/level_2"), Identifier.ofVanilla("container/enchanting_table/level_3")};
//    private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1_disabled"), Identifier.ofVanilla("container/enchanting_table/level_2_disabled"), Identifier.ofVanilla("container/enchanting_table/level_3_disabled")};
private static final Identifier SCROLLER_TEXTURE = Identifier.of(Magicky.MOD_ID, "container/enchanting_table/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.of(Magicky.MOD_ID, "container/enchanting_table/scroller_disabled");
    private static final Identifier ENCHANTMENT_SLOT_TEXTURE = Identifier.of(Magicky.MOD_ID, "container/enchanting_table/enchantment_slot");
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_TEXTURE = Identifier.of(Magicky.MOD_ID, "container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = Identifier.of(Magicky.MOD_ID, "container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier TEXTURE = Identifier.of(Magicky.MOD_ID, "textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/entity/enchanting_table_book.png");
    private final Random random = Random.create();
    private BookModel BOOK_MODEL;
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack;
    private static final int OFFSET_Y = 15;
    private static final int HEIGHT = 55;
    private static final int SLOT_HEIGHT = 11;
    private static final int FIELD_OFFSET_X = 60;
    private static final int FIELD_WIDTH = 93;
    private static final int TEXT_BUFFER = 2;
    private static final int SCROLLBAR_OFFSET_X = 156;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private float scrollAmount;
    private int scrollOffset;
    private boolean mouseClicked;

    public EnchantmentScreenM(EnchantmentScreenHandlerM handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.stack = ItemStack.EMPTY;
    }

    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        this.doTick();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
        int localX = (this.width - this.backgroundWidth) / 2;
        int localY = (this.height - this.backgroundHeight) / 2;

        for(int k = 0; k < this.handler.getEnchantmentCount() && k < (HEIGHT / SLOT_HEIGHT); ++k) {
            int slotOffset = SLOT_HEIGHT * k;
            double horizontalBounds = mouseX - (double)(localX + FIELD_OFFSET_X);
            double verticalBounds = mouseY - (double)(localY + OFFSET_Y + slotOffset);
            if (horizontalBounds >= 0.0 && verticalBounds >= 0.0 && horizontalBounds < FIELD_WIDTH && verticalBounds < SLOT_HEIGHT && this.handler.onButtonClick(this.client.player, k + this.scrollOffset)) {
                this.client.interactionManager.clickButton(this.handler.syncId, k + this.scrollOffset);
                return true;
            }
        }
        if (this.inScrollArea(mouseX, mouseY)) {
            this.mouseClicked = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.getMaxScroll() > 0 && this.inScrollArea(mouseX, mouseY)) {
            int i = this.y + OFFSET_Y;
            int j = i + HEIGHT;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * 4;
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.getMaxScroll() > 0 && this.inEnchantmentArea(mouseX, mouseY)) {
            int maxScroll = this.getMaxScroll();
            float scrollFraction = (float)verticalAmount / (float)maxScroll;
            this.scrollAmount = MathHelper.clamp(this.scrollAmount - scrollFraction, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)maxScroll) + 0.5) * 4;
        }

        return true;
    }

    public int getMaxScroll() {
        int displayable = HEIGHT / SLOT_HEIGHT;
        int total = this.handler.getEnchantmentCount();
        return displayable >= total ? 0 : total - displayable;
    }

    public int firstShown() {
        return (int) (this.scrollAmount * this.getMaxScroll());
    }

    public boolean inEnchantmentArea(double mouseX, double mouseY) {
        int localX = (this.width - this.backgroundWidth) / 2;
        int localY = (this.height - this.backgroundHeight) / 2;

        return (mouseX >= localX + FIELD_OFFSET_X - 1
                && mouseX <= localX + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH + 1
                && mouseY >= localY + OFFSET_Y - 1
                && mouseY <= localY + OFFSET_Y + HEIGHT + 1);
    }
    public boolean inScrollArea(double mouseX, double mouseY) {
        int localX = (this.width - this.backgroundWidth) / 2;
        int localY = (this.height - this.backgroundHeight) / 2;

        return (mouseX >= localX + SCROLLBAR_OFFSET_X - 1
                && mouseX <= localX + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH + 1
                && mouseY >= localY + OFFSET_Y - 1
                && mouseY <= localY + OFFSET_Y + HEIGHT + 1);
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int zeroX = (this.width - this.backgroundWidth) / 2;
        int zeroY = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(TEXTURE, zeroX, zeroY, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawBook(context, zeroX, zeroY, delta);
        //not really used lol
        EnchantingPhrases.getInstance().setSeed(0);

        int skroll = (int)((HEIGHT - SCROLLBAR_HEIGHT) * this.scrollAmount);
        Identifier scroller = this.getMaxScroll() > 0 ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.drawGuiTexture(scroller, zeroX + SCROLLBAR_OFFSET_X,  zeroY + OFFSET_Y + skroll, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);

        int catCount = this.handler.getCatalystCount();
        //Magicky.LOGGER.info("has: "+this.handler.getEnchantmentCount());

        for(int i = 0; i < (HEIGHT / SLOT_HEIGHT); ++i) {
            int slot = this.firstShown() + i;
            RegistryEntry<Enchantment> enchantment = this.handler.getEnchantment(this.client.world, slot);
            //make good power calc
            int power = this.handler.getEnchantmentPower(this.client.world, slot);
            //Magicky.LOGGER.info("power:"+power);
            if (power == 0 || enchantment == null) {
                RenderSystem.enableBlend();
                context.drawGuiTexture(ENCHANTMENT_SLOT_DISABLED_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
                RenderSystem.disableBlend();
            } else {
                String string = "" + power;
                int p = FIELD_WIDTH - 7 - this.textRenderer.getWidth(string);
                StringVisitable stringVisitable = textRenderer.getTextHandler().trimToWidth(Text.literal(Enchantment.getName(enchantment, this.handler.enchantmentLevels.get(slot)).getString()).fillStyle(Style.EMPTY.withFont(Identifier.ofVanilla("alt"))), width, Style.EMPTY);
                StringVisitable stringClear = textRenderer.getTextHandler().trimToWidth(Text.literal(Enchantment.getName(enchantment, this.handler.enchantmentLevels.get(slot)).getString()), width, Style.EMPTY);//EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, p);
                int q = 6839882;
                if ((catCount < i + 1 || this.client.player.experienceLevel < power) && !this.client.player.getAbilities().creativeMode) {
                    RenderSystem.enableBlend();
                    context.drawGuiTexture(ENCHANTMENT_SLOT_DISABLED_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
//                    context.drawGuiTexture(LEVEL_DISABLED_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    RenderSystem.disableBlend();
                    context.drawTextWrapped(this.textRenderer, stringVisitable, zeroX + FIELD_OFFSET_X + TEXT_BUFFER, zeroY + OFFSET_Y + TEXT_BUFFER + SLOT_HEIGHT * i, p, (q & 16711422) >> 1);
                } else {
                    int r = mouseX - (zeroX + FIELD_OFFSET_X);
                    int s = mouseY - (zeroY + OFFSET_Y + SLOT_HEIGHT * i);
                    RenderSystem.enableBlend();
                    if (r >= 0 && s >= 0 && r < FIELD_WIDTH && s < SLOT_HEIGHT) {
                        context.drawGuiTexture(ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
                        q = 16777088;
                    } else {
                        context.drawGuiTexture(ENCHANTMENT_SLOT_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
                    }

                    //context.drawGuiTexture(LEVEL_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    RenderSystem.disableBlend();
                    context.drawTextWrapped(this.textRenderer, stringVisitable, zeroX + FIELD_OFFSET_X + TEXT_BUFFER, zeroY + OFFSET_Y + TEXT_BUFFER + SLOT_HEIGHT * i, p, q);
                    q = 8453920;
                }

                context.drawTextWithShadow(this.textRenderer, string, zeroX + FIELD_OFFSET_X + FIELD_WIDTH - TEXT_BUFFER - this.textRenderer.getWidth(string), zeroY + OFFSET_Y + TEXT_BUFFER + SLOT_HEIGHT * i /*+ 7*/, q);
            }
        }

    }

    private void drawBook(DrawContext context, int x, int y, float delta) {
        float f = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float g = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle);
        DiffuseLighting.method_34742();
        context.getMatrices().push();
        context.getMatrices().translate((float)x + 33.0F, (float)y + 31.0F, 100.0F);
        float h = 40.0F;
        context.getMatrices().scale(-40.0F, 40.0F, 40.0F);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0F));
        context.getMatrices().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float i = -(1.0F - f) * 90.0F - 90.0F;
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        float j = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float k = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.BOOK_MODEL.setPageAngles(0.0F, j, k, f);
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
        this.BOOK_MODEL.render(context.getMatrices(), vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
        context.draw();
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        boolean creative = this.client.player.getAbilities().creativeMode;
        int catCount = this.handler.getCatalystCount();

        for(int i = 0; i < (HEIGHT / SLOT_HEIGHT); ++i) {
            int slot = this.firstShown() + i;
            int power = this.handler.getEnchantmentPower(this.client.world, slot);
            //Optional<RegistryEntry.Reference<Enchantment>> optional = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(indexedIterable.getRawId(this.handler.enchantments.get(j).enchantment));
            RegistryEntry<Enchantment> enchantment = this.handler.getEnchantment(this.client.world, slot);
            if (enchantment != null) {
                int level = this.handler.enchantmentLevels.get(slot);
                int m = slot + 1;
                if (this.isPointWithinBounds(FIELD_OFFSET_X, OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT, (double)mouseX, (double)mouseY) && power > 0 && level >= 0 /*&& optional != null*/) {
                    List<Text> list = Lists.newArrayList();
                    list.add(Text.translatable("container.enchant.clue", new Object[]{Enchantment.getName(enchantment, level)}).formatted(Formatting.WHITE));
                    if (!creative) {
                        list.add(ScreenTexts.EMPTY);
                        if (this.client.player.experienceLevel < power) {
                            list.add(Text.translatable("container.enchant.level.requirement", new Object[]{this.handler.getEnchantmentPower(this.client.world, slot)}).formatted(Formatting.RED));
                        } else {
                            MutableText mutableText;
                            if (m == 1) {
                                mutableText = Text.translatable("container.enchant.lapis.one");
                            } else {
                                mutableText = Text.translatable("container.enchant.lapis.many", new Object[]{m});
                            }

                            list.add(mutableText.formatted(catCount >= m ? Formatting.GRAY : Formatting.RED));
                            MutableText mutableText2;
                            if (m == 1) {
                                mutableText2 = Text.translatable("container.enchant.level.one");
                            } else {
                                mutableText2 = Text.translatable("container.enchant.level.many", new Object[]{m});
                            }

                            list.add(mutableText2.formatted(Formatting.GRAY));
                        }
                    }

                    context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
                    break;
                }
            }
        }

    }

    public void doTick() {
        ItemStack itemStack = this.handler.getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack, this.stack)) {
            this.stack = itemStack;

            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while(this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }

        //a bit of fun
        if (this.handler.getSlot(1).hasStack()) this.handler.getSlot(1).onTake(this.ticks);

        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;

        for(int i = 0; i < this.handler.getEnchantmentCount(); ++i) {
            if (this.handler.getEnchantmentPower(this.client.world, i) != 0) {
                bl = true;
            }
        }

        if (bl) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }

        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        float g = 0.2F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}
