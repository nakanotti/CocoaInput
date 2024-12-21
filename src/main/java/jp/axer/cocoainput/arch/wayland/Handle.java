package jp.axer.cocoainput.arch.wayland;

import com.sun.jna.*;

public interface Handle extends Library{
	Handle INSTANCE = Native.load("waylandcocoainput", Handle.class);
	
	void unfocus();
	void focus();
	void initialize(
			DoneCallback doneCallback,
			PreeditCallback preeditCallback,
			CommitCallback commitCallback,
			long display,
			Callback log,
			Callback error,
			Callback debug
	);
	
	interface PreeditCallback extends Callback {
	    void invoke(String str, int before, int after);
	}

	interface DoneCallback extends Callback {
	    void invoke();
	}

	interface CommitCallback extends Callback {
		void invoke(String str);
	}

}
