package frc.robot.subsystems;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVelocityDutyCycle;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RedRockTalon;
import frc.robot.SmartDashboardNumber;

public class Intake extends SubsystemBase {
    // private TalonFX m_spinMotor;
    private RedRockTalon m_spinMotor = new RedRockTalon(17, "spinMotor");
    private final double stallCurrent = 58;

    private SmartDashboardNumber fwdSpeed = new SmartDashboardNumber("fwd-speed", 3600);
    private SmartDashboardNumber reverseSpeed = new SmartDashboardNumber("reverse-speed", -900);
    private SmartDashboardNumber tolerance = new SmartDashboardNumber("tolerance", 60);
    private SmartDashboardNumber rateLimit = new SmartDashboardNumber("slew rate limit", 180);

    public static final double stallSpitOutTime = 0.2;

    private SlewRateLimiter limiter;

    private double target = 0;

    @Override
    public void periodic()
    {
        this.m_spinMotor.update();
        SmartDashboard.putNumber("torquecurrent", m_spinMotor.motor.getTorqueCurrent().getValueAsDouble());
        SmartDashboard.putNumber("closed loop error", m_spinMotor.motor.getClosedLoopDerivativeOutput().getValueAsDouble());
        SmartDashboard.putBoolean("atTarget", this.atVeloTarget());
        SmartDashboard.putNumber("slewed torque current", this.getTorqueCurrent());
    }

    public Intake()
    {
        limiter = new SlewRateLimiter(rateLimit.getNumber());
        // m_spinMotor = new TalonFX(17);
        this.m_spinMotor
        .withMotorOutputConfigs(
            new MotorOutputConfigs()
            .withInverted(InvertedValue.CounterClockwise_Positive)
            .withPeakForwardDutyCycle(1d)
            .withPeakReverseDutyCycle(-1d)
            .withNeutralMode(NeutralModeValue.Brake)
        )
        .withSlot0Configs(
            new Slot0Configs()
            .withKA(0)
            .withKS(0)
            .withKV(0)
            .withKP(0.6)
            .withKI(0)
            .withKD(0)
        )
        .withMotionMagicConfigs(
            new MotionMagicConfigs()
            .withMotionMagicCruiseVelocity(100)
            .withMotionMagicAcceleration(250)
            .withMotionMagicJerk(200)
        )
        .withCurrentLimitConfigs(
            new CurrentLimitsConfigs()
            .withStatorCurrentLimit(80)
            .withStatorCurrentLimitEnable(true)
            .withSupplyCurrentLimit(40)
            .withSupplyCurrentLimitEnable(true)
        )
        .withSpikeThreshold(stallCurrent);
    }

    public void spin()
    {
        this.target = fwdSpeed.getNumber();
        this.m_spinMotor.setMotionMagicVelocity(fwdSpeed.getNumber());
        // this.m_spinMotor.motor.setControl(
        //     new MotionMagicVelocityVoltage(fwdSpeed.getNumber())
        //     .withSlot(0)
        //     .withEnableFOC(true)
        //     .withOverrideBrakeDurNeutral(true)
        // );
    }

    public double getTorqueCurrent() {
        return limiter.calculate(this.m_spinMotor.motor.getTorqueCurrent().getValueAsDouble());
    }

    public void reverse()
    {
        this.target = -fwdSpeed.getNumber();
        this.m_spinMotor.setMotionMagicVelocity(-fwdSpeed.getNumber());
    }

    public void stop()
    {
        // this.m_spinMotor.setMotionMagicVelocity(0);
        this.target = 0;
        this.m_spinMotor.motor.setControl(new DutyCycleOut(0));
    }

    public boolean currentSpike()
    {
        return Math.abs(this.getTorqueCurrent()) > m_spinMotor.getSpikeThreshold();
    }

    public Command spinforward()
    {
        return Commands.runOnce(() -> spin(), this);
    }

    public void setStallReverseSpeed() {
        this.target = reverseSpeed.getNumber();
        this.m_spinMotor.motor.setControl(
            new VelocityVoltage(reverseSpeed.getNumber() / 60d)
            .withSlot(0)
            .withEnableFOC(true)
            .withOverrideBrakeDurNeutral(true)
        );
    }

    public boolean atVeloTarget() {
        return Math.abs(this.target / 60 - this.m_spinMotor.motor.getVelocity().getValueAsDouble()) < this.tolerance.getNumber();
    }


    public Trigger stalling()
    {
        return new Trigger(() -> currentSpike());
    }

    public Command spinBackward()
    {
        return Commands.runOnce(() -> reverse(), this);
    }

    public Command stopSpinning()
    {
        return Commands.runOnce(() -> stop(), this);
    }
}
