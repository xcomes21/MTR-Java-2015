package org.usfirst.frc.team1528.robot;

import edu.wpi.first.wpilibj.*;

public class LiftControl implements Runnable {

	private boolean running;
	private final Joystick driver;
	private final int upID, downID;
	private final boolean upAxisPositive, downAxisPositive;
	private final SpeedController motor;
	private final String inputType;
	private final double defaultSpeed;
	private final ExecutiveOrder order;
	private final DigitalInput liftSwitch;
	
	/**
	 * Constructor. Uses buttons and a single operator.
	 * @param driver The operator's joystick.
	 * @param upButtonID The ID of the up button.
	 * @param downButtonID The ID of the down button.
	 * @param defaultSpeed The speed of movement from 0 to 1.
	 * @param motor The speed controller object.
	 */
	public LiftControl(Joystick driver, int upButtonID, int downButtonID, double defaultSpeed, SpeedController motor, DigitalInput liftSwitch){
		this.driver = driver;
		this.order = null;
		this.upID = upButtonID;
		this.downID = downButtonID;
		this.motor = motor;
		this.inputType = "button";
		if(defaultSpeed < -1){
			defaultSpeed = -1;
		}else if(defaultSpeed > 1){
			defaultSpeed = 1;
		}
		this.defaultSpeed = Math.abs(defaultSpeed);
		this.upAxisPositive = false;
		this.downAxisPositive = false;
		this.liftSwitch = liftSwitch;
	}
	
	/**
	 * Constructor. Uses axes and a single operator.
	 * @param driver The operator's joystick.
	 * @param upAxisID The ID of the up axis.
	 * @param downAxisID The ID of the down axis.
	 * @param upAxisPostive Is it using the positive end of the up axis?
	 * @param downAxisPositive Is it using the positive end of the up axis?
	 * @param motor The speed controller object.
	 */
	public LiftControl(Joystick driver, int upAxisID, int downAxisID, boolean upAxisPositive, boolean downAxisPositive, SpeedController motor, DigitalInput liftSwitch){
		this.driver = driver;
		this.order = null;
		this.upID = upAxisID;
		this.downID = downAxisID;
		this.upAxisPositive = upAxisPositive;
		this.downAxisPositive = downAxisPositive;
		this.motor = motor;
		this.inputType = "axis";
		this.defaultSpeed = 0.0;
		this.liftSwitch = liftSwitch;
	}
	
	/**
	 * Constructor. Uses buttons and an ExecutiveOrder.
	 * @param order The ExecutiveOrder of two operators.
	 * @param upButtonID The ID of the up button.
	 * @param downButtonID The ID of the down button.
	 * @param defaultSpeed The speed of movement from 0 to 1.
	 * @param motor The speed controller object.
	 */
	public LiftControl(ExecutiveOrder order, int upButtonID, int downButtonID, double defaultSpeed, SpeedController motor, DigitalInput liftSwitch){
		this.driver = null;
		this.order = order;
		this.upID = upButtonID;
		this.downID = downButtonID;
		this.motor = motor;
		this.inputType = "button";
		if(defaultSpeed < -1){
			defaultSpeed = -1;
		}else if(defaultSpeed > 1){
			defaultSpeed = 1;
		}
		this.defaultSpeed = Math.abs(defaultSpeed);
		this.upAxisPositive = false;
		this.downAxisPositive = false;
		this.liftSwitch = liftSwitch;
		
	}
	
	/**
	 * Constructor. Uses axes and an ExecutiveOrder.
	 * @param order The ExecutiveOrder of two operators.
	 * @param upAxisID The ID of the up axis.
	 * @param downAxisID The ID of the down axis.
	 * @param upAxisPostive Is it using the positive end of the up axis?
	 * @param downAxisPositive Is it using the positive end of the up axis?
	 * @param motor The speed controller object.
	 */
	public LiftControl(ExecutiveOrder order, int upAxisID, int downAxisID, boolean upAxisPositive, boolean downAxisPositive, SpeedController motor, DigitalInput liftSwitch){
		this.driver = null;
		this.order = order;
		this.upID = upAxisID;
		this.downID = downAxisID;
		this.upAxisPositive = upAxisPositive;
		this.downAxisPositive = downAxisPositive;
		this.motor = motor;
		this.inputType = "axis";
		this.defaultSpeed = 0.0;
		this.liftSwitch = liftSwitch;
	}
	
