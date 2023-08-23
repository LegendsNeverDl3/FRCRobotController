// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.DriveTrain;

public class DriveCommand extends CommandBase {
  private final DriveTrain swerve;
  private final CommandXboxController controller;

  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
  private final SlewRateLimiter xSpeedLimiter = new SlewRateLimiter(3);
  private final SlewRateLimiter ySpeedLimiter = new SlewRateLimiter(3);
  private final SlewRateLimiter rotLimiter = new SlewRateLimiter(3);
  /** Creates a new DriveCommand. */
  public DriveCommand(DriveTrain swerve, CommandXboxController controller) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.swerve = swerve;
    this.controller = controller;
    addRequirements(swerve);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    final var xSpeed = -xSpeedLimiter.calculate(MathUtil.applyDeadband(controller.getLeftY(), 0.02)) * swerve.kMaxSpeed;

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    final var ySpeed = -ySpeedLimiter.calculate(MathUtil.applyDeadband(controller.getLeftX(), 0.02)) * swerve.kMaxSpeed;

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    final var rot = -rotLimiter.calculate(MathUtil.applyDeadband(controller.getRightX(), 0.02)) * swerve.kMaxAngularSpeed;

    swerve.drive(xSpeed, ySpeed, rot, fieldRelative, getPeriod());
    

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}