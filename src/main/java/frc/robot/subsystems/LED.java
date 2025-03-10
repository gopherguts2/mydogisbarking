package frc.robot.subsystems;

import static edu.wpi.first.units.Units.InchesPerSecond;
import static edu.wpi.first.units.Units.Meters;

import java.util.Random;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class LED extends SubsystemBase{

    private static LED instance = null;

    private AddressableLED control = new AddressableLED(9);
    private AddressableLEDBuffer buffer = new AddressableLEDBuffer(720);


    private boolean policeModeEnabled = false;
    private int policeMode = 0;
    private int policeModeControl1 = 0;
    private int policeModeControl2 = 0;
    private int policeModeColorControl2 = 0;

    private final Color NOTE_ORANGE = new Color(255, 15, 0);
    private final Color INIT_YELLOW = new Color(255, 165, 0);
    private final Color GREEN = new Color(0, 255, 0);
    private final Color BLUE = new Color(0, 0, 255);
    private final Color RED = new Color(255, 0, 0);
    private final Color MAGENTA = new Color(255, 0, 255);
    private final Color OFF = new Color(0, 0, 0);

    private LEDPattern rainbowPattern;

    private Star[] stars = new Star[720];
    private final float starFreq = 0.003f;
    private final Color starColor = new Color(255, 40, 0);
    

    /**
     * Constructor for LED which registers the subsystem and sets a specified portion of the LED lights to the alliance color
     */
    private LED() {
        super("LED");
        rainbowPattern = LEDPattern.rainbow(255, 255).scrollAtAbsoluteSpeed(InchesPerSecond.of(24.0),Meters.of(1.0/144.0));
        this.control.setLength(this.buffer.getLength());
        this.control.setColorOrder(AddressableLED.ColorOrder.kRGB);

        this.setLights(INIT_YELLOW);
        this.control.setData(buffer);
        
        this.control.start();

        SmartDashboard.putNumber("huecontrol", huethingcontrol);
        SmartDashboard.putBoolean("policemode", policeModeEnabled);
        initStars();
    }


    public void setLights(int r, int g, int b) {
        if (r > 255 || g > 255 || b > 255) {
            for (int i = 0; i < buffer.getLength(); i++) {
                this.buffer.setRGB(i, 255, 255, 255);
            }
        }
        else {
            for (int i = 0; i < buffer.getLength(); i++) {
                this.buffer.setRGB(i, r, g, b);
            }
        }
    }

    public void setLights(int start, int end, int r, int g, int b) {
        if (r > 255 || g > 255 || b > 255) {
            for (int i = start; i < end; i++) {
                this.buffer.setRGB(i, 255, 255, 255);
            }
        }
        else {
            for (int i = start; i < end; i++) {
                this.buffer.setRGB(i, r, g, b);
            }
        }
    }

    public void setLights(Color c) {
        for (int i = 0; i < buffer.getLength(); i++) {
            buffer.setLED(i, c);
        }
    }

    public void setLights(int start, int end, Color c) {
        for (int i = start; i < end; i++) {
            buffer.setLED(i, c);
        }
    }

    public void togglePoliceModeEnabled() {
        if (policeModeEnabled) policeModeEnabled = false;
        else policeModeEnabled = true;
        SmartDashboard.putBoolean("policemode", policeModeEnabled);
    }

    int huething = 0;
    int huethingcontrol = 3;
    private void rainbow() {
        // For every pixel
        for (var i = 0; i < buffer.getLength(); i++) {
          // Calculate the hue - hue is easier for rainbows because the color
          // shape is a circle so only one value needs to precess
          final var hue = (huething + (i * 180 / buffer.getLength())) % 180;
          // Set the value
          buffer.setHSV(i, hue, 255, 128);
        }
        // Increase by to make the rainbow "move"
        huething += huethingcontrol;
        // Check bounds
        huething %= 180;
      }

    private void initStars()
    {
        for(int i = 0; i < this.stars.length; i++)
        {
            this.stars[i] = new Star(starColor, starFreq,i);
        }
    }

    private void processStars()
    {
        for(int i = 0; i < this.stars.length; i++)
        {
            buffer.setLED(i, stars[i].getTemperatureColor());
        }
        this.control.setData(buffer);
    }

      public void increaseHueControl() {huethingcontrol++;SmartDashboard.putNumber("huecontrol", huethingcontrol);}
      public void decreaseHueControl() {huethingcontrol--;SmartDashboard.putNumber("huecontrol", huethingcontrol);}

    int blinkControl = 0;
    @SuppressWarnings("unused")
    public void periodic() {

        
        if (policeModeEnabled) {
            if (policeMode == 0) {//solid color
                this.setLights(0, 7, RED);
                this.setLights(buffer.getLength() / 2, buffer.getLength(), BLUE);
            }
            if (policeMode == 1) {//solid alternating color
                policeModeControl1++;
                if (policeModeControl1 % 50 == 0) this.setLights(RED);
                else if (policeModeControl1 % 25 == 0) this.setLights(BLUE);
            }
            else if (policeMode == 2) {//three flashes each color on two halves
                policeModeControl2++;
                if (policeModeColorControl2 % 2 == 0) {
                    if (policeModeControl2 % 8 == 0) {
                        this.setLights(0, 7, BLUE);
                        this.setLights(22, 29, RED);
                        this.setLights(29, 34, BLUE);
                        this.setLights(39, 44, BLUE);
                    }
                    else if (policeModeControl2 % 4 == 0) this.setLights(OFF);
                    if (policeModeControl2 == 16) {
                        policeModeColorControl2++;
                        policeModeControl2 = 0;
                    }
                }
                else {
                    if (policeModeControl2 % 8 == 0) {
                        this.setLights(14, 22, BLUE);
                        this.setLights(7, 14, RED);
                        this.setLights(34, 39, RED);
                        this.setLights(44, 49, RED);
                    }
                    else if (policeModeControl2 % 4 == 0) this.setLights(OFF);
                    if (policeModeControl2 == 16) {
                        policeModeColorControl2++;
                        policeModeControl2 = 0;
                    }
                }
            }
        }


        //rainbowPattern.applyTo(buffer);
        //this.control.setData(buffer);
        processStars();
    }

    /**
     * Singleton architecture which returns the singular instance of LED
     * @return the instance (which is instantiated when first called)
     */
    public static LED getInstance(){
        if (instance == null) instance = new LED();
        return instance;
    }

    

    private class Star 
    {
        private Color color;
        private float brightness;
        private boolean increasing;
        private boolean lit;
        private float frequency;
        private Random randomizer;
        private float speed;
        private float temperature;
        private float red, green, blue;
        private float temperatureSpeed;



        public Star(Color color, float frequency, int seed)
        {
            this.color = color;
            this.frequency = frequency;
            this.brightness = 0f;
            this.lit = false;
            this.increasing = false;
            this.randomizer = new Random(seed);
            this.speed = 0.01f;
            this.temperatureSpeed = 100;
            this.temperature = 1200f;
        }

        public Color getColor()
        {
            Color outColor;
            if (lit) {
                float[] hsbValues = new float[3];
                java.awt.Color.RGBtoHSB((int)(color.red*255), (int)(color.blue*255), (int)(color.green*255), hsbValues);
                hsbValues[2]*=brightness;
                hsbValues[1]-=brightness;
                //hsbValues[0] = hsbValues[0]+brightness/0.05f;
                outColor = Color.fromHSV((int)((hsbValues[0]*180)%180), (int)(hsbValues[1]*255), (int)(hsbValues[2]*255));

                if(brightness>=1) increasing = false;
                if(increasing) brightness +=speed; else brightness -= speed;
                brightness = (float)Math.min(brightness, 1);
                brightness = (float)Math.max(brightness, 0);
                if(brightness<=0)  lit = false;

            } else if(randomizer.nextFloat()<frequency)
            {
                lit = true;
                increasing = true;
                outColor = Color.kBlack;
                speed = randomizer.nextFloat(0.01f, 0.05f);
            } else outColor = Color.kBlack;
            return outColor;
        }

        public Color getTemperatureColor()
        {
            float temptemp = this.temperature/100;
            if(this.temperature == 0) temptemp = 1;
            Color outColor;
            if (lit)
            {
                if(temptemp <= 66)
                {
                    red = 255f;
                } 
                else
                {
                    red = 329.698727446f * (float)Math.pow(temptemp - 60f, -0.1332047592);
                    red = (float)Math.min(red, 255);
                    red = (float)Math.max(red, 0);
                }

                if( temptemp <= 66)
                {
                    green = 99.4708025861f * (float)Math.log(temptemp) - 161.1195681661f;
                    green = (float)Math.min(green, 255);
                    green = (float)Math.max(green, 0);
                }
                else
                {
                    green = 288.1221695283f * (float)Math.pow(temptemp-60, -0.0755148492);
                    green = (float)Math.min(green, 255);
                    green = (float)Math.max(green, 0);
                }
                if(temptemp >= 66)
                {
                    blue = 255f;
                }
                else
                {
                    blue = 138.5177312231f * (float)Math.log(temptemp-10) - 305.0447927307f;
                    blue = (float)Math.min(blue, 255);
                    blue = (float)Math.max(blue, 0);
                }
                //outColor = new Color(red/255.0, green/255.0*4.0, blue/255.0/4);
                float[] hsvvalues = new float[3];
                java.awt.Color.RGBtoHSB((int)red, (int)green, (int)blue, hsvvalues);
                hsvvalues[2]*=Math.pow(((temperature-800)/5000.0),1);
                hsvvalues[2] = Math.max(hsvvalues[2], 0.01f);
                outColor = Color.fromHSV((int)(hsvvalues[0]*180), (int)(hsvvalues[1]*255), (int)(hsvvalues[2]*255));
                if(temperature>=5800) increasing = false;
                if(increasing) temperature +=temperatureSpeed; else temperature -= temperatureSpeed;
                if(temperature<=800)  lit = false;
            } 
            else if(randomizer.nextFloat()<frequency)
            {
                lit = true;
                increasing = true;
                outColor = new Color(0, 0, 1);
                temperatureSpeed = randomizer.nextFloat(100f, 400f);
            } else outColor = new Color(0, 0, 1);
            return outColor;

        }
        public static double[] TemptoRGB(double temperature)
        {
            double[] outputs = new double[3];
            double red, green, blue;
            double temptemp = temperature/100;
            if(temptemp <= 66)
                {
                    red = 255f;
                } 
                else
                {
                    red = 329.698727446f * (float)Math.pow(temptemp - 60f, -0.1332047592);
                    red = (float)Math.min(red, 255);
                    red = (float)Math.max(red, 0);
                }

                if( temptemp <= 66)
                {
                    green = 99.4708025861f * (float)Math.log(temptemp) - 161.1195681661f;
                    green = (float)Math.min(green, 255);
                    green = (float)Math.max(green, 0);
                }
                else
                {
                    green = 288.1221695283f * (float)Math.pow(temptemp-60, -0.0755148492);
                    green = (float)Math.min(green, 255);
                    green = (float)Math.max(green, 0);
                }
                if(temptemp >= 66)
                {
                    blue = 255f;
                }
                else
                {
                    blue = 138.5177312231f * (float)Math.log(temptemp-10) - 305.0447927307f;
                    blue = (float)Math.min(blue, 255);
                    blue = (float)Math.max(blue, 0);
                }
                outputs[0] = red;
                outputs[1] = green;
                outputs[2] = blue;
                return outputs;
        }
        
    }

}