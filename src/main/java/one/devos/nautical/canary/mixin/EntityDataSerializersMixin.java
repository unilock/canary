package one.devos.nautical.canary.mixin;

import one.devos.nautical.canary.CanaryException;

import one.devos.nautical.canary.Config;
import one.devos.nautical.canary.Utils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

@Mixin(EntityDataSerializers.class)
public class EntityDataSerializersMixin {
	@SuppressWarnings("FieldMayBeFinal")
	@Unique
	private static boolean vanillaRegistered;

	static {
		// static blocks are injected to tail of default static block
		vanillaRegistered = true;
	}

	@Inject(method = "registerSerializer", at = @At("HEAD"))
	private static void onRegister(EntityDataSerializer<?> serializer, CallbackInfo ci) {
		// check the stacktrace for caller class name
		StackTraceElement caller = Utils.getCaller();
		String callerClassName = caller.getClassName();
		if (Config.INSTANCE.dataSerializerWhitelist().contains(callerClassName))
			return; // explicitly declared as safe; ex. util methods

		if (vanillaRegistered) {
			throw new CanaryException("Unsafe EntityDataSerializer registration from [" + callerClassName + "]");
		}
	}
}
