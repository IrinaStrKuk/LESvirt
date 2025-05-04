package org.example.lesvirt;

import javafx.animation.FadeTransition;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.paint.Color;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LabStandController {
    private static final Logger logger = Logger.getLogger(LabStandController.class.getName());
    // Add these fields to LabStandController
    @FXML private Label voltmeterValue;
    @FXML private ToggleButton voltmeterButton;
    private boolean voltmeterConnected = false;
    private double voltmeterReading = 0;
    // Components
    private PowerSupply ps = new PowerSupply();
    private Transformer3Phase transformer = new Transformer3Phase();
    private LampBank lampBank = new LampBank();

    // Capacitors and resistors
    private boolean[] capacitors = new boolean[13];
    private double[] capacitorValues = {0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 10, 20, 30};
    private boolean[] resistors = new boolean[7];
    private double[] resistorValues = {100, 200, 300, 400, 500, 1000, 2000};

    // System parameters
    private boolean overloadProtection = true;
    private double maxPower = 1000; // 1 kW

    // UI Elements
    @FXML private Button powerSwitch;
    @FXML private Slider voltageSlider;
    @FXML private Label voltageValue;
    @FXML private Label freqValue;

    @FXML private Circle phase1Visual, phase2Visual, phase3Visual;
    @FXML private CheckBox phase1, phase2, phase3;
    @FXML private Label transVoltage;
    @FXML private Label transPower;

    @FXML private Circle lamp1_1, lamp1_2, lamp1_3, lamp1_4, lamp1_5;
    @FXML private Circle lamp2_1, lamp2_2, lamp2_3, lamp2_4, lamp2_5;
    @FXML private Circle lamp3_1, lamp3_2, lamp3_3, lamp3_4, lamp3_5;

    @FXML private ToggleButton cap1, cap2, cap3, cap4, cap5, cap6, cap7, cap8, cap9, cap10, cap11, cap12, cap13;
    @FXML private ToggleButton res1, res2, res3, res4, res5, res6, res7;

    @FXML private Label statusLabel;
    @FXML private CheckBox protectionCheckbox;
    @FXML private Label totalCap;
    @FXML private Label totalResistance;
    @FXML private Label powerFactor;
    @FXML private Label dc15v, dc10v;
    @FXML private Rectangle dc15vIndicator, dc10vIndicator;

    private List<Circle> lampCircles = new ArrayList<>();
    private List<ToggleButton> capButtons = new ArrayList<>();
    private List<ToggleButton> resButtons = new ArrayList<>();

    public void initialize() {
        lampCircles.clear();

        // Initialize lamp buttons list (только один раз)
        lampCircles.addAll(Arrays.asList(
                lamp1_1, lamp1_2, lamp1_3, lamp1_4, lamp1_5,
                lamp2_1, lamp2_2, lamp2_3, lamp2_4, lamp2_5,
                lamp3_1, lamp3_2, lamp3_3, lamp3_4, lamp3_5
        ));

        for (int i = 0; i < lampCircles.size(); i++) {
            if (lampCircles.get(i) == null) {
                logger.severe("Ошибка: lamp" + (i / 5 + 1) + "_" + (i % 5 + 1) + " не инициализирована!");
            }
        }
        // Проверка количества загруженных ламп
        if (lampCircles.size() != 15) {
            logger.severe("Ошибка: загружено " + lampCircles.size() + " ламп вместо 15!");
            return; // Прекращаем инициализацию при ошибке
        }

        // Initialize lamp bank with all lamps off
        lampBank.setLamps(new boolean[3][5]);

        // Настройка обработчиков событий для каждой лампы
        for (int i = 0; i < lampCircles.size(); i++) {
            final int group = i / 5;
            final int lamp = i % 5;
            Circle circle = lampCircles.get(i);

            // Очищаем все стили перед добавлением
            circle.getStyleClass().clear();

            // Добавляем CSS классы для стилизации
            circle.getStyleClass().add("lamp-circle");
            circle.getStyleClass().add("lamp-off"); // Начальное состояние - выключено

            // Проверка CSS классов после добавления
            if (!circle.getStyleClass().contains("lamp-circle")) {
                logger.warning("Лампа " + circle.getId() + " не получила CSS класс 'lamp-circle'");
            }

            // Обработчик клика
            circle.setOnMouseClicked(e -> {
                System.out.println("Клик по лампе " + circle.getId() + " / " + circle.getStyleClass());
                if (group < 3 && lamp < 5) {
                    toggleLamp(group, lamp);
                }
            });

            // Эффекты наведения
            circle.setOnMouseEntered(e -> {
                if (group < 3 && lamp < 5 && !lampBank.getLamps()[group][lamp]) {
                    circle.setOpacity(0.7);
                }
            });

            circle.setOnMouseExited(e -> {
                if (group < 3 && lamp < 5 && !lampBank.getLamps()[group][lamp]) {
                    circle.setOpacity(1.0);
                }
            });
        }

        // Initialize capacitor buttons list
        capButtons.addAll(List.of(
                cap1, cap2, cap3, cap4, cap5, cap6, cap7, cap8, cap9, cap10, cap11, cap12, cap13
        ));
        /* Add this to the initialize() method
        voltmeterButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            voltmeterConnected = newVal;
            updateVoltmeter();
        });
         */
        // Initialize DC indicators
        dc15vIndicator.getStyleClass().addAll("dc-indicator", "dc-indicator-off");
        dc10vIndicator.getStyleClass().addAll("dc-indicator", "dc-indicator-off");

        // Initialize resistor buttons list
        resButtons.addAll(List.of(
                res1, res2, res3, res4, res5, res6, res7
        ));

        // Set up event handlers
        setupEventHandlers();

        // Initial update
        updateDisplay();
    }
    private void updateVoltmeter() {
        if (voltmeterConnected && ps.isOn()) {
            // Calculate voltage based on what's being measured
            // For simplicity, we'll measure the main power supply voltage
            voltmeterReading = ps.getVoltage();
            voltmeterValue.setText(String.format("%.1f В", voltmeterReading));
            voltmeterValue.setStyle("-fx-text-fill: #006600; -fx-font-weight: bold;");
        } else {
            voltmeterValue.setText("--- В");
            voltmeterValue.setStyle("-fx-text-fill: #666666;");
        }
    }

    private void setupEventHandlers() {
        // Power switch
        powerSwitch.setOnAction(this::togglePower);

        // Voltage slider
        voltageSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateVoltage(newVal.doubleValue());
        });

        // Transformer phases
        phase1.selectedProperty().addListener((obs, oldVal, newVal) -> updatePhases());
        phase2.selectedProperty().addListener((obs, oldVal, newVal) -> updatePhases());
        phase3.selectedProperty().addListener((obs, oldVal, newVal) -> updatePhases());

        // Capacitor buttons
        for (int i = 0; i < capButtons.size(); i++) {
            final int index = i;
            capButtons.get(i).selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (index < capacitors.length) { // Добавлена проверка
                    capacitors[index] = newVal;
                    updateSystem();
                }
            });
        }

        // Resistor buttons
        for (int i = 0; i < resButtons.size(); i++) {
            final int index = i;
            resButtons.get(i).selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (index < resistors.length) { // Добавлена проверка
                    resistors[index] = newVal;
                    updateSystem();
                }
            });
        }

        // Protection checkbox
        protectionCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            overloadProtection = newVal;
            toggleProtection();
        });
    }

    @FXML
    private void togglePower(ActionEvent event) {
        ps.setOn(!ps.isOn());
        if (ps.isOn()) {
            powerSwitch.setText("ВКЛ");
            powerSwitch.getStyleClass().remove("power-off");
            powerSwitch.getStyleClass().add("power-on");

            // Set some dummy DC voltages when turned on for testing
            ps.setDcVoltage15(15);
            ps.setDcVoltage10(10);
        } else {
            powerSwitch.setText("ВЫКЛ");
            powerSwitch.getStyleClass().remove("power-on");
            powerSwitch.getStyleClass().add("power-off");

            // Set DC voltages to zero when turned off explicitly
            ps.setDcVoltage15(0);
            ps.setDcVoltage10(0);

            // Turn off all lamps
            boolean[][] lamps = lampBank.getLamps();
            for (int i = 0; i < lamps.length; i++) {
                Arrays.fill(lamps[i], false);
            }
        }

        updateDcIndicators();

        // Fade transition for power switch
        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), powerSwitch);
        fade.setFromValue(ps.isOn() ? 0.7 : 1.0);
        fade.setToValue(ps.isOn() ? 1.0 : 0.7);
        fade.play();

        updateDisplay();
    }

    private void updateDcIndicators() {
        if (ps.isOn()) {
            if (ps.getDcVoltage15() > 0) {
                dc15vIndicator.getStyleClass().remove("dc-indicator-off");
                if (!dc15vIndicator.getStyleClass().contains("dc-indicator-on")) {
                    dc15vIndicator.getStyleClass().add("dc-indicator-on");
                }
            } else {
                dc15vIndicator.getStyleClass().remove("dc-indicator-on");
                if (!dc15vIndicator.getStyleClass().contains("dc-indicator-off")) {
                    dc15vIndicator.getStyleClass().add("dc-indicator-off");
                }
            }

            if (ps.getDcVoltage10() > 0) {
                dc10vIndicator.getStyleClass().remove("dc-indicator-off");
                if (!dc10vIndicator.getStyleClass().contains("dc-indicator-on")) {
                    dc10vIndicator.getStyleClass().add("dc-indicator-on");
                }
            } else {
                dc10vIndicator.getStyleClass().remove("dc-indicator-on");
                if (!dc10vIndicator.getStyleClass().contains("dc-indicator-off")) {
                    dc10vIndicator.getStyleClass().add("dc-indicator-off");
                }
            }
        } else {
            dc15vIndicator.getStyleClass().remove("dc-indicator-on");
            if (!dc15vIndicator.getStyleClass().contains("dc-indicator-off")) {
                dc15vIndicator.getStyleClass().add("dc-indicator-off");
            }
            dc10vIndicator.getStyleClass().remove("dc-indicator-on");
            if (!dc10vIndicator.getStyleClass().contains("dc-indicator-off")) {
                dc10vIndicator.getStyleClass().add("dc-indicator-off");
            }
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        // Сброс ламп
        boolean[][] lamps = lampBank.getLamps();
        for (int i = 0; i < lamps.length; i++) {
            Arrays.fill(lamps[i], false);
        }

        for (Circle circle : lampCircles) {
            circle.getStyleClass().remove("lamp-on");
            circle.getStyleClass().add("lamp-off");
        }

        // Reset capacitors
        for (ToggleButton btn : capButtons) {
            btn.setSelected(false);
        }

        // Reset resistors
        for (ToggleButton btn : resButtons) {
            btn.setSelected(false);
        }

        // Reset voltage
        voltageSlider.setValue(220);
        ps.setVoltage(220);

        // Reset DC voltages
        ps.setDcVoltage15(0);
        ps.setDcVoltage10(0);

        // Update UI
        updateDcIndicators();

        // Update UI
        updateDisplay();
    }

    private void updateVoltage(double value) {
        if (ps.isOn()) {
            ps.setVoltage(value);
            voltageValue.setText(String.format("%.1f В", value));
            updateDisplay();
        }
    }

    private void updatePhases() {
        boolean[] phases = new boolean[]{
                phase1.isSelected(),
                phase2.isSelected(),
                phase3.isSelected()
        };
        transformer.setPhases(phases);

        // Update visual representation
        phase1Visual.setFill(phase1.isSelected() ? Color.web("#ff5555") : Color.web("#ccc"));
        phase2Visual.setFill(phase2.isSelected() ? Color.web("#55ff55") : Color.web("#ccc"));
        phase3Visual.setFill(phase3.isSelected() ? Color.web("#5555ff") : Color.web("#ccc"));

        int activePhases = transformer.getActivePhasesCount();

        // Adjust output voltage and power based on active phases
        if (activePhases == 3) {
            transformer.setOutputVoltage(20.0);
            transformer.setPower(120);
        } else if (activePhases == 2) {
            transformer.setOutputVoltage(18.0);
            transformer.setPower(80);
        } else if (activePhases == 1) {
            transformer.setOutputVoltage(15.0);
            transformer.setPower(40);
        } else {
            transformer.setOutputVoltage(0.0);
            transformer.setPower(0);
        }

        updateDisplay();
    }

    private void toggleLamp(int group, int lamp) {
        try {
            boolean[][] lamps = lampBank.getLamps();
            boolean newState = !lamps[group][lamp];
            lamps[group][lamp] = newState;
            lampBank.setLamps(lamps);

            Circle circle = lampCircles.get(group * 5 + lamp);
            circle.getStyleClass().remove(newState ? "lamp-off" : "lamp-on");
            circle.getStyleClass().add(newState ? "lamp-on" : "lamp-off");

            updateSystem();
            updateDisplay();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка переключения лампы", e);
        }
    }
    private void toggleProtection() {
        if (overloadProtection) {
            showAlert("Защита", "Защита от перегрузки включена", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Защита", "Защита от перегрузки отключена!", Alert.AlertType.WARNING);
        }
        updateDisplay();
    }

    private void updateSystem() {
        if (!ps.isOn()) {
            return;
        }

        // Calculate total capacitance
        double totalCapValue = 0;
        for (int i = 0; i < capacitors.length; i++) {
            if (capacitors[i]) {
                totalCapValue += capacitorValues[i];
            }
        }
        totalCap.setText(String.format("Общая ёмкость: %.2f мкФ", totalCapValue));

        // Calculate total resistance
        double invTotalR = 0;
        for (int i = 0; i < resistors.length; i++) {
            if (resistors[i]) {
                invTotalR += 1.0 / resistorValues[i];
            }
        }

        double totalR = invTotalR > 0 ? 1.0 / invTotalR : Double.POSITIVE_INFINITY;
        String resText = Double.isInfinite(totalR) ?
                "Общее сопротивление: ∞ Ω" :
                String.format("Общее сопротивление: %.2f Ω", totalR);
        totalResistance.setText(resText);

        // Calculate power and current
        double lampPower = calculateLampPower();
        double resPower = (!Double.isInfinite(totalR) && totalR > 0) ?
                (Math.pow(ps.getVoltage(), 2) / totalR) : 0;
        double totalPower = lampPower + resPower;

        // Check for overload
        if (overloadProtection && totalPower > maxPower) {
            showAlert("Перегрузка!",
                    String.format("Превышена максимальная мощность %.0f Вт!", maxPower),
                    Alert.AlertType.ERROR);
            togglePower(null);
            return;
        }

        double current = (ps.getVoltage() > 0) ? totalPower / ps.getVoltage() : 0;

        // Calculate power factor
        double pf = calculatePowerFactor(totalCapValue, totalR);
        powerFactor.setText(String.format("Коэффициент мощности: %.2f", pf));

        // Update rectified voltages info
        dc15v.setText(String.format("15В: %.1f В", ps.getDcVoltage15()));
        dc10v.setText(String.format("10В: %.1f В", ps.getDcVoltage10()));
        updateDcIndicators();
    }

    private double calculateLampPower() {
        int activeLamps = lampBank.getActiveLampsCount();
        double power = activeLamps * lampBank.getPowerPerLamp();
        System.out.println("Active lamps: " + activeLamps + ", power: " + power);
        return power;
    }

    private double calculatePowerFactor(double capacitance, double resistance) {
        if (capacitance == 0 || Double.isInfinite(resistance)) {
            return 1.0; // Pure active load
        }

        // Calculate reactive resistance
        double Xc = 1 / (2 * Math.PI * ps.getFrequency() * capacitance * 1e-6);
        double Z = Math.sqrt(Math.pow(resistance, 2) + Math.pow(Xc, 2));
        return resistance / Z;
    }

    private void updateDisplay() {
        // Обновляем состояние ламп
        boolean[][] lamps = lampBank.getLamps();
        for (int i = 0; i < lampCircles.size(); i++) {
            int group = i / 5;
            int lamp = i % 5;
            Circle circle = lampCircles.get(i);

            circle.getStyleClass().remove("lamp-on");
            circle.getStyleClass().remove("lamp-off");

            if (ps.isOn() && group < lamps.length && lamp < lamps[group].length && lamps[group][lamp]) {
                circle.getStyleClass().add("lamp-on");
            } else {
                circle.getStyleClass().add("lamp-off");
            }
        }
        updateDcIndicators();
        double lampPower = calculateLampPower();
        double current = (ps.getVoltage() > 0) ? lampPower / ps.getVoltage() : 0;

        // Transformer status
        int activePhases = transformer.getActivePhasesCount();
        String transStatus = activePhases > 0 ?
                String.format("%d/3 фаз", activePhases) : "отключен";

        // Build status text
        String statusText = String.format(
                "Напряжение: %.1f В\n" +
                        "Частота: %.0f Гц\n" +
                        "Мощность ламп: %.2f Вт\n" +
                        "Ток: %.2f А\n" +
                        "Трансформатор: %s\n" +
                        "Статус питания: %s\n" +
                        "Защита: %s",
                ps.getVoltage(),
                ps.getFrequency(),
                lampPower,
                current,
                transStatus,
                ps.isOn() ? "ВКЛ" : "ВЫКЛ",
                overloadProtection ? "ВКЛ" : "ВЫКЛ"
        );
        statusLabel.setText(statusText);

        // Update transformer info
        transVoltage.setText(String.format("Выходное напряжение: %.1f В", transformer.getOutputVoltage()));
        transPower.setText(String.format("Мощность: %.0f Вт", transformer.getPower()));

        // Call updateVoltmeter() in the updateDisplay() method
        //updateVoltmeter();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}