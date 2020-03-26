// START SNIPPET: blink-gpio-snippet


/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  BlinkGpioExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * This example code demonstrates how to perform simple
 * blinking LED logic of a GPIO pin on the Raspberry Pi
 * using the Pi4J library.
 *
 * @author Robert Savage
 */
public class Switch_LED {
        static boolean isLightOn = true;
        
        // create gpio controller
        static final GpioController gpio = GpioFactory.getInstance();
        
        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        static final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN);

        // provision gpio pin #01 & #03 as an output pins and blink
        static final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);
        static final GpioPinDigitalOutput led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28);
        static final GpioPinDigitalOutput led3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29);

        static LED led;

    public static void main(String[] args) throws Exception {
            
        System.out.println("Press the button");
        
        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    // when button is pressed, speed up the blink rate on LED #2
                    if(event.getState().isHigh()){
                        if(isLightOn){
                          isLightOn = false;
                          //Thread.sleep(500);
                        }
                        else{
                          isLightOn = true;
                          //Thread.sleep(500);
                        }
                    }
                }
        });
        led = new LED();
        led.start();
        led.turnOn();
        


        System.out.println(" ... the LED will continue turn on until the program is terminated.");
        System.out.println(" ... PRESS <CTRL-C> TO STOP THE PROGRAM.");

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }
        // stop all GPIO activity/threads
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
    }   
        
    static class LED extends Thread{
        boolean on;
        LED(){
           on = false;
        }
        public void turnOn(){
            led1.high();
            led2.high();
            led3.high();
            System.out.println(" turnOn");
        }
        public static void turnOff(){
            led1.low();
            led2.low();
            led3.low();
            System.out.println(" turnOff");
        }       
        public void run(){
            try{
                while(true){
                    if(isLightOn){
                        if(on){
                            on = false;
                            turnOff();
                        }
                    }else{
                        if(!on){
                            on = true;
                            turnOn();
                        }
                    }
                    Thread.sleep(1000);
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
