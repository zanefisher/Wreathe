package game;

import processing.core.PApplet;
import ddf.minim.*;
import ddf.minim.ugens.*;

public class Music extends PApplet implements Runnable {

	Minim minim;
	AudioOutput out;
	
	int[] section = {0, 0, 1, 1, 0, 0, 1, 1, 0, 2, 3, 3, 4, 4}; 
	

	float[][][][] score =
		//treble
	   {{{{64f, 62f, 60f, 64f, 62f, 60f, 64f, 62f, 60f, 64f, 62f, 60f,
		   64f, 63f, 61f, 64f, 63f, 61f, 64f, 63f, 61f, 64f, 63f, 61f},
		  {1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f,
		   1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f, 1/8f}},
		   
		 {{69f, 62f, 60f, 57f, 69f, 62f, 60f, 57f, 64f, 60f, 57f, 64f, 62f, 60f, 57f,
		   67f, 62f, 61f, 59f, 67f, 62f, 61f, 59f, 64f, 61f, 59f, 64f, 62f, 61f, 59f},
		  {3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 1/8f, 1/8f, 1/8f, 3/16f, 3/16f, 3/16f, 3/16f,
		   3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 1/8f, 1/8f, 1/8f, 3/16f, 3/16f, 3/16f, 3/16f}},
		   
		 {{64f, 62f, 60f, 59f, 64f, 62f, 60f, 59f, 64f, 62f, 60f, 59f, 64f, 62f, 60f, 59f,
		   64f, 63f, 61f, 56f, 68f, 64f, 63f, 61f, 56f, 57f, 64f, 63f, 61f, 56f, 68f, 64f, 63f, 61f, 56f, 57f},
		  {3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f,
		   3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f, 3/20f}},
		   
		 {{68f, 63f, 61f, 59f, 68f, 63f, 61f, 59f, 66f, 61f, 59f, 66f, 63f, 61f, 59f,
		   71f, 63f, 61f, 59f, 71f, 63f, 61f, 59f, 70f, 61f, 59f, 70f, 66f, 61f, 59f},
	      {3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 1/8f, 1/8f, 1/8f, 3/16f, 3/16f, 3/16f, 3/16f,
		   3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 3/16f, 1/8f, 1/8f, 1/8f, 3/16f, 3/16f, 3/16f, 3/16f}},
		   
		 {{70f, 66f, 65f, 63f, 70f, 66f, 65f, 63f, 68f, 66f, 65f, 63f, 68f, 66f, 65f, 63f,
		   68f, 65f, 63f, 59f, 68f, 65f, 63f, 59f, 68f, 65f, 63f, 59f, 68f, 65f, 63f, 59f},
		  {1/8f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f,
		   1/8f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f}}},
		   
		//tenor
		{{{53f, 60f, 53f, 60f, 53f, 60f, 65f, 53f, 60f, 52f, 60f, 52f, 60f, 64f, 52f, 57f, 52f,
		   56f, 52f, 56f, 49f, 51f, 56f, 51f, 56f, 52f, 56f, 49f, 52f, 56f, 52f, 57f, 52f, 59f},
		  {1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/8f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/8f, 1/8f, 1/8f, 1/8f,
		   1/8f, 1/8f, 1/8f, 1/8f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/8f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f}},
		   
		 {{48f, 53f, 48f, 53f, 57f, 60f, 48f, 53f, 48f, 53f, 57f, 62f, 48f, 53f, 48f, 53f, 57f, 64f,
		   47f, 47f, 47f, 50f, 57f, 62f, 0f},
		  {1/16f, 1/16f, 1/12f, 1/12f, 1/12f, 1/8f, 1/16f, 1/16f, 1/12f, 1/12f, 1/12f, 1/8f, 1/16f, 1/16f, 1/12f, 1/12f, 1/12f, 1/8f,
		   1/8f, 1/12f, 1/12f, 1/12f, 1/16f, 7/16f, 3/4f}},
		   
		 {{53f, 60f, 53f, 60f, 53f, 60f, 65f, 53f, 60f, 52f, 60f, 52f, 60f, 64f, 52f, 57f, 52f,
		   56f, 52f, 56f, 49f, 51f, 56f, 51f, 56f, 52f, 56f, 49f, 52f, 56f, 52f, 57f, 52f, 59f},
		  {1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/8f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/8f, 1/8f, 1/8f, 1/8f,
		   1/8f, 1/8f, 1/8f, 1/8f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/8f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f}},
		   
		 {{47f, 51f, 47f, 51f, 56f, 59f, 47f, 51f, 47f, 51f, 56f, 61f, 47f, 51f, 47f, 51f, 56f, 61f,
		   47f, 47f, 47f, 51f, 59f, 59f, 59f, 59f, 54f, 0f},
		  {1/16f, 1/16f, 1/12f, 1/12f, 1/12f, 1/8f, 1/16f, 1/16f, 1/12f, 1/12f, 1/12f, 1/8f, 1/16f, 1/16f, 1/12f, 1/12f, 1/12f, 1/8f,
		   1/8f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/16f, 5/16f, 1/2f}},
		   
		 {{51f, 58f, 56f, 58f, 51f, 58f, 61f, 51f, 58f, 56f, 54f, 56f, 58f, 61f, 51f, 54f, 58f,
		   56f, 51f, 54f, 58f, 56f, 51f, 54f, 51f, 54f, 56f, 58f, 51f, 54f, 56f, 58f},
		  {1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/16f, 1/8f, 5/32f, 5/32f, 5/32f, 5/32f, 5/32f, 5/32f, 5/32f, 5/32f, 5/32f, 5/32f,
		   1/8f, 1/8f, 1/8f, 1/8f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/12f, 1/8f, 1/12f, 1/12f, 1/12f, 1/8}}},
		   
		//bass
	    {{{29f, 31f, 33f, 32f, 33f, 35f, 37f},
		  {1/2f, 1/4f, 3/4f, 1/2f, 1/4f, 1/2f, 1/4f}},
		  
		 {{41f, 40f, 38f, 36f, 35f, 33f, 41f, 42f, 44f, 45f},
		  {1/2f, 1/4f, 5/12f, 1/6f, 1/6f, 1/2f, 1/4f, 5/12f, 1/6f, 1/6}},
		  
		 {{29f, 31f, 33f, 32f, 33f, 35f, 37f},
		  {1/2f, 1/4f, 3/4f, 1/2f, 1/4f, 1/2f, 1/4f}},
		  
		 {{44f, 42f, 40f, 39f, 37f, 35f, 30f, 32f, 32f, 32f, 35f, 37f},
		  {1/2f, 1/4f, 5/12f, 1/6f, 1/6f, 1/2f, 1/4f, 1/8f, 1/8f, 1/4f, 1/6f, 1/6f}},
		  
		 {{39f, 41f, 42f, 30f, 35f, 34f, 32f},
		  {1/2f, 1/4f, 3/8f, 3/8f, 1/2f, 1/4f, 3/4f}}}};
	
	
	Music(Sketch sketch){

		minim = new Minim(sketch);
		out = minim.getLineOut(Minim.STEREO, 1024);
		
	}
	
