package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class SwerveModule {

    private final CANSparkMax driveMotor, turningMotor;
    private final RelativeEncoder driveEncoder, turningEncoder;
    private final CANCoder absolute;
    //private final AnalogInput absoluteEncoder;
    private final boolean absoluteEncoderReversed;
    

    private static final double kModuleMaxAngularVelocity = Constants.Speeds.MaxAngularSpeed;
    private static final double kModuleMaxAngularAcceleration = 2 * Math.PI; // radians per second squared
    private final PIDController drivePIDController = new PIDController(1, 0, 0);

    // Gains are for example purposes only - must be determined for your own robot!
    private final ProfiledPIDController turningPIDController =
      new ProfiledPIDController(1,0,0,new TrapezoidProfile.Constraints(kModuleMaxAngularVelocity, kModuleMaxAngularAcceleration));

    // Gains are for example purposes only - must be determined for your own robot!
    private final SimpleMotorFeedforward driveFeedforward = new SimpleMotorFeedforward(1, 3);
    private final SimpleMotorFeedforward turnFeedforward = new SimpleMotorFeedforward(1, 0.5);


    // In case when installed, the forward on the encoder isnt the actual forward
    private final double absoluteEncoderOffset;

    public SwerveModule(int driveMotorId, int turningMotorId, boolean driveMotorReversed,
            boolean turningMotorReversed, int absoluteEncoderId, double absoluteEncoderOffset,
            boolean absoluteEncoderReversed) {
        this.absoluteEncoderOffset = absoluteEncoderOffset;
        this.absoluteEncoderReversed = absoluteEncoderReversed;
        //absoluteEncoder = new AnalogInput(absoluteEncoderId);
        absolute = new CANCoder(absoluteEncoderId);

        driveMotor = new CANSparkMax(driveMotorId, MotorType.kBrushless);
        turningMotor = new CANSparkMax(turningMotorId, MotorType.kBrushless);

        driveMotor.setInverted(driveMotorReversed);
        turningMotor.setInverted(turningMotorReversed);

        driveEncoder = driveMotor.getEncoder();
        turningEncoder = turningMotor.getEncoder();
       
        driveEncoder.setVelocityConversionFactor(Constants.ModuleConversion.VELOCITY_CONVERSION_FACTOR);
       /* 
        
        // Set the distance per pulse for the drive encoder. We can simply use the
        // distance traveled for one rotation of the wheel divided by the encoder
        // resolution.
        m_driveEncoder.setDistancePerPulse(2 * Math.PI * kWheelRadius / kEncoderResolution);

        // Set the distance (in this case, angle) in radians per pulse for the turning encoder.
        // This is the the angle through an entire rotation (2 * pi) divided by the
        // encoder resolution.
        m_turningEncoder.setDistancePerPulse(2 * Math.PI / kEncoderResolution);

        // Limit the PID Controller's input range between -pi and pi and set the input
        // to be continuous.
        m_turningPIDController.enableContinuousInput(-Math.PI, Math.PI);

        resetEncoders();

        */
        // Limit the PID Controller's input range between -pi and pi and set the input
        // to be continuous.
        turningPIDController.enableContinuousInput(-Math.PI, Math.PI);
        resetEncoders();

    }

    public void setMode(IdleMode mode){
        driveMotor.setIdleMode(mode);
        turningMotor.setIdleMode(mode);
    }

    // returns the current position of the swerve module; both 
   public double getDrivePosition() {
        return driveEncoder.getPosition();
    }

    public double getTurningPosition() {
        return turningEncoder.getPosition();
    }

    // returns distance traveled by each individual motor
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(getDrivePosition() * Constants.ModuleConversion.DRIVE_MOTOR_CONVERSION, Rotation2d.fromRotations(
            getTurningPosition() * Constants.ModuleConversion.TURNING_MOTOR_CONVERSION));
    }

    // public double getAbsoluteEncoderRad() {
    //     double angle = absoluteEncoder.getVoltage() / RobotController.getVoltage5V();
    //     angle *= 2.0 * Math.PI;
    //     angle -= absoluteEncoderOffset;
    //     return angle * (absoluteEncoderReversed ? -1.0 : 1.0);

    // }

    public double getAbsoluteEncoderRad(){
        return absolute.getAbsolutePosition();
    }


    private double wrapAngle(double angle){

        double temp = angle;

        while(temp > Math.PI){
            temp -= (2 * Math.PI);
        }

        while(temp < -1 * Math.PI){
            temp += (2 * Math.PI);
        }

        return temp;
    }

    public void resetEncoders() {
        driveEncoder.setPosition(0);
        turningEncoder.setPosition(getAbsoluteEncoderRad());
    }

    public void setTurningPower(double power) {
        turningMotor.set(power);
    }

    public void setDrivePower(double power) {
        driveMotor.set(power);
    }

    public void setDesiredState(SwerveModuleState desiredState) {
        // Optimize the reference state to avoid spinning further than 90 degrees
        SwerveModuleState state =
            SwerveModuleState.optimize(desiredState, new Rotation2d(turningEncoder.getPosition() * Constants.ModuleConversion.TURNING_MOTOR_CONVERSION));
    
        // Calculate the drive output from the drive PID controller.
        final double driveOutput =
            drivePIDController.calculate(driveEncoder.getVelocity(), state.speedMetersPerSecond); // first param is the rate (speed) of the encoder in distance per second (needs scaling to get that)
    
        final double drvFeedforward = driveFeedforward.calculate(state.speedMetersPerSecond);
    
        // Calculate the turning motor output from the turning PID controller.
        final double turnOutput =
            turningPIDController.calculate(getTurningPosition() * Constants.ModuleConversion.TURNING_MOTOR_CONVERSION, state.angle.getRadians());
    
        final double trnFeedforward =
            turnFeedforward.calculate(turningPIDController.getSetpoint().velocity);
    
        driveMotor.setVoltage(driveOutput + drvFeedforward);
        turningMotor.setVoltage(turnOutput + trnFeedforward);
      }

}