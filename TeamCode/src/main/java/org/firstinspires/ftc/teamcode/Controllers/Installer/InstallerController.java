package org.firstinspires.ftc.teamcode.Controllers.Installer;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.sun.tools.javac.code.Attribute;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Controllers.DisSensor;
import org.firstinspires.ftc.teamcode.Controllers.RobotStates;
import org.firstinspires.ftc.teamcode.Controllers.RobotStates.INSTALL_RUNMODE;
public class InstallerController{
    boolean isUpping = false;
    double Not_Installing = 0;
    double Install_Finished = 0.2;
    double InstallPos = 233;
    double SixthClipInstallPos = 58;
    double ClipLength = 25;
    int CurrentNum = 1;
    HardwareMap hardwareMap;
    Gamepad gamepad1;
    Gamepad gamepad2;
    Telemetry telemetry;
    Servo clipInstaller,clipInstallPuller,beamSpinner;
    INSTALL_RUNMODE installStates = INSTALL_RUNMODE.WAITING;
    public DisSensor disSensor = new DisSensor();
    public InstallerController(HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry) {
        // Initialize the installer with the provided hardware map and game pads
        // This method can be used to set up any necessary components or configurations
        // for the installation process.
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        clipInstaller = this.hardwareMap.get(Servo.class,"servoc0");
        clipInstallPuller = this.hardwareMap.get(Servo.class,"servoc1");
        beamSpinner = this.hardwareMap.get(Servo.class,"servoc2");
        clipInstaller.setDirection(Servo.Direction.FORWARD);
        clipInstallPuller.setDirection(Servo.Direction.FORWARD);
        beamSpinner.setDirection(Servo.Direction.FORWARD);

        disSensor.init(hardwareMap);

        // Example initialization code (to be replaced with actual implementation):
        telemetry.addData("Installer", "Initialization started");
        telemetry.update();
        beamSpinner.setPosition(0.08);
        // Perform any setup tasks here...

        telemetry.addData("Installer", "Initialization complete");
        telemetry.update();
    }
    long UpStartTime = 0;
    boolean InstallInited = false;
    long installStartTime = 0;
    public void SetCurrentNum(int currentNum) {
        // Set the current number of clip
        this.CurrentNum = currentNum;
    }
    public int getCurrentNum() {
        // Set the current number of clip
        return CurrentNum;
    }
    public RobotStates.INSTALL_RUNMODE getInstallStates(){return this.installStates;}

    public void BeamSpinner(boolean ifdown){
        if (ifdown) {
            if(!isUpping) {
                beamSpinner.setPosition(0.08);
            }
        }
        else {
             beamSpinner.setPosition(0.3);
             UpStartTime = System.currentTimeMillis();
             isUpping = true;
             if(System.currentTimeMillis() - UpStartTime > 300 && System.currentTimeMillis() - UpStartTime < 1000){
                 beamSpinner.setPosition(0.135);
            }
        }
    }
    public void setMode(INSTALL_RUNMODE installStates) {
        // Set the installation mode to the specified run mode
        this.installStates = installStates;
        switch (this.installStates) {
            case WAITING:

                clipInstaller.setPosition(Not_Installing);
                if(disSensor.getDis() > InstallPos){
                    clipInstallPuller.setPosition(0.5);
                }
                else{
                    clipInstallPuller.setPosition(0);
                }
                break;
            case EATING:
                clipInstallPuller.setPosition(1);
                if(disSensor.getDis() < SixthClipInstallPos + (6 - CurrentNum) * ClipLength) {
                    clipInstallPuller.setPosition(0.5);
                    this.installStates = INSTALL_RUNMODE.WAITING;
                } else {
                    clipInstallPuller.setPosition(1);
                }
                break;
            case INSTALLING:
                clipInstaller.setPosition(Install_Finished);
                break;
            case BACKING:
                if(disSensor.getDis() > InstallPos + 40) {
                    clipInstallPuller.setPosition(0.5);
                }
                else{
                    clipInstallPuller.setPosition(0);
                }
                break;
        }
    }
    public void run() {
        switch (this.installStates) {
            case WAITING:
                clipInstaller.setPosition(Not_Installing);

                if(disSensor.getDis() > InstallPos){
                    clipInstallPuller.setPosition(0.5);
                }
                else{
                    clipInstallPuller.setPosition(0);
                }
                break;
            case EATING:
                clipInstallPuller.setPosition(1);
                if(disSensor.getDis() < SixthClipInstallPos + (6 - CurrentNum) * ClipLength) {
                    clipInstallPuller.setPosition(0.5);
                    this.installStates = INSTALL_RUNMODE.WAITING;
                } else {
                    clipInstallPuller.setPosition(1);
                }
                break;
            case INSTALLING:
                clipInstaller.setPosition(Install_Finished);
                break;
            case BACKING:
                if(disSensor.getDis() > InstallPos + 40) {
                    clipInstallPuller.setPosition(0.5);
                }
                else{
                    clipInstallPuller.setPosition(0);
                }
                break;
        }
    }
}