	public void run(){
		
		try{
			
			while(true){
				
				out.pauseNotes();
				float time = 0;
				
				for(int v = 0; v < 3; v++){
				
					time = 0;
					
					for(int i = 0; i < 12; i++){
						
						for(int s = 0; s < section.length; s++){
							
							for(int n = 0; n < score[v][section[s]][0].length; n++){
								
								if(score[v][section[s]][0][n] != 0){
									
									out.playNote(time, score[v][section[s]][1][n] * 60/23f,
											new Synth(cpsmidi(score[v][section[s]][0][n] - i), i));
									
									//Sketch.println(score[v][section[s]][0][n] - i);
									//Sketch.println(cpsmidi(score[v][section[s]][0][n] - i));
									
								}
								
								time += score[v][section[s]][1][n] * 60/23f;
								
							}
							//Sketch.println("s: " + s);
						}
						
					}
				
				}
				
				out.resumeNotes();
				Thread.sleep((long)time * 1000);
				
			}
			
		} catch (InterruptedException e){
			
			System.err.println(e);
			
		}	
		
	}
	
	float cpsmidi(float freq){
		
	  return Sketch.pow(2, (freq - 69) / 12) * 440;
	  
	}
	
	class Synth implements Instrument {
		
		Oscil[] oscil = new Oscil[2];
		ADSR[] adsr = new ADSR[2];
		
		Synth(float frequency, int iteration){
			oscil[0] = new Oscil(frequency, (12 - iteration) / 12f, Waves.SINE);
			oscil[1] = new Oscil(frequency * 2f, iteration / 12f, Waves.SINE);
			adsr[0] = new ADSR(0.2f, 0.01f, 0.01f, 0.5f, 0.01f);
			adsr[1] = new ADSR(0.2f, 0.01f, 0.01f, 0.5f, 0.01f);
			oscil[0].patch(adsr[0]);
			oscil[1].patch(adsr[1]);
			
		}
		
		public void noteOn(float duration){
			
			adsr[0].noteOn();
			adsr[1].noteOn();
			adsr[0].patch(out);
			adsr[1].patch(out);
			
		}
		
		public void noteOff(){
			
			adsr[0].unpatchAfterRelease(out);
			adsr[1].unpatchAfterRelease(out);
			adsr[0].noteOff();
			adsr[1].noteOff();
			
		}
		
	}
	
}
