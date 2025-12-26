package net.pm.magicky.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class MovingSlot extends Slot {
    public int x;
    public int y;

    public MovingSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
}
