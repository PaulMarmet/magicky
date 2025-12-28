package net.pm.magicky.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.pm.magicky.Magicky;
import net.pm.magicky.enchantment.EnchantingCatalyst;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;

import java.util.List;

@Environment(EnvType.CLIENT)
public class EnchantmentScreenM extends AbstractContainerScreen<EnchantmentScreenHandlerM> {
//    private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1"), Identifier.ofVanilla("container/enchanting_table/level_2"), Identifier.ofVanilla("container/enchanting_table/level_3")};
//    private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1_disabled"), Identifier.ofVanilla("container/enchanting_table/level_2_disabled"), Identifier.ofVanilla("container/enchanting_table/level_3_disabled")};
private static final Identifier SCROLLER_TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "container/enchanting_table/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "container/enchanting_table/scroller_disabled");
    private static final Identifier ENCHANTMENT_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "container/enchanting_table/enchantment_slot");
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/entity/enchanting_table_book.png");
    private final RandomSource random = RandomSource.create();
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

    public EnchantmentScreenM(EnchantmentScreenHandlerM handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.stack = ItemStack.EMPTY;
    }

    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    public void containerTick() {
        super.containerTick();
        this.doTick();
    }

    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.mouseClicked = false;
        int localX = (this.width - this.imageWidth) / 2;
        int localY = (this.height - this.imageHeight) / 2;

        for(int k = 0; k < this.menu.getEnchantmentCount() && k < (HEIGHT / SLOT_HEIGHT); ++k) {
            int slotOffset = SLOT_HEIGHT * k;
            double horizontalBounds = mouseButtonEvent.x() - (double)(localX + FIELD_OFFSET_X);
            double verticalBounds = mouseButtonEvent.y() - (double)(localY + OFFSET_Y + slotOffset);
            if (horizontalBounds >= 0.0 && verticalBounds >= 0.0 && horizontalBounds < FIELD_WIDTH && verticalBounds < SLOT_HEIGHT && this.menu.clickMenuButton(this.minecraft.player, k + this.firstShown())) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k + this.firstShown());
                return true;
            }
        }
        if (this.inScrollArea(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.mouseClicked = true;
        }

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double deltaX, double deltaY) {
        if (this.mouseClicked && this.getMaxScroll() > 0 && this.inScrollArea(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            int i = this.topPos + OFFSET_Y;
            int j = i + HEIGHT;
            this.scrollAmount = ((float)mouseButtonEvent.y() - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * 4;
            return true;
        } else {
            return super.mouseDragged(mouseButtonEvent, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.getMaxScroll() > 0 && this.inEnchantmentArea(mouseX, mouseY)) {
            int maxScroll = this.getMaxScroll();
            float scrollFraction = (float)verticalAmount / (float)maxScroll;
            this.scrollAmount = Mth.clamp(this.scrollAmount - scrollFraction, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)maxScroll) + 0.5) * 4;
        }

        return true;
    }

    public int getMaxScroll() {
        int displayable = HEIGHT / SLOT_HEIGHT;
        int total = this.menu.getEnchantmentCount();
        return displayable >= total ? 0 : total - displayable;
    }

    public int firstShown() {
        return (int) (this.scrollAmount * this.getMaxScroll());
    }

    public boolean inEnchantmentArea(double mouseX, double mouseY) {
        int localX = (this.width - this.imageWidth) / 2;
        int localY = (this.height - this.imageHeight) / 2;

        return (mouseX >= localX + FIELD_OFFSET_X - 1
                && mouseX <= localX + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH + 1
                && mouseY >= localY + OFFSET_Y - 1
                && mouseY <= localY + OFFSET_Y + HEIGHT + 1);
    }
    public boolean inScrollArea(double mouseX, double mouseY) {
        int localX = (this.width - this.imageWidth) / 2;
        int localY = (this.height - this.imageHeight) / 2;

        return (mouseX >= localX + SCROLLBAR_OFFSET_X - 1
                && mouseX <= localX + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH + 1
                && mouseY >= localY + OFFSET_Y - 1
                && mouseY <= localY + OFFSET_Y + HEIGHT + 1);
    }

    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int zeroX = (this.width - this.imageWidth) / 2;
        int zeroY = (this.height - this.imageHeight) / 2;

        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, zeroX, zeroY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.drawBook(context, zeroX, zeroY, delta);
        //not really used lol
        EnchantmentNames.getInstance().initSeed(0);

        int skroll = (int)((HEIGHT - SCROLLBAR_HEIGHT) * this.scrollAmount);
        Identifier scroller = this.getMaxScroll() > 0 ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.blitSprite(RenderPipelines.GUI_TEXTURED ,scroller, zeroX + SCROLLBAR_OFFSET_X,  zeroY + OFFSET_Y + skroll, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);

        int catCount = this.menu.getCatalystCount();
        EnchantingCatalyst catType = this.menu.getCatType();
        //Magicky.LOGGER.info("has: "+this.handler.getEnchantmentCount());

        for(int i = 0; i < (HEIGHT / SLOT_HEIGHT); ++i) {
            int slot = this.firstShown() + i;
            Holder<Enchantment> enchantment = this.menu.getEnchantment(this.minecraft.level, slot);
            //make good power calc
            int power = catType == null ? 0 : catType.xpCost(this.menu, slot, this.minecraft.level); //prevent from null
            //Magicky.LOGGER.info("power:"+power);
            if (power == 0 || enchantment == null) {
                //RenderSystem.enableBlend();
                context.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
                //RenderSystem.disableBlend();
            } else {
                String string = "" + power;
                int textWidth = FIELD_WIDTH - 7 - this.font.width(string);
                FormattedText stringVisitable = font.getSplitter().headByWidth(Component.literal(Enchantment.getFullname(enchantment, this.menu.enchantmentLevels.get(slot)).getString()), textWidth, Style.EMPTY.withFont(EnchantmentNames.ALT_FONT));
//                stringVisitable = (StringVisitable) this.textRenderer.wrapLines(stringVisitable, p).getFirst();
                FormattedText stringClear = font.getSplitter().headByWidth(Component.literal(Enchantment.getFullname(enchantment, this.menu.enchantmentLevels.get(slot)).getString()), width, Style.EMPTY);//EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, p);
                int q = 6839882;
                if ((catCount < catType.catCost(this.menu, slot, this.minecraft.level) || this.minecraft.player.experienceLevel < power) && !this.minecraft.player.getAbilities().instabuild) {
                    //RenderSystem.enableBlend();
                    context.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
//                    context.drawGuiTexture(LEVEL_DISABLED_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    //RenderSystem.disableBlend();
                    context.drawWordWrap(this.font, stringVisitable, zeroX + FIELD_OFFSET_X + TEXT_BUFFER, zeroY + OFFSET_Y + TEXT_BUFFER + SLOT_HEIGHT * i, textWidth, (q & 16711422) >> 1);
                } else {
                    int r = mouseX - (zeroX + FIELD_OFFSET_X);
                    int s = mouseY - (zeroY + OFFSET_Y + SLOT_HEIGHT * i);
                    //RenderSystem.enableBlend();
                    if (r >= 0 && s >= 0 && r < FIELD_WIDTH && s < SLOT_HEIGHT) {
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
                        q = 16777088;
                    } else {
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_TEXTURE, zeroX + FIELD_OFFSET_X, zeroY + OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT);
                    }

                    //context.drawGuiTexture(LEVEL_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    //RenderSystem.disableBlend();
                    context.drawWordWrap(this.font, stringVisitable, zeroX + FIELD_OFFSET_X + TEXT_BUFFER, zeroY + OFFSET_Y + TEXT_BUFFER + SLOT_HEIGHT * i, textWidth, q);
                    q = 8453920;
                }

                context.drawString(this.font, string, zeroX + FIELD_OFFSET_X + FIELD_WIDTH - TEXT_BUFFER - this.font.width(string), zeroY + OFFSET_Y + TEXT_BUFFER + SLOT_HEIGHT * i /*+ 7*/, q);
            }
        }

    }

    private void drawBook(GuiGraphics context, int x, int y, float delta) {
        float f = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float g = Mth.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float h = Mth.lerp(f, this.pageAngle, this.nextPageAngle);
        int k = x + 14;
        int l = y + 14;
        int m = k + 38;
        int n = l + 31;
        context.submitBookModelRenderState(this.BOOK_MODEL, BOOK_TEXTURE, 40.0F, g, h, k, l, m, n);
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
        boolean creative = this.minecraft.player.getAbilities().instabuild;
        int catCount = this.menu.getCatalystCount();
        EnchantingCatalyst catType = this.menu.getCatType();

        for(int i = 0; i < (HEIGHT / SLOT_HEIGHT); ++i) {
            int slot = this.firstShown() + i;
            Holder<Enchantment> enchantment = this.menu.getEnchantment(this.minecraft.level, slot);
            int enchantmentLevel = this.menu.getEnchantmentLevel(slot);
            if (enchantment != null && enchantmentLevel >= 0 && catType != null) {
                int levelCost = catType.xpCost(this.menu, slot, this.minecraft.level);
                int catalystCost = catType.catCost(this.menu, slot, this.minecraft.level);
                if (this.isHovering(FIELD_OFFSET_X, OFFSET_Y + SLOT_HEIGHT * i, FIELD_WIDTH, SLOT_HEIGHT, mouseX, mouseY)/* && levelCost > 0 && optional != null*/) {
                    List<Component> list = Lists.newArrayList();
                    list.add(Component.translatable("container.enchant.clue", new Object[]{Enchantment.getFullname(enchantment, enchantmentLevel)}).withStyle(ChatFormatting.WHITE));
                    if (!creative) {
                        list.add(CommonComponents.EMPTY);
                        if (this.minecraft.player.experienceLevel < levelCost) {
                            list.add(Component.translatable("container.enchant.level.requirement", levelCost).withStyle(ChatFormatting.RED));
                        } else {
                            MutableComponent catalystText = Component.literal(catalystCost + " ").append(this.menu.getSlot(1).getItem().getHoverName());

                            list.add(catalystText.withStyle(catCount >= catalystCost ? ChatFormatting.GRAY : ChatFormatting.RED));
                            MutableComponent experienceText;
                            if (levelCost == 1) {
                                experienceText = Component.translatable("container.enchant.level.one");
                            } else {
                                experienceText = Component.translatable("container.enchant.level.many", new Object[]{levelCost});
                            }

                            list.add(experienceText.withStyle(ChatFormatting.GRAY));
                        }
                    }

                    context.setComponentTooltipForNextFrame(this.font, list, mouseX, mouseY);
                    break;
                }
            }
        }

    }

    public void doTick() {
        ItemStack itemStack = this.menu.getSlot(0).getItem();
        if (!ItemStack.matches(itemStack, this.stack)) {
            this.stack = itemStack;

            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while(this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }


        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;

        for(int i = 0; i < this.menu.getEnchantmentCount(); ++i) {
            if (this.menu.getCatType().xpCost(this.menu, i, this.minecraft.level) != 0) {
                bl = true;
            }
        }

        if (bl) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }

        this.nextPageTurningSpeed = Mth.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        //float g = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}
