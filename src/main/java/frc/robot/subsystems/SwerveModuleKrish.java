package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;



public class SwerveModuleKrish{

    private final CANSparkMax driveMotor, turningMotor;
    private final RelativeEncoder driveEncoder, turningEncoder;
    private final AnalogInput absoluteEncoder;
    private final boolean absoluteEncoderReversed;

    //In case when installed, the forward on the encoder isnt the actual forward
    private final double absoluteEncoderOffset;

    public SwerveModuleKrish(int driveMotorId, int turningMotorId, boolean driveMotorReversed, boolean turningMotorReversed, int absoluteEncoderId, double absoluteEncoderOffset, boolean absoluteEncoderReversed){
        this.absoluteEncoderOffset = absoluteEncoderOffset;
        this.absoluteEncoderReversed = absoluteEncoderReversed;
        absoluteEncoder = new AnalogInput(absoluteEncoderId);

        driveMotor = new CANSparkMax(driveMotorId, MotorType.kBrushless);
        turningMotor = new CANSparkMax(turningMotorId, MotorType.kBrushless);

        driveMotor.setInverted(driveMotorReversed);
        turningMotor.setInverted(turningMotorReversed);

        driveEncoder = driveMotor.getEncoder();
        turningEncoder = turningMotor.getEncoder();

        // driveEncoder.setPositionConversionFactor(ModuleConversion.DriveEncoderRot2Meter);
        // driveEncoder.setVelocityConversionFactor(ModuleConversion.DriveEncoderRPM2MeterPerSec);

        // turningEncoder.setPositionConversionFactor(ModuleConversion.DriveEncoderRot2Meter);
        // turningEncoder.setVelocityConversionFactor(ModuleConversion.DriveEncoderRPM2MeterPerSec);

        resetEncoders();

    }


    public double getDrivePosition(){
        return driveEncoder.getPosition();
    }

    public double getTurningPosition(){
        return turningEncoder.getPosition();
    }

    public double getAbsoluteEncoderRad(){
        double angle = absoluteEncoder.getVoltage() / RobotController.getVoltage5V();
        angle *= 2.0 * Math.PI;
        angle -= absoluteEncoderOffset;
        return angle * (absoluteEncoderReversed ? -1.0 : 1.0);
    
    }

    public void resetEncoders(){
        driveEncoder.setPosition(0);
        turningEncoder.setPosition(getAbsoluteEncoderRad());
    }

    public void setTurningPower(double power){
        turningMotor.set(power);
    }

    public void setDrivePower(double power){
        driveMotor.set(power);
    }


}