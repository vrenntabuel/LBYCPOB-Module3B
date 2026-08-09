package ph.edu.dlsu.lbycpob.formulamenu;

public class ElectricalEnergy implements IFormula {

    private final String[] parameterList = {
            "Energy",
            "Voltage",
            "Current",
            "Time"
    };

    private double energy;
    private double voltage;
    private double current;
    private double time;

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void computeEnergy() {
        energy = voltage * current * time;
    }

    public void computeVoltage() {
        voltage = energy / (current * time);
    }

    public void computeCurrent() {
        current = energy / (voltage * time);
    }

    public void computeTime() {
        time = energy / (voltage * current);
    }

    @Override
    public double compute(String variable, String[] values) {

        if (variable == null) {
            return 0;
        }

        if (variable.equalsIgnoreCase("Energy")) {

            voltage = Double.parseDouble(values[0]);
            current = Double.parseDouble(values[1]);
            time = Double.parseDouble(values[2]);

            computeEnergy();
            return energy;

        } else if (variable.equalsIgnoreCase("Voltage")) {

            energy = Double.parseDouble(values[0]);
            current = Double.parseDouble(values[1]);
            time = Double.parseDouble(values[2]);

            computeVoltage();
            return voltage;

        } else if (variable.equalsIgnoreCase("Current")) {

            energy = Double.parseDouble(values[0]);
            voltage = Double.parseDouble(values[1]);
            time = Double.parseDouble(values[2]);

            computeCurrent();
            return current;

        } else if (variable.equalsIgnoreCase("Time")) {

            energy = Double.parseDouble(values[0]);
            voltage = Double.parseDouble(values[1]);
            current = Double.parseDouble(values[2]);

            computeTime();
            return time;
        }

        return 0;
    }

    @Override
    public String[] getParameterList() {
        return parameterList;
    }
}