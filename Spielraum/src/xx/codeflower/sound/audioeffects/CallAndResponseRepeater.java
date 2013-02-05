package xx.codeflower.sound.audioeffects;

import java.util.ArrayList;

import ddf.minim.AudioEffect;

public class CallAndResponseRepeater implements AudioEffect {

	private int sampleLength = 20;
	private int timesToRepeatSample = 3;
	private int currentRepetition = 0;
	private int currentSamplePosition = 0;
	
	private ArrayList<float[]> samples = new ArrayList<float[]>();
	
	public void setSampleLength(float l) {
		System.out.println(l);
		sampleLength = (int) Math.floor(l);
	}
	
	public void setTimesToRepeat(float r) {
		int ttr = (int) Math.floor(r);
		System.out.println(ttr);
		timesToRepeatSample = ttr;
	}
	
	@Override
	public void process(float[] samp) {
		if(samples.size() < sampleLength) {
			float[] tmp = new float[samp.length];
			System.arraycopy(samp, 0, tmp, 0, samp.length);
			samples.add(tmp);
//			System.out.println("Stored: "+tmp);
			//for(int i = 0; i < samp.length; i++) samp[i] = 0.0f;			
		} else {
//			System.out.println("Copiing");
			
			float[] tmp = new float[samp.length];
			System.arraycopy(samples.get(currentSamplePosition++), 0, tmp, 0, tmp.length);
			
			for(int i = 0; i < samp.length; i++) samp[i] += tmp[i];
			
//			System.out.println("Play: "+samp);
			if(currentSamplePosition >= samples.size()) {
				if(currentRepetition >= timesToRepeatSample) {
					samples.clear();
					currentRepetition = -1;
//					System.out.println("Renew");
				}
				currentSamplePosition = 0;
				currentRepetition++;
			}
		}
	}

	@Override
	public void process(float[] left, float[] right) {
		process(left);
		process(right);
	}

}
