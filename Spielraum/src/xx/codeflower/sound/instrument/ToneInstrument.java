package xx.codeflower.sound.instrument;

class ToneInstrument implements Instrument {

	public Oscil sineOsc;
	public Multiplier multiplyGate;
	public AudioOutput out;

	public ToneInstrument(float frequency, float amplitude, AudioOutput output) {
	
		sineOsc = new Oscil( frequency, amplitude );
		multiplyGate = new Multiplier( 0 );

		sineOsc.patch( multiplyGate );
	}

	public void noteOn(float dur) {
		multiplyGate.setValue(1.0);
		multiplyGate.patch(out);
	}

	public void noteOff() {
		multiplyGate.setValue(0.0);
		multiplyGate.unpatch(out);
	}

}
