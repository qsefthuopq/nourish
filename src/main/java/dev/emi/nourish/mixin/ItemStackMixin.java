package dev.emi.nourish.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.groups.NourishGroups;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This isn't compiled in release builds, it's just a way I can search for all food items in a large modpack when developing
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(at = @At("RETURN"), method = "getTooltip")
	public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info) {
		//if (getItem().isFood()) {
		//	info.getReturnValue().add(new LiteralText("InstanceOfFood"));
		//}
		ItemStack stack = (ItemStack) (Object) this;
		Identifier id = Registry.ITEM.getId(stack.getItem());
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<String> groups = new ArrayList<String>();
		if (id.toString().equals("sandwichable:sandwich")) {
			DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
			Inventories.fromTag(stack.getSubTag("BlockEntityTag"), foods);
			items.addAll(items);
		} else {
			items.add(stack);
		}
		for (NourishGroup group: NourishGroups.groups) {
			for (ItemStack food: items) {
				if (group.tag.contains(food.getItem())) {
					groups.add(new TranslatableText("nourish.group." + group.name).asFormattedString());
					break;
				}
			}
		}
		if (groups.size() > 0) {
			info.getReturnValue().add(new LiteralText(String.join(", ", groups)).formatted(Formatting.GOLD));
		}
	}

}