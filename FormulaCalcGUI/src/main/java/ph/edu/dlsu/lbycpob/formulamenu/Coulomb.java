package ph.edu.dlsu.lbycpob.formulamenu;

public class Coulomb implements IFormula {
    private final String[] parameterList = {"Q1","Q2","Distance","Force"};
    private double force; // Coulomb force between two charges
    private double q1;   //  Point charge 1 magnitude
    private double q2;   //  Point charge 2 magnitude
    private double distance;    //  Distance between two point charges
    private final double k = 8987551787.3681764;//Coulomb force constant

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public double getQ1() {
        return q1;
    }

    public void setQ1(double q1) {
        this.q1 = q1;
    }

    public double getQ2() {
        return q2;
    }

    public void setQ2(double q2) {
        this.q2 = q2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void computeForce() {
        force = ((k * q1 * q2) / (distance * distance));
    }

    public void computeQ1() {
        q1 = (force * (distance * distance) / (k * q2));
    }

    public void computeQ2() {
        q2 = (force * (distance * distance) / (k * q1));
    }

    public void computeDistance() {
        distance = (Math.sqrt(k * q1 * q2 / force));
    }

    @Override
    public double compute(String variable, String[] values) {
        if (variable == null) {
            return 0;
        }
        if (variable.equalsIgnoreCase("Force")) {
            q1 = Double.parseDouble(values[0]);
            q2 = Double.parseDouble(values[1]);
            distance = Double.parseDouble(values[2]);
            computeForce();
            return force;

        } else if (variable.equalsIgnoreCase("Q1")) {
            q2 = Double.parseDouble(values[0]);
            distance = Double.parseDouble(values[1]);
            force = Double.parseDouble(values[2]);
            computeQ1();
            return q1;

        } else if (variable.equalsIgnoreCase("Q2")) {
            distance = Double.parseDouble(values[0]);
            force = Double.parseDouble(values[1]);
            q1 = Double.parseDouble(values[2]);
            computeQ2();
            return q2;

        } else if (variable.equalsIgnoreCase("distance")) {
            force = Double.parseDouble(values[0]);
            q1 = Double.parseDouble(values[1]);
            q2 = Double.parseDouble(values[2]);
            computeDistance();
            return distance;
        }
        return 0;
    }

    @Override
    public String[] getParameterList() {
        return parameterList;
    }
}
