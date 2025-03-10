// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LED;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final Intake m_Intake = new Intake();
  private final LED led = LED.getInstance();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  private boolean isStallReversing = false;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {

    m_driverController.a()
      .onTrue(
        // Commands.sequence(
        //   m_Intake.spinforward(),
        //   Commands.runOnce(() -> this.setStallReverse(false))
        // )
        // Commands.repeatingSequence(
        // Commands.parallel(m_Intake.spinforward(),Commands.waitUntil(() -> m_Intake.currentSpike())),
        // Commands.parallel(m_Intake.spinBackward(),Commands.waitSeconds(0.2)))
        // Commands.repeatingSequence(
        // new FunctionalCommand(
        //   () -> m_Intake.spin(),
        //   () -> {},
        //   (interrrupted) -> m_Intake.reverse(),
        //   () -> m_Intake.currentSpike()
        // ),
        // Commands.waitSeconds(0.1)
        // )
        Commands.repeatingSequence(
          m_Intake.spinforward(),
          Commands.waitUntil(() -> m_Intake.currentSpike()),
          Commands.runOnce(() -> m_Intake.setStallReverseSpeed(), m_Intake),
          Commands.waitUntil(() -> m_Intake.atVeloTarget())
        )
      )
      .onFalse(
        Commands.sequence(
          m_Intake.stopSpinning(),
          Commands.runOnce(() -> this.setStallReverse(false)))
          
        );

    // new Trigger(
    //   () -> m_driverController.getHID().getAButton() && m_Intake.currentSpike() && !this.getIsStallReversing()
    // ).onTrue(
    //   Commands.sequence(
    //     Commands.print("hiiiiii"),
    //     Commands.runOnce(() -> this.setStallReverse(true)),
    //     Commands.runOnce(() -> m_Intake.setStallReverseSpeed(), m_Intake),
    //     Commands.waitSeconds(Intake.stallSpitOutTime),
    //     m_Intake.spinforward(),
    //     Commands.waitSeconds(Intake.stallSpitOutTime),
    //     Commands.runOnce(() -> this.setStallReverse(false))
    //   )
    // );

    m_driverController.x().onTrue(
      Commands.sequence(
        Commands.print("hiiiiii"),
        Commands.runOnce(() -> this.setStallReverse(true)),
        Commands.runOnce(() -> m_Intake.setStallReverseSpeed(), m_Intake),
        Commands.waitUntil(() -> m_Intake.atVeloTarget()),
        m_Intake.spinforward(),
        Commands.print("skull"),
        Commands.runOnce(() -> this.setStallReverse(false))
      )
    );

    m_driverController.b()
      .onTrue(m_Intake.spinBackward())
      .onFalse(m_Intake.stopSpinning());  
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    
  }

  private void setStallReverse(boolean b) {
    this.isStallReversing = b;
  }

  private boolean getIsStallReversing() {
    return this.isStallReversing;
  }

  public void loop() {
    SmartDashboard.putBoolean("is stall reversing", isStallReversing);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
   return null;
  }
}
