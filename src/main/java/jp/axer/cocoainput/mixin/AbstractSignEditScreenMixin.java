package jp.axer.cocoainput.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jp.axer.cocoainput.wrapper.AbstractSignEditScreenWrapper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin {
	AbstractSignEditScreenWrapper wrapper;
	
	@Inject(method="init",at=@At("RETURN"))
	private void init(CallbackInfo ci) {
		wrapper = new AbstractSignEditScreenWrapper((AbstractSignEditScreen)(Object)this);
	}
	
	@Redirect(method="tick",at = @At(value="FIELD", target="Lnet/minecraft/client/gui/screens/inventory/AbstractSignEditScreen;frame:I",opcode=Opcodes.PUTFIELD))
	private void injectCurosr(AbstractSignEditScreen esc,int n) {
		if (wrapper == null) wrapper = new AbstractSignEditScreenWrapper((AbstractSignEditScreen)(Object)this);	// May come before init() is called by stendhal-1.3.3-1.19.jar
		esc.frame = wrapper.renewCursorCounter();
	}
}