	@Override
	public void run() {
		running = true;
		if(order != null && inputType.equalsIgnoreCase("button")){
			executiveButtonControl();
		}else if(order != null && inputType.equalsIgnoreCase("axis")){
			executiveAxisControl();
		}else if(driver != null && inputType.equalsIgnoreCase("button")){
			buttonControl();
		}else if(driver != null && inputType.equalsIgnoreCase("axis")){
			axisControl();
		}else{
			throw new IllegalArgumentException("Something was invalid.");
		}
		
	}
	
	/**
	 * Controls the lift with buttons and an ExecutiveOrder.
	 */
	private void executiveButtonControl(){
		while(running){
			if(getExecutiveButtonPressed(upID) && !getExecutiveButtonPressed(downID)){
				if(!liftSwitch.get()){
					motor.set(0.4);
				}else{
					motor.set(defaultSpeed);
				}
				
			}else if(!getExecutiveButtonPressed(upID) && getExecutiveButtonPressed(downID)){
				if(!liftSwitch.get()){
					motor.set(-0.4);
				}else{
					motor.set(-defaultSpeed);
				}
			}else{
				motor.set(0.0);
			}
			
			
			Timer.delay(0.005);
		}
	}
	
	/**
	 * Controls the lift with axes and an ExecutiveOrder.
	 */
	private void executiveAxisControl(){
		
		while(running){
			Joystick currentDriver;
			
			if(order.getReleaseState()){
				currentDriver = order.congress;
			}else{
				currentDriver = order.president;
			}
			
			if(!liftSwitch.get()){
				double value = buffer(upID,currentDriver,0.18,-0.18,true)+buffer(downID,currentDriver,0.18,-0.18,false);
				motor.set(value*0.4);
			}else{
				double value = buffer(upID,currentDriver,0.18,-0.18,true)+buffer(downID,currentDriver,0.18,-0.18,false);
				motor.set(value);
			}
			
			Timer.delay(0.005);
		}
	}
	
	
	/**
	 * Controls the lift with buttons and a single driver.
	 */
	private void buttonControl(){
		while(running){
			if(driver.getRawButton(upID) && !driver.getRawButton(downID)){
				if(!liftSwitch.get()){
					motor.set(0.4);
				}else{
					motor.set(defaultSpeed);
				}
			}else if(!driver.getRawButton(upID) && driver.getRawButton(downID)){
				if(!liftSwitch.get()){
					motor.set(-0.4);
				}else{
					motor.set(-defaultSpeed);
				}
			}else{
				motor.set(0.0);
			}
			
			
			Timer.delay(0.005);
		}
	}
	
	/**
	 * Controls lift with axes and a single driver.
	 */
	private void axisControl(){
		while(running){
		
			if(!liftSwitch.get()){
				double value = buffer(upID,driver,0.18,-0.18,true)+buffer(downID,driver,0.18,-0.18,false);
				motor.set(value*0.4);
			}else{
				double value = buffer(upID,driver,0.18,-0.18,true)+buffer(downID,driver,0.18,-0.18,false);
				motor.set(value);
			}
			
		Timer.delay(0.005);
		}
	}
	
	/**
	 * Stops the control loop.
	 */
	public void stop(){
		running = false;
		motor.set(0.0);
	}
	
