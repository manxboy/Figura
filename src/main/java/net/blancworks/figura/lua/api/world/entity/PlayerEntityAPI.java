package net.blancworks.figura.lua.api.world.entity;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.lua.CustomScript;
import net.blancworks.figura.lua.api.ReadOnlyLuaTable;
import net.blancworks.figura.lua.api.item.ItemStackAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.function.Supplier;

public class PlayerEntityAPI {

    public static Identifier getID() {
        return new Identifier("default", "player");
    }

    public static ReadOnlyLuaTable getForScript(CustomScript script) {

        //Global table will get the local player.
        return new PlayerEntityLuaAPITable(() -> script.playerData.lastEntity);
    }

    public static ReadOnlyLuaTable get(PlayerEntity entity) {
        PlayerEntityLuaAPITable pentTable = new PlayerEntityLuaAPITable(() -> entity);

        return pentTable;
    }

    public static class PlayerEntityLuaAPITable extends LivingEntityAPI.LivingEntityAPITable<PlayerEntity> {

        public PlayerEntityLuaAPITable(Supplier<PlayerEntity> entitySupplier) {
            super(entitySupplier);
            super.setTable(getTable());
        }

        public LuaTable getTable() {
            LuaTable superTable = super.getTable();

            superTable.set("getHeldItem", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {

                    int hand = arg.checkint();

                    ItemStack targetStack = null;

                    if (hand == 1)
                        targetStack = targetEntity.get().getMainHandStack();
                    else if (hand == 2)
                        targetStack = targetEntity.get().getOffHandStack();
                    else
                        return NIL;

                    if (targetStack.equals(ItemStack.EMPTY))
                        return NIL;

                    LuaTable getItemRepresentation = ItemStackAPI.getTable(targetStack);
                    return getItemRepresentation;
                }
            });

            superTable.set("getFood", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaNumber.valueOf(targetEntity.get().getHungerManager().getFoodLevel());
                }
            });

            superTable.set("getSaturation", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaNumber.valueOf(targetEntity.get().getHungerManager().getSaturationLevel());
                }
            });

            superTable.set("getExperienceProgress", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaNumber.valueOf(targetEntity.get().experienceProgress);
                }
            });

            superTable.set("getExperienceLevel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaNumber.valueOf(targetEntity.get().experienceLevel);
                }
            });

            return superTable;
        }
    }

}
