package com.sschr15.mods.af25;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FixRecordsMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	private static final String[][] FIXES = {
		{"net/minecraft/class_10976", "comp_3913", "b"}, // PlayerUnlock.parent()
		{"net/minecraft/class_10976", "comp_3915", "d"}, // PlayerUnlock.display()
		{"net/minecraft/class_11105", "comp_3985", "b"}, // SpecialMine.name()
		{"net/minecraft/class_11109", "comp_3993", "b"}, // WorldEffect.name()
	};

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) return;
		if (!targetClassName.endsWith("CustomCommands")) return;

		for (MethodNode method : targetClass.methods) {
			for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
				if (!(insn instanceof MethodInsnNode methodInsn)) continue;
				for (String[] fix : FIXES) {
					if (methodInsn.owner.equals(fix[0]) && methodInsn.name.equals(fix[1])) {
						methodInsn.name = fix[2];
						break;
					}
				}
			}
		}
	}
}