	 /**
     * Uses an ExecutiveOrder object to check if the button is pressed.
     * @param toggler The toggler button ID
     * @return Whether or not the button is pressed.
     */
    private boolean getExecutiveButtonPressed(int toggler) {
        boolean pressed = false;
        
        if(order.president.getRawButton(toggler)){
            order.trap();
            pressed = true;
        }
        else if(order.congress.getRawButton(toggler) && order.getReleaseState()){
            pressed = true;
        }
        
        
        return pressed;
    }
    /**
     * Checks if axis is pressed.
     * @param id ID of the axis 
     * @param currentDriver The current driver.
     * @param axisPositive Does the axis use the positive end?
     * @return Whether or not the axis is pressed.
     */
    private boolean getAxisPressed(int id, Joystick currentDriver, boolean axisPositive){
    	boolean pressed;
    	if(buffer(id,driver,true,0.18,-0.18) > 0.0 == axisPositive){
    		pressed = true;
    	}else if(buffer(id,driver,true,0.18,-0.18) < 0.0 == !axisPositive){
    		pressed = true;
    	}else{
    		pressed = false;
    	}
    	
    	return pressed;
    }
	
	/**
     * This function buffers Joystick.getRawAxis() input.
     * @param axisNum The ID for the axis of a Joystick.
     * @param joystickName The Joystick that input is coming from. 
     * @param inverted Is it flipped?
     * @param highMargin The high margin of the buffer.
     * @param lowMargin The low margin of the buffer.
     * @return moveOut - The buffered axis data from joystickName.getRawAxis().
     **/
    private double buffer(int axisNum, Joystick joystickName, boolean inverted, double highMargin, double lowMargin) {
        double moveIn = joystickName.getRawAxis(axisNum);
        double moveOut;
        moveOut = 0.0;
        
        if(moveIn >= lowMargin && moveIn <= highMargin ) {
            moveOut = 0.0;
        }
        else{
            if(inverted){
                moveOut = -moveIn;
            }
            else if(!inverted){ 
                moveOut = moveIn;
            }    
        }
	
	return moveOut;
   }
   
    
    /**
     * This function buffers Joystick.getRawAxis() input.
     * @param axisNum The ID for the axis of a Joystick.
     * @param joystickName The Joystick that input is coming from. 
     * @param inverted Is it flipped?
     * @param highMargin The high margin of the buffer.
     * @param lowMargin The low margin of the buffer.
     * @param scale The amount you want to multiply the output by.
     * @return moveOut - The buffered axis data from joystickName.getRawAxis().
     **/
    public double buffer(int axisNum, Joystick joystickName, boolean inverted, double highMargin, double lowMargin, double scale) {
        double moveIn = joystickName.getRawAxis(axisNum);
        double moveOut;
        moveOut = 0.0;
        
        if(moveIn >= lowMargin && moveIn <= highMargin ) {
            moveOut = 0.0;
        }
        else{
            if(inverted){
                moveOut = -moveIn;
            }
            else if(!inverted){ 
                moveOut = moveIn;
            }    
        }
        
        scale = Math.abs(scale);
        
        if(scale >= 1){
            scale = 1;
        }
        
        moveOut = moveOut*scale;
        
	return moveOut;
   }
    
    /**
     * This function buffers Joystick.getRawAxis() input.
     * @param axisNum The ID for the axis of a Joystick.
     * @param joystickName The Joystick that input is coming from. 
     * @param isPositive Is it positive?
     * @param highMargin The high margin of the buffer.
     * @param lowMargin The low margin of the buffer.
     * @return moveOut - The buffered axis data from joystickName.getRawAxis().
     **/
    private double buffer(int axisNum, Joystick joystickName, double highMargin, double lowMargin, boolean isPositive) {
        double moveIn = joystickName.getRawAxis(axisNum);
        double moveOut;
        moveOut = 0.0;
        
        if(moveIn >= lowMargin && moveIn <= highMargin ) {
            moveOut = 0.0;
        }
        else{
            if(isPositive){
                moveOut = Math.abs(moveIn);
            }
            else{ 
                moveOut = -Math.abs(moveIn);
            }    
        }
	
	return moveOut;
   }

}
