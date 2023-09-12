package jp.axer.cocoainput.util;


public class FCConfig extends TinyConfig implements ConfigPack{

	@Entry(comment="AdvancedPreeditDraw - Is preedit marking - Default:true")
	public static boolean advancedPreeditDraw=true;
	@Entry(comment="NativeCharTyped - Is text inserted with native way - Default:true")
	public static boolean nativeCharTyped=true;



	@Override
	public boolean isAdvancedPreeditDraw() {
		return advancedPreeditDraw;
	}
	@Override
	public boolean isNativeCharTyped() {
		return nativeCharTyped;
	}


}
