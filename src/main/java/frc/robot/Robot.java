// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXSimCollection;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.ExampleSubsystem;



/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  //private TalonFX motor1 = new TalonFX(1);
  //private DoubleLogEntry telemetry;
  private RobotContainer m_robotContainer;
 // private Timer time = new Timer();
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    //motor1.set(ControlMode.PercentOutput, 0); 
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {



  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    ExampleSubsystem.t.restart();
    ExampleSubsystem.l.restart();
    ExampleSubsystem.motor6.getEncoder().setPosition(0);
    ExampleSubsystem.g.restart();
    
   // time.restart();
    //motor1.setSelectedSensorPosition(0);
   // DataLogManager.start();
    //DataLog log = DataLogManager.getLog();
    //telemetry = new DoubleLogEntry(log, "/my/double");

    
  }
  //CANCoder angle = new CANCoder(1);
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    /*
    while (time.get() < 4.0) {
      motor1.set(ControlMode.PercentOutput, 0.3);
    
    }
    */
    // motor1.set(ControlMode.PercentOutput, 0);

    //2048 tics / revolution
    /*
     while(motor1.getSelectedSensorPosition() < 5000){
      motor1.set(ControlMode.PercentOutput, 0.2);
      telemetry.append(motor1.getSelectedSensorPosition());
    }
    motor1.set(ControlMode.PercentOutput, 0);
    */
    /* 
    while(motor2.getEncoder().getPosition() < 42){
      motor2.set(0.2);
      System.out.println(motor2.getEncoder().getPosition());
    }
    motor2.set(0);
    */
    /*
     
    while(angle.getPosition() < 180){
      motor1.set(ControlMode.PercentOutput, 0.2);
      System.out.println(angle.getPosition());
    }
    motor1.set(ControlMode.PercentOutput, 0);
    */
    

  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
